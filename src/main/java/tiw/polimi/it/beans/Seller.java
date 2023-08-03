package tiw.polimi.it.beans;

import java.util.List;

public class Seller {
    private final int sellerId;
    private final int freeShipment;
    private final String name;
    private final int rating;
    private final List<ShippingPolicy> sPolicy;

    public Seller(int sellerId, int freeShipment, String name, int rating, List<ShippingPolicy> sPolicy) {
        this.sellerId = sellerId;
        this.freeShipment = freeShipment;
        this.name = name;
        this.rating = rating;
        this.sPolicy = sPolicy;
    }

    public int getSellerId() {
        return sellerId;
    }

    public int getFreeShipment() {
        return freeShipment;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public List<ShippingPolicy> getsPolicy() {
        return sPolicy;
    }

    @Override
    public String toString() {
        return "Seller{" +
                "sellerId=" + sellerId +
                ", freeShipment=" + freeShipment +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                ", sPolicy=" + sPolicy +
                '}';
    }
}
