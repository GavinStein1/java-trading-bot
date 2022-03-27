package src.main.java.start;

import java.util.LinkedHashMap;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

import com.google.gson.*;

import src.main.java.exceptions.BellmanFordException;
import src.main.java.exceptions.ConfigException;
import src.main.java.exceptions.MarketEdgeException;

import com.binance.connector.client.impl.*;

public class Start {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("No config file provided. Exiting application");
            return;
        }

        LinkedHashMap<String, String> configMap = readConfigFile(args[0]);

        System.out.println("Hello");
        SpotClientImpl client = new SpotClientImpl(Private.API_KEY, Private.API_SECRET);
		// LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
		// params.put("symbol", "BTCUSDT");
        // String result = client.createMarket().depth(params);

        // JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        
        // for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        //     System.out.println(entry.getKey());
        //     System.out.println(entry.getValue());
        // }

        WebsocketClientImpl wsClient = new WebsocketClientImpl();
        ArrayList<String> streams = new ArrayList<>();
        streams.add("btcusdt");
        streams.add("ethusdt");

        int streamID2 = wsClient.bookTicker("btcusdt", ((event) -> {
            System.out.println(event);
        }));

        wsClient.closeAllConnections();


        Graph graph = new Graph();
        AssetVertex btc = new AssetVertex("BTC");
        AssetVertex usd = new AssetVertex("USD");
        AssetVertex eth = new AssetVertex("ETH");
        try {
            MarketEdge btcUsd = new MarketEdge(btc, usd);
            MarketEdge ethUsd = new MarketEdge(eth, usd);
            MarketEdge btcEth = new MarketEdge(btc, eth);

            graph.addMarketEdge(btcUsd);
            graph.addMarketEdge(ethUsd);
            graph.addMarketEdge(btcEth);
            graph.bellmanFord("BTC");
        } catch (MarketEdgeException e) {
            System.err.println("Market edge exception");
        } catch (BellmanFordException b) {
            System.err.println("Bellman ford exception");;
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
                String[] split = line.split(":");
                if (split.length != 2) {
                    throw new ConfigException("Config formatting error");
                }
                configMap.put(split[0], split[1]);
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