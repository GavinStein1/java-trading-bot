package src.main.java.start;

import com.binance.connector.client.impl.spot.Market;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebsocketClientImpl;

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
    
}
