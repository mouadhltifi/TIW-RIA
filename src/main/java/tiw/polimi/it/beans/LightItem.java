package tiw.polimi.it.beans;

public class LightItem {
    private final String name;
    private final int itemId;
    private int qty;

    public LightItem(String name, int itemId,int qty) {
        this.name = name;
        this.itemId = itemId;
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void incrementQuantity(int qty){
        this.qty += qty;
    }
    public void decrementQuantity(int qty){
        this.qty-= qty;
        if(qty < 0) this.qty = 0;
    }

    public boolean myEquals(Item item) {
        return item.getId_item() == this.itemId && item.getName().equals(this.name);
    }

    @Override
    public String toString() {
        return "LightItem{" +
                "name='" + name + '\'' +
                ", itemId=" + itemId +
                ", qty=" + qty +
                '}';
    }
}
