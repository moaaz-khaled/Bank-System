package DB;

import DB.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;


public class DBHelper
{    
    public static int authenticateUser(String username, String password) 
    {
        try (Connection conn = Database.getInstance().getConnection()) 
        {
            String query = "SELECT * FROM users WHERE username = ? and password = ?";
            
            PreparedStatement SelectStmt = conn.prepareStatement(query);
            
            SelectStmt.setString(1, username);
            SelectStmt.setString(2, password);
            
            ResultSet result = SelectStmt.executeQuery();
            if (result.next()) 
            {
                int userId = result.getInt("id");
                int activeAccount = result.getInt("Active");
                if (activeAccount == 0)
                {
                    String updateQuery = "UPDATE users SET Active = 1 WHERE id = ?";
                    PreparedStatement update = conn.prepareStatement(updateQuery);
                    update.setInt(1, userId);
                    update.executeUpdate();
                    return userId;
                } 
                else {
                    return 0;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    
    
    public static int CreateAccount(String username, String pass) 
    {
        try (Connection conn = Database.getInstance().getConnection()) 
        {    
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1,username);
            ResultSet res1 =  checkStmt.executeQuery();
            
            
            if (res1.next() && res1.getInt(1) > 0) {
                    return 0;
            }
            
            else{
                String insert_query = "INSERT INTO users (username, password , account_balance , Active) VALUES (?, ? , ? , ?)";
                
                PreparedStatement InsertStmt = conn.prepareStatement(insert_query);
                
                InsertStmt.setString(1, username);
                InsertStmt.setString(2, pass);
                InsertStmt.setDouble(3,0.0 );
                InsertStmt.setInt(4, 0);
                
                int rowsAffected = InsertStmt.executeUpdate();
                if(rowsAffected > 0){
                    return 1;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    
    public static List<String[]> getLogs() 
    {
        List<String[]> logs = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection()) 
        {
            String selectQuery = "SELECT * FROM log";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            ResultSet result = selectStmt.executeQuery();
            try (result)
            {
                while (result.next()) 
                {
                    String userId = result.getString("user_id");
                    String action = result.getString("action");
                    String amount = result.getString("amount");
                    String timestamp = result.getString("timestamp");
                    String[] Data = new String[]{userId, action, amount, timestamp};
                    logs.add(Data);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
    
    
    
    public static boolean UpdateBalance(int UserId , double amount , boolean isDeposit)
    {
        try(Connection conn = Database.getInstance().getConnection()){
            String updateQuery;
            if(isDeposit){
                updateQuery = "UPDATE users SET account_balance = account_balance + ? WHERE id = ?";
            }
            else{
                updateQuery = "UPDATE users SET account_balance = account_balance - ? WHERE id = ? AND account_balance >= ?";
            }
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, UserId);
            if(!isDeposit){
                updateStmt.setDouble(3,amount);
            }
            return updateStmt.executeUpdate() > 0;
            
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
    
    
    public static void logTransaction(int userId, String action, double amount) 
    {
        try (Connection conn = Database.getInstance().getConnection()){
            
            String insertQuery = "INSERT INTO log (user_id, action, amount, timestamp) VALUES (?, ?, ?, NOW())";
            
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            
            insertStmt.setInt(1, userId);
            insertStmt.setString(2, action);
            insertStmt.setDouble(3, amount);
                
            insertStmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public static void resetFlag(int userId) 
    {
        try (Connection conn = Database.getInstance().getConnection()) 
        {
            String updateQuery = "UPDATE users SET Active = 0 WHERE id = ?";
            
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            
            updateStmt.setInt(1, userId);
            
            updateStmt.executeUpdate();
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}