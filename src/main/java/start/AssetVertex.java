package src.main.java.start;

public class AssetVertex extends Object {

    private String assetName;

    public AssetVertex(String name) {
        super();
        this.assetName = name;
    }

    public String getName() {
        return this.assetName;
    }

    @Override
    public boolean equals(Object o) {
        try {
            AssetVertex a = (AssetVertex) o;
            return (a.getName().equals(this.getName()));
        } catch (ClassCastException e) {
            return false;
        }
    }
    
}
