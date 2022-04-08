package src.main.java.start;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedHashMap;
import java.util.HashMap;

import com.binance.connector.client.impl.SpotClientImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Tester {

    public static void main (String[] args) {

        BinanceClient client = new BinanceClient();
        LinkedHashMap<String,Object> parameters = new LinkedHashMap<String,Object>();

        SpotClientImpl spotClient = client.getSpotClient();

        parameters.put("type", "SPOT");

        String result = spotClient.createWallet().accountSnapshot(parameters);

        WalletInfo wallet = null;

        Gson gson = new GsonBuilder().create();

        wallet = gson.fromJson(result, WalletInfo.class);

        for (SnapshotVos s : wallet.snapshotVos) {
            for (Balance b : s.data.balances) {
                System.out.println(b.asset + String.format(" %.4f", b.free));
            }
            System.out.println("------------------------");
        }

        System.out.println(wallet.snapshotVos[0]);
        // // TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
        // try {
        //     ObjectMapper objectMapper = new ObjectMapper();
        //     wallet = objectMapper.readValue(result, WalletInfo.class);
        // } catch (JsonProcessingException e) {
        //     System.out.println(e.getMessage());
        // }
    }
    
}