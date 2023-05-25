package app.networking.rpcprotocol;

import app.model.Client;
import app.model.Employee;
import app.model.Order;
import app.model.Product;
import app.networking.dto.*;
import app.networking.rpcprotocol.request.Request;
import app.networking.rpcprotocol.request.RequestType;
import app.networking.rpcprotocol.response.Response;
import app.networking.rpcprotocol.response.ResponseType;
import app.services.AppException;
import app.services.AppObserver;
import app.services.AppServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class AppServerRpcProxy implements AppServices {

    private String host;
    private int port;
    private AppObserver client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public AppServerRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<Response>();
    }

    @Override
    public Employee login(String username, String password, AppObserver clientObserver) throws AppException {
        initializeConnection();
        CredentialsDto credentialsDto = new CredentialsDto(username, password);
        Request request =
                new Request.Builder().type(RequestType.LOGIN).data(credentialsDto).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            EmployeeDto employeeDto = (EmployeeDto) response.data();
            Employee employee = DtoUtils.getFromDto(employeeDto);
            this.client = clientObserver;
            return employee;
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
        throw new AppException("Error while handling request for login!");
    }

    @Override
    public void logout(Employee employee, AppObserver clientObserver) throws AppException {
        EmployeeDto employeeDto = DtoUtils.getDto(employee);
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(employeeDto).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
        closeConnection();
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error while closing the connection![SERVER PROXY]");
        }
    }

    @Override
    public void addOrder(Order entity) throws AppException {
        OrderDto orderDto = DtoUtils.getDto(entity);
        Request request = new Request.Builder().type(RequestType.ADD_ORDER).data(orderDto).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
    }

    @Override
    public void addProduct(Product product) throws AppException {
        ProductDto productDto = DtoUtils.getDto(product);
        Request request = new Request.Builder().type(RequestType.ADD_PRODUCT).data(productDto).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
    }

    @Override
    public void updateProduct(Product newProduct) throws AppException {
        ProductDto productDto = DtoUtils.getDto(newProduct);
        Request request = new Request.Builder().type(RequestType.UPDATE_PRODUCT).data(productDto).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
    }

    @Override
    public void deleteProduct(Product product) throws AppException {
        ProductDto productDto = DtoUtils.getDto(product);
        Request request = new Request.Builder().type(RequestType.DELETE_PRODUCT).data(productDto).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
    }

    @Override
    public Client addClient(Client entity) throws AppException {
        ClientDto clientDto = DtoUtils.getDto(entity);
        Request request = new Request.Builder().type(RequestType.ADD_CLIENT).data(clientDto).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            ClientDto clientDto1 = (ClientDto) response.data();
            Client client1 = DtoUtils.getFromDto(clientDto1);
            return client1;
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
        throw new AppException("Error while adding client!");
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeObserverForClient(Employee employee, AppObserver newClientObserver) throws AppException {
        EmployeeDto employeeDto = DtoUtils.getDto(employee);
        Request request = new Request.Builder().type(RequestType.CHANGE_OBSERVER).data(employeeDto).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = newClientObserver;
            return;
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
        throw new AppException("Error while changing observer!");
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    @Override
    public List<Product> getAllProducts() throws AppException {
        Request request =
                new Request.Builder().type(RequestType.GET_PRODUCTS).data(null).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
        return Arrays.stream((ProductDto[]) response.data()).map(DtoUtils::getFromDto).toList();
    }

    @Override
    public boolean isProductPresentInAnyOrder(Product product) throws AppException {
        Request request =
                new Request.Builder().type(RequestType.IS_PRODUCT_PRESENT_IN_ORDERS)
                        .data(DtoUtils.getDto(product)).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
        String finalResponse = (String) response.data();
        return finalResponse.equals("YES");
    }

    private Response readResponse() {
        Response response = null;
        try {
            System.out.println("Taking response!");
            response = qresponses.take();
            System.out.println("Response has been taken!");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    private void sendRequest(Request request) throws AppException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new AppException("Error sending request : " + request);
        }
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.ORDERED_ADDED) {
            Order order = DtoUtils.getFromDto((OrderDto) response.data());
            try {
                if (client != null) {
                    client.orderUpdate(order);
                }
            } catch (AppException ex) {
                ex.printStackTrace();
            }
        } else if (response.type() == ResponseType.PRODUCT_ADDED) {
            Product product = DtoUtils.getFromDto((ProductDto) response.data());
            try {
                if (client != null) {
                    client.addProductUpdate(product);
                }
            } catch (AppException ex) {
                ex.printStackTrace();
            }
        } else if (response.type() == ResponseType.PRODUCT_UPDATED) {
            Product product = DtoUtils.getFromDto((ProductDto) response.data());
            try {
                if (client != null) {
                    client.productUpdate(product);
                }
            } catch (AppException ex) {
                ex.printStackTrace();
            }
        } else if (response.type() == ResponseType.PRODUCT_DELETED) {
            Product product = DtoUtils.getFromDto((ProductDto) response.data());
            try {
                if (client != null) {
                    client.deleteProductUpdate(product);
                }
            } catch (AppException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean isUpdate(Response response) {
        return
                response.type() == ResponseType.ORDERED_ADDED ||
                        response.type() == ResponseType.PRODUCT_ADDED ||
                        response.type() == ResponseType.PRODUCT_UPDATED ||
                        response.type() == ResponseType.PRODUCT_DELETED;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("Response received " + response);
                    if (isUpdate((Response) response)) {
                        System.out.println("Handling an update response!");
                        handleUpdate((Response) response);
                    } else {
                        try {
                            System.out.println("Qresponse Size before put: " + qresponses.size());
                            qresponses.put((Response) response);
                            System.out.println("Qresponse Size after put: " + qresponses.size());
                        } catch (InterruptedException e) {
                            System.out.println("Reading error " + e);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
