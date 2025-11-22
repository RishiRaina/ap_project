package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentQueryService;
import edu.univ.erp.service.StudentRegistrationService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewMyEnrollments extends JPanel {

    private final MainFrame mainFrame;
    private final StudentQueryService queryService;
    private final StudentRegistrationService regService;
    private JTable table;

    // Rounded card panel
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

    public ViewMyEnrollments(MainFrame mainFrame) {

        this.mainFrame = mainFrame;
        this.queryService = new StudentQueryService();
        this.regService = new StudentRegistrationService();

        // ----- ROLE CHECK -----
        if (!SessionManager.isLoggedIn() ||
                !"STUDENT".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {

            JOptionPane.showMessageDialog(
                    this, "Access Denied: Students Only",
                    "Access Error", JOptionPane.ERROR_MESSAGE
            );
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ----- MAINTENANCE BANNER -----
        if (MaintenanceChecker.isMaintenanceOn()) {

            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY",
                    SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));

            bannerPanel.add(banner, BorderLayout.CENTER);
            add(bannerPanel, BorderLayout.NORTH);
        }

        // ----- TITLE -----
        JLabel title = new JLabel("My Enrollments", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));

        add(title, BorderLayout.PAGE_START);

        // ----- CARD PANEL -----
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 40, 25, 40));

        // ----- TABLE MODEL -----
        // Column 0 = hidden EnrollmentID
        String[] cols = {"EnrollmentID", "Course", "Section", "Status", "Drop"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 4;   // Only Drop column
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Hide EnrollmentID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Button
        table.getColumn("Drop").setCellRenderer(new ButtonRenderer());
        table.getColumn("Drop").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        // ----- BACK BUTTON -----
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.refreshStudentDashboard());

        // Load rows
        loadEnrollments(model);
    }

    // ----- LOAD ENROLLMENTS -----
    public void loadEnrollments(DefaultTableModel model) {

        model.setRowCount(0);
        int studentId = SessionManager.getCurrentUserId();

        try {

            List<Enrollment> list = queryService.getMyEnrollments(studentId);

            for (Enrollment e : list) {

                Section sec = queryService.getSection(e.getSectionId());
                if (sec == null) continue;

                Course c = queryService.getCourseById(sec.getCourseId());

                model.addRow(new Object[]{
                        e.getEnrollmentId(),   // hidden
                        c.getCode().toUpperCase() + " - " + c.getTitle(),
                        sec.toString().toUpperCase(),
                        e.getStatus(),
                        "Drop"
                });
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(
                    this, ex.getMessage(),
                    "Access Error", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ----- DROP ENROLLMENT -----
    public void dropEnrollment(int enrollmentId) {

        if (MaintenanceChecker.isMaintenanceOn() &&
                !"ADMIN".equals(SessionManager.getCurrentUserRole())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Dropping NOT allowed during Maintenance!",
                    "Maintenance", JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int studentId = SessionManager.getCurrentUserId();

        try {
            regService.drop(studentId, enrollmentId);

            JOptionPane.showMessageDialog(
                    this,
                    "Enrollment dropped!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadEnrollments((DefaultTableModel) table.getModel());

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Drop Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ----- STYLE BUTTON -----
    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
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
