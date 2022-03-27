package src.main.java.start;

public class Symbol {

    public String symbol;
    public String status;
    public String baseAsset;
    public int baseAssetPrecision;
    public String quoteAsset;
    public int quotePrecision;
    public int quoteAssetPrecision;
    public String[] orderTypes;
    public boolean icebergAllowed;
    public boolean ocoAllowed;
    public boolean quoteOrderQtyMarketAllowed;
    public boolean allowTrailingStop;
    public boolean isSpotTradingAllowed;
    public boolean isMarginTradingAllowed;
    public Object filters;
    public String[] permissions;

    public Object baseCommissionPrecision;
    public Object quoteCommissionPrecision;

}
