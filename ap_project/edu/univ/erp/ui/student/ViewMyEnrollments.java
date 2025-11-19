package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentQueryService;
import edu.univ.erp.service.StudentRegistrationService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewMyEnrollments extends JPanel {

    private MainFrame mainFrame;
    private StudentQueryService queryService;
    private StudentRegistrationService regService;
    private JTable table;

    public ViewMyEnrollments(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new StudentQueryService();
        this.regService = new StudentRegistrationService();

        // Role check
        if (!SessionManager.isLoggedIn() || !"STUDENT".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Students only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());

        // Maintenance banner
        if (AccessControl.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance - VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        // Title
        JLabel title = new JLabel("My Enrollments", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"Enrollment ID", "Course Code", "Course Title", "Section", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 5; // Only Drop column editable
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("Back");
        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.STUDENT_DASH));

        // Load data
        loadEnrollments(model);
    }

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
                        e.getEnrollmentId(),
                        c != null ? c.getCode() : "N/A",
                        c != null ? c.getTitle() : "N/A",
                        sec.getSectionId(),
                        e.getStatus(),
                        "Drop"
                });
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void dropEnrollment(int enrollmentId) {

        if (AccessControl.isMaintenanceOn() && !"ADMIN".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Dropping NOT allowed during Maintenance Mode.", "Maintenance Mode", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = SessionManager.getCurrentUserId();

        try {
            regService.drop(studentId, enrollmentId);
            JOptionPane.showMessageDialog(this, "Enrollment dropped!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // refresh
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            loadEnrollments(model);

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Drop Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
