package app.networking.dto;

import java.io.Serializable;

public class OrderEntryDto implements Serializable {

    private String productId;
    private String productName;
    private String productStock;
    private String productPrice;

    private String orderQuantity;

    public OrderEntryDto(String productId, String productName, String productStock, String productPrice, String orderQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.productStock = productStock;
        this.productPrice = productPrice;
        this.orderQuantity = orderQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductStock() {
        return productStock;
    }

    public void setProductStock(String productStock) {
        this.productStock = productStock;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
}
