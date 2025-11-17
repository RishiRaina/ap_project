package edu.univ.erp.ui.student;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewCourseCatalog extends JPanel {

    private MainFrame mainFrame;
    private CourseDAO courseDAO;

    public ViewCourseCatalog(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.courseDAO = new CourseDAO();

        setLayout(new BorderLayout());


        JLabel title = new JLabel("Course Catalog", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columns = {"Course ID", "Code", "Title", "Credits"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ===== BUTTONS (BACK) =====
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // ===== LOAD DATA INTO TABLE =====
        loadCourses(model);

        // ===== BACK EVENT =====
        backBtn.addActionListener(e ->
                mainFrame.showScreen(MainFrame.STUDENT_DASH)
        );
    }

    // Loads all courses from DB
    private void loadCourses(DefaultTableModel model) {
        model.setRowCount(0); // clear old data

        List<Course> list = courseDAO.getAllCourses();
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
