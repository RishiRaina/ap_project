package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorGradeService;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.access.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class GradeEntryDialog extends JDialog {

    private final int enrollmentId;
    private final InstructorGradeService gradeService;
    private final InstructorSectionStudents parentPanel;

    // Fixed scheme
    private static final String[] COMPONENTS = {
            "ASSIGNMENTS", "QUIZZES", "PROJECT", "MID", "END"
    };

    private JComboBox<String> compDropdown;
    private JTextField scoreField;
    private JTextField finalField;

    // cache of existing grades for this enrollment
    private HashMap<String, Grade> existingMap = new HashMap<>();

    public GradeEntryDialog(Window owner, int enrollmentId, InstructorSectionStudents parentPanel) {

        super(owner, "Enter Grades", ModalityType.APPLICATION_MODAL);

        this.enrollmentId = enrollmentId;
        this.parentPanel = parentPanel;
        this.gradeService = new InstructorGradeService();

        // ROLE CHECK
        if (!SessionManager.isLoggedIn()
                || !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied.");
            dispose();
            return;
        }

        // Maintenance block
        if (MaintenanceChecker.isMaintenanceOn()
                && !"ADMIN".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this,
                    "System in Maintenance — Grades cannot be edited.");
            dispose();
            return;
        }

        // Ownership check
        try {
            InstructorQueryService qs = new InstructorQueryService();
            int secId = parentPanel.getSectionId();
            Section sec = qs.getSection(secId);

            AccessControl.assertInstructorOwnsSection(
                    SessionManager.getCurrentUserId(),
                    sec.getInstructorId(),
                    AccessControl.Actions.ENTER_SCORES
            );
        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            dispose();
            return;
        }

        // Load existing grades into map
        reloadExistingGrades();

        buildUI(owner);
    }

    // --------- Load all grades for this enrollment into existingMap ---------
    private void reloadExistingGrades() {
        existingMap.clear();
        try {
            List<Grade> list = new InstructorQueryService()
                    .getGradesForEnrollment(enrollmentId);
            for (Grade g : list) {
                if (g.getComponent() != null) {
                    existingMap.put(g.getComponent().toUpperCase(), g);
                }
            }
        } catch (Exception ignored) {}
    }

    private void buildUI(Window owner) {
        setSize(480, 320);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(5, 2, 10, 10));

        // Dropdown
        add(new JLabel("Component:"));
        compDropdown = new JComboBox<>(COMPONENTS);
        add(compDropdown);

        // Score field
        add(new JLabel("Score (0–100):"));
        scoreField = new JTextField();
        add(scoreField);

        // Final grade display / manual override
        add(new JLabel("Final Grade (optional, A/B/C/D/F):"));
        finalField = new JTextField();
        add(finalField);

        JButton saveBtn = new JButton("Save Component / Final");
        JButton autoBtn = new JButton("Compute Final Grade");
        JButton cancelBtn = new JButton("Close");

        add(saveBtn);
        add(autoBtn);
        add(cancelBtn);

        // Auto-fill when dropdown changes
        compDropdown.addActionListener(e -> loadExistingForComponent());

        // Save component and/or final manual override
        saveBtn.addActionListener(e -> saveComponent());

        // Auto compute final letter grade
        autoBtn.addActionListener(e -> computeFinal());

        // Close dialog
        cancelBtn.addActionListener(e -> dispose());

        // Initialize fields for first component
        loadExistingForComponent();
    }

    private void loadExistingForComponent() {
        String comp = (String) compDropdown.getSelectedItem();
        if (comp == null) return;

        if (existingMap.containsKey(comp)) {
            Grade g = existingMap.get(comp);
            scoreField.setText(String.valueOf(g.getScore()));
        } else {
            scoreField.setText("");
        }

        // For FINAL we always show from FINAL row, not per-component
        Grade finalRow = existingMap.get("FINAL");
        if (finalRow != null && finalRow.getFinalGrade() != null) {
            finalField.setText(finalRow.getFinalGrade());
        } else {
            finalField.setText("");
        }
    }

    private void saveComponent() {
        String comp = (String) compDropdown.getSelectedItem();
        String scoreStr = scoreField.getText().trim();
        String finalStr = finalField.getText().trim();

        if (comp == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a component.");
            return;
        }

        try {
            // 1) Save numeric component score if provided
            if (!scoreStr.isEmpty()) {
                double score;
                try {
                    score = Double.parseDouble(scoreStr);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this,
                            "Score must be a valid number.");
                    return;
                }
                // basic UI-side bounds check
                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(this,
                            "Score must be between 0 and 100.");
                    return;
                }

                gradeService.addOrUpdateComponentGrade(enrollmentId, comp, score);
            }

            // 2) Save manual final letter grade if provided
            if (!finalStr.isEmpty()) {
                String upper = finalStr.trim().toUpperCase();
                if (!upper.matches("[ABCDF]")) {
                    JOptionPane.showMessageDialog(this,
                            "Final Grade must be one of A, B, C, D, or F.");
                    return;
                }
                gradeService.saveFinalGrade(enrollmentId, upper);
            }

            // Reload local copy + UI
            reloadExistingGrades();
            loadExistingForComponent();
            parentPanel.reloadTable();

            // Check if all 5 components exist
            if (hasAllFiveComponents()) {
                JOptionPane.showMessageDialog(this,
                        "All 5 components are now entered. You can Compute Final Grade.");
            } else {
                JOptionPane.showMessageDialog(this, "Saved.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private boolean hasAllFiveComponents() {
        for (String comp : COMPONENTS) {
            if (!existingMap.containsKey(comp)) {
                return false;
            }
        }
        return true;
    }

    private void computeFinal() {
        try {
            String letter = gradeService.autoComputeFinalLetterGrade(enrollmentId);
            JOptionPane.showMessageDialog(this,
                    "Final Grade Computed: " + letter);
            reloadExistingGrades();
            loadExistingForComponent();
            parentPanel.reloadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
