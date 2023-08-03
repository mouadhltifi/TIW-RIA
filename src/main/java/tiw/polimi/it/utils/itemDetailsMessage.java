package tiw.polimi.it.utils;

import tiw.polimi.it.beans.OnSale;

import java.util.HashMap;
import java.util.List;

public class itemDetailsMessage {

    private HashMap<Integer, List<Double>> pricesOnCart;
    private OnSale onsale;

    public itemDetailsMessage(HashMap<Integer, List<Double>> pricesOnCart, OnSale onsale) {
        this.pricesOnCart = pricesOnCart == null ? new HashMap<>() : pricesOnCart;
        this.onsale = onsale;
    }
}
