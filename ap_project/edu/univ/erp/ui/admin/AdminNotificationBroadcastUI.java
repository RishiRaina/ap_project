package edu.univ.erp.ui.admin;

import edu.univ.erp.service.NotificationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminNotificationBroadcastUI extends JPanel {

    private final NotificationService notificationService = new NotificationService();

    public AdminNotificationBroadcastUI() {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Send Notification", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(10, 0, 20, 0));

        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        // target selection
        JPanel targetRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        targetRow.setOpaque(false);

        JLabel targetLabel = new JLabel("Target:");
        targetLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        targetRow.add(targetLabel);

        String[] options = {"All Users", "All Students", "All Instructors", "All Admins"};
        JComboBox<String> targetBox = new JComboBox<>(options);
        targetBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        targetRow.add(targetBox);

        form.add(targetRow);

        // title field
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel l1 = new JLabel("Title:");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleRow.add(l1, BorderLayout.NORTH);
        titleRow.add(titleField, BorderLayout.CENTER);

        form.add(Box.createVerticalStrut(10));
        form.add(titleRow);

        // message area
        JPanel msgRow = new JPanel(new BorderLayout());
        msgRow.setOpaque(false);
        JLabel l2 = new JLabel("Message:");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextArea msgArea = new JTextArea(6, 40);
        msgArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        JScrollPane msgScroll = new JScrollPane(msgArea);

        msgRow.add(l2, BorderLayout.NORTH);
        msgRow.add(msgScroll, BorderLayout.CENTER);

        form.add(Box.createVerticalStrut(10));
        form.add(msgRow);

        // send button
        JButton sendBtn = new JButton("Send Notification");
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sendBtn.setBackground(new Color(46, 204, 113));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);

        sendBtn.addActionListener(e -> {
            String chosen = (String) targetBox.getSelectedItem();
            String titleText = titleField.getText().trim();
            String messageText = msgArea.getText().trim();

            if (titleText.isEmpty() || messageText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Title and message cannot be empty.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            switch (chosen) {
                case "All Users":
                    notificationService.notifyAllUsers(titleText, messageText);
                    break;
                case "All Students":
                    notificationService.notifyRole("STUDENT", titleText, messageText);
                    break;
                case "All Instructors":
                    notificationService.notifyRole("INSTRUCTOR", titleText, messageText);
                    break;
                case "All Admins":
                    notificationService.notifyRole("ADMIN", titleText, messageText);
                    break;
            }

            JOptionPane.showMessageDialog(this,
                    "Notification sent.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            titleField.setText("");
            msgArea.setText("");
        });

        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.add(sendBtn);

        form.add(Box.createVerticalStrut(15));
        form.add(btnRow);

        add(form, BorderLayout.CENTER);
    }
}
