package tiw.polimi.it.beans;

import java.util.List;

public class OnSale {
    private final List<Seller> sellers;
    private final List<Double> prices;
    private final List<Item> items;
    private final List<Integer> ids_inVendita;

    public OnSale(List<Seller> sellers, List<Double> prices, List<Item> items,List<Integer> id_inVendita) {
        this.sellers = sellers;
        this.prices = prices;
        this.items = items;
        this.ids_inVendita = id_inVendita;
    }

    public List<Seller> getSellers() {
        return sellers;
    }

    public List<Double> getPrices() {
        return prices;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Integer> getIds_inVendita() {
        return ids_inVendita;
    }

    @Override
    public String toString() {
        return "OnSale{" +
                "sellers=" + sellers +
                ", prices=" + prices +
                ", items=" + items +
                ", id_inVendita=" + ids_inVendita +
                '}';
    }
}
