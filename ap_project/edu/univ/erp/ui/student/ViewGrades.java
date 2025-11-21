package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentQueryService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewGrades extends JPanel {

    private MainFrame mainFrame;
    private StudentQueryService queryService;

    public ViewGrades(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new StudentQueryService();

        // role check
        if (!SessionManager.isLoggedIn() || !"STUDENT".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Students only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());

        // maintenance banner
        JLabel banner=null;
        if (MaintenanceChecker.isMaintenanceOn()) {
            banner = new JLabel("System Under Maintenance - VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        // title
        JLabel title = new JLabel("My Grades", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        if(banner!=null){
            add(title,BorderLayout.CENTER);
        }
        else{
            add(title,BorderLayout.NORTH);
        }

        // table
        String[] columns = {"Course Code", "Course Title", "Component", "Score", "Final Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // back button
        JButton backBtn = new JButton("Back");
        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.refreshStudentDashboard());

        // load
        loadGrades(model);
    }

    private void loadGrades(DefaultTableModel model) {
        model.setRowCount(0);
        int studentId = SessionManager.getCurrentUserId();

        try {
            List<Enrollment> enrollments = queryService.getMyEnrollments(studentId);

            for (Enrollment e : enrollments) {
                Section sec = queryService.getSection(e.getSectionId());
                if (sec == null) continue;

                Course c = queryService.getCourseById(sec.getCourseId());
                List<Grade> grades = queryService.getGradesForEnrollment(e.getEnrollmentId());

                for (Grade g : grades) {
                    model.addRow(new Object[]{
                            c != null ? c.getCode() : "N/A",
                            c != null ? c.getTitle() : "N/A",
                            g.getComponent(),
                            g.getScore(),
                            g.getFinalGrade() != null ? g.getFinalGrade() : "-"
                    });
                }
            }
        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
