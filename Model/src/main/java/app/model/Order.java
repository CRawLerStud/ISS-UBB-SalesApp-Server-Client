package app.model;

import java.time.LocalDateTime;
import java.util.*;

public class Order {

    private Long id;
    private LocalDateTime date;
    private PaymentMethod payment;
    private OrderStatus status;
    private Client client;
    private List<OrderEntry> orderEntries;

    public Order(Long id, LocalDateTime date, PaymentMethod payment, OrderStatus status, Client client) {
        this.id = id;
        this.date = date;
        this.payment = payment;
        this.status = status;
        this.client = client;
        this.orderEntries = new ArrayList<>();
    }

    public Order(Long id, LocalDateTime date, PaymentMethod payment, OrderStatus status, Client client, List<OrderEntry> orderEntries) {
        this.id = id;
        this.date = date;
        this.payment = payment;
        this.status = status;
        this.client = client;
        this.orderEntries = orderEntries;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public PaymentMethod getPayment() {
        return payment;
    }

    public void setPayment(PaymentMethod payment) {
        this.payment = payment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<OrderEntry> getOrderEntries() {
        return orderEntries;
    }

    public void setOrderEntries(List<OrderEntry> orderEntries) {
        this.orderEntries = orderEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return getId().equals(order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                '}';
    }
}
