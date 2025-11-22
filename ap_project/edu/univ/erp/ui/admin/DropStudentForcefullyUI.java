package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.SectionService;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.data.*

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UnenrollStudentUI extends JPanel {

    private AdminService adminService = new AdminService();
    private CourseService courseService = new CourseService();
    private SectionService sectionService = new SectionService();
    private EnrollmentService enrollmentService = new EnrollmentService();
    private SectionDAO SectionDAO = new SectionDAO();

    public UnenrollStudentUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ---------- HEADER ----------
        JLabel title = new JLabel("Unenroll Student From Section", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ---------- FORM PANEL ----------
        JPanel form = new JPanel(new GridLayout(3, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        // --- Course Dropdown ---
        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(labelFont);
        JComboBox<Course> courseDropdown = new JComboBox<>();
        courseDropdown.setFont(inputFont);
        courseDropdown.addItem(null); // placeholder
        for (Course c : courseService.getAllCourses()) {
            courseDropdown.addItem(c);
        }
        courseDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    setText(((Course) value).getCode().toUpperCase());
                } else if (value == null) {
                    setText("SELECT COURSE...");
                }
                return this;
            }
        });

        // --- Section Dropdown ---
        JLabel sectionLabel = new JLabel("Select Section:");
        sectionLabel.setFont(labelFont);
        JComboBox<Section> sectionDropdown = new JComboBox<>();
        sectionDropdown.setFont(inputFont);
        sectionDropdown.addItem(null);

        courseDropdown.addActionListener(e -> {
            sectionDropdown.removeAllItems();
            sectionDropdown.addItem(null); // placeholder
            Course selectedCourse = (Course) courseDropdown.getSelectedItem();
            if (selectedCourse != null) {
                List<Section> sections = SectionDAO.getSectionsByCourse(selectedCourse.getCourseId());
                for (Section s : sections) {
                    sectionDropdown.addItem(s);
                }
            }
        });

        sectionDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Section) {
                    setText("SECTION " + ((Section) value).getSectionId());
                } else if (value == null) {
                    setText("SELECT SECTION...");
                }
                return this;
            }
        });

        // --- Enrollment Dropdown (Students) ---
        JLabel studentLabel = new JLabel("Select Student:");
        studentLabel.setFont(labelFont);
        JComboBox<Enrollment> studentDropdown = new JComboBox<>();
        studentDropdown.setFont(inputFont);
        studentDropdown.addItem(null);

        sectionDropdown.addActionListener(e -> {
            studentDropdown.removeAllItems();
            studentDropdown.addItem(null); // placeholder
            Section selectedSection = (Section) sectionDropdown.getSelectedItem();
            if (selectedSection != null) {
                List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySection(selectedSection.getSectionId());
                for (Enrollment e1 : enrollments) {
                    studentDropdown.addItem(e1);
                }
            }
        });

        studentDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Enrollment) {
                    setText(((Enrollment) value).getStudentUsername().toUpperCase());
                } else if (value == null) {
                    setText("SELECT STUDENT...");
                }
                return this;
            }
        });

        // --- Add components to form ---
        form.add(courseLabel);
        form.add(courseDropdown);
        form.add(sectionLabel);
        form.add(sectionDropdown);
        form.add(studentLabel);
        form.add(studentDropdown);

        // ---------- BUTTONS ----------
        JButton unenrollBtn = new JButton("Unenroll");
        JButton backBtn = new JButton("Back");

        styleButton(unenrollBtn, new Color(231, 76, 60), new Color(192, 57, 43)); // Red
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185)); // Blue

        JPanel btnPanel = new JPanel();
        btnPanel.add(unenrollBtn);
        btnPanel.add(backBtn);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // ---------- ACTIONS ----------
        unenrollBtn.addActionListener(e -> {
            Enrollment selectedEnrollment = (Enrollment) studentDropdown.getSelectedItem();
            if (selectedEnrollment == null) {
                JOptionPane.showMessageDialog(this, "No student selected.");
                return;
            }

            boolean success = adminService.unenrollStudent(
                    selectedEnrollment.getSectionId(),
                    selectedEnrollment.getStudentId()
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Student unenrolled successfully!");
                studentDropdown.removeItem(selectedEnrollment);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to unenroll student.");
            }
        });

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }

    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
