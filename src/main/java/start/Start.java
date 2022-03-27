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

    public static void main(String[] args) throws ConfigException {

        if (args.length < 1) {
            System.out.println("No config file provided. Exiting application");
            return;
        }

        System.out.println("Hello...");
        System.out.println("Configuring bot...");
        BinanceClient binanceClient = new BinanceClient();
        Graph graph = new Graph();

        // read in from config file
        LinkedHashMap<String, String> configMap = readConfigFile(args[0]);
        
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
                MarketEdge edge = new MarketEdge(base, quote, 0.0);
                edges.add(edge);
            } catch (MarketEdgeException e) {
                System.out.println(String.format("Failed initialising market edge for %s%s", base, quote));
            }
        }
        
        // Gson gson = new Gson();
        // Map exchangeInfoResponse = gson.fromJson(response, Map.class);
        // ArrayList<Object> l = (ArrayList<Object>) exchangeInfoResponse.get("symbols");
        // LinkedList<Map> mapList = new LinkedList<>();
        // for (Object o : l) {
        //     mapList.add((Map) o);
        // }
        // System.out.println(mapList.size());

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