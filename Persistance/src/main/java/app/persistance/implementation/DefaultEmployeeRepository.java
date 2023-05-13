package app.persistance.implementation;

import app.model.Admin;
import app.model.Employee;
import app.model.Salesman;
import app.persistance.EmployeeRepository;
import app.persistance.utils.JdbcUtils;
import app.persistance.utils.RepositoryException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DefaultEmployeeRepository implements EmployeeRepository {

    private final JdbcUtils dbUtils;

    public DefaultEmployeeRepository(Properties properties) {
        this.dbUtils = new JdbcUtils(properties);
    }

    @Override
    public Long save(Employee entity) throws RepositoryException {
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO employees(\"name\", surname, birthdate, username, \"password\", \"type\") " +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ){
                statement.setString(1, entity.getName());
                statement.setString(2, entity.getSurname());
                statement.setTimestamp(3, Timestamp.valueOf(entity.getBirthdate().atStartOfDay()));
                statement.setString(4, entity.getUsername());
                statement.setString(5, entity.getPassword());

            switch (entity.getClass().getSimpleName()) {
                case "Admin" -> statement.setString(6, "Admin");
                case "Salesman" -> statement.setString(6, "Salesman");
                default -> throw new RepositoryException("Invalid type of employee found while inserting!");
            }

                statement.executeUpdate();

                try(ResultSet resultSet = statement.getGeneratedKeys()){

                    if(resultSet.next()){

                        System.out.println("Employee has been added successfully!");
                        return resultSet.getLong(1);
                    }

                }
        }
        catch(SQLException exception){
            throw new RepositoryException("Error while inserting employee: " + exception.getMessage());
        }

        throw new RepositoryException("Could not insert employee!");

    }

    @Override
    public Employee delete(Long id) throws RepositoryException {
        Employee employee = findOne(id);

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM employees " +
                        "WHERE id = ?")
        ){
            statement.setLong(1, id);

            statement.executeUpdate();

            System.out.println("Employee has been deleted successfully!");
        }
        catch(SQLException exception){
            throw new RepositoryException("Error while deleting a employee!");
        }

        return employee;
    }

    @Override
    public Employee findOne(Long aLong) throws RepositoryException {
        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM employees " +
                        "WHERE id = ?")
        ){
            statement.setLong(1, aLong);

            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()){

                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");
                    LocalDate birthdate = resultSet.getTimestamp("birthdate").toLocalDateTime().toLocalDate();
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    String type = resultSet.getString("type");

                    return switch (type) {
                        case "Admin" -> new Admin(aLong, name, surname, birthdate, username, password);
                        case "Salesman" -> new Salesman(aLong, name, surname, birthdate, username, password);
                        default -> throw new RepositoryException("Invalid type of employee found!");
                    };

                }

            }
        }
        catch(SQLException exception){
            throw new RepositoryException("Error while finding employee: " + exception.getMessage());
        }

        throw new RepositoryException("Could not find any employee with the specified id!");
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM employees"
        )){

            try(ResultSet resultSet = statement.executeQuery()){

                while(resultSet.next()) {

                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");
                    LocalDate birthdate = resultSet.getTimestamp("birthdate").toLocalDateTime().toLocalDate();
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    String type = resultSet.getString("type");

                    switch (type) {
                        case "Admin" -> employees.add(new Admin(id, name, surname, birthdate, username, password));
                        case "Salesman" -> employees.add(new Salesman(id, name, surname, birthdate, surname, password));
                        default -> {
                            return null;
                        }
                    }
                }

            }

        }
        catch(SQLException exception){
            System.out.println("Error while finding all employees: " + exception.getMessage());
        }

        return employees;
    }

    @Override
    public Employee checkCredentials(String username, String password) throws RepositoryException {

        Connection connection = dbUtils.getConnection();

        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM employees " +
                        "WHERE username = ? AND \"password\" = ?")
        ){

            statement.setString(1, username);
            statement.setString(2, password);

            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){

                    System.out.println("Result set has a next!");

                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");
                    LocalDate birthdate = resultSet.getTimestamp("birthdate").toLocalDateTime().toLocalDate();

                    String type = resultSet.getString("type");

                    return switch (type) {
                        case "Admin" -> new Admin(id, name, surname, birthdate, username, password);
                        case "Salesman" -> new Salesman(id, name, surname, birthdate, username, password);
                        default -> throw new RepositoryException("Invalid type found while checking credentials!");
                    };

                }
            }

        }
        catch(SQLException ex){
            throw new RepositoryException("Error while checking credentials: " + ex.getMessage());
        }

        throw new RepositoryException("Employee with this specific credentials does not exist!");

    }
}
