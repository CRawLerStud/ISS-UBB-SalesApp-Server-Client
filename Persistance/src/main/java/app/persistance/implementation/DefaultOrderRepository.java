package app.persistance.implementation;

import app.model.Order;
import app.model.OrderEntry;
import app.persistance.OrderRepository;
import app.persistance.utils.JdbcUtils;
import app.persistance.utils.RepositoryException;

import java.sql.*;
import java.util.List;
import java.util.Properties;

public class DefaultOrderRepository implements OrderRepository {

    private final JdbcUtils dbUtils;

    public DefaultOrderRepository(Properties properties) {
        this.dbUtils = new JdbcUtils(properties);
    }

    @Override
    public Long save(Order entity) throws RepositoryException {
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO orders(\"date\", payment, status, client_id) " +
                        "VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ){

            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setString(2, entity.getPayment().toString());
            statement.setString(3, entity.getStatus().toString());
            statement.setLong(4, entity.getClient().getId());

            statement.executeUpdate();

            try(ResultSet resultSet = statement.getGeneratedKeys()){
                if(resultSet.next()){
                    Long orderID = resultSet.getLong(1);

                    for(OrderEntry orderEntry : entity.getOrderEntries()){

                        try(PreparedStatement statement1 = connection.prepareStatement(
                                "INSERT INTO orders_products(order_id, product_id, quantity) " +
                                        "VALUES(?, ?, ?)")
                        ){
                            statement1.setLong(1, orderID);
                            statement1.setLong(2, orderEntry.getProduct().getId());
                            statement1.setInt(3, orderEntry.getQuantity());

                            statement1.executeUpdate();
                        }

                    }

                    return orderID;

                }
            }
        }
        catch(SQLException ex){
            throw new RepositoryException("Error while inserting order: " + ex.getMessage());
        }
        throw new RepositoryException("Couldn't insert order!");
    }

    @Override
    public Order delete(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public Order findOne(Long aLong) throws RepositoryException {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }
}
