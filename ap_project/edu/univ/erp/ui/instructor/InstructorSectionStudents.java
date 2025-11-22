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

        // -------- ROLE CHECK --------
        if (!SessionManager.isLoggedIn() ||
                !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Access Denied: Instructors only.",
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // -------- MAINTENANCE BANNER --------
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel(
                    "System Under Maintenance — VIEW ONLY",
                    SwingConstants.CENTER
            );
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));

            bannerPanel.add(banner, BorderLayout.CENTER);
            add(bannerPanel, BorderLayout.NORTH);
        }

        // -------- TITLE (USE SECTION.TOSTRING IN UPPERCASE) --------
        Section sec;
        try {
            sec = queryService.getSection(sectionId);
        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String sectionLabel = (sec != null)
                ? sec.toString().toUpperCase()
                : ("SECTION " + sectionId);

        JLabel title = new JLabel("STUDENTS IN " + sectionLabel, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.PAGE_START);

        // -------- TABLE --------
        String[] cols = {"Username", "Roll No", "Program", "Year", "Final Grade", "Action"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5; // Only "Action" column editable
            }
        };

        studentTable = new JTable(model);
        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));

        studentTable.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        studentTable.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        loadStudents(model);

        // -------- BACK BUTTON --------
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        backBtn.addActionListener(e -> {
            InstructorSections sections = new InstructorSections(mainFrame);

            InstructorDashboard dash = (InstructorDashboard)
                    SwingUtilities.getAncestorOfClass(InstructorDashboard.class, this);

            if (dash != null) {
                // Go back inside dashboard center
                dash.setCenter(sections);
            } else {
                // Fallback: card-based navigation
                mainFrame.addScreen("instructor_my_sections", sections);
                mainFrame.showScreen("instructor_my_sections");
            }
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    public int getSectionId() {
        return sectionId;
    }

    // ---------- LOAD STUDENTS INTO TABLE ----------
    public void loadStudents(DefaultTableModel model) {
        model.setRowCount(0);

        try {
            int instructorId = SessionManager.getCurrentUserId();

            AccessControl.assertAllowed(
                    AccessControl.Role.INSTRUCTOR,
                    AccessControl.Actions.VIEW_SECTIONS
            );

            Section sec = queryService.getSection(sectionId);
            if (sec == null) {
                throw new AccessException("Section does not exist.");
            }

            AccessControl.assertInstructorOwnsSection(
                    instructorId,
                    sec.getInstructorId(),
                    AccessControl.Actions.VIEW_SECTIONS
            );

            List<Enrollment> enrollments = queryService.getSectionEnrollments(sectionId);

            for (Enrollment e : enrollments) {
                Student s = queryService.getStudentForEnrollment(e.getEnrollmentId());
                if (s == null) continue;

                // Get username from auth DB
                String username = SessionManager.getUsernameByUserId(s.getUserId());
                if (username == null || username.isBlank()) {
                    username = "UNKNOWN";
                }

                String finalGrade = "-";
                for (Grade g : queryService.getGradesForEnrollment(e.getEnrollmentId())) {
                    if (g.getFinalGrade() != null && !g.getFinalGrade().isBlank()) {
                        finalGrade = g.getFinalGrade();
                        break;
                    }
                }

                model.addRow(new Object[]{
                        username,                              // keep original case; easier for lookup
                        s.getRollNo() != null ? s.getRollNo().toUpperCase() : "",
                        s.getProgram() != null ? s.getProgram().toUpperCase() : "",
                        s.getYear(),
                        finalGrade,
                        "Enter Grades"
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

    // ---------- ACTION: ENTER GRADES ----------
    public void enterGrades(int row) {
        if (MaintenanceChecker.isMaintenanceOn() &&
                !"ADMIN".equals(SessionManager.getCurrentUserRole())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cannot edit grades during Maintenance.",
                    "Maintenance ON",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            String username = (String) studentTable.getValueAt(row, 0);
            if (username == null || username.isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username for selected row.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Lookup userId from auth DB
            int userId = userAuthDAO.getUserIdByUsername(username);
            if (userId <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not find user for username: " + username,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Find enrollment for that user in this section
            Enrollment enr = queryService.getEnrollmentForUserInSection(userId, sectionId);
            if (enr == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Enrollment not found for " + username + " in this section.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int enrollmentId = enr.getEnrollmentId();

            Section sec = queryService.getSection(sectionId);

            AccessControl.assertInstructorOwnsSection(
                    SessionManager.getCurrentUserId(),
                    sec.getInstructorId(),
                    AccessControl.Actions.ENTER_SCORES
            );

            new GradeEntryDialog(
                    SwingUtilities.getWindowAncestor(this),
                    enrollmentId,
                    this
            ).setVisible(true);

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
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
