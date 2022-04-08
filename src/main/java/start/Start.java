package src.main.java.start;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.Thread;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

import src.main.java.exceptions.BellmanFordException;
import src.main.java.exceptions.ConfigException;
import src.main.java.exceptions.MarketEdgeException;
import src.main.java.start.Order.OrderType;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.binance.connector.client.impl.spot.Market;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebsocketClientImpl;
import com.google.gson.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Start {

    private static LinkedHashMap<String, String> configMap;
    private static Graph graph = new Graph();
    private static BinanceClient binanceClient = new BinanceClient();
    private static boolean executingTrade = false;

    public static void main(String[] args) throws ConfigException {

        if (args.length < 1) {
            System.out.println("No config file provided. Exiting application");
            return;
        }

        System.out.println("Hello...");
        System.out.println("Configuring bot...");
        
        // read in from config file
        System.out.println("    Reading in config file...");
        configMap = readConfigFile(args[0]);
        
        String[] markets = null;
        try {
            markets = configMap.get("markets").split(", ");
        } catch (NullPointerException e) {
            throw new ConfigException("Config formatting error: no \"markets\" key");
        }

        // Query exchange information on spot client and initialise ExchangeInfo object.
        System.out.println("    Getting exchange market info...");
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        ArrayList<String> symbols = new ArrayList<>();
        for (String m : markets) {
            symbols.add(m.strip().toUpperCase());
        }
        parameters.put("symbols", symbols);
        
        Market binanceMarket = binanceClient.getMarket();
        String exchangeInfoResponse = binanceMarket.exchangeInfo(parameters);
        ExchangeInfo exchangeInfo = null;
        try {
            ObjectMapper ObjectMapper = new ObjectMapper();
            exchangeInfo = ObjectMapper.readValue(exchangeInfoResponse, ExchangeInfo.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        // Create AssetVertex objects and MarketEdge objects
        System.out.println("    Creating algorithm objects...");
        LinkedList<MarketEdge> edges = new LinkedList<>();
        for (Symbol symbol : exchangeInfo.symbols) {
            AssetVertex base = new AssetVertex(symbol.baseAsset);
            AssetVertex quote = new AssetVertex(symbol.quoteAsset);
            try {
                MarketEdge edge = new MarketEdge(base, quote);
                edges.add(edge);
            } catch (MarketEdgeException e) {
                System.out.println(String.format("Failed initialising market edge for %s%s", base, quote));
            }
        }

        // Add edges to graph
        for (MarketEdge edge : edges) {
            graph.addMarketEdge(edge);
        }

        // Connect to each edge's data stream (we only need best buy and ask)
        System.out.println("    Connecting to websocket streams...");
        LinkedHashMap<String, Integer> connectionIDs = new LinkedHashMap<>();
        for (String edgeKey : graph.getEdges().keySet()) {
            MarketEdge edge = graph.getEdges().get(edgeKey);
            System.out.println(edgeKey);
            int connectionID = binanceClient.createBookTickerStream(edgeKey.toLowerCase(), graph);
            connectionIDs.put(edgeKey, connectionID);
        }
        try {
            System.out.println("Waiting 8 seconds...");
            Thread.sleep(8000);
            String ping = binanceMarket.ping();
            System.out.println(ping);
            System.out.println("Let's go!");
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(Start::runBellmanFord, 0, 60, TimeUnit.MILLISECONDS);
    }

    public static void runBellmanFord() {
        if (executingTrade) {
            System.out.println("Currently executing a trade");
            return;
        }

        LinkedList<Order> result = new LinkedList<>();
        try {
            result = graph.bellmanFord("USDT");
        } catch (BellmanFordException e) {
            System.out.println("Bellman ford exception thrown");
        }

        if (result.size() != 0) {
            executingTrade = true;
            executeTrades(result);
            executingTrade = false;
        }
    }

    public static void executeTrades(LinkedList<Order> orders) {
        // WalletInfo wallet = binanceClient.getWalletInfo();
        // LinkedHashMap<String, Double> balances = new LinkedHashMap<>();
        // for (Balance b : wallet.snapshotVos[5].data.balances) {
        //     balances.put(b.asset, b.free);
        // }
        // System.out.println(balances);
        // int counter = 0;
        // while (counter < orders.size()) {
        //     System.out.println("Iterating through orders");
        //     double quantity = 0.0;
        //     if (orders.get(counter).getOrderType() == OrderType.BUY) {
        //         // Get quote balance
        //         double balance = balances.get(orders.get(counter).getMarketEdge().getQuote().getName());
        //         quantity = balance/orders.get(counter).getPrice();
        //     } else {
        //         // Get base balance
        //         double balance = balances.get(orders.get(counter).getMarketEdge().getBase().getName());
        //         quantity = balance * orders.get(counter).getPrice();
        //     }
        //     LinkedHashMap<String,Object> parameters = new LinkedHashMap<String,Object>();
        //     parameters.put("symbol", orders.get(counter).getMarketEdge().toString());
        //     parameters.put("type", "LIMIT");
        //     parameters.put("side", orders.get(counter).getOrderType());
        //     parameters.put("quantity", quantity);
        //     parameters.put("price", orders.get(counter).getPrice());
        //     System.out.println(parameters);
        //     String responseString = binanceClient.getSpotClient().createMarket().ping();
        //     System.out.println(responseString);

        //     counter ++;
        // }

    }

    public static LinkedHashMap<String, String> readConfigFile(String filepath) {
        try {
            LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
            File file = new File(filepath);
            Scanner sc = new Scanner(file);
            String line = "";
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                if (line.length() > 0 && line.charAt(0) == '#') {
                    continue;
                }
                String[] split = line.split(":");
                if (split.length != 2) {
                    throw new ConfigException("Config formatting error");
                }
                configMap.put(split[0].strip(), split[1].strip());
            }
            sc.close();
            return configMap;

        } catch (FileNotFoundException e) {
            System.out.println("Config file not found. Exiting application");
            return null;
        } catch (ConfigException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}