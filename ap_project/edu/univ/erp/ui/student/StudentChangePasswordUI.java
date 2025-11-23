package edu.univ.erp.ui.student;

import edu.univ.erp.auth.AuthHelper;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentChangePasswordUI extends JPanel {

    private final MainFrame root;
    private JPasswordField oldPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private JLabel statusLabel;

    public StudentChangePasswordUI(MainFrame root) {
        this.root = root;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ---------- HEADER ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Change Password");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            SessionManager.clear();
            root.showScreen(MainFrame.LOGIN_SCREEN);
        });

        header.add(logoutBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ---------- CENTER FORM PANEL ----------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // OLD PASSWORD
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(makeLabel("Old Password:"), gbc);
        gbc.gridx = 1;
        oldPassField = makeField();
        formPanel.add(oldPassField, gbc);

        // NEW PASSWORD
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(makeLabel("New Password:"), gbc);
        gbc.gridx = 1;
        newPassField = makeField();
        formPanel.add(newPassField, gbc);

        // CONFIRM PASSWORD
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(makeLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPassField = makeField();
        formPanel.add(confirmPassField, gbc);

        // STATUS LABEL
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // ---------- BOTTOM BUTTONS ----------
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(10, 0, 30, 0));

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(52, 152, 219));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> root.showScreen(MainFrame.STUDENT_DASH));

        JButton saveBtn = new JButton("Save");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setBackground(new Color(46, 204, 113));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> handlePasswordChange());

        bottomPanel.add(backBtn);
        bottomPanel.add(saveBtn);

        add(bottomPanel, BorderLayout.SOUTH);
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
