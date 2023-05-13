package app.networking.dto;

import java.io.Serializable;
import java.util.Objects;

public class ProductDto implements Serializable {

    private String id;
    private String name;
    private String stock;
    private String price;

    public ProductDto(String id, String name, String stock, String price) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDto that)) return false;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
