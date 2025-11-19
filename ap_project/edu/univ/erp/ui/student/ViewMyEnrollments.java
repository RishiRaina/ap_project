package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.access.AccessException;
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

    public ViewMyEnrollments(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new StudentQueryService();
        this.regService = new StudentRegistrationService();

        if (!SessionManager.isLoggedIn() || !"STUDENT".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Students only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;   // stop with this screen after showing back to login screen
        }

        setLayout(new BorderLayout());

        JLabel banner=null;
        if (AccessControl.isMaintenanceOn()) {
            banner = new JLabel("System Under Maintenance - VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        JLabel title = new JLabel("My Enrollments", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));

        if (banner != null)
            add(title, BorderLayout.CENTER);
        else
            add(title, BorderLayout.NORTH);

        // TABLE MODEL
        String[] cols = {"Enrollment ID", "Course Code", "Course Title", "Section", "Status", "Action"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 5; // Only Drop button column
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button column
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        // Load enrollments
        loadEnrollments(model);

        // Back button
        JButton backBtn = new JButton("Back");
        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.STUDENT_DASH));
    }

    // load your enrollments for display in table
    public void loadEnrollments(DefaultTableModel model) {
        model.setRowCount(0);
        int studentId = SessionManager.getCurrentUserId();
        List<Enrollment> list = queryService.getMyEnrollments(studentId);
        for (Enrollment e : list) {
            Section sec = queryService.getSection(e.getSectionId());
            if (sec == null) continue;
            Course c = queryService.getCourseById(sec.getCourseId());
            model.addRow(new Object[]{e.getEnrollmentId(), c != null ? c.getCode() : "N/A", c != null ? c.getTitle() : "N/A", sec.getSectionId(), e.getStatus(), "Drop"});
        }
    }

    // Drop an enrollment , not possible in maintennce mode
    public void dropEnrollment(int enrollmentId) {

        int studentId = SessionManager.getCurrentUserId();
        if (AccessControl.isMaintenanceOn() && !"ADMIN".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Dropping NOT possible as system in Maintenance Mode.", "Maintenance Mode ON", JOptionPane.WARNING_MESSAGE);
            return;
        }


        try {
            regService.drop(studentId, enrollmentId);
            JOptionPane.showMessageDialog(this, "Enrollment dropped!", "Successful operation ", JOptionPane.INFORMATION_MESSAGE);

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Drop Failed", JOptionPane.ERROR_MESSAGE);
        }
        JTable table = (JTable) ((JScrollPane) getComponent(1)).getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        loadEnrollments(model);
    }
}
