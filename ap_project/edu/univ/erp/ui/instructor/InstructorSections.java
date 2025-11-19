package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.*;

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

        // ===== ROLE CHECK =====
        if (!SessionManager.isLoggedIn() ||
                !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {

            JOptionPane.showMessageDialog(this, "Access Denied: Instructors only.", "Access Error", JOptionPane.ERROR_MESSAGE);

            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());

        //maintenance banner
        if (AccessControl.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance - VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.SOUTH);
        }

        // title
        JLabel title = new JLabel("My Sections", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Section ID", "Course Code", "Course Title", "Day/Time", "Room", "Action"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5; // Only Action button column
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // button render and edit to form button and table
        table.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        table.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));
        loadMySections(model);

        // ===== BACK BUTTON =====
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.INSTRUCTOR_DASH));

        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    public void loadMySections(DefaultTableModel model) {

        model.setRowCount(0);
        try {
            int instructorId = SessionManager.getCurrentUserId();
            AccessControl.assertAllowed(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.VIEW_SECTIONS);
            List<Section> list = queryService.getMySections(instructorId);
            for (Section s : list) {
                Course c = courseService.getCourseById(s.getCourseId());
                model.addRow(new Object[]{s.getSectionId(), c != null ? c.getCode() : "N/A", c != null ? c.getTitle() : "N/A", s.getDayTime(), s.getRoom(), "View Students"});
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // view students button handler
    public void viewStudents(int row) {

        try {
            JTable table = (JTable) ((JScrollPane) getComponent(1)).getViewport().getView();
            int sectionId = (Integer) table.getValueAt(row, 0);

            int instructorId = SessionManager.getCurrentUserId();
            Section sec = queryService.getSection(sectionId);

            // ownership check
            AccessControl.assertInstructorOwnsSection(
                    instructorId,
                    sec.getInstructorId(),
                    AccessControl.Actions.VIEW_SECTIONS
            );

            // Open student list screen
            mainFrame.addScreen("instructor_section_students", new InstructorSectionStudents(mainFrame, sectionId));
            mainFrame.showScreen("instructor_section_students");

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
