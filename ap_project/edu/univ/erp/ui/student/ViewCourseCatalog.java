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
        this.courseService  = new CourseService();


        // role check
        if (!SessionManager.isLoggedIn() || !"STUDENT".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Only students allowed here! ", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return; // stops the execution of this screen
        }

        setLayout(new BorderLayout());
        JLabel banner=null;
        if(AccessControl.isMaintenanceOn()){
            banner = new JLabel("System Under Maintenance - VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        JLabel title = new JLabel("Course Catalog", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        if (banner != null)
            add(title, BorderLayout.CENTER);
        else
            add(title, BorderLayout.NORTH);

        // table
        String[] columns = {"Course ID", "Code", "Title", "Credits"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);//scrollable

        // back button
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        //load data into table
        loadCourses(model);

        //back to student dashhboard upon pressing abck button
        backBtn.addActionListener(e ->
                mainFrame.showScreen(MainFrame.STUDENT_DASH)
        );
    }

    private void loadCourses(DefaultTableModel model) {
        model.setRowCount(0);
        List<Course> list = courseService.getAllCourses();
        for (Course c : list) {
            model.addRow(new Object[]{c.getCourseId(), c.getCode(), c.getTitle(), c.getCredits()});
        }
    }
}
