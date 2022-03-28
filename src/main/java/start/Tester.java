package src.main.java.start;

import com.binance.connector.client.impl.WebsocketClientImpl;

public class Tester {

    public static void main (String[] args) {

        WebsocketClientImpl wsClient = new WebsocketClientImpl();
        wsClient.bookTicker("btcusdt", ((event) -> {
            System.out.println(event);
        }));

    }
}