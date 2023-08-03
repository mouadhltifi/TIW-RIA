package tiw.polimi.it.beans;

public class ShippingPolicy {
    private final int minItems;
    private final int maxItems;
    private final double price;

    public ShippingPolicy(int minItems, int maxItems, double price) {
        this.minItems = minItems;
        this.maxItems = maxItems;
        this.price = price;
    }

    public int getMinItems() {
        return minItems;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "ShippingPolicy{" +
                "minItems=" + minItems +
                ", maxItems=" + maxItems +
                ", price=" + price +
                '}';
    }
}
