package src.main.java.start;

public class Order {

    enum OrderType {
        BUY,
        SELL
    }

    private MarketEdge marketEdge;
    private OrderType orderType;
    private double price;

    public Order(MarketEdge edge, OrderType type, double price) {
        this.marketEdge = edge;
        this.orderType = type;
        this.price = price;
    }

    public MarketEdge getMarketEdge() {
        return this.marketEdge;
    }

    public OrderType getOrderType() {
        return this.orderType;
    }

    public double getPrice() {
        return this.price;
    }
    
}
