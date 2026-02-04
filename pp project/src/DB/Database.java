package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database 
{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/thread_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    private static Database instance;
    private Connection connection;
    
    
    private Database() throws SQLException 
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Database connection creation failed: " + e.getMessage());
        }
    }
    
    public static Database getInstance() throws SQLException 
    {
        if (instance == null) 
        {
            synchronized(Database.class){
                if(instance == null){
                    instance = new Database();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException 
    {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }
    
    
    public void closeConnection(Connection con) throws SQLException 
    {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}