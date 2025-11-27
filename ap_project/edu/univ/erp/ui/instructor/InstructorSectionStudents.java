package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.common.ActionButtonEditor;
import edu.univ.erp.ui.common.ActionButtonRenderer;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorSectionStudents extends JPanel {

    private final MainFrame mainFrame;
    private final int sectionId;
    private final InstructorQueryService queryService;
    private final UserAuthDAO userAuthDAO;
    private JTable studentTable;

    public InstructorSectionStudents(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;
        this.queryService = new InstructorQueryService();
        this.userAuthDAO = new UserAuthDAO();


        if (!SessionManager.isLoggedIn() || !"INSTRUCTOR".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {

            JOptionPane.showMessageDialog(this, "Access Denied: Instructors only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));

            bannerPanel.add(banner, BorderLayout.CENTER);
            add(bannerPanel, BorderLayout.NORTH);
        }


        String headerText;
        try {
            Section sec = queryService.getSection(sectionId);
            if (sec == null) {
                headerText = "SECTION " + sectionId;
            } else {
                headerText = sec.toString().toUpperCase();
            }

        } catch (AccessException e) {
            headerText = "SECTION " + sectionId;
        }

        JLabel title = new JLabel("STUDENTS IN " + headerText, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.PAGE_START);

        String[] cols = {
                "Username", "Roll No", "Program", "Year", "Final Grade", "Action", "ENROLLMENT_ID"
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5;     // Only Action button cell editable
            }
        };

        studentTable = new JTable(model);
        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        studentTable.getColumn("Action")
                .setCellRenderer(new ActionButtonRenderer());
        studentTable.getColumn("Action")
                .setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        loadStudents(model);
        studentTable.removeColumn(studentTable.getColumnModel().getColumn(6));

        add(new JScrollPane(studentTable), BorderLayout.CENTER);
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        backBtn.addActionListener(e -> {
            InstructorSections panel = new InstructorSections(mainFrame);

            InstructorDashboard dash =
                    (InstructorDashboard) SwingUtilities.getAncestorOfClass(InstructorDashboard.class, this);

            if (dash != null) {
                dash.setCenter(panel);
            } else {
                mainFrame.addScreen("instructor_my_sections", panel);
                mainFrame.showScreen("instructor_my_sections");
            }
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    public void loadStudents(DefaultTableModel model) {
        model.setRowCount(0);

        try {
            int instructorId = SessionManager.getCurrentUserId();

            Section sec = queryService.getSection(sectionId);
            if (sec == null) throw new AccessException("Section does not exist.");

            // Check ownership
            AccessControl.assertInstructorOwnsSection(
                    instructorId,
                    sec.getInstructorId(),
                    AccessControl.Actions.VIEW_SECTIONS
            );

            List<Enrollment> enrollments = queryService.getSectionEnrollments(sectionId);

            for (Enrollment e : enrollments) {

                Student s = queryService.getStudentForEnrollment(e.getEnrollmentId());
                if (s == null) continue;

                String username = SessionManager.getUsernameByUserId(s.getUserId());
                if (username == null || username.isBlank()) username = "UNKNOWN";

                String finalGrade = "-";
                List<Grade> grades = queryService.getGradesForEnrollment(e.getEnrollmentId());
                for (Grade g : grades) {
                    if (g.getFinalGrade() != null && !g.getFinalGrade().isBlank()) {
                        finalGrade = g.getFinalGrade();
                        break;
                    }
                }

                model.addRow(new Object[]{
                        username,
                        s.getRollNo() != null ? s.getRollNo().toUpperCase() : "",
                        s.getProgram() != null ? s.getProgram().toUpperCase() : "",
                        s.getYear(),
                        finalGrade,
                        "Enter Grades",
                        e.getEnrollmentId()  // HIDDEN COLUMN
                });
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    public void enterGrades(int row) {
        if (MaintenanceChecker.isMaintenanceOn() && !"ADMIN".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Cannot edit grades during Maintenance.", "Maintenance ON", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Retrieve enrollmentId from hidden model
            int enrollmentId = (int) studentTable.getModel().getValueAt(row, 6);
            new GradeEntryDialog(SwingUtilities.getWindowAncestor(this), enrollmentId, this).setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void reloadTable() {
        loadStudents((DefaultTableModel) studentTable.getModel());
    }
    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(normal); }
        });
    }
}
