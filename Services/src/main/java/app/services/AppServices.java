package app.services;

import app.model.Client;
import app.model.Employee;
import app.model.Order;
import app.model.Product;

import java.util.List;

public interface AppServices {

    Employee login(String username, String password, AppObserver clientObserver) throws AppException;

    void changeObserverForClient(Employee employee, AppObserver newClientObserver) throws AppException;

    List<Product> getAllProducts() throws AppException;

    void addOrder(Order entity) throws AppException;

    Client addClient(Client entity) throws AppException;

}
