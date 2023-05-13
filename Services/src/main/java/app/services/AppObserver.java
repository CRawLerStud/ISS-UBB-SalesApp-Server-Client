package app.services;

import app.model.Order;

public interface AppObserver {

    void orderUpdate(Order order) throws AppException;

}
