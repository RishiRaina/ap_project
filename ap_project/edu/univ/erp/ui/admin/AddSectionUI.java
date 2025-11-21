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
        JLabel courseLabel = new JLabel("Course Code:");
        courseLabel.setFont(labelFont);

        List<Course> courses = courseService.getAllCourses();
        String[] courseCodes = courses.stream().map(c -> c.getCode().toUpperCase()).toArray(String[]::new);

        JComboBox<String> courseBox = new JComboBox<>();
        courseBox.addItem("Select Course"); // placeholder
        for (String c : courseCodes) courseBox.addItem(c);
        courseBox.setFont(inputFont);
        courseBox.setSelectedIndex(0);

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

        // ---------- OTHER FIELDS ----------
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
                String courseCode = (String) courseBox.getSelectedItem();
                String instrUsername = (String) instrBox.getSelectedItem();
                String dayTime = timeField.getText().trim();
                String room = roomField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());

                // Validate dropdowns
                if(courseCode.equals("Select Course")) {
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
                boolean success = sectionService.addSection(s, courseCode);

                if(success) {
                    JOptionPane.showMessageDialog(this, "Section Added Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Add Section.");
                }

            } catch(Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
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
