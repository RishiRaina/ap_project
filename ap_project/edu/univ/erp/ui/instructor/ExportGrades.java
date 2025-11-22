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

    // Rounded panel
    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;

        public RoundedPanel() {
            setOpaque(false);
        }

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

        // Role check
        if (!SessionManager.isLoggedIn() || !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Instructors only.");
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        // Maintenance banner
        if (MaintenanceChecker.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));
            add(banner, BorderLayout.SOUTH);
        }

        // Header
        JLabel title = new JLabel("Export Grades (CSV)", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Form panel
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
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {

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
        styleButton(exportBtn, new Color(46, 204, 113), new Color(39, 174, 96));  // green
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185)); // blue

        JPanel btnPanel = new JPanel();
        btnPanel.add(exportBtn);
        btnPanel.add(backBtn);

        form.add(selLabel);
        form.add(sectionDropdown);
        form.add(btnPanel);

        add(form, BorderLayout.CENTER);

        // Back action
        backBtn.addActionListener(e -> mainFrame.refreshInstructorDashboard());

        // Export action
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
            for (Section s : list) {
                box.addItem(s);}

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unexpected error loading sections.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(normal); }
        });
    }

    private void exportGrades(int sectionId) throws AccessException {

        Section sec = queryService.getSection(sectionId);
        AccessControl.assertInstructorOwnsSection(SessionManager.getCurrentUserId(),
                sec.getInstructorId(),
                AccessControl.Actions.EXPORT_GRADES_CSV);

        List<Enrollment> enrollments = queryService.getSectionEnrollments(sectionId);
        List<String[]> rows = new ArrayList<>();
        String[] header = {"EnrollmentID", "RollNo", "Component", "Score", "FinalGrade"};

        for (Enrollment e : enrollments) {

            Student st = queryService.getStudentForEnrollment(e.getEnrollmentId());
            List<Grade> grades = queryService.getGradesForEnrollment(e.getEnrollmentId());

            if (grades.isEmpty()) {
                rows.add(new String[]{
                        "" + e.getEnrollmentId(),
                        st != null ? st.getRollNo() : "N/A",
                        "-", "-", "-"
                });
            } else {
                for (Grade g : grades) {
                    rows.add(new String[]{
                            "" + e.getEnrollmentId(),
                            st != null ? st.getRollNo() : "N/A",
                            g.getComponent(),
                            "" + g.getScore(),
                            g.getFinalGrade() != null ? g.getFinalGrade() : ""
                    });
                }
            }
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("section_" + sectionId + "_grades.csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            boolean ok = CSVutil.writecsv(chooser.getSelectedFile(), rows, header);
            JOptionPane.showMessageDialog(this, ok ? "CSV Exported!" : "Failed to Export.");
        }
    }
}
