package GUI.Utils;

import DB.DBHelper;
import GUI.LoginScreen;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class ATMUtils 
{
    public static void log(JTextArea logArea, String message) {
        logArea.append(message + "\n");
    }
    
    public static ScheduledFuture<?> resetInactivityTimer(ScheduledExecutorService inactivityMonitor, ScheduledFuture<?> inactivityTask, int userId, String atmName, JFrame atmFrame) 
    {
        if (inactivityTask != null) {
            inactivityTask.cancel(true);
        }
        return inactivityMonitor.schedule(() -> {
            SwingUtilities.invokeLater(() -> {
                DBHelper.resetFlag(userId);
                atmFrame.dispose();
                new LoginScreen(atmName).setVisible(true);
            });
        }, 30, TimeUnit.SECONDS);
    }
}