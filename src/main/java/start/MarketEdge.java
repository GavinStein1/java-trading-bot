package src.main.java.start;

import src.main.java.exceptions.MarketEdgeException;

public class MarketEdge extends Object {

    private AssetVertex base;
    private AssetVertex quote;
    private Double price;

    public MarketEdge(AssetVertex base, AssetVertex quote) throws MarketEdgeException {
        super();
        if (base == null || quote == null) {
            throw new MarketEdgeException("Base or quote AssetVertex is null");
        }
        this.base = base;
        this.quote = quote;
        this.price = 0.0;
    }

    public MarketEdge(AssetVertex base, AssetVertex quote, Double price) throws MarketEdgeException {
        super();
        if (base == null || quote == null) {
            throw new MarketEdgeException("Base or quote AssetVertex is null");
        }
        this.base = base;
        this.quote = quote;
        this.price = price;
    }

    public AssetVertex getBase() {
        return this.base;
    }

    public AssetVertex getQuote() {
        return this.quote;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        String str = this.base.getName() + "-" + this.quote.getName();
        return str;
    }
    
}
