package src.main.java.start;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.*;
import com.binance.connector.client.impl.SpotClientImpl;

public class Start {

    public static void main(String[] args) {

        System.out.println("Hello");
        SpotClientImpl client = new SpotClientImpl(Private.API_KEY, Private.API_SECRET);
		LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("symbol", "BTCUSDT");
        String result = client.createMarket().depth(params);

        JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
        
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }

        // System.out.println(jsonObject.get("symbols"));


    }
}