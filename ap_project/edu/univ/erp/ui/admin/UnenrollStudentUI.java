package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.EnrollmentService;
import edu.univ.erp.service.SectionService;
import edu.univ.erp.service.UserAuthService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnenrollStudentUI extends JPanel {

    private AdminService adminService = new AdminService();
    private CourseService courseService = new CourseService();
    private SectionService sectionService = new SectionService();
    private EnrollmentService enrollmentService = new EnrollmentService();
    private UserAuthService userAuthService = new UserAuthService();


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

    public UnenrollStudentUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));


        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        JLabel title = new JLabel("Unenroll Student From Section");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);
        add(header, BorderLayout.NORTH);


        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(3, 2, 25, 25)); // ONLY real fields
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);


        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(labelFont);

        JComboBox<Course> courseBox = new JComboBox<>();
        courseBox.setFont(inputFont);
        courseBox.addItem(null);
        for (Course c : courseService.getAllCourses()) courseBox.addItem(c);

        courseBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) setText(((Course) value).getCode().toUpperCase() + " - " + ((Course) value).getTitle());
                else if (value == null) setText("Select Course...");
                setFont(inputFont);
                return this;
            }
        });


        JLabel sectionLabel = new JLabel("Select Section:");
        sectionLabel.setFont(labelFont);

        JComboBox<Section> sectionBox = new JComboBox<>();
        sectionBox.setFont(inputFont);
        sectionBox.addItem(null);

        courseBox.addActionListener(e -> {
            sectionBox.removeAllItems();
            sectionBox.addItem(null);
            Course selected = (Course) courseBox.getSelectedItem();
            if (selected != null) {
                List<Section> sections = sectionService.getSectionsByCourse(selected.getCourseId());
                for (Section s : sections) sectionBox.addItem(s);
            }
        });

        sectionBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Section) setText(value.toString().toUpperCase());
                else if (value == null) setText("Select Section...");
                setFont(inputFont);
                return this;
            }
        });


        JLabel studentLabel = new JLabel("Select Student:");
        studentLabel.setFont(labelFont);

        JComboBox<Enrollment> studentBox = new JComboBox<>();
        studentBox.setFont(inputFont);
        studentBox.addItem(null);

        Map<Integer, String> studentMap = new HashMap<>();

        sectionBox.addActionListener(e -> {
            studentBox.removeAllItems();
            studentBox.addItem(null);
            studentMap.clear();
            Section sec = (Section) sectionBox.getSelectedItem();
            if (sec != null) {
                List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySection(sec.getSectionId());
                for (Enrollment en : enrollments) {
                    String username = userAuthService.getUsernameById(en.getStudentId());
                    studentMap.put(en.getEnrollmentId(), username.toUpperCase());
                    studentBox.addItem(en);
                }
            }
        });

        studentBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Enrollment) setText(studentMap.get(((Enrollment) value).getEnrollmentId()));
                else if (value == null) setText("Select Student...");
                setFont(inputFont);
                return this;
            }
        });


        form.add(courseLabel); form.add(courseBox);
        form.add(sectionLabel); form.add(sectionBox);
        form.add(studentLabel); form.add(studentBox);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);


        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 245, 245));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        JButton unenrollBtn = new JButton("Unenroll");
        JButton backBtn = new JButton("Back");

        styleButton(unenrollBtn, new Color(231, 76, 60), new Color(192, 57, 43));
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        btnPanel.add(unenrollBtn);
        btnPanel.add(backBtn);

        add(btnPanel, BorderLayout.SOUTH);


        unenrollBtn.addActionListener(e -> {
            Enrollment en = (Enrollment) studentBox.getSelectedItem();
            if (en == null) {
                JOptionPane.showMessageDialog(this, "Select a student.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = adminService.unenrollStudent(en.getSectionId(), en.getStudentId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Student unenrolled successfully.");
                studentBox.removeItem(en);
            } else {
                JOptionPane.showMessageDialog(this, "Error while unenrolling.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> mainFrame.refreshAdminDashboard());
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
