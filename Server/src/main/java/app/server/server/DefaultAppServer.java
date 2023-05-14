package app.server.server;

import app.model.*;
import app.persistance.ClientRepository;
import app.persistance.EmployeeRepository;
import app.persistance.OrderRepository;
import app.persistance.ProductRepository;
import app.persistance.utils.RepositoryException;
import app.services.AppException;
import app.services.AppObserver;
import app.services.AppServices;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DefaultAppServer implements AppServices {

    ProductRepository productRepository;
    EmployeeRepository employeeRepository;
    OrderRepository orderRepository;
    ClientRepository clientRepository;

    private Map<Long, AppObserver> loggedClients;
    private final int defaultThradsNo = 7;

    public DefaultAppServer(ProductRepository productRepository, EmployeeRepository employeeRepository, OrderRepository orderRepository, ClientRepository clientRepository) {
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Employee login(String username, String password, AppObserver clientObserver) throws AppException {
        Employee employee = null;
        try{
            employee = employeeRepository.checkCredentials(username, password);

            if(loggedClients.containsKey(employee.getId())){
                throw new AppException("Employee already logged in!");
            }

            loggedClients.put(employee.getId(), clientObserver);
        }
        catch(RepositoryException ex){
            throw new AppException("Authentication failed!");
        }
        return employee;
    }

    @Override
    public synchronized void logout(Employee employee, AppObserver clientObserver) throws AppException {
        if(!loggedClients.containsKey(employee.getId())){
            throw new AppException("Employee is not logged in!");
        }
        loggedClients.remove(employee.getId());
        System.out.println("Local Client logged out!");
    }

    @Override
    public void addOrder(Order entity) throws AppException {
        try{
            for(OrderEntry entry : entity.getOrderEntries()){
                Long productId = entry.getProduct().getId();
                Product product = productRepository.findOne(productId);

                if(entry.getQuantity() > product.getStock()){
                    throw new RepositoryException("Not enough stock for a product!");
                }
            }

            Long orderId = orderRepository.save(entity);
            entity.setId(orderId);

            for(OrderEntry entry : entity.getOrderEntries()){
                Integer newQuantity = entry.getProduct().getStock() - entry.getQuantity();
                productRepository.sellQuantity(entry.getProduct().getId(), newQuantity);
            }

            notifyAddedOrder(entity);
        }
        catch(RepositoryException ex){
            throw new AppException("Order could not be saved " + ex.getMessage());
        }
    }

    @Override
    public Client addClient(Client entity) throws AppException {
        try{

            Long clientId = clientRepository.save(entity);
            entity.setId(clientId);
            return entity;

        }
        catch(RepositoryException ex){
            throw new AppException("Client could not be saved " + ex.getMessage());
        }
    }

    private void notifyAddedOrder(Order order) {
        ExecutorService executor = Executors.newFixedThreadPool(defaultThradsNo);
        for(AppObserver observer : loggedClients.values()){
            if(observer != null){
                executor.execute(() -> {
                    try{
                        System.out.println("Invoking method orderUpdate for observer!" + observer);
                        observer.orderUpdate(order);
                    }
                    catch(AppException ex){
                        System.err.println("Error notifying employee: " + ex.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void changeObserverForClient(Employee employee, AppObserver newClientObserver) throws AppException {
        if(loggedClients.containsKey(employee.getId())){
            loggedClients.put(employee.getId(), newClientObserver);
            return;
        }
        throw new AppException("Employee is not logged in! Can't switch observer!");
    }

    @Override
    public synchronized List<Product> getAllProducts() throws AppException {
        return StreamSupport
                .stream(productRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
