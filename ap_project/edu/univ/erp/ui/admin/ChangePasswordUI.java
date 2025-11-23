package edu.univ.erp.ui.admin;

import edu.univ.erp.auth.AuthHelper;
import edu.univ.erp.auth.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChangePasswordUI extends JPanel {

    private JPasswordField oldPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private JLabel statusLabel;

    public ChangePasswordUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ---------- HEADER AREA ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Change Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);

        // Button aligned next to logout of dashboard (visual match)
        JButton changeBtnTop = new JButton("Save");
        changeBtnTop.setFont(new Font("Segoe UI", Font.BOLD, 14));
        changeBtnTop.setForeground(Color.WHITE);
        changeBtnTop.setBackground(new Color(46, 204, 113));
        changeBtnTop.setFocusPainted(false);
        changeBtnTop.setCursor(new Cursor(Cursor.HAND_CURSOR));

        changeBtnTop.addActionListener(e -> handlePasswordChange());
        header.add(changeBtnTop, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ---------- CENTER FORM PANEL ----------
        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // OLD PASSWORD
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(makeLabel("Old Password:"), gbc);

        gbc.gridx = 1;
        oldPassField = makeField();
        card.add(oldPassField, gbc);

        // NEW PASSWORD
        gbc.gridx = 0; gbc.gridy++;
        card.add(makeLabel("New Password:"), gbc);

        gbc.gridx = 1;
        newPassField = makeField();
        card.add(newPassField, gbc);

        // CONFIRM PASSWORD
        gbc.gridx = 0; gbc.gridy++;
        card.add(makeLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        confirmPassField = makeField();
        card.add(confirmPassField, gbc);

        // STATUS LABEL
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(Color.RED);
        card.add(statusLabel, gbc);

        add(card, BorderLayout.CENTER);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return lbl;
    }

    private JPasswordField makeField() {
        JPasswordField pf = new JPasswordField(15);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return pf;
    }

    private void handlePasswordChange() {
        String oldPass = new String(oldPassField.getPassword());
        String newPass = new String(newPassField.getPassword());
        String confirm = new String(confirmPassField.getPassword());

        statusLabel.setForeground(Color.RED);

        if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            statusLabel.setText("All fields are required.");
            return;
        }

        if (!newPass.equals(confirm)) {
            statusLabel.setText("New passwords do not match.");
            return;
        }

        int userId = SessionManager.getCurrentUserId();

        boolean ok = AuthHelper.changePassword(userId, oldPass, newPass);

        if (ok) {
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Password changed successfully.");
            clearFields();
        } else {
            statusLabel.setText("Old password incorrect.");
        }
    }

    private void clearFields() {
        oldPassField.setText("");
        newPassField.setText("");
        confirmPassField.setText("");
    }
}
