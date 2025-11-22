package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthHelper;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JPanel {

    private MainFrame mainFrame;

    public LoginScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("ERP Login");
        title.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);

        JButton loginBtn = new JButton("Login");
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        add(userLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(passLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(loginBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(errorLabel, gbc);

        // LOGIN LOGIC
        loginBtn.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            AuthHelper.AuthResult result = AuthHelper.login(username, password);

            if (!result.success) {
                errorLabel.setText(result.message);
                return;
            }

            errorLabel.setText(""); // clear errors

            // ✔ Get role from result
            String role = result.role;

            // ✔ Switch dashboard based on role
            switch (role) {
                case "INSTRUCTOR":
                    mainFrame.refreshInstructorDashboard();
                    break;
                case "STUDENT":
                    mainFrame.refreshStudentDashboard();
                    break;
                case "ADMIN":
                    mainFrame.refreshAdminDashboard();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown role: " + role);
            }
        });
    }
}
