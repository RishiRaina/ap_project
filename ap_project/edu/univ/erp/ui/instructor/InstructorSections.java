package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.*;
import edu.univ.erp.ui.student.ButtonEditor;
import edu.univ.erp.ui.student.ButtonRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorSections extends JPanel {

    private MainFrame mainFrame;
    private InstructorQueryService queryService;
    private CourseService courseService;

    public InstructorSections(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new InstructorQueryService();
        this.courseService = new CourseService();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("My Sections", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Section ID", "Course Code", "Course Title", "Day/Time", "Room", "Action"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5; // Only Action = button
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        add(new JScrollPane(table), BorderLayout.CENTER);
        // Button column
        table.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        table.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));


        // Load data
        loadMySections(model);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 16));

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.INSTRUCTOR_DASH));
        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    public void loadMySections(DefaultTableModel model) {
        model.setRowCount(0);
        int instructorId = SessionManager.getCurrentUserId();
        List<Section> list = queryService.getMySections(instructorId);
        for (Section s : list) {
            Course c = courseService.getCourseById(s.getCourseId());
            model.addRow(new Object[]{s.getSectionId(), c != null ? c.getCode() : "N/A", c != null ? c.getTitle() : "N/A", s.getDayTime(), s.getRoom(), "View Students"});
        }
    }

    // Method called from ButtonEditor when clicking "View Students"
    public void viewStudents(int row) {

        JTable table = (JTable) ((JScrollPane) getComponent(1)).getViewport().getView();
        int sectionId = (Integer) table.getValueAt(row, 0);

        // sectio id passed as an argyment to next screen
        mainFrame.addScreen("instructor_section_students", new InstructorSectionStudents(mainFrame, sectionId));
        mainFrame.showScreen("instructor_section_students");
    }
}
