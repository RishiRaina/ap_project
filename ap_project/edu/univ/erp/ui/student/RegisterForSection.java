package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.SectionService;
import edu.univ.erp.service.StudentQueryService;
import edu.univ.erp.service.StudentRegistrationService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegisterForSection extends JPanel {

    private final MainFrame mainFrame;
    private final SectionService sectionService;
    private final CourseService courseService;
    private final StudentRegistrationService regService;
    private final StudentQueryService queryService;
    private final UserAuthDAO userAuthDAO = new UserAuthDAO();
    class RoundedPanel extends JPanel {
        private final int radius = 20;
        public RoundedPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    public RegisterForSection(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.sectionService = new SectionService();
        this.courseService = new CourseService();
        this.regService = new StudentRegistrationService();
        this.queryService = new StudentQueryService();


        if (!SessionManager.isLoggedIn() || !"STUDENT".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Students Only", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bp = new JPanel(new BorderLayout());
            bp.setBackground(new Color(255, 179, 71));
            bp.setBorder(new EmptyBorder(10, 10, 10, 10));
            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));
            bp.add(banner, BorderLayout.CENTER);
            add(bp, BorderLayout.NORTH);
        }
        JLabel title = new JLabel("Register for a Section", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.PAGE_START);
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 40, 25, 40));
        add(card, BorderLayout.CENTER);
        String[] cols = {"Section", "Course", "Instructor", "Time", "Room", "Seats Left", "Deadline"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(200); // Section
        table.getColumnModel().getColumn(1).setPreferredWidth(240); // Course
        table.getColumnModel().getColumn(2).setPreferredWidth(130); // Instructor
        table.getColumnModel().getColumn(3).setPreferredWidth(170); // Time
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Room
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Seats Left
        table.getColumnModel().getColumn(6).setPreferredWidth(170); // Deadline

        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        card.add(scroll, BorderLayout.CENTER);
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");
        styleButton(registerBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));
        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(registerBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
        loadSections(model);
        registerBtn.addActionListener(e -> {

            if (MaintenanceChecker.isMaintenanceOn() && !"ADMIN".equals(SessionManager.getCurrentUserRole())) {
                JOptionPane.showMessageDialog(this, "Registration disabled — Maintenance Mode ON", "Maintenance", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a section first!", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String sectionText = (String) model.getValueAt(row, 0);
            Section sec = sectionService.getSectionByString(sectionText);

            if (sec == null) {
                JOptionPane.showMessageDialog(this, "Section lookup failed!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int studentId = SessionManager.getCurrentUserId();
            int sectionId = sec.getSectionId();

            try {
                regService.register(studentId, sectionId);

                JOptionPane.showMessageDialog(
                        this,
                        "Successfully registered!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadSections(model);  // refresh available sections
            } catch (AccessException ex2) {
                JOptionPane.showMessageDialog(
                        this,
                        ex2.getMessage(),
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        backBtn.addActionListener(e -> mainFrame.refreshStudentDashboard());
    }
    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);

        int studentId = SessionManager.getCurrentUserId();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        try {
            Set<Integer> mySections = new HashSet<>();
            List<Enrollment> myEnrollments = queryService.getMyEnrollments(studentId);
            for (Enrollment e : myEnrollments) {
                mySections.add(e.getSectionId());
            }

            List<Section> allSections = sectionService.getAllSections();
            for (Section s : allSections) {
                if (mySections.contains(s.getSectionId())) {
                    continue;
                }
                Course c = courseService.getCourseById(s.getCourseId());
                String instructor = "TBA";
                if (s.getInstructorId() != null) {
                    instructor = userAuthDAO
                            .getUsernameByUserId(s.getInstructorId())
                            .toUpperCase();
                }
                int totalCap = s.getCapacity();
                int enrolledCount = queryService.getEnrollmentsBySection(s.getSectionId()).size();
                int remaining = Math.max(0, totalCap - enrolledCount);
                String seatsText = remaining + " / " + totalCap;

                model.addRow(new Object[]{
                        s.toString().toUpperCase(),
                        (c != null) ? (c.getCode().toUpperCase() + " - " + c.getTitle()) : "N/A",
                        instructor,
                        s.getDayTime(),
                        s.getRoom().toUpperCase(),
                        seatsText,
                        (s.getRegistrationDeadline() != null)
                                ? sdf.format(s.getRegistrationDeadline())
                                : "N/A"
                });
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load sections: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
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
