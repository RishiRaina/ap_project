package edu.univ.erp.ui.student;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.access.*;
import edu.univ.erp.auth.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewCourseCatalog extends JPanel {

    private MainFrame mainFrame;
    private CourseService courseService;

    public ViewCourseCatalog(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.courseService = new CourseService();

        // Role check
        if (!SessionManager.isLoggedIn() || !"STUDENT".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied: Only students allowed!",
                    "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());

        // Maintenance banner (ALWAYS at NORTH)
        if (AccessControl.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        // Title (ALWAYS NORTH, below banner automatically)
        JLabel title = new JLabel("Course Catalog", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {"Course ID", "Code", "Title", "Credits"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Back button
        JButton backBtn = new JButton("Back");
        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.STUDENT_DASH));

        // Load courses
        loadCourses(model);
    }

    private void loadCourses(DefaultTableModel model) {
        model.setRowCount(0);
        List<Course> list = courseService.getAllCourses();
        for (Course c : list) {
            model.addRow(new Object[]{
                    c.getCourseId(),
                    c.getCode(),
                    c.getTitle(),
                    c.getCredits()
            });
        }
    }
}
