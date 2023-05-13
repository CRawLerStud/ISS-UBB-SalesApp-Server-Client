package app.networking.rpcprotocol;

import app.model.Client;
import app.model.Employee;
import app.model.Order;
import app.networking.dto.*;
import app.networking.rpcprotocol.request.Request;
import app.networking.rpcprotocol.response.Response;
import app.networking.rpcprotocol.response.ResponseType;
import app.services.AppException;
import app.services.AppObserver;
import app.services.AppServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class AppClientRpcReflectionWorker implements Runnable, AppObserver {

    private AppServices server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public AppClientRpcReflectionWorker(AppServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void orderUpdate(Order order) throws AppException {
        System.out.println("Order update -> started method");
        Response response = new Response.Builder().type(ResponseType.ORDERED_ADDED).data(DtoUtils.getDto(order)).build();
        try{
            sendResponse(response);
        }
        catch(IOException e){
            throw new AppException("Error sending object: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while(connected){
            try{
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if(response != null){
                    sendResponse(response);
                }
            }
            catch(IOException | ClassNotFoundException ex){
                System.out.println("Error while handling request! (RUN - ClientRpcReflectionWorker)");
                System.out.println("Error: " + ex.getMessage());
            }

            try{
                Thread.sleep(1000);
            }
            catch(InterruptedException e){
                System.out.println("Error while trying to sleep!");
                System.out.println("Error: " + e.getMessage());
            }
        }

        try{
            input.close();
            output.close();
            connection.close();
        }
        catch(IOException e){
            System.out.println("Erorr while closing connection,input,output!");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static final Response okResponse =
            new Response.Builder().type(ResponseType.OK).build();

    private void sendResponse(Response response) throws IOException {
        System.out.println("Sending response -> " + response.toString());
        output.writeObject(response);
        output.flush();
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle"+(request).type();
        System.out.println("HandlerName: " + handlerName);
        try{
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked!");
        }
        catch(InvocationTargetException | IllegalAccessException | NoSuchMethodException e){
            System.out.println("Error while handling request!");
            System.out.println("Error: " + e.getMessage());
        }
        return response;
    }

    private Response handleLOGIN(Request request){
        System.out.println("Login Request -> " + request);
        CredentialsDto credentialsDto = (CredentialsDto) request.data();
        try{
            Employee employee = server.login(credentialsDto.getUsername(), credentialsDto.getPassword(), this);
            return new Response.Builder().type(ResponseType.OK).data(DtoUtils.getDto(employee)).build();
        }
        catch (AppException ex){
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response handleCHANGE_OBSERVER(Request request){
        System.out.println("Change Observer request -> " + request);
        EmployeeDto employeeDto = (EmployeeDto) request.data();
        try{
            Employee employee = DtoUtils.getFromDto(employeeDto);
            server.changeObserverForClient(employee, this);
            return okResponse;
        }
        catch(AppException ex){
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }

    }

    private Response handleADD_ORDER(Request request){
        System.out.println("Add Order Request -> " + request);
        OrderDto orderDto = (OrderDto) request.data();
        Order order = DtoUtils.getFromDto(orderDto);
        try{
            server.addOrder(order);
            return okResponse;
        }
        catch(AppException ex){
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response handleADD_CLIENT(Request request){
        System.out.println("Add Client Request -> " + request);
        ClientDto clientDto = (ClientDto) request.data();
        Client client = DtoUtils.getFromDto(clientDto);
        try{
            Client client1 = server.addClient(client);
            return new Response.Builder().type(ResponseType.OK).data(DtoUtils.getDto(client1)).build();
        }
        catch(AppException ex){
            return new Response.Builder().type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response handleGET_PRODUCTS(Request request){
        System.out.println("Get Products Request -> " + request);
        try{
            ProductDto[] productDtos =
                    server.getAllProducts().stream()
                            .map(DtoUtils::getDto).toArray(ProductDto[]::new);
            return new Response.Builder().type(ResponseType.GET_PRODUCTS).data(productDtos).build();
        }
        catch(AppException e){
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

}
