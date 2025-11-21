package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentQueryService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewTimeTable extends JPanel {

    private MainFrame mainFrame;
    private StudentQueryService queryService;

    public ViewTimeTable(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new StudentQueryService();

        if (!SessionManager.isLoggedIn() || !"STUDENT".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Students only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;   // stop with this screen after showing back to login screen
        }

        setLayout(new BorderLayout());

        JLabel banner=null;
        if (MaintenanceChecker.isMaintenanceOn()) {
            banner = new JLabel("System Under Maintenance - VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        JLabel title = new JLabel("Timetable", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        if(banner!=null){
            add(title,BorderLayout.CENTER);
        }
        else{
            add(title,BorderLayout.NORTH);
        }


        // tbale structure
        String[] cols = {"Day/Time", "Room", "Course Code", "Course Title"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        JTable table = new JTable(model);
        table.setRowHeight(25);

        add(new JScrollPane(table), BorderLayout.CENTER);

        //back button
        JButton backBtn = new JButton("Back");
        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.refreshStudentDashboard());
        loadTimetable(model);
    }

    private void loadTimetable(DefaultTableModel model)  {
        model.setRowCount(0);
        int studentId = SessionManager.getCurrentUserId();
        try {
            List<Enrollment> enrollments = queryService.getMyEnrollments(studentId);
            for (Enrollment e : enrollments) {
                Section sec = queryService.getSection(e.getSectionId());
                if (sec == null) continue;
                Course c = queryService.getCourseById(sec.getCourseId());
                model.addRow(new Object[]{sec.getDayTime(), sec.getRoom(), c != null ? c.getCode() : "N/A", c != null ? c.getTitle() : "N/A"});
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.STUDENT_DASH);
        }
    }

}
