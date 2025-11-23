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

        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        JLabel title = new JLabel("Add Section");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(8, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(labelFont);

        JComboBox<Course> courseBox = new JComboBox<>();
        courseBox.setFont(inputFont);
        courseBox.addItem(null);

        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) courseBox.addItem(c);

        courseBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) setText(((Course) value).getCode().toUpperCase() + " - " + ((Course) value).getTitle());
                else if (value == null) setText("Select Course...");
                return this;
            }
        });

        JLabel instrLabel = new JLabel("Instructor Username:");
        instrLabel.setFont(labelFont);

        List<Instructor> instructors = instructorService.getAllInstructors();
        String[] instrUsernames = instructors.stream()
                .map(i -> instructorDAO.getUsernameById(i.getUserId()).toUpperCase())
                .toArray(String[]::new);

        JComboBox<String> instrBox = new JComboBox<>();
        instrBox.addItem("Select Instructor");
        for (String u : instrUsernames) instrBox.addItem(u);
        instrBox.setFont(inputFont);

        JLabel timeLabel = new JLabel("Day & Time:");
        timeLabel.setFont(labelFont);
        JTextField timeField = new JTextField();
        timeField.setFont(inputFont);

        JLabel roomLabel = new JLabel("Room:");
        roomLabel.setFont(labelFont);
        JTextField roomField = new JTextField();
        roomField.setFont(inputFont);

        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setFont(labelFont);
        JTextField capacityField = new JTextField();
        capacityField.setFont(inputFont);

        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setFont(labelFont);
        JComboBox<String> semesterBox = new JComboBox<>();
        semesterBox.addItem("Select Semester");
        for (int i = 1; i <= 8; i++) semesterBox.addItem(String.valueOf(i));
        semesterBox.setFont(inputFont);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setFont(labelFont);
        JComboBox<String> yearBox = new JComboBox<>();
        yearBox.addItem("Select Year");
        for (int y = 2020; y <= 2030; y++) yearBox.addItem(String.valueOf(y));
        yearBox.setFont(inputFont);

        JLabel deadlineLabel = new JLabel("Registration Deadline (YYYY-MM-DD):");
        deadlineLabel.setFont(labelFont);
        JTextField deadlineField = new JTextField();
        deadlineField.setFont(inputFont);

        form.add(courseLabel); form.add(courseBox);
        form.add(instrLabel); form.add(instrBox);
        form.add(timeLabel); form.add(timeField);
        form.add(roomLabel); form.add(roomField);
        form.add(capacityLabel); form.add(capacityField);
        form.add(semesterLabel); form.add(semesterBox);
        form.add(yearLabel); form.add(yearBox);
        form.add(deadlineLabel); form.add(deadlineField);

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

        addBtn.addActionListener(e -> {
            try {
                Course selectedCourse = (Course) courseBox.getSelectedItem();
                String instrUsername = (String) instrBox.getSelectedItem();
                String dayTime = timeField.getText().trim();
                String room = roomField.getText().trim();
                String capStr = capacityField.getText().trim();
                String deadlineStr = deadlineField.getText().trim();

                if (selectedCourse == null) {
                    JOptionPane.showMessageDialog(this, "Select a course.");
                    return;
                }
                if (instrUsername == null || instrUsername.equals("Select Instructor")) {
                    JOptionPane.showMessageDialog(this, "Select an instructor.");
                    return;
                }
                if (dayTime.isEmpty() || !dayTime.matches("[A-Za-z0-9 :\\-/]+")) {
                    JOptionPane.showMessageDialog(this, "Invalid day & time. Use letters, numbers, spaces, :, -, /");
                    return;
                }
                if (room.isEmpty() || !room.matches("[A-Za-z0-9\\s!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/|`~]+")) {
                    JOptionPane.showMessageDialog(this, "Invalid room. Only letters, numbers, spaces.");
                    return;
                }
                if (!capStr.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "Capacity must be a positive number.");
                    return;
                }
                int capacity = Integer.parseInt(capStr);
                if (capacity <= 0 || capacity >= 1000) {
                    JOptionPane.showMessageDialog(this, "Capacity must be between 1 and 999.");
                    return;
                }
                if (semesterBox.getSelectedItem().equals("Select Semester")) {
                    JOptionPane.showMessageDialog(this, "Select a semester.");
                    return;
                }
                if (yearBox.getSelectedItem().equals("Select Year")) {
                    JOptionPane.showMessageDialog(this, "Select a year.");
                    return;
                }

                // Remove ALL spaces from the input
                String cleaned = deadlineStr.replaceAll("\\s+", "");

                if (!cleaned.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    JOptionPane.showMessageDialog(this, "Invalid date. Use format YYYY-MM-DD.");
                    return;
                }

                Date deadline;
                try {
                    deadline = Date.valueOf(cleaned);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date. Please enter a valid YYYY-MM-DD date.");
                    return;
                }


                int semester = Integer.parseInt((String) semesterBox.getSelectedItem());
                int year = Integer.parseInt((String) yearBox.getSelectedItem());

                Integer instructorId = userAuthDAO.getUserByUsername(instrUsername).getUserId();

                Section s = new Section(0, 0, instructorId, dayTime, room, capacity,
                        String.valueOf(semester), year, deadline);

                boolean success = sectionService.addSection(s, selectedCourse.getCode());

                JOptionPane.showMessageDialog(this, success ? "Section Added Successfully!" : "Failed to Add Section.");

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
}
