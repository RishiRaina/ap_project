package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthHelper;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JPanel {

    private final MainFrame mainFrame;

    public LoginScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));
        card.setPreferredSize(new Dimension(450, 380));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);

        JLabel title = new JLabel("ERP LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(44, 62, 80));

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        card.add(title, c);
        c.gridwidth = 1;

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JTextField userField = createField();

        c.gridx = 0;
        c.gridy = 1;
        card.add(userLabel, c);

        c.gridx = 1;
        card.add(userField, c);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JPasswordField passField = createPasswordField();

        c.gridx = 0;
        c.gridy = 2;
        card.add(passLabel, c);

        c.gridx = 1;
        card.add(passField, c);

        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(new Color(192, 57, 43));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        card.add(errorLabel, c);
        c.gridwidth = 1;

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        card.add(loginBtn, c);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(card, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both username and password.");
                userField.requestFocusInWindow();
                return;
            }

            errorLabel.setText("Checking credentials...");

            new Thread(() -> {
                AuthHelper.AuthResult result = AuthHelper.login(username, password);

                SwingUtilities.invokeLater(() -> {
                    if (!result.success) {
                        errorLabel.setText(result.message);
                        userField.requestFocusInWindow();
                        return;
                    }
                    errorLabel.setText("");

                    switch (result.role) {
                        case "INSTRUCTOR":
                            mainFrame.refreshInstructorDashboard();
                            break;
                        case "STUDENT":
                            mainFrame.refreshStudentDashboard();
                            break;
                        case "ADMIN":
                            mainFrame.refreshAdminDashboard();
                            break;
                    }
                });
            }).start();
        });
    }

    private JTextField createField() {
        JTextField field = new JTextField(12);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(12);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(41, 128, 185));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(31, 97, 141));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185));
            }
        });
    }
}
