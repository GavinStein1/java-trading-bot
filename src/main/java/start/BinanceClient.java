package src.main.java.start;

import java.util.LinkedHashMap;

import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebsocketClientImpl;
import com.binance.connector.client.impl.spot.Market;
import com.google.gson.*;

public class BinanceClient {

    private String apiKey;
    private String apiSecret;
    private WebsocketClientImpl wsClient;
    private SpotClientImpl spotClient;


    public BinanceClient() {
        this.apiKey = Private.API_KEY;
        this.apiSecret = Private.API_SECRET;

        this.wsClient = new WebsocketClientImpl();
        this.spotClient = new SpotClientImpl(this.apiKey, this.apiSecret);
    }

    public int createBookTickerStream(String market, Graph graph) {
        return this.wsClient.bookTicker(market, new WebsocketCallbackImpl(graph));
    }    

    public void closeWSConnection(int id) {
        this.wsClient.closeConnection(id);
    }

    public Market getMarket() {
        return this.spotClient.createMarket();
    }

    public WebsocketClientImpl getWsClient() {
        return this.wsClient;
    }

    public SpotClientImpl getSpotClient() {
        return this.spotClient;
    }

    public WalletInfo getWalletInfo() {

        LinkedHashMap<String,Object> parameters = new LinkedHashMap<String,Object>();
        parameters.put("type", "SPOT");
        String result = this.spotClient.createWallet().accountSnapshot(parameters);
        WalletInfo wallet = null;
        Gson gson = new GsonBuilder().create();
        wallet = gson.fromJson(result, WalletInfo.class);
        return wallet;
    }
    
}
