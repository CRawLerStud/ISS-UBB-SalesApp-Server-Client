package app.persistance.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {

    private final Properties properties;

    private Connection connection = null;

    public JdbcUtils(Properties properties){
        this.properties = properties;
    }

    public Connection getConnection(){

        try {
            if (connection == null || connection.isClosed()) {
                connection = startNewConnection();
            }
        }
        catch(SQLException e){
            System.out.println("Connection Error : " + e);
        }

        return connection;

    }

    private Connection startNewConnection(){

        String url = properties.getProperty("jdbc.url");
        String user = properties.getProperty("jdbc.user");
        String password = properties.getProperty("jdbc.password");

        Connection con = null;

        try{

            con = DriverManager.getConnection(url, user, password);

        }
        catch(SQLException e){
            System.out.println("Connection Error : " + e);
        }

        return con;
    }

}
