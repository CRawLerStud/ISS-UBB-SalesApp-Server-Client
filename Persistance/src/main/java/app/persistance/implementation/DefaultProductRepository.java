package app.persistance.implementation;

import app.model.Product;
import app.persistance.ProductRepository;
import app.persistance.utils.JdbcUtils;
import app.persistance.utils.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DefaultProductRepository implements ProductRepository {

    private final JdbcUtils dbUtils;

    public DefaultProductRepository(Properties properties) {
        this.dbUtils = new JdbcUtils(properties);
    }

    @Override
    public Long save(Product entity) throws RepositoryException {

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO products(\"name\", stock, price) " +
                    "VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ){
            statement.setString(1, entity.getName());
            statement.setInt(2, entity.getStock());
            statement.setFloat(3, entity.getPrice().floatValue());

            statement.executeUpdate();

            try(ResultSet resultSet = statement.getGeneratedKeys()){

                if(resultSet.next()){
                    return resultSet.getLong(1);
                }

            }

        }
        catch(SQLException exception){
            System.out.println("Error while inserting!");
            throw new RepositoryException(exception.getMessage());
        }

        throw new RepositoryException("Error while inserting product!");

    }

    @Override
    public Product delete(Long id) throws RepositoryException {

        Product product = findOne(id);

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM products " +
                        "WHERE id = ?")
        ){
            statement.setLong(1, id);

            statement.executeUpdate();

            System.out.println("Product has been deleted successfully!");
        }
        catch(SQLException exception){
            throw new RepositoryException("Error while deleting a product!");
        }

        return product;
    }

    @Override
    public Product findOne(Long aLong) throws RepositoryException {

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM products " +
                        "WHERE id = ?")
        ){

            statement.setLong(1, aLong);

            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()){

                    String name = resultSet.getString("name");
                    Integer stock = resultSet.getInt("stock");
                    Double price = resultSet.getDouble("price");

                    return new Product(aLong, name, stock, price);
                }

            }

        }
        catch(SQLException exception){
            throw new RepositoryException("Error while finding a product: " + exception.getMessage());
        }

        throw new RepositoryException("Couldn't find a product with the specified id!");
    }

    @Override
    public List<Product> findAll() {

        List<Product> products = new ArrayList<>();

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM products"
        )){

            try(ResultSet resultSet = statement.executeQuery()){

                while(resultSet.next()){

                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    Integer stock = resultSet.getInt("stock");
                    Double price = resultSet.getDouble("price");

                    Product product = new Product(id, name, stock, price);
                    products.add(product);
                }

            }

        }
        catch(SQLException exception){
            System.out.println("Error while finding all products: " + exception.getMessage());
        }

        return products;
    }

    @Override
    public void sellQuantity(Long productId, Integer newQuantity) {

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "UPDATE products " +
                        "SET stock = ? " +
                        "WHERE id = ?")
        ){

            statement.setInt(1, newQuantity);
            statement.setLong(2, productId);

            statement.executeUpdate();

        }
        catch(SQLException ex){
            System.out.println("Error while updating stock for a product : " + ex.getMessage());
        }
    }
}
