package src.main.java.start;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.binance.connector.client.utils.WebSocketCallback;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebsocketCallbackImpl implements WebSocketCallback {

    private Graph graph;

    public WebsocketCallbackImpl(Graph graph) {
        this.graph = graph;
    }

    public void onReceive(String data) {
        BookTickerResponse response = null;
        try {
            ObjectMapper ObjectMapper = new ObjectMapper();
            response = ObjectMapper.readValue(data, BookTickerResponse.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        String key = response.s;
        this.graph.getEdges().get(key).setBuyPrice(response.b);
        this.graph.getEdges().get(key).setAskPrice(response.a);

    }
    
}
