package src.main.java.start;

import src.main.java.exceptions.MarketEdgeException;

import com.binance.connector.client.impl.spot.Market;

public class MarketEdge extends Object {

    private AssetVertex base;
    private AssetVertex quote;
    private double buyPrice;
    private double askPrice;
    private String symbol;
    private Market market;

    public MarketEdge(AssetVertex base, AssetVertex quote) throws MarketEdgeException {
        super();
        if (base == null || quote == null) {
            throw new MarketEdgeException("Base or quote AssetVertex is null");
        }
        this.base = base;
        this.quote = quote;
        this.buyPrice = 0.0;
        this.askPrice = 0.0;
        this.symbol = this.base.getName() + this.quote.getName();
        this.symbol = this.symbol.toUpperCase();
        this.market = market;
    }

    // public MarketEdge(AssetVertex base, AssetVertex quote, Double price) throws MarketEdgeException {
    //     super();
    //     if (base == null || quote == null) {
    //         throw new MarketEdgeException("Base or quote AssetVertex is null");
    //     }
    //     this.base = base;
    //     this.quote = quote;
    //     this.price = price;
    // }

    public AssetVertex getBase() {
        return this.base;
    }

    public AssetVertex getQuote() {
        return this.quote;
    }

    public double getBuyPrice() {
        return this.buyPrice;
    }

    public void setBuyPrice(double price) {
        this.buyPrice = price;
    }

    public double getAskPrice() {
        return this.askPrice;
    }

    public void setAskPrice(double price) {
        this.askPrice = price;
    }

    public Market getMarket() {
        return this.market;
    }

    @Override
    public String toString() {
        String str = this.base.getName().toUpperCase() + this.quote.getName().toUpperCase();
        return str;
    }
    
}
