package edu.univ.erp.ui;

import edu.univ.erp.service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LoginWindow extends JFrame {
    
    private JTextField userText;
    private JPasswordField passwordText;
    private AuthService authService;

    public LoginWindow() {

        setTitle("University ERP Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        authService = new AuthService();
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel userLabel = new JLabel("Username:");
        userText = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordText = new JPasswordField();
        JButton loginButton = new JButton("Login");

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(new JLabel());
        panel.add(loginButton);
        add(panel);

        loginButton.addActionListener(e -> onLoginButtonClick());
    }

    private void onLoginButtonClick() {
        String username = userText.getText();
        String password = new String(passwordText.getPassword());
        Map<String, Object> userData = authService.checkCredentials(username, password);

        if (userData != null) {
            int userId = (int) userData.get("id");
            String userRole = (String) userData.get("role");
            JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            MainDashboard dashboard = new MainDashboard(userId, userRole);
            dashboard.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}