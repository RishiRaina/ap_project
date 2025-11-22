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
        loadStudents();

        // When course is selected → load sections
        courseCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                CourseItem selected = (CourseItem) courseCombo.getSelectedItem();
                if (selected != null) {
                    loadSections(selected.courseId);
                }
            }
        });

        enrollBtn.addActionListener(e -> enrollStudentAction());
    }

    // ----------------------------- DATA LOADING -----------------------------

    private void loadCourses() {
        courseCombo.removeAllItems();

        // placeholder (NORMAL FONT)
        courseCombo.addItem(new CourseItem(-1, "Select Course..."));

        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) {
            courseCombo.addItem(new CourseItem(c.getCourseId(), c.getTitle()));
        }

        // renderer (normal font always)
        courseCombo.setRenderer(getNormalRenderer());
    }

    private void loadSections(int courseId) {
        sectionCombo.removeAllItems();

        // placeholder
        sectionCombo.addItem(new SectionItem(-1, "Select Section..."));

        List<Section> sections = sectionService.getSectionsByCourse(courseId);

        for (Section sec : sections) {
            String display = sec.toString().toUpperCase(); // UPPERCASE DISPLAY
            sectionCombo.addItem(new SectionItem(sec.getSectionId(), display));
        }

        sectionCombo.setRenderer(getNormalRenderer());
    }

    private void loadStudents() {
        studentCombo.removeAllItems();

        studentCombo.addItem(new StudentItem(-1, "Select Student..."));

        List<edu.univ.erp.domain.Student> students = adminService.getAllStudents();

        for (edu.univ.erp.domain.Student s : students) {
            String username = adminService.getUsernameById(s.getUserId());
            if (username == null) username = "UNKNOWN";

            studentCombo.addItem(new StudentItem(s.getUserId(), username));
        }

        studentCombo.setRenderer(getNormalRenderer());
    }

    // ----------------------------- RENDERER FIX -----------------------------

    /**
     * Renderer that forces ALL items including placeholders to normal/plain font.
     */
    private DefaultListCellRenderer getNormalRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                setFont(new Font("Segoe UI", Font.PLAIN, 15)); // NOT BOLD
                return this;
            }
        };
    }

    // ----------------------------- SUBMIT ACTION -----------------------------
    private void enrollStudentAction() {

        CourseItem course = (CourseItem) courseCombo.getSelectedItem();
        SectionItem section = (SectionItem) sectionCombo.getSelectedItem();
        StudentItem student = (StudentItem) studentCombo.getSelectedItem();

        if (course == null || section == null || student == null ||
                course.courseId == -1 || section.sectionId == -1 || student.userId == -1) {
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

    // ----------------------------- DROPDOWN ITEM CLASSES -----------------------------

    class CourseItem {
        int courseId;
        String name;

        CourseItem(int id, String name) {
            this.courseId = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    class SectionItem {
        int sectionId;
        String name;

        SectionItem(int id, String name) {
            this.sectionId = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
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
            return username;
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
