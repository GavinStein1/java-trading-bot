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

    public static void main(String[] args) throws ConfigException {

        if (args.length < 1) {
            System.out.println("No config file provided. Exiting application");
            return;
        }

        System.out.println("Hello...");
        System.out.println("Configuring bot...");
        

        // read in from config file
        configMap = readConfigFile(args[0]);
        
        String[] markets = null;
        try {
            markets = configMap.get("markets").split(", ");
        } catch (NullPointerException e) {
            throw new ConfigException("Config formatting error: no \"markets\" key");
        }

        // Query exchange information on spot client and initialise ExchangeInfo object.
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
        LinkedHashMap<String, Integer> connectionIDs = new LinkedHashMap<>();
        for (String edgeKey : graph.getEdges().keySet()) {
            MarketEdge edge = graph.getEdges().get(edgeKey);
            int connectionID = binanceClient.createBookTickerStream(edgeKey.toLowerCase(), graph);
            connectionIDs.put(edgeKey, connectionID);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(Start::runBellmanFord, 0, 10, TimeUnit.MILLISECONDS);
    }

    public static void runBellmanFord() {
        int a = 1;
        try {
        LinkedList<MarketEdge> result = graph.bellmanFord("USDT");
        if (result.size() != 0) {
            System.out.println(result);
        }
        } catch (BellmanFordException e) {
            System.out.println("Bellman ford exception thrown");
        }
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