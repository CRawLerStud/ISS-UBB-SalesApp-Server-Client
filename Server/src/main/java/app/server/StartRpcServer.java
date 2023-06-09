package app.server;

import app.networking.utils.AbstractServer;
import app.networking.utils.RpcConcurrentServer;
import app.persistance.ClientRepository;
import app.persistance.EmployeeRepository;
import app.persistance.OrderRepository;
import app.persistance.ProductRepository;
import app.persistance.hibernate.HibernateClientRepository;
import app.persistance.hibernate.HibernateEmployeeRepository;
import app.persistance.hibernate.HibernateOrderRepository;
import app.persistance.hibernate.HibernateProductRepository;
import app.server.server.DefaultAppServer;
import app.services.AppException;
import app.services.AppServices;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {

    private static int defaultPort = 55555;

    public static void main(String[] args){
        Properties properties = new Properties();

        try{
            properties.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set: ");
            properties.list(System.out);
        }
        catch(IOException ex){
            System.err.println("Cannot find server.properties");
            return;
        }

        ProductRepository productRepository = new HibernateProductRepository();
        EmployeeRepository employeeRepository = new HibernateEmployeeRepository();
        OrderRepository orderRepository = new HibernateOrderRepository();
        ClientRepository clientRepository = new HibernateClientRepository();

        AppServices services = new DefaultAppServer(productRepository, employeeRepository, orderRepository, clientRepository);

        int serverPort = defaultPort;
        try{
            serverPort = Integer.parseInt(properties.getProperty("server.port"));
        }
        catch(NumberFormatException nef){
            System.err.println("Wrong port number: " + nef.getMessage());
            System.err.println("Using default port: " + serverPort);
        }

        System.out.println("Starting server on port " + serverPort);

        AbstractServer server = new RpcConcurrentServer(serverPort, services);

        try{
            server.start();
        }
        catch(AppException ex){
            System.err.println("Error starting the server " + ex.getMessage());
        }
        finally{
            try{
                server.stop();
            }
            catch(AppException ex){
                System.err.println("Error stopping the server " + ex.getMessage());
            }
        }

    }
}
