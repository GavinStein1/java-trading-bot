package src.main.java.start;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import src.main.java.exceptions.BellmanFordException;

public class Graph {

    private LinkedList<AssetVertex> vertices;
    private LinkedHashMap<String, MarketEdge> edges;
    
    public Graph() {
        this.vertices = new LinkedList<>();
        this.edges = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, MarketEdge> getEdges() {
        return this.edges;
    }

    public LinkedList<AssetVertex> getVertices() {
        return this.vertices;
    }

    public void addMarketEdge(MarketEdge edge) {

        if (edge == null) {
            return;
        }
        String edgeName = edge.toString();
        for (String m : this.edges.keySet()) {
            if (m.equals(edgeName)) {
                return;
            }
        }
        this.edges.put(edge.toString(), edge);
        AssetVertex base = edge.getBase();
        AssetVertex quote = edge.getQuote();
        boolean baseExists = false;
        for (AssetVertex a : this.vertices) {
            if (a.getName().equals(base.getName())) {
                baseExists = true;
                break;
            }
        }
        if (!baseExists) {
            this.vertices.add(base);
        }

        boolean quoteExists = false;
        for (AssetVertex a : this.vertices) {
            if (a.getName().equals(quote.getName())) {
                quoteExists = true;
                break;
            }
        }
        if (!quoteExists) {
            this.vertices.add(quote);
        }
    }

    public void updateEdge(MarketEdge edge, double buyPrice, double askPrice) {
        this.edges.get(edge.toString()).setBuyPrice(buyPrice);
        this.edges.get(edge.toString()).setAskPrice(askPrice);
    }

    public LinkedList<MarketEdge> bellmanFord(String source) throws BellmanFordException {

        // initialise a map object to track distances to vertices and the previous vertices to each vertex
        LinkedHashMap<String, Double> distances = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedList<String>> previous = new LinkedHashMap<>();

        for (AssetVertex a : this.vertices) {
            distances.put(a.getName(), Double.NEGATIVE_INFINITY);
            previous.put(a.getName(), new LinkedList<String>());
        }
        
        distances.replace(source, 0.0);

        int loopCtr = 0;
        while (loopCtr < this.vertices.size() - 1) {
            LinkedList<String> edgesKeys = new LinkedList<>();
            this.edges.forEach( (k, v) -> edgesKeys.add(k));
            for (String key : edgesKeys) {
                MarketEdge edge = this.edges.get(key);
                AssetVertex base = edge.getBase();
                AssetVertex quote = edge.getQuote();
                Double buyPrice = edge.getBuyPrice();   
                Double askPrice = edge.getAskPrice();   
                Double price = (buyPrice + askPrice)/2;
                if (price == 0.0) {
                    // No path found
                    return new LinkedList<MarketEdge>();
                }
                
                if (distances.get(quote.getName()) != Double.NEGATIVE_INFINITY && distances.get(quote.getName()) + Math.log(0.99925/price) > distances.get(base.getName())) {
                    if (!previous.get(base.getName()).contains(quote.getName())) {
                        distances.replace(base.getName(), distances.get(quote.getName()) + Math.log(0.99925/price));
                        previous.get(base.getName()).clear();
                        previous.get(base.getName()).addAll(previous.get(quote.getName()));
                        previous.get(base.getName()).add(quote.getName());
                    }
                }
                if (distances.get(base.getName()) != Double.NEGATIVE_INFINITY && distances.get(base.getName()) + Math.log(0.99925 * price) > distances.get(quote.getName())) {
                    if (!previous.get(quote.getName()).contains(base.getName())) {
                        distances.replace(quote.getName(), distances.get(base.getName()) + Math.log(0.99925 * price));
                        previous.get(quote.getName()).clear();
                        previous.get(quote.getName()).addAll(previous.get(base.getName()));
                        previous.get(quote.getName()).add(base.getName());
                    }
                }
            }
            loopCtr++;
        }

        LinkedList<MarketEdge> path = new LinkedList<>();
        if (previous.get(source).size() == 0) {
            // No path found...
            return path;
        }

        int i = 0;
        double profit = 1.0;
        while (i < (previous.get(source).size() - 1)) {
            try {
                String edgeKey = previous.get(source).get(i) + previous.get(source).get(i+1);
                if (this.getEdges().keySet().contains(edgeKey)) {
                    path.add(this.edges.get(edgeKey));
                    double askPrice = this.edges.get(edgeKey).getAskPrice();
                    double buyPrice = this.edges.get(edgeKey).getBuyPrice();
                    profit = profit * (0.99925 * (askPrice + buyPrice)/2);
                } else {
                    edgeKey = previous.get(source).get(i + 1) + previous.get(source).get(i);
                    path.add(this.edges.get(edgeKey));
                    profit = profit * (0.99925/this.edges.get(edgeKey).getBuyPrice());
                }
            } catch (NullPointerException e) { 
                System.out.println(e.getStackTrace());
                System.out.println(e.getMessage());
            }
            i ++;
        }

        try {
            String edgeKey = previous.get(source).get(i) + source;
            if (this.getEdges().keySet().contains(edgeKey)) {
                path.add(this.edges.get(previous.get(source).get(i) + source));
                double askPrice = this.edges.get(edgeKey).getAskPrice();
                double buyPrice = this.edges.get(edgeKey).getBuyPrice();
                profit = profit * (0.99925 * (askPrice + buyPrice)/2);
            } else {
                edgeKey = source + previous.get(source).get(i);
                path.add(this.edges.get(edgeKey));
                double askPrice = this.edges.get(edgeKey).getAskPrice();
                double buyPrice = this.edges.get(edgeKey).getBuyPrice();
                profit = profit * (0.99925/((askPrice + buyPrice)/2));
            }
        } catch (NullPointerException e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
        }
        profit = (profit - 1)*100;
        return path;

    }
    
}
