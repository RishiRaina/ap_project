package edu.univ.erp.ui.student;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewGrades extends JPanel {

    private MainFrame mainFrame;
    private EnrollmentDAO enrollmentDAO;
    private GradeDAO gradeDAO;

    public ViewGrades(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.enrollmentDAO = new EnrollmentDAO();
        this.gradeDAO = new GradeDAO();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("My Grades", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // table
        String[] columns = {"Enrollment ID", "Component", "Score", "Final Grade"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(table);//scorllable
        add(scroll, BorderLayout.CENTER);

        // back
        JButton backBtn = new JButton("Back");
        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // load grades
        loadGrades(model);

        backBtn.addActionListener(e ->
                mainFrame.showScreen(MainFrame.STUDENT_DASH)
        );
    }

    private void loadGrades(DefaultTableModel model) {
        model.setRowCount(0);
        int studentId = SessionManager.getCurrentUserId();
        List<Enrollment> enrollList = enrollmentDAO.getEnrollmentsByStudent(studentId);

        for (Enrollment e : enrollList) {
            List<Grade> grades = gradeDAO.getGradesByEnrollment(e.getEnrollmentId());

            for (Grade g : grades) {
                model.addRow(new Object[]{
                        e.getEnrollmentId(),
                        g.getComponent(),
                        g.getScore(),
                        (g.getFinalGrade() != null ? g.getFinalGrade() : "-")
                });
            }
        }
    }
}
