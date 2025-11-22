package edu.univ.erp.ui.admin;

import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.service.SectionService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.List;

public class AddSectionUI extends JPanel {

    private SectionService sectionService = new SectionService();
    private InstructorService instructorService = new InstructorService();
    private UserAuthDAO userAuthDAO = new UserAuthDAO();
    private InstructorDAO instructorDAO = new InstructorDAO();
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

    public AddSectionUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ---------- HEADER ----------
        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        JLabel title = new JLabel("Add Section");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // ---------- MAIN WRAPPER ----------
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(8, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        // ---------- COURSE DROPDOWN ----------
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(labelFont);

        JComboBox<Course> courseBox = new JComboBox<>();
        courseBox.setFont(inputFont);
        courseBox.addItem(null); // placeholder

        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) {
            courseBox.addItem(c);
        }

        // Renderer to show "CODE - TITLE"
        courseBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    Course c = (Course) value;
                    setText(c.getCode().toUpperCase() + " - " + c.getTitle());
                } else if (value == null) {
                    setText("Select Course...");
                }
                return this;
            }
        });

        // ---------- INSTRUCTOR DROPDOWN ----------
        JLabel instrLabel = new JLabel("Instructor Username :");
        instrLabel.setFont(labelFont);

        List<Instructor> instructors = instructorService.getAllInstructors();
        String[] instrUsernames = instructors.stream()
                .map(i -> instructorDAO.getUsernameById(i.getUserId()).toUpperCase())
                .toArray(String[]::new);

        JComboBox<String> instrBox = new JComboBox<>();
        instrBox.addItem("Select Instructor"); // placeholder
        for (String u : instrUsernames) instrBox.addItem(u);
        instrBox.setFont(inputFont);
        instrBox.setSelectedIndex(0);

        // ---------- OTHER FIELDS WITH PLACEHOLDERS ----------
        JLabel timeLabel = new JLabel("Day & Time:");
        timeLabel.setFont(labelFont);
        JTextField timeField = new JTextField();
        timeField.setFont(inputFont);
        setPlaceholder(timeField, "e.g., Mon 10:00-12:00");

        JLabel roomLabel = new JLabel("Room:");
        roomLabel.setFont(labelFont);
        JTextField roomField = new JTextField();
        roomField.setFont(inputFont);
        setPlaceholder(roomField, "e.g., R101");

        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setFont(labelFont);
        JTextField capacityField = new JTextField();
        capacityField.setFont(inputFont);
        setPlaceholder(capacityField, "e.g., 50");

        // ---------- SEMESTER DROPDOWN ----------
        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setFont(labelFont);
        JComboBox<String> semesterBox = new JComboBox<>();
        semesterBox.addItem("Select Semester"); // placeholder
        for (int i = 1; i <= 8; i++) semesterBox.addItem(String.valueOf(i));
        semesterBox.setFont(inputFont);
        semesterBox.setSelectedIndex(0);

        // ---------- YEAR DROPDOWN ----------
        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(labelFont);
        JComboBox<String> yearBox = new JComboBox<>();
        yearBox.addItem("Select Year"); // placeholder
        for (int y = 2020; y <= 2030; y++) yearBox.addItem(String.valueOf(y));
        yearBox.setFont(inputFont);
        yearBox.setSelectedIndex(0);

        // ---------- REGISTRATION DEADLINE ----------
        JLabel deadlineLabel = new JLabel("Registration Deadline (YYYY-MM-DD):");
        deadlineLabel.setFont(labelFont);
        JTextField deadlineField = new JTextField();
        deadlineField.setFont(inputFont);
        setPlaceholder(deadlineField, "YYYY-MM-DD");

        // ---------- ADD COMPONENTS TO FORM ----------
        form.add(courseLabel); form.add(courseBox);
        form.add(instrLabel); form.add(instrBox);
        form.add(timeLabel); form.add(timeField);
        form.add(roomLabel); form.add(roomField);
        form.add(capacityLabel); form.add(capacityField);
        form.add(semesterLabel); form.add(semesterBox);
        form.add(yearLabel); form.add(yearBox);
        form.add(deadlineLabel); form.add(deadlineField);

        // ---------- BUTTONS ----------
        JButton addBtn = new JButton("Add Section");
        JButton back = new JButton("Back");

        styleButton(addBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(back, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(245, 245, 245));
        buttons.add(addBtn);
        buttons.add(back);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        // ---------- ACTION LISTENERS ----------
        addBtn.addActionListener(e -> {
            try {
                Course selectedCourse = (Course) courseBox.getSelectedItem();
                String instrUsername = (String) instrBox.getSelectedItem();
                String dayTime = timeField.getText().trim();
                String room = roomField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());

                // Validate dropdowns
                if(selectedCourse == null) {
                    JOptionPane.showMessageDialog(this, "Please select a course.");
                    return;
                }
                if(semesterBox.getSelectedItem().equals("Select Semester")) {
                    JOptionPane.showMessageDialog(this, "Please select a semester.");
                    return;
                }
                if(yearBox.getSelectedItem().equals("Select Year")) {
                    JOptionPane.showMessageDialog(this, "Please select a year.");
                    return;
                }

                int semester = Integer.parseInt((String) semesterBox.getSelectedItem());
                int year = Integer.parseInt((String) yearBox.getSelectedItem());
                Date deadline = Date.valueOf(deadlineField.getText().trim());

                Integer instructorId = null;
                if(instrUsername != null && !instrUsername.equals("Select Instructor")) {
                    instructorId = userAuthDAO.getUserByUsername(instrUsername).getUserId();
                    if(instructorId == null) {
                        JOptionPane.showMessageDialog(this, "Instructor not found!");
                        return;
                    }
                }

                Section s = new Section(0, 0, instructorId, dayTime, room, capacity,
                        String.valueOf(semester), year, deadline);
                boolean success = sectionService.addSection(s, selectedCourse.getCode());

                if(success) {
                    JOptionPane.showMessageDialog(this, "Section Added Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Add Section.");
                }

            } catch(Exception ex) {
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

    // ---------- Helper method for placeholder ----------
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
