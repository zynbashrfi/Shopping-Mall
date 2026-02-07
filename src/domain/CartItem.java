package domain;

public class CartItem {
    private String productId;
    private double unitPrice;
    private int qty;

    public CartItem() {}

    public CartItem(String productId, double unitPrice, int qty) {
        this.productId = productId;
        this.unitPrice = unitPrice;
        this.qty = qty;
    }

    public String getProductId() { return productId; }
    public double getUnitPrice() { return unitPrice; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    @Override
    public String toString() {
        return "CartItem {" +
                "ProductId = '" + productId +'\'' +
                ", UnitPrice = " + unitPrice +'\''+
                ", qty =" + qty +
                "}";
    }
}