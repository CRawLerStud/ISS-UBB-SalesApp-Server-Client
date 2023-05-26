package app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders_products")
public class OrderEntry {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(name = "quantity")
    private Integer quantity;

    public OrderEntry() {
    }

    public OrderEntry(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
