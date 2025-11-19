package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.InstructorQueryService;
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

        // ROLE CHECK
        if (!SessionManager.isLoggedIn() ||
                !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied: Instructor only.",
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        // Maintenance banner
        if (AccessControl.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.SOUTH);
        }

        JLabel title = new JLabel("Select Section for Stats", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Section ID", "Course Code", "Course Title", "Action"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 3;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        table.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadSections(model);

        JButton back = new JButton("Back");
        back.addActionListener(e -> mainFrame.showScreen(MainFrame.INSTRUCTOR_DASH));
        add(back, BorderLayout.SOUTH);
    }

    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);
        int instructorId = SessionManager.getCurrentUserId();
        try {
            List<Section> list = queryService.getMySections(instructorId);

            for (Section s : list) {
                Course c = courseService.getCourseById(s.getCourseId());
                model.addRow(new Object[]{s.getSectionId(), c != null ? c.getCode() : "N/A", c != null ? c.getTitle() : "N/A","View Stats"});
            }
        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void viewStats(int row) {
        JTable table = (JTable) ((JScrollPane) getComponent(1)).getViewport().getView();
        int sectionId = (Integer) table.getValueAt(row, 0);
        mainFrame.addScreen("instructor_stats_view", new SectionStatsView(mainFrame, sectionId));
        mainFrame.showScreen("instructor_stats_view");
    }
}
