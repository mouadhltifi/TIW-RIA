package tiw.polimi.it.beans;

import java.sql.Date;
import java.util.List;

public class Order {
    private final String sellerName;
    private final String address;
    private final Date shippingDate;
    private final List<LightItem> itemList;
    private final int orderId;
    private final double totalPrice;

    public Order(String sellerName, String address, Date shippingDate, List<LightItem> itemList, int orderId, double totalPrice) {
        this.sellerName = sellerName;
        this.address = address;
        this.shippingDate = shippingDate;
        this.itemList = itemList;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getSellerName() {
        return sellerName;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getAddress() {
        return address;
    }

    public Date getShippingDate() {
        return shippingDate;
    }

    public List<LightItem> getItemList() {
        return itemList;
    }

    @Override
    public String toString() {
        return "Order{" +
                "sellerName='" + sellerName + '\'' +
                ", address='" + address + '\'' +
                ", shippingDate=" + shippingDate +
                ", itemList=" + itemList +
                ", orderId=" + orderId +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
