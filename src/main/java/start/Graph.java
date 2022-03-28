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

    public void updateEdge(MarketEdge edge, Double price) {
        this.edges.get(edge.toString()).setPrice(price);
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
                String base = "";
                String quote = "";
                try {
                    base = key.split("-")[0];
                    quote = key.split("-")[1];
                } catch (IndexOutOfBoundsException e) {
                    throw new BellmanFordException("Key formatting error in edges list");
                }
                Double weight = this.edges.get(key).getPrice();       
                
                if (distances.get(base) != Double.NEGATIVE_INFINITY && distances.get(base) + Math.log(weight) > distances.get(quote)) {
                    if (!previous.get(quote).contains(base)) {
                        distances.replace(quote, distances.get(base) + Math.log(weight));
                        previous.get(quote).clear();
                        previous.get(quote).addAll(previous.get(base));
                        previous.get(quote).add(base);
                    }
                }
            }

            loopCtr++;
        }

        LinkedList<MarketEdge> path = new LinkedList<>();

        if (previous.get(source).size() == 0) {
            return path;
        }

        int i = 0;
        while (i < (previous.get(source).size() - 1)) {
            System.out.println(i);
            String edgeKey = previous.get(source).get(i) + "-" + previous.get(source).get(i+1);
            path.add(this.edges.get(edgeKey));
            i ++;
        }
        path.add(this.edges.get(previous.get(source).get(i) + "-" + source));

        return path;

    }
    
}
