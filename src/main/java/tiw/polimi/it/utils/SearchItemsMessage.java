package tiw.polimi.it.utils;

import tiw.polimi.it.beans.Item;

public class SearchItemsMessage {

    private final Item item;
    private final Double price;

    public SearchItemsMessage(Item item, Double price) {
        this.item = item;
        this.price = price;
    }

    public Item getItem() {
        return item;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "SearchItemsMessage{" +
                "item=" + item +
                ", price=" + price +
                '}';
    }
}
