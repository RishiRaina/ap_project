package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.common.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorSectionStudents extends JPanel {

    private MainFrame mainFrame;
    private int sectionId;
    private InstructorQueryService queryService;
    private JTable studentTable;

    public InstructorSectionStudents(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;
        this.queryService = new InstructorQueryService();

        // role check
        if (!SessionManager.isLoggedIn() || !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Instructors only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());

        JLabel banner = null;
        if (MaintenanceChecker.isMaintenanceOn()) {
            banner = new JLabel("System Under Maintenance – VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        JLabel title = new JLabel("Students in Section " + sectionId, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        if(banner!=null){
            add(title,BorderLayout.CENTER);
        }
        else{
            add(title,BorderLayout.NORTH);
        }
        String[] cols = {"Enrollment ID", "Roll No", "Program", "Year", "Final Grade", "Action"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5;  // Only action column
            }
        };
        studentTable = new JTable(model);
        studentTable.setRowHeight(28);

        studentTable.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        studentTable.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        loadStudents(model);
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        backBtn.addActionListener(e -> mainFrame.showScreen("instructor_my_sections"));

        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }
    public int getSectionId() {
        return sectionId;
    }


    public void loadStudents(DefaultTableModel model) {
        model.setRowCount(0);

        try {
            int instructorId = SessionManager.getCurrentUserId();
            // access check
            AccessControl.assertAllowed(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.VIEW_SECTIONS);
            // 2. Ownership check
            Section sec = queryService.getSection(sectionId);
            if (sec == null)
                throw new AccessException("Section does not exist.");

            AccessControl.assertInstructorOwnsSection(instructorId, sec.getInstructorId(), AccessControl.Actions.VIEW_SECTIONS);

            // 3. Fetch enrollments
            List<Enrollment> enrollments = queryService.getSectionEnrollments(sectionId);

            for (Enrollment e : enrollments) {

                Student s = queryService.getStudentForEnrollment(e.getEnrollmentId());
                if (s == null) continue;

                // find final grade
                String finalGrade = "-";
                List<Grade> list = queryService.getGradesForEnrollment(e.getEnrollmentId());
                for (Grade g : list) {
                    if (g.getFinalGrade() != null && !g.getFinalGrade().isBlank()) {
                        finalGrade = g.getFinalGrade();
                        break;
                    }
                }

                model.addRow(new Object[]{e.getEnrollmentId(), s.getRollNo(), s.getProgram(), s.getYear(), finalGrade, "Enter Grades"});
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void enterGrades(int row) {

        // Maintenance check
        if (MaintenanceChecker.isMaintenanceOn() && !"ADMIN".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Cannot edit grades while system is in Maintenance Mode.", "Maintenance ON", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int enrollmentId = (Integer) studentTable.getValueAt(row, 0);

            // ensure instructor owns this section
            Section sec = queryService.getSection(sectionId);
            AccessControl.assertInstructorOwnsSection(SessionManager.getCurrentUserId(), sec.getInstructorId(), AccessControl.Actions.ENTER_SCORES);

            // Open grade entry dialog
            new GradeEntryDialog(SwingUtilities.getWindowAncestor(this), enrollmentId, this).setVisible(true);

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // After grade dialog updates data
    public void reloadTable() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        loadStudents(model);
    }
}
