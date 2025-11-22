package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.*;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class EnrollStudentUI extends JPanel {

    private AdminService adminService = new AdminService();
    private CourseService courseService = new CourseService();
    private SectionService sectionService = new SectionService();
    private EnrollmentService enrollmentService = new EnrollmentService();


    private JComboBox<CourseItem> courseCombo;
    private JComboBox<SectionItem> sectionCombo;
    private JComboBox<StudentItem> studentCombo;

    public EnrollStudentUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Enroll Student", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(41, 128, 185));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));
        formPanel.setBackground(Color.WHITE);

        // ---- Course Dropdown ----
        formPanel.add(createLabel("Select Course:"));
        courseCombo = new JComboBox<>();
        formPanel.add(courseCombo);

        // ---- Section Dropdown ----
        formPanel.add(createLabel("Select Section:"));
        sectionCombo = new JComboBox<>();
        formPanel.add(sectionCombo);

        // ---- Student Dropdown ----
        formPanel.add(createLabel("Select Student:"));
        studentCombo = new JComboBox<>();
        formPanel.add(studentCombo);

        add(formPanel, BorderLayout.CENTER);

        // ---- Buttons ----
        JButton enrollBtn = new JButton("Enroll");
        styleButton(enrollBtn, new Color(39, 174, 96), new Color(33, 140, 72));

        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.add(enrollBtn);
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.refreshAdminDashboard());

        // Load data
        loadCourses();
        loadStudents();   // load once → usernames display from user_auth

        // ---- When course is selected load only linked sections ----
        courseCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                CourseItem selected = (CourseItem) courseCombo.getSelectedItem();
                if (selected != null) {
                    loadSections(selected.courseId);
                }
            }
        });

        // ---- Enroll Button ----
        enrollBtn.addActionListener(e -> enrollStudentAction());
    }


    // ----------------------------- DATA LOADING -----------------------------

    private void loadCourses() {
        courseCombo.removeAllItems();

        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) {
            courseCombo.addItem(new CourseItem(c.getCourseId(), c.getTitle()));
        }
    }

    private void loadSections(int courseId) {
        sectionCombo.removeAllItems();

        List<Section> sections = sectionService.getSectionsByCourse(courseId);

        for (Section sec : sections) {
            sectionCombo.addItem(new SectionItem(
                    sec.getSectionId(),
                    sec.toString()
            ));
        }
    }

    private void loadStudents() {
        studentCombo.removeAllItems();

        List<edu.univ.erp.domain.Student> students = adminService.getAllStudents();

        for (edu.univ.erp.domain.Student s : students) {

            String username = adminService.getUsernameById(s.getUserId());
            if (username == null) username = "UNKNOWN";

            // Show username but keep ID stored
            studentCombo.addItem(new StudentItem(s.getUserId(), username));
        }
    }


    // ----------------------------- SUBMIT ACTION -----------------------------

    private void enrollStudentAction() {

        CourseItem course = (CourseItem) courseCombo.getSelectedItem();
        SectionItem section = (SectionItem) sectionCombo.getSelectedItem();
        StudentItem student = (StudentItem) studentCombo.getSelectedItem();

        if (course == null || section == null || student == null) {
            JOptionPane.showMessageDialog(this, "Please select all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = enrollmentService.enrollStudentInSection(
                student.userId,
                section.sectionId
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "Student enrolled successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Enrollment failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // ----------------------------- HELPER CLASSES -----------------------------

    class CourseItem {
        int courseId;
        String courseName;

        CourseItem(int id, String name) {
            this.courseId = id;
            this.courseName = name;
        }

        @Override
        public String toString() {
            return courseName;
        }
    }

    class SectionItem {
        int sectionId;
        String sectionName;

        SectionItem(int id, String name) {
            this.sectionId = id;
            this.sectionName = name;
        }

        @Override
        public String toString() {
            return sectionName;
        }
    }

    class StudentItem {
        int userId;
        String username;

        StudentItem(int id, String name) {
            this.userId = id;
            this.username = name;
        }

        @Override
        public String toString() {
            return username; // shown in dropdown
        }
    }


    // ----------------------------- UI HELPERS -----------------------------

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(new Color(44, 62, 80));
        return lbl;
    }

    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(normal);
            }
        });
    }
}
