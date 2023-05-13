package app.persistance.implementation;

import app.model.Client;
import app.persistance.ClientRepository;
import app.persistance.utils.JdbcUtils;
import app.persistance.utils.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DefaultClientRepository implements ClientRepository {

    private final JdbcUtils dbUtils;

    public DefaultClientRepository(Properties properties) {
        this.dbUtils = new JdbcUtils(properties);
    }

    @Override
    public Long save(Client entity) throws RepositoryException {

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO clients(\"name\", address) " +
                        "VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS)
        ){
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getAdress());

            statement.executeUpdate();

            try(ResultSet resultSet = statement.getGeneratedKeys()){

                if(resultSet.next()){
                    return resultSet.getLong(1);
                }

            }

        }
        catch(SQLException exception){
            System.out.println("Error while inserting a client!");
        }

        throw new RepositoryException("Error while inserting client!");
    }

    @Override
    public Client delete(Long id) throws RepositoryException {
        Client client = findOne(id);

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM clients " +
                        "WHERE id = ?")
        ){
            statement.setLong(1, id);

            statement.executeUpdate();

            System.out.println("Client has been deleted successfully!");
        }
        catch(SQLException exception){
            System.out.println("Error while deleting a client!");
        }

        return client;
    }

    @Override
    public Client findOne(Long id) throws RepositoryException {
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM clients " +
                        "WHERE id = ?")
        ){

            statement.setLong(1, id);

            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()){

                    String name = resultSet.getString("name");
                    String address = resultSet.getString("address");

                    return new Client(id, name, address);
                }

            }

        }
        catch(SQLException exception){
            System.out.println("Error while finding a client!");
        }

        throw new RepositoryException("Couldn't find a client with the specified id!");
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM clients"
        )){

            try(ResultSet resultSet = statement.executeQuery()){

                while(resultSet.next()){

                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    String address = resultSet.getString("address");

                    Client client = new Client(id, name, address);
                    clients.add(client);
                }

            }

        }
        catch(SQLException exception){
            System.out.println("Error while finding all clients!");
        }

        return clients;
    }
}
