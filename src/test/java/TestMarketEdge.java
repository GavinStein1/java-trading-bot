package src.test.java;

import static org.junit.Assert.*;
import org.junit.Test;

import src.main.java.exceptions.MarketEdgeException;
import src.main.java.start.AssetVertex;
import src.main.java.start.MarketEdge;

public class TestMarketEdge {

    @Test
    public void testGoodEdge() throws MarketEdgeException {
        MarketEdge marketEdge = new MarketEdge(new AssetVertex("BTC"), new AssetVertex("USD"));
        assertEquals(0.0, marketEdge.getBuyPrice(), 0.001); 
        assertEquals("BTC", marketEdge.getBase().getName());
        assertEquals("USD", marketEdge.getQuote().getName());
    }

    @Test
    public void testNullEdge() throws MarketEdgeException {
        try {
            MarketEdge marketEdge = new MarketEdge(null, null);
        } catch (Exception e) {
            assertEquals(new MarketEdgeException().getClass(), e.getClass());
        }
    }
    
    
}
