package pp.project;

import DB.DBHelper;
import DB.Database;
import Models.SharedAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author DELL
 */


public class ATMProcess 
{
    private final SharedAccount account;
    private final ReentrantLock lock = new ReentrantLock();
    
    //    private final ReentrantLock lock;
    
    public ATMProcess(int userId) 
    {
        double initial_Balance = getBalanceFromDB(userId);
        this.account = new SharedAccount(initial_Balance);
    }

//    public ATMProcess(int userId , ReentrantLock lock) 
//    {
//        this.lock = lock;
//        double initial_Balance = getBalanceFromDB(userId);
//        this.account = new SharedAccount(initial_Balance);
//    }

    private double getBalanceFromDB(int userId) 
    {
        try (Connection conn = Database.getInstance().getConnection()) 
        {
            String selectQuery = "SELECT account_balance FROM users WHERE id = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setInt(1, userId);
            ResultSet result = selectStmt.executeQuery();
            if (result.next()) {
                return result.getDouble("account_balance");
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean deposit(int userId, double amount) throws InterruptedException 
    {
        lock.lock();
        try{
            if (account.deposit(amount)) 
            {
                Thread.sleep(5000);
                boolean success = DBHelper.UpdateBalance(userId, amount, true);
                if (success) {
                    DBHelper.logTransaction(userId, "Deposit", amount);
                }
                return success;
            } 
            else {
                return false;
            }
        }
        finally{
            lock.unlock();
        }
    }

    public boolean withdraw(int userId, double amount) throws InterruptedException 
    {
        lock.lock();
        try{
            if (account.withdraw(amount)) 
            {
                Thread.sleep(5000);
                boolean success = DBHelper.UpdateBalance(userId, amount, false);
                if (success) {
                    DBHelper.logTransaction(userId, "Withdraw", amount);
                }
                return success;
            } 
            else {
                return false;
            }
        }
        finally{
            lock.unlock();
        }
    }

    public double getBalance() throws InterruptedException 
    {
        lock.lock();
        try{
            if(account.getBalance() > 0)
            {
                Thread.sleep(2000);
                return account.getBalance();
            }
            else
                return 0.0;
        }
        finally{
            lock.unlock();
        }
    }
}