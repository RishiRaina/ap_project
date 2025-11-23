package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class UpdateCourseUI extends JPanel {

    private AdminService adminService = new AdminService();
    private CourseService courseService = new CourseService();

    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;
        public RoundedPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    public UpdateCourseUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        JLabel title = new JLabel("Update Course");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);

        add(header, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(3, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(labelFont);

        List<Course> courses = courseService.getAllCourses();

        JComboBox<Course> courseBox = new JComboBox<>();
        courseBox.setFont(inputFont);
        courseBox.addItem(null);
        for (Course c : courses) courseBox.addItem(c);

        courseBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    Course c = (Course) value;
                    setText(c.getCode().toUpperCase() + " - " + c.getTitle());
                } else if (value == null) {
                    setText("SELECT COURSE...");
                }
                return this;
            }
        });

        JLabel titleLabel = new JLabel("Updated Title:");
        titleLabel.setFont(labelFont);
        JTextField titleField = new JTextField();
        titleField.setFont(inputFont);
        setPlaceholder(titleField, "Enter new course title");

        JLabel creditsLabel = new JLabel("Updated Credits:");
        creditsLabel.setFont(labelFont);

        JComboBox<String> creditsBox = new JComboBox<>();
        creditsBox.setFont(inputFont);
        creditsBox.addItem("Select Credits");
        creditsBox.addItem("1");
        creditsBox.addItem("2");
        creditsBox.addItem("4");
        creditsBox.setSelectedIndex(0);

        form.add(courseLabel);
        form.add(courseBox);
        form.add(titleLabel);
        form.add(titleField);
        form.add(creditsLabel);
        form.add(creditsBox);

        JButton submit = new JButton("Update");
        JButton back = new JButton("Back");

        styleButton(submit, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(back, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(245, 245, 245));
        buttons.add(submit);
        buttons.add(back);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        submit.addActionListener(e -> {
            try {
                Course selectedCourse = (Course) courseBox.getSelectedItem();
                String newTitle = titleField.getText().trim();
                String creditsStr = (String) creditsBox.getSelectedItem();

                if (selectedCourse == null) {
                    JOptionPane.showMessageDialog(this, "Please select a course.");
                    return;
                }
                if (newTitle.isEmpty() || newTitle.equalsIgnoreCase("Enter new course title")) {
                    JOptionPane.showMessageDialog(this, "Course title cannot be empty.");
                    return;
                }
                if (!newTitle.matches("[A-Za-z0-9 ]+")) {
                    JOptionPane.showMessageDialog(this, "Title must be Alphanumeric only.");
                    return;
                }
                if (creditsStr.equals("Select Credits")) {
                    JOptionPane.showMessageDialog(this, "Please select credits.");
                    return;
                }

                int credits = Integer.parseInt(creditsStr);

                selectedCourse.setTitle(newTitle);
                selectedCourse.setCredits(credits);

                if (adminService.updateCourse(selectedCourse)) {
                    JOptionPane.showMessageDialog(this, "Course Updated!");
                } else {
                    JOptionPane.showMessageDialog(this, "Update Failed!");
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
            public void mouseEntered(MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent evt) { btn.setBackground(normal); }
        });
    }

    private void setPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
}
