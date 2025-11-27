package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddCourseUI extends JPanel {

    private AdminService adminService = new AdminService();


    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;

        public RoundedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    public AddCourseUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Soft background


        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));  // Modern blue
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        JLabel title = new JLabel("Add New Course");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);

        add(header, BorderLayout.NORTH);


        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));


        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(4, 2, 20, 20)); // increased spacing
        form.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100)); // bigger padding
        form.setPreferredSize(new Dimension(550, 350)); // increased overall size

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        JLabel codeLabel = new JLabel("Course Code:");
        codeLabel.setFont(labelFont);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setFont(labelFont);

        JLabel creditsLabel = new JLabel("Credits:");
        creditsLabel.setFont(labelFont);

        JTextField codeField = new JTextField();
        codeField.setFont(inputFont);
        setPlaceholder(codeField, "Enter course code");

        JTextField titleField = new JTextField();
        titleField.setFont(inputFont);
        setPlaceholder(titleField, "Enter course title");


        JComboBox<String> creditsBox = new JComboBox<>();
        creditsBox.setFont(inputFont);
        creditsBox.addItem("Select Credits");
        creditsBox.addItem("1");
        creditsBox.addItem("2");
        creditsBox.addItem("4");
        creditsBox.setSelectedIndex(0);

        form.add(codeLabel);
        form.add(codeField);

        form.add(titleLabel);
        form.add(titleField);

        form.add(creditsLabel);
        form.add(creditsBox);


        JButton submit = new JButton("Add Course");
        JButton back = new JButton("Back");

        styleButton(submit, new Color(46, 204, 113), new Color(39, 174, 96)); // Green
        styleButton(back, new Color(52, 152, 219), new Color(41, 128, 185)); // Blue

        form.add(submit);
        form.add(back);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);


        submit.addActionListener(e -> {
            try {
                String code = codeField.getText().trim();
                String ctitle = titleField.getText().trim();
                String creditsStr = (String) creditsBox.getSelectedItem();


                if (code.equals("") || code.equals("Enter course code")) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid course code.");
                    return;
                }

                if (ctitle.equals("") || ctitle.equals("Enter course title")) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid course title.");
                    return;
                }


                if (!isValidAlphaNumeric(code)) {
                    JOptionPane.showMessageDialog(this,
                            "Course code must be alphanumeric only .");
                    return;
                }

                if (!isValidAlphaNumeric(ctitle)) {
                    JOptionPane.showMessageDialog(this,
                            "Course title must be alphanumeric only .");
                    return;
                }


                if (creditsStr.equals("Select Credits")) {
                    JOptionPane.showMessageDialog(this, "Please select credits.");
                    return;
                }

                int credits = Integer.parseInt(creditsStr);

                Course c = new Course();
                c.setCode(code);
                c.setTitle(ctitle);
                c.setCredits(credits);

                if (adminService.addCourse(c)) {
                    JOptionPane.showMessageDialog(this, "Course Added Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Add Course due to Duplicate Code.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.refreshAdminDashboard());
    }


    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(hover);
            }

            public void mouseExited(MouseEvent evt) {
                btn.setBackground(normal);
            }
        });
    }


    private void setPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }


    private boolean isValidAlphaNumeric(String s) {
        return s.matches("^[a-zA-Z0-9 ]+$");
        // alphanumeric only
    }
}
