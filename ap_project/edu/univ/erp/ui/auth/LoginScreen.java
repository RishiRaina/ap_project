package edu.univ.erp.ui.auth;

import edu.univ.erp.auth.AuthHelper;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JPanel {

    private MainFrame mainFrame;

    public LoginScreen(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250)); // light clean background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // ===================== MAIN CARD PANEL =====================
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        card.setPreferredSize(new Dimension(450, 380));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);

        // ===================== TITLE =====================
        JLabel title = new JLabel("ERP LOGIN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(44, 62, 80));

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        card.add(title, c);

        c.gridwidth = 1;

        // ===================== USERNAME =====================
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        userLabel.setForeground(new Color(52, 73, 94));

        JTextField userField = createInputField();

        c.gridx = 0;
        c.gridy = 1;
        card.add(userLabel, c);

        c.gridx = 1;
        card.add(userField, c);

        // ===================== PASSWORD =====================
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        passLabel.setForeground(new Color(52, 73, 94));

        JPasswordField passField = createPasswordField();

        c.gridx = 0;
        c.gridy = 2;
        card.add(passLabel, c);

        c.gridx = 1;
        card.add(passField, c);

        // ===================== ERROR LABEL =====================
        JLabel errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setForeground(new Color(192, 57, 43));
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        card.add(errorLabel, c);

        c.gridwidth = 1;

        // ===================== LOGIN BUTTON =====================
        JButton loginBtn = new JButton("Login");
        stylePrimaryButton(loginBtn);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;

        card.add(loginBtn, c);

        // Add card to center
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(card, gbc);

        // ===================== LOGIN ACTION =====================
        loginBtn.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            AuthHelper.AuthResult result = AuthHelper.login(username, password);

            if (!result.success) {
                errorLabel.setText(result.message);
                return;
            }

            errorLabel.setText("");

            String role = result.role;

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

    // ======================================================
    // COMPONENT STYLING HELPERS
    // ======================================================

    private JTextField createInputField() {
        JTextField field = new JTextField(12);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(12);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(41, 128, 185)); // blue
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

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
