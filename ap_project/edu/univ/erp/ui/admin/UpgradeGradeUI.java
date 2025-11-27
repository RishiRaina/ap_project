package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.*;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.ui.instructor.GradeEntryDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeGradeUI extends JPanel {

    private CourseService courseService = new CourseService();
    private SectionService sectionService = new SectionService();
    private EnrollmentService enrollmentService = new EnrollmentService();
    private UserAuthService userAuthService = new UserAuthService();

    private JComboBox<CourseItem> courseCombo;
    private JComboBox<SectionItem> sectionCombo;
    private JComboBox<StudentItem> studentCombo;

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

    public UpgradeGradeUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));


        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        JLabel title = new JLabel("Upgrade Grades");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);

        add(header, BorderLayout.NORTH);


        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(4, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);


        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(labelFont);
        courseCombo = new JComboBox<>();
        courseCombo.setFont(inputFont);


        JLabel sectionLabel = new JLabel("Select Section:");
        sectionLabel.setFont(labelFont);
        sectionCombo = new JComboBox<>();
        sectionCombo.setFont(inputFont);


        JLabel studentLabel = new JLabel("Select Student:");
        studentLabel.setFont(labelFont);
        studentCombo = new JComboBox<>();
        studentCombo.setFont(inputFont);


        form.add(courseLabel); form.add(courseCombo);
        form.add(sectionLabel); form.add(sectionCombo);
        form.add(studentLabel); form.add(studentCombo);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);


        JButton openBtn = new JButton("Open Grade Entry Window");
        styleButton(openBtn, new Color(46, 204, 113), new Color(39, 174, 96));

        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(openBtn);
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);


        backBtn.addActionListener(e -> mainFrame.refreshAdminDashboard());

        loadCourses();

        courseCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                CourseItem item = (CourseItem) courseCombo.getSelectedItem();
                if (item != null) loadSections(item.courseId);
            }
        });

        sectionCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                SectionItem item = (SectionItem) sectionCombo.getSelectedItem();
                if (item != null) loadStudents(item.sectionId);
            }
        });

        openBtn.addActionListener(e -> openGradeEntry());
    }



    private void loadCourses() {
        courseCombo.removeAllItems();
        courseCombo.addItem(new CourseItem(-1, "Select Course..."));

        List<Course> list = courseService.getAllCourses();
        for (Course c : list) {
            courseCombo.addItem(new CourseItem(c.getCourseId(), c.getCode() + " - " + c.getTitle()));
        }

        courseCombo.setRenderer(getNormalRenderer());
    }

    private void loadSections(int courseId) {
        sectionCombo.removeAllItems();
        sectionCombo.addItem(new SectionItem(-1, "Select Section..."));

        List<Section> sections = sectionService.getSectionsByCourse(courseId);
        for (Section sec : sections) {
            sectionCombo.addItem(new SectionItem(sec.getSectionId(), sec.toString().toUpperCase()));
        }

        sectionCombo.setRenderer(getNormalRenderer());
        studentCombo.removeAllItems();
        studentCombo.addItem(new StudentItem(-1, "Select Student..."));
    }

    private void loadStudents(int sectionId) {
        studentCombo.removeAllItems();
        studentCombo.addItem(new StudentItem(-1, "Select Student..."));

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySection(sectionId);

        for (Enrollment en : enrollments) {
            String username = userAuthService.getUsernameById(en.getStudentId());
            if (username == null) username = "UNKNOWN";
            studentCombo.addItem(new StudentItem(en.getEnrollmentId(), username.toUpperCase()));
        }

        studentCombo.setRenderer(getNormalRenderer());
    }

    private DefaultListCellRenderer getNormalRenderer() {
        return new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(new Font("Segoe UI", Font.PLAIN, 15));
                return this;
            }
        };
    }



    private void openGradeEntry() {
        StudentItem st = (StudentItem) studentCombo.getSelectedItem();

        if (st == null || st.enrollmentId == -1) {
            JOptionPane.showMessageDialog(this, "Please select all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        GradeEntryDialog dlg = new GradeEntryDialog(
                SwingUtilities.getWindowAncestor(this),
                st.enrollmentId,
                true
        );
        dlg.setVisible(true);
    }



    class CourseItem {
        int courseId;
        String name;
        CourseItem(int id, String name) { this.courseId = id; this.name = name; }
        public String toString() { return name; }
    }

    class SectionItem {
        int sectionId;
        String name;
        SectionItem(int id, String name) { this.sectionId = id; this.name = name; }
        public String toString() { return name; }
    }

    class StudentItem {
        int enrollmentId;
        String name;
        StudentItem(int id, String name) { this.enrollmentId = id; this.name = name; }
        public String toString() { return name; }
    }



    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(normal); }
        });
    }
}
