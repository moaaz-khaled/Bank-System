package Models;

/**
 *
 * @author DELL
 */

public class SharedAccount
{
    private double balance;

    public SharedAccount(double initialBalance) {
        this.balance = initialBalance;
    }

    public boolean deposit(double amount) 
    {
        if (amount <= 0) 
            return false;
        else
        {
            balance += amount;
            return true;
        }
    }

    public boolean withdraw(double amount) 
    {
        if (amount <= 0 || amount > balance) 
            return false;
        else{
            balance -= amount;
            return true;
        }
    }

    public double getBalance() {
        return balance;
    }
}







// هنا ممكن يحصل  DeadLock 
//  لان كدا احنا عاملين duplicate Lock 
// مش متأكدين
// غير كدا مفيش ديد لوك



//    public boolean deposit(double amount) 
//    {
//        lock.lock();
//        try {
//            if (amount <= 0) 
//                return false;
//            else{
//                balance += amount;
//                return true;
//            }
//        } 
//        finally {
//            lock.unlock();
//        }
//    }
//
//    public boolean withdraw(double amount) 
//    {
//        lock.lock();
//        try {
//            if (amount <= 0 || amount > balance) 
//                return false;
//            else{
//                balance -= amount;
//                return true;
//            }
//        } 
//        finally {
//            lock.unlock();
//        }
//    }
//
//    public double getBalance() 
//    {
//        lock.lock();
//        try {
//            return balance;
//        } 
//        finally {
//            lock.unlock();
//        }
//    }