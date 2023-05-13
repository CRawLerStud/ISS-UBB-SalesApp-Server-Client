package app.client.gui.model_for_display;

import app.model.Product;

public class OrderEntryForTableView{
    private Product product;
    private String productName;
    private Integer quantity;

    public OrderEntryForTableView(Product product, String productName, Integer quantity) {
        this.product = product;
        this.productName = productName;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
