package app.networking.dto;

import java.io.Serializable;

public class OrderDto implements Serializable {

    private String id;
    private String date;
    private String payment;
    private String status;

    private ClientDto clientDto;

    private OrderEntryDto[] orderEntries;

    public OrderDto(String id, String date, String payment, String status, ClientDto clientDto, OrderEntryDto[] orderEntries) {
        this.id = id;
        this.date = date;
        this.payment = payment;
        this.status = status;
        this.clientDto = clientDto;
        this.orderEntries = orderEntries;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ClientDto getClientDto() {
        return clientDto;
    }

    public void setClientDto(ClientDto clientDto) {
        this.clientDto = clientDto;
    }

    public OrderEntryDto[] getOrderEntries() {
        return orderEntries;
    }

    public void setOrderEntries(OrderEntryDto[] orderEntries) {
        this.orderEntries = orderEntries;
    }
}
