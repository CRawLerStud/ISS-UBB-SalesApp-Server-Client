package app.services;

import app.model.Order;
import app.model.Product;

public interface AppObserver {

    void orderUpdate(Order order) throws AppException;
    void addProductUpdate(Product product) throws AppException;
    void productUpdate(Product product) throws AppException;
    void deleteProductUpdate(Product product) throws AppException;

}
