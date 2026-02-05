package domain;

public class Product {
    private String id;
    private String title;
    private double price;
    private String category;
    private int stock;
    private boolean availableForClient;

    public Product() {}

    public Product(String id, String title, double price, String category, int stock, boolean availableForClient) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.availableForClient = availableForClient;
    }

    public String getId() { return id; }
    public String getTitle() { return id; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public int getStock() { return stock; }
    public boolean isAvailableForClient() { return availableForClient; }

    public void setTitle(String title) { this.title = title; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setStock(int stock) { this.stock = stock; }
    public void setAvailableForClient(boolean availableForClient) { this.availableForClient = availableForClient; }

    @Override
    public String toString() {
        return "Product {"+
                "id ='" + id + '\'' +
                ", title ='" + title + '\'' +
                ", price ='" + price + '\'' +
                ", category ='" + category + '\'' +
                ", stock ='" + stock + '\'' +
                ", availableForClient ='" + availableForClient +
                '}';
    }

}