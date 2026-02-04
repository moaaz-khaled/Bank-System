package GUI;

import DB.DBHelper;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import pp.project.ATMProcess;

/**
 *
 * @author DELL
 */

public class ATMGUI extends JFrame implements Runnable 
{
    private final int userId;
    private final String atmName;
    private final ATMProcess atmProcess;
    private final JTextArea logArea;
    private final ScheduledExecutorService inactivityMonitor;
    private final ExecutorService executorService;
    
//    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        setVisible(true);
    }

    public ATMGUI(int userId, String atmName) 
    {
        this.userId = userId;
        this.atmName = atmName;
        this.atmProcess = new ATMProcess(userId);
//        this.atmProcess = new ATMProcess(userId , lock);
        
        setTitle(atmName);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new java.awt.event.WindowAdapter() 
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmAndExit();
            }
        });

        inactivityMonitor = Executors.newSingleThreadScheduledExecutor();
        executorService = Executors.newFixedThreadPool(4);
        resetInactivityTimer();
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2));
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton checkBalanceButton = new JButton("Check Balance");
        JButton leaveATMButton = new JButton("Leave ATM");
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(checkBalanceButton);
        panel.add(leaveATMButton);
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        depositButton.addActionListener(e -> handleDeposit(amountField));
        withdrawButton.addActionListener(e -> handleWithdraw(amountField));
        checkBalanceButton.addActionListener(e -> handleCheckBalance());
        leaveATMButton.addActionListener(e -> leaveATM());
    }

    private void handleDeposit(JTextField amountField) 
    {
        resetInactivityTimer();
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0.0) 
            {
                log("Amount must be positive!");
            } 
            else 
            {
                executorService.submit(() -> {
                    try {
                        if (atmProcess.deposit(userId, amount)) {
                            log("Deposited: $" + amount);
                        } else {
                            log("Error during deposit.");
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ATMGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        } catch (NumberFormatException ex) {
            log("Invalid input.");
        }
    }

    private void handleWithdraw(JTextField amountField) 
    {
        resetInactivityTimer();
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0.0) 
            {
                log("Amount must be positive!");
            } 
            else {
                executorService.submit(() -> {
                    try {
                        if (atmProcess.withdraw(userId, amount))
                        {
                            log("Withdrawn: $" + amount);
                        }
                        else {
                            log("Insufficient funds or error during withdrawal.");
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ATMGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
        } 
        catch (NumberFormatException ex) {
            log("Invalid input.");
        }
    }

    private void handleCheckBalance() 
    {
        resetInactivityTimer();
        executorService.submit(() -> {
            try {
                double balance = atmProcess.getBalance();
                log("Current Balance: $" + balance);
            } 
            catch (InterruptedException ex) {
                Logger.getLogger(ATMGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
    }

    private void leaveATM() 
    {
        inactivityMonitor.shutdownNow();
        DBHelper.resetFlag(userId);
        executorService.shutdown();
        SwingUtilities.invokeLater(() -> {
            new LoginScreen(atmName).setVisible(true);
            dispose();
        });
    }

    private void confirmAndExit() 
    {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) 
        {
            executorService.shutdownNow();
            inactivityMonitor.shutdownNow();
            DBHelper.resetFlag(userId);
            dispose();
        }
    }

    private void resetInactivityTimer() 
    {
        inactivityMonitor.schedule(() -> {
            SwingUtilities.invokeLater(() -> {
                leaveATM();
            });
        }, 5, TimeUnit.MINUTES);
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }
}
