package edu.univ.erp.ui.admin;

import edu.univ.erp.service.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnenrollStudentUI extends JPanel {

    private AdminService adminService = new AdminService();
    private CourseService courseService = new CourseService();
    private SectionService sectionService = new SectionService();
    private EnrollmentService enrollmentService = new EnrollmentService();
    private UserAuthService userAuthService = new UserAuthService(); // fetch usernames

    public UnenrollStudentUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ---------- HEADER ----------
        JLabel title = new JLabel("Unenroll Student From Section", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // ---------- CARD PANEL ----------
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        add(card, BorderLayout.CENTER);

        // ---------- FORM PANEL ----------
        JPanel form = new JPanel(new GridLayout(3, 2, 20, 20));
        form.setBackground(Color.WHITE);

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
                    Course c = (Course) value;
                    setText(c.getCode().toUpperCase() + " - " + c.getTitle());
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
            sectionDropdown.addItem(null);
            Map<Integer, String> enrollmentUsernameMap = new HashMap<>();
            Course selectedCourse = (Course) courseDropdown.getSelectedItem();
            if (selectedCourse != null) {
                List<Section> sections = sectionService.getSectionsByCourse(selectedCourse.getCourseId());
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

        Map<Integer, String> enrollmentUsernameMap = new HashMap<>(); // enrollmentId -> username

        sectionDropdown.addActionListener(e -> {
            studentDropdown.removeAllItems();
            studentDropdown.addItem(null);
            enrollmentUsernameMap.clear();

            Section selectedSection = (Section) sectionDropdown.getSelectedItem();
            if (selectedSection != null) {
                List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySection(selectedSection.getSectionId());
                for (Enrollment en : enrollments) {
                    String username = userAuthService.getUsernameById(en.getStudentId());
                    enrollmentUsernameMap.put(en.getEnrollmentId(), username.toUpperCase());
                    studentDropdown.addItem(en);
                }
            }
        });

        studentDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Enrollment) {
                    Enrollment e = (Enrollment) value;
                    setText(enrollmentUsernameMap.get(e.getEnrollmentId()));
                } else if (value == null) {
                    setText("SELECT STUDENT...");
                }
                return this;
            }
        });

        // Add form components
        form.add(courseLabel);
        form.add(courseDropdown);
        form.add(sectionLabel);
        form.add(sectionDropdown);
        form.add(studentLabel);
        form.add(studentDropdown);

        card.add(form, BorderLayout.CENTER);

        // ---------- BUTTON PANEL ----------
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton unenrollBtn = new JButton("Unenroll");
        JButton backBtn = new JButton("Back");

        styleButton(unenrollBtn, new Color(231, 76, 60), new Color(192, 57, 43));
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        btnPanel.add(unenrollBtn);
        btnPanel.add(backBtn);

        card.add(btnPanel, BorderLayout.SOUTH);

        // ---------- BUTTON ACTIONS ----------
        unenrollBtn.addActionListener(e -> {
            Enrollment selectedEnrollment = (Enrollment) studentDropdown.getSelectedItem();
            if (selectedEnrollment == null) {
                JOptionPane.showMessageDialog(this, "No student selected.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = adminService.unenrollStudent(
                    selectedEnrollment.getSectionId(),
                    selectedEnrollment.getStudentId()
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Student unenrolled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                studentDropdown.removeItem(selectedEnrollment);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to unenroll student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
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
