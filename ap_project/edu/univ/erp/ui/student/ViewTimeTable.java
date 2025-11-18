package edu.univ.erp.ui.student;

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

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Timetable", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

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

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.STUDENT_DASH));
        loadTimetable(model);
    }

    private void loadTimetable(DefaultTableModel model) {
        model.setRowCount(0);

        int studentId = SessionManager.getCurrentUserId();

        // Get all enrollments
        List<Enrollment> enrollments = queryService.getMyEnrollments(studentId);
        for (Enrollment e : enrollments) {
            Section sec = queryService.getSection(e.getSectionId());//Get section using sectionId
            if (sec == null) continue;
            Course c = queryService.getCourseById(sec.getCourseId());

            model.addRow(new Object[]{sec.getDayTime(), sec.getRoom(), c != null ? c.getCode() : "N/A", c != null ? c.getTitle() : "N/A"});
        }
    }
}
