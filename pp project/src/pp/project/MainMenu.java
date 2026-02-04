package pp.project;

import GUI.LoginScreen;
import DB.DBHelper;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author DELL
 */

public class MainMenu extends JFrame 
{
    public MainMenu() 
    {
        setTitle("ATM System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        JLabel welcomeLabel = new JLabel("Welcome To the System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(welcomeLabel, BorderLayout.NORTH);

        JLabel loginAsLabel = new JLabel("Login As:", JLabel.CENTER);
        loginAsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        loginAsLabel.setForeground(Color.WHITE);
        loginAsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        add(loginAsLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

        JButton adminButton = createCustomButton("Admin");
        JButton customerButton = createCustomButton("User");

        adminButton.addActionListener(e -> openAdminPanel());

        customerButton.addActionListener(e -> {
            String input_NoOf_Atm = JOptionPane.showInputDialog(this
                    , "Enter the number of ATMs:");
            try {
                int numATMs = Integer.parseInt(input_NoOf_Atm);
                if (numATMs <= 0) {
                    JOptionPane.showMessageDialog(this
                            , "Number of ATMs must be greater than 0.");
                } 
                else {
                    startCustomerLogin(numATMs);
                }
            } 
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this
                        , "Invalid input. Please enter a valid number.");
            }
        });

        buttonPanel.add(adminButton);
        buttonPanel.add(customerButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createCustomButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 150, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setOpaque(true);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 100, 200));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 150, 255));
            }
        });
        
        return button;
    }

    private void openAdminPanel() 
    {
        JDialog adminDialog = new JDialog(this, "Admin Panel", true);
        adminDialog.setSize(400, 300);
        adminDialog.setLayout(new BorderLayout());
        adminDialog.getContentPane().setBackground(Color.BLACK);

        JLabel adminWelcomeLabel = new JLabel("Welcome Admin!", JLabel.CENTER);
        adminWelcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        adminWelcomeLabel.setForeground(Color.WHITE);
        adminWelcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        adminDialog.add(adminWelcomeLabel, BorderLayout.NORTH);

        JPanel adminButtonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        adminButtonPanel.setBackground(Color.BLACK);
        adminButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

        JButton addAccountButton = createCustomButton("Add Account");
        JButton showLogsButton = createCustomButton("Show Logs");
        JButton backButton = createCustomButton("Back");

        addAccountButton.addActionListener(e -> openAddAccountForm(adminDialog));
        showLogsButton.addActionListener(e -> openLogsPanel());
        backButton.addActionListener(e -> adminDialog.dispose());

        adminButtonPanel.add(addAccountButton);
        adminButtonPanel.add(showLogsButton);
        adminButtonPanel.add(backButton);

        adminDialog.add(adminButtonPanel, BorderLayout.CENTER);
        adminDialog.setLocationRelativeTo(this);
        adminDialog.setVisible(true);
    }


    private void openAddAccountForm(JDialog parentDialog) 
    {
        JDialog addAccountDialog = new JDialog(parentDialog, "Add Account", true);
        addAccountDialog.setSize(400, 300);
        addAccountDialog.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();

        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            String username = usernameField.getText().strip();
            String password = new String(passwordField.getPassword()).strip();
            String confirmPassword = new String(confirmPasswordField.getPassword()).strip();

            if (password.equals(confirmPassword)) 
            {
                if (username.length() < 3) {
                    JOptionPane.showMessageDialog(addAccountDialog,
                            "Username length must be at least 3 characters.");
                } else if (password.length() < 4) {
                    JOptionPane.showMessageDialog(addAccountDialog,
                            "Password length must be greater than 4 characters.");
                } else {
                    int createAccount = DBHelper.CreateAccount(username, password);
                    if (createAccount > 0) {
                        JOptionPane.showMessageDialog(addAccountDialog,
                                "Account created successfully!");
                        addAccountDialog.dispose();
                    } else if (createAccount == 0) {
                        JOptionPane.showMessageDialog(addAccountDialog,
                                "Account already exists.");
                    } else {
                        JOptionPane.showMessageDialog(addAccountDialog,
                                "Failed to create account.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(addAccountDialog,
                        "Passwords do not match!");
            }
        });

        cancelButton.addActionListener(e -> addAccountDialog.dispose());

        addAccountDialog.add(usernameLabel);
        addAccountDialog.add(usernameField);
        addAccountDialog.add(passwordLabel);
        addAccountDialog.add(passwordField);
        addAccountDialog.add(confirmPasswordLabel);
        addAccountDialog.add(confirmPasswordField);
        addAccountDialog.add(confirmButton);
        addAccountDialog.add(cancelButton);

        addAccountDialog.setLocationRelativeTo(parentDialog);
        addAccountDialog.setVisible(true);
    }
    
    
    private void openLogsPanel() 
    {
        List<String[]> logs = DBHelper.getLogs();

        String[] columns = {"User ID", "Action", "Amount", "Timestamp"};
        String[][] data = new String[logs.size()][4];
        for (int i = 0; i < logs.size(); i++) {
            data[i] = logs.get(i);
        }
        JTable logsTable = new JTable(data, columns);
        logsTable.setPreferredScrollableViewportSize(new Dimension(500, 200));
        logsTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(logsTable);

        JDialog logsDialog = new JDialog(this, "Logs", true);
        logsDialog.setSize(600, 300);
        logsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        logsDialog.setLayout(new BorderLayout());

        logsDialog.add(scrollPane, BorderLayout.CENTER);

        logsDialog.setVisible(true);
    }
    
    private void startCustomerLogin(int numATMs) 
    {
        for (int i = 1; i <= numATMs; i++) 
        {
            String atmName = "ATM " + i;
            new LoginScreen(atmName).run();
        }
    }
    
//    private void startCustomerLogin(int numATMs) 
//    {
//        ExecutorService executorService = Executors.newFixedThreadPool(numATMs);
//        for (int i = 1; i <= numATMs; i++) 
//        {
//            String atmName = "ATM " + i;
//            executorService.submit(() -> {
//                new LoginScreen(atmName).run();
//            });
//        }
//        executorService.shutdown();
//    }
}