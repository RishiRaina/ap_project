package edu.univ.erp.ui.student;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewMyEnrollments extends JPanel {

    private MainFrame mainFrame;
    private EnrollmentDAO enrollmentDAO;
    private CourseDAO courseDAO;
    private SectionDAO sectionDAO;

    public ViewMyEnrollments(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.enrollmentDAO = new EnrollmentDAO();
        this.courseDAO = new CourseDAO();
        this.sectionDAO = new SectionDAO();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("My Enrollments", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // TABLE
        String[] cols = {"Enrollment ID", "Course", "Section", "Status", "Action"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 4; // Only "Action" column is editable (Drop button)
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Add DROP BUTTONS
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        // Load data into table
        loadEnrollments(model);

        // Back button
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e ->
                mainFrame.showScreen(MainFrame.STUDENT_DASH)
        );
    }

    // Load enrollments
    public void loadEnrollments(DefaultTableModel model) {
        model.setRowCount(0);

        int studentId = SessionManager.getCurrentUserId();
        List<Enrollment> list = enrollmentDAO.getEnrollmentsByStudent(studentId);

        for (Enrollment e : list) {

            // IMPORTANT FIX:
            // 1. Get section using sectionId
            Section sec = sectionDAO.getSectionById(e.getSectionId());

            // Section may be missing (very rare DB issue)
            if (sec == null) {
                model.addRow(new Object[]{
                        e.getEnrollmentId(),
                        e.getSectionId(),
                        "N/A", "N/A",
                        e.getStatus()
                });
                continue;
            }

            // 2. From section → get courseId
            int courseId = sec.getCourseId();

            // 3. Get corresponding course
            Course c = courseDAO.getCourseById(courseId);

            model.addRow(new Object[]{
                    e.getEnrollmentId(),
                    e.getSectionId(),
                    (c != null) ? c.getCode() : "N/A",
                    (c != null) ? c.getTitle() : "N/A",
                    e.getStatus()
            });
        }
    }

    // Called when drop button is clicked
    public void dropEnrollment(int enrollmentId) {
        boolean ok = enrollmentDAO.deleteEnrollment(enrollmentId);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Section dropped successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to drop section.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Reload table
        loadEnrollments((DefaultTableModel) ((JTable)((JScrollPane)getComponent(1)).getViewport().getView()).getModel());
    }
}
