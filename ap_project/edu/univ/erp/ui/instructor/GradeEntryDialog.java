package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.NotificationsDAO;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Notification;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminGradeService;
import edu.univ.erp.service.InstructorGradeService;
import edu.univ.erp.service.InstructorQueryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class GradeEntryDialog extends JDialog {

    private final int enrollmentId;
    private final InstructorSectionStudents parentPanel;
    private final InstructorQueryService queryService;
    private final NotificationsDAO notificationsDAO;
    private final boolean adminMode;

    private final InstructorGradeService instructorGradeService;
    private final AdminGradeService adminGradeService;

    private static final String[] COMPONENTS = {"ASSIGNMENTS", "QUIZZES", "PROJECT", "MID", "END"};
    private JComboBox<String> compDropdown;
    private JTextField scoreField;
    private JTextField finalField;
    private HashMap<String, Grade> existingMap = new HashMap<>();
    public GradeEntryDialog(Window owner, int enrollmentId, InstructorSectionStudents parentPanel) {
        super(owner, "Enter Grades", ModalityType.APPLICATION_MODAL);
        this.enrollmentId = enrollmentId;
        this.parentPanel = parentPanel;
        this.adminMode = false;
        this.instructorGradeService = new InstructorGradeService();
        this.adminGradeService = null;
        this.queryService = new InstructorQueryService();
        this.notificationsDAO = new NotificationsDAO();
        checkAccess();
        reloadExistingGrades();
        buildUI(owner);
    }
    //used for admin , previous one for insturctor
    public GradeEntryDialog(Window owner, int enrollmentId, boolean adminMode) {
        super(owner, "Enter Grades", ModalityType.APPLICATION_MODAL);
        this.enrollmentId = enrollmentId;
        this.parentPanel = null;
        this.adminMode = adminMode;
        this.instructorGradeService = null;
        this.adminGradeService = new AdminGradeService();
        this.queryService = new InstructorQueryService();
        this.notificationsDAO = new NotificationsDAO();
        checkAccess();
        reloadExistingGrades();
        buildUI(owner);
    }

    private void checkAccess() {

        if (!SessionManager.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "You must be logged in.");
            dispose();
            return;
        }
        String role = SessionManager.getCurrentUserRole().toUpperCase();
        // Instructor mode check
        if (!adminMode && !role.equals("INSTRUCTOR")) {
            JOptionPane.showMessageDialog(this, "Access denied — instructor only.");
            dispose();
            return;
        }
        // Admin mode check
        if (adminMode && !role.equals("ADMIN")) {
            JOptionPane.showMessageDialog(this,
                    "Access denied — admin only mode.");
            dispose();
            return;
        }
        // Instructor blocked during maintenance
        if (!adminMode && MaintenanceChecker.isMaintenanceOn()) {
            JOptionPane.showMessageDialog(this,
                    "System in maintenance — grade editing disabled.");
            dispose();
        }
    }

    private void reloadExistingGrades() {
        existingMap.clear();
        try {
            List<Grade> list = queryService.getGradesForEnrollment(enrollmentId);
            for (Grade g : list) {
                if (g.getComponent() != null)
                    existingMap.put(g.getComponent().toUpperCase(), g);
            }
        } catch (Exception ignored) {}
    }

    private void buildUI(Window owner) {
        setSize(520, 440);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel bg = new JPanel(new BorderLayout());
        bg.setBorder(new EmptyBorder(20, 20, 20, 20));
        bg.setBackground(new Color(245, 245, 245));
        add(bg);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(25, 35, 25, 35));
        bg.add(card, BorderLayout.CENTER);

        JLabel title = new JLabel("ENTER / UPDATE GRADES", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(52, 152, 219));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        card.add(Box.createVerticalStrut(20));
        compDropdown = new JComboBox<>(COMPONENTS);
        card.add(centerField("Component:", compDropdown));
        card.add(Box.createVerticalStrut(15));
        scoreField = new JTextField();
        card.add(centerField("Score (0–100):", scoreField));
        card.add(Box.createVerticalStrut(15));
        finalField = new JTextField();
        card.add(centerField("Final Grade (A/B/C/D/F):", finalField));
        card.add(Box.createVerticalStrut(25));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnRow.setOpaque(false);
        JButton saveBtn = styledBtn("Save");
        JButton computeBtn = styledBtn("Auto Compute");
        JButton closeBtn = styledBtn("Close");
        btnRow.add(saveBtn);
        btnRow.add(computeBtn);
        btnRow.add(closeBtn);
        card.add(btnRow);
        compDropdown.addActionListener(e -> loadExistingForComponent());
        saveBtn.addActionListener(e -> saveComponent());
        computeBtn.addActionListener(e -> computeFinal());
        closeBtn.addActionListener(e -> dispose());
        loadExistingForComponent();
    }

    private JPanel centerField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(new Dimension(300, 32));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(l);
        p.add(Box.createVerticalStrut(5));
        p.add(field);

        return p;
    }

    private JButton styledBtn(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(52, 152, 219));
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { b.setBackground(new Color(41, 128, 185)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { b.setBackground(new Color(52, 152, 219)); }
        });

        return b;
    }


    private void loadExistingForComponent() {
        String comp = (String) compDropdown.getSelectedItem();
        if (comp == null) return;

        Grade g = existingMap.get(comp);
        scoreField.setText(g != null ? String.valueOf(g.getScore()) : "");

        Grade finalRow = existingMap.get("FINAL");
        finalField.setText(finalRow != null ? finalRow.getFinalGrade() : "");
    }


    private void saveComponent() {
        String comp = (String) compDropdown.getSelectedItem();
        if (comp == null) return;
        String scoreStr = scoreField.getText().trim();
        String finalStr = finalField.getText().trim();
        boolean anyUpdate = false;
        try {
            if (!scoreStr.isEmpty()) {
                double score = Double.parseDouble(scoreStr);
                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(this, "Score must be 0–100");
                    return;
                }
                boolean ok;
                if (adminMode) ok = adminGradeService.addOrUpdateComponentGrade(enrollmentId, comp, score);
                else ok = instructorGradeService.addOrUpdateComponentGrade(enrollmentId, comp, score);

                if (!ok) throw new Exception("Failed to save component score.");
                anyUpdate = true;
            }
            if (!finalStr.isEmpty()) {
                String upper = finalStr.toUpperCase();
                if (!upper.matches("[ABCDF]")) {
                    JOptionPane.showMessageDialog(this, "Invalid final grade");
                    return;
                }

                boolean ok;
                if (adminMode) ok = adminGradeService.saveFinalGrade(enrollmentId, upper);
                else ok = instructorGradeService.saveFinalGrade(enrollmentId, upper);

                if (!ok) throw new Exception("Failed to save final grade.");
                anyUpdate = true;
            }

            if (anyUpdate) {
                Student s = queryService.getStudentForEnrollment(enrollmentId);
                if (s != null) {
                    Notification n = new Notification(
                            s.getUserId(),
                            null,
                            "Grade Updated",
                            "Your grade for " + comp + " has been updated."
                    );
                    notificationsDAO.addNotification(n);
                }
            }

            reloadExistingGrades();
            loadExistingForComponent();

            if (!adminMode && parentPanel != null) parentPanel.reloadTable();

            JOptionPane.showMessageDialog(this, "Saved.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    private void computeFinal() {
        try {
            String letter;

            if (adminMode)
                letter = adminGradeService.autoComputeFinalLetterGrade(enrollmentId);
            else
                letter = instructorGradeService.autoComputeFinalLetterGrade(enrollmentId);

            JOptionPane.showMessageDialog(this, "Final Grade: " + letter);

            reloadExistingGrades();
            loadExistingForComponent();

            if (!adminMode && parentPanel != null) parentPanel.reloadTable();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
