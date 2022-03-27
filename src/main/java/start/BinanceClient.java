package src.main.java.start;

import com.binance.connector.client.impl.spot.Market;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebsocketClientImpl;

public class BinanceClient {

    private String apiKey;
    private String apiSecret;
    public WebsocketClientImpl wsClient;
    public SpotClientImpl spotClient;


    public BinanceClient() {
        this.apiKey = Private.API_KEY;
        this.apiSecret = Private.API_SECRET;

        this.wsClient = new WebsocketClientImpl();
        this.spotClient = new SpotClientImpl(this.apiKey, this.apiSecret);
    }

    public int createBookTickerStream(String market) {
        return this.wsClient.bookTicker(market, ((event) -> {
            System.out.println(event);
        }));
    }    

    public void closeWSConnection(int id) {
        this.wsClient.closeConnection(id);
    }

    public Market getMarket() {
        return this.spotClient.createMarket();
    }
    
}
