package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DeleteCourseUI extends JPanel {

    private AdminService adminService = new AdminService();
    private CourseService courseService = new CourseService();


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

    public DeleteCourseUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Soft background


        JPanel header = new JPanel();
        header.setBackground(new Color(231, 76, 60)); // Red header for delete
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        JLabel title = new JLabel("Delete Course");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);
        add(header, BorderLayout.NORTH);


        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));


        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(1, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);


        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(labelFont);

        JComboBox<Course> courseBox = new JComboBox<>();
        courseBox.setFont(inputFont);


        Runnable refreshCourseDropdown = () -> {
            courseBox.removeAllItems();
            courseBox.addItem(null);
            List<Course> courses = courseService.getAllCourses();
            for (Course c : courses) {
                courseBox.addItem(c);
            }
        };


        refreshCourseDropdown.run();


        courseBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
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

        form.add(courseLabel);
        form.add(courseBox);


        JButton deleteBtn = new JButton("Delete");
        JButton back = new JButton("Back");

        styleButton(deleteBtn, new Color(231, 76, 60), new Color(192, 57, 43)); // Red
        styleButton(back, new Color(52, 152, 219), new Color(41, 128, 185)); // Blue

        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(245, 245, 245));
        buttons.add(deleteBtn);
        buttons.add(back);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);


        deleteBtn.addActionListener(e -> {
            try {
                Course selectedCourse = (Course) courseBox.getSelectedItem();


                if (selectedCourse == null) {
                    JOptionPane.showMessageDialog(this, "Please select a course to delete!");
                    return;
                }

                if (adminService.deleteCourse(selectedCourse.getCode())) {
                    JOptionPane.showMessageDialog(this, "Course Deleted Successfully!");
                    refreshCourseDropdown.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Delete!");
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
}
