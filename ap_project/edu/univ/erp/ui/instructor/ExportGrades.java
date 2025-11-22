package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.util.CSVutil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExportGrades extends JPanel {

    private MainFrame mainFrame;
    private InstructorQueryService queryService = new InstructorQueryService();
    private CourseService courseService = new CourseService();

    // =========== Modern Rounded Panel ===========
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

    public ExportGrades(MainFrame mainFrame) {

        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ============= Role Check =============
        if (!SessionManager.isLoggedIn() ||
                !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Instructors only.");
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        // ============= Maintenance Banner (TOP) =============
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));

            bannerPanel.add(banner, BorderLayout.CENTER);
            add(bannerPanel, BorderLayout.NORTH);
        }

        // ============= Header ============
        JLabel title = new JLabel("Export Grades (CSV)", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.PAGE_START);

        // ============= Main Form Panel =============
        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(3, 1, 20, 20));
        form.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JLabel selLabel = new JLabel("Select Section:", SwingConstants.CENTER);
        selLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JComboBox<Section> sectionDropdown = new JComboBox<>();
        sectionDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sectionDropdown.addItem(null);
        loadSectionChoices(sectionDropdown);

        sectionDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Section) {
                    Section s = (Section) value;
                    Course c = courseService.getCourseById(s.getCourseId());
                    setText(c.getCode() + " – " + c.getTitle() + " — Section " + s.getSectionId());
                } else if (value == null) {
                    setText("Select Section...");
                }
                return this;
            }
        });

        // Buttons
        JButton exportBtn = new JButton("Download CSV");
        JButton backBtn = new JButton("Back");
        styleButton(exportBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel btnPanel = new JPanel();
        btnPanel.add(exportBtn);
        btnPanel.add(backBtn);

        form.add(selLabel);
        form.add(sectionDropdown);
        form.add(btnPanel);

        add(form, BorderLayout.CENTER);

        // ============= Button Actions =============
        backBtn.addActionListener(e -> mainFrame.refreshInstructorDashboard());

        exportBtn.addActionListener(e -> {
            Section sec = (Section) sectionDropdown.getSelectedItem();
            if (sec == null) {
                JOptionPane.showMessageDialog(this, "No section selected!");
                return;
            }
            try {
                exportGrades(sec.getSectionId());
            } catch (AccessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }

    private void loadSectionChoices(JComboBox<Section> box) {
        try {
            int instructorId = SessionManager.getCurrentUserId();
            List<Section> list = queryService.getMySections(instructorId);
            for (Section s : list) box.addItem(s);

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(normal); }
        });
    }

    private void exportGrades(int sectionId) throws AccessException {

        // ownership check
        Section sec = queryService.getSection(sectionId);
        AccessControl.assertInstructorOwnsSection(
                SessionManager.getCurrentUserId(),
                sec.getInstructorId(),
                AccessControl.Actions.EXPORT_GRADES_CSV
        );

        // get data
        List<Enrollment> enrollments = queryService.getSectionEnrollments(sectionId);
        List<String[]> rows = new ArrayList<>();


        String[] header = {"RollNo", "Assignments", "Quizzes", "Project", "Mid", "End", "FinalScore", "FinalLetter"};
        for (Enrollment e : enrollments) {
            Student st = queryService.getStudentForEnrollment(e.getEnrollmentId());
            String roll = (st != null ? st.getRollNo() : "N/A");

            Double A = null, Q = null, P = null, M = null, Efinal = null;
            Double finalScore = null;
            String finalLetter = null;

            List<Grade> grades = queryService.getGradesForEnrollment(e.getEnrollmentId());
            for (Grade g : grades) {
                if (g.getComponent() == null) continue;
                String comp = g.getComponent().toUpperCase();

                switch (comp) {
                    case "ASSIGNMENTS": A = g.getScore(); break;
                    case "QUIZZES":     Q = g.getScore(); break;
                    case "PROJECT":     P = g.getScore(); break;
                    case "MID":         M = g.getScore(); break;
                    case "END":         Efinal = g.getScore(); break;
                    case "FINAL":
                        finalScore = g.getScore();
                        finalLetter = g.getFinalGrade();
                        break;
                }
            }

            rows.add(new String[]{roll, fmt(A), fmt(Q), fmt(P), fmt(M), fmt(Efinal), fmt(finalScore), (finalLetter != null ? finalLetter : "")});
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("section_" + sectionId + "_grades.csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            boolean ok = CSVutil.writecsv(chooser.getSelectedFile(), rows, header);
            JOptionPane.showMessageDialog(this, ok ? "CSV Exported!" : "Failed to Export.");
        }
    }

    // helper formatter
    private String fmt(Double x) {
        return (x == null ? "" : String.valueOf(x));
    }

}
