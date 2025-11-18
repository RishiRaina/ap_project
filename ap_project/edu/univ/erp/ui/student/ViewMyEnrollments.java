package edu.univ.erp.ui.student;

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

        setLayout(new BorderLayout());

        JLabel title = new JLabel("My Enrollments", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
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

    // LOAD TABLE DATA
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

    // DROP ACTION
    public void dropEnrollment(int enrollmentId) {

        int studentId = SessionManager.getCurrentUserId();

        try {
            regService.drop(studentId, enrollmentId);

            JOptionPane.showMessageDialog(this,
                    "Enrollment dropped!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (AccessException ex) {

            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Drop Failed", JOptionPane.ERROR_MESSAGE);
        }

        JTable table = (JTable) ((JScrollPane) getComponent(1)).getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        loadEnrollments(model);
    }
}
