package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Course;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorStatsSectionSelect extends JPanel {

    private MainFrame mainFrame;
    private InstructorQueryService queryService;
    private CourseService courseService;

    public InstructorStatsSectionSelect(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new InstructorQueryService();
        this.courseService = new CourseService();
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Select Section for Stats", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);
        String[] cols = {"Section ID", "Course Code", "Course Title", "Action"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return c == 3; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        table.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadSections(model);

        JButton back = new JButton("Back");
        back.addActionListener(e -> mainFrame.showScreen("instructor_dashboard"));
        add(back, BorderLayout.SOUTH);
    }

    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);
        int instructorId = SessionManager.getCurrentUserId();
        List<Section> list = queryService.getMySections(instructorId);
        for (Section s : list) {
            Course c = courseService.getCourseById(s.getCourseId());
            model.addRow(new Object[]{s.getSectionId(), c != null ? c.getCode() : "N/A", c != null ? c.getTitle() : "N/A", "View Stats"});
        }
    }

    public void viewStats(int row) {
        JTable table = (JTable) ((JScrollPane) getComponent(1)).getViewport().getView();
        int sectionId = (Integer) table.getValueAt(row, 0);
        //add and show the linked screen to show stats for each section
        mainFrame.addScreen("instructor_stats_view", new SectionStatsView(mainFrame, sectionId));
        mainFrame.showScreen("instructor_stats_view");
    }
}
