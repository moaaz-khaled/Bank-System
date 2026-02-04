package GUI;

import DB.DBHelper;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author DELL
 */

public class LoginScreen extends JFrame implements Runnable 
{
    private final String atmName;
    
    public LoginScreen(String atmName) 
    {
        this.atmName = atmName;
        setTitle(atmName + " - Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        add(userLabel);
        add(userField);
        add(passLabel);
        add(passField);
        add(loginButton);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            int userId = DBHelper.authenticateUser(username, password);
            if (userId > 0) 
            {
                SwingUtilities.invokeLater(() -> {
                    ATMGUI atm = new ATMGUI(userId, atmName);
                    Thread atmThread = new Thread(atm);
                    atmThread.start();
                    dispose();
                });
            } 
            else if (userId == 0) {
                JOptionPane.showMessageDialog(this
                        , "This Account is Open in Another ATM");
            } 
            else {
                JOptionPane.showMessageDialog(this
                        , "Invalid username or password.");
            }
        });
    }

    @Override
    public void run() {
        setVisible(true);
    }
}