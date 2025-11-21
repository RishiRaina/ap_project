package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorGradeService;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.instructor.InstructorSectionStudents;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.access.*;

import javax.swing.*;
import java.awt.*;

public class GradeEntryDialog extends JDialog {

    private int enrollmentId;
    private InstructorGradeService gradeService;
    private InstructorSectionStudents parentPanel;

    public GradeEntryDialog(Window owner, int enrollmentId, InstructorSectionStudents parentPanel) {
        super(owner, "Enter Grades", ModalityType.APPLICATION_MODAL);
        this.enrollmentId = enrollmentId;
        this.parentPanel = parentPanel;
        this.gradeService = new InstructorGradeService();

        // ROLE CHECK
        if (!SessionManager.isLoggedIn() || !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Maintenance check
        if (MaintenanceChecker.isMaintenanceOn() && !"ADMIN".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "System is under Maintenance — grade editing not allowed.", "Maintenance ON", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        // Ownership check
        try {
            InstructorQueryService qs = new InstructorQueryService();
            int secId = parentPanel.getSectionId();
            Section sec = qs.getSection(secId);

            if (sec == null) throw new AccessException("Section not found.");

            AccessControl.assertInstructorOwnsSection(SessionManager.getCurrentUserId(), sec.getInstructorId(), AccessControl.Actions.ENTER_SCORES);

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // ui code
        setSize(450, 280);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(5, 2, 10, 10));

        JTextField componentField = new JTextField();
        JTextField scoreField = new JTextField();
        JTextField finalGradeField = new JTextField();

        add(new JLabel("Component (e.g., ASSIGNMENTS):"));
        add(componentField);

        add(new JLabel("Score:"));
        add(scoreField);

        add(new JLabel("Final Grade (manual, optional):"));
        add(finalGradeField);

        JButton saveBtn = new JButton("Save Component");
        JButton autoBtn = new JButton("Compute Final Grade");  // NEW BUTTON
        JButton cancelBtn = new JButton("Cancel");

        add(saveBtn);
        add(autoBtn);
        add(cancelBtn);

        // this is the manual option ( if the instructor wants to insert the grade manually)
        saveBtn.addActionListener(e -> {
            String comp = componentField.getText().trim();
            String scoreStr = scoreField.getText().trim();
            String finalGrade = finalGradeField.getText().trim();

            if (comp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Component required!");
                return;
            }

            try {
                // manual final
                if (comp.equalsIgnoreCase("FINAL")) {
                    if (finalGrade.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Final grade required.");
                        return;
                    }
                    gradeService.saveFinalGrade(enrollmentId, finalGrade);
                } else {
                    double score = Double.parseDouble(scoreStr);
                    gradeService.addOrUpdateComponentGrade(enrollmentId, comp, score);
                }
                JOptionPane.showMessageDialog(this, "Saved!");
                parentPanel.reloadTable();
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        // this is the autocomplete button
        autoBtn.addActionListener(e -> {
            try {
                String letter = gradeService.autoComputeFinalLetterGrade(enrollmentId);
                JOptionPane.showMessageDialog(this, "Final Grade Computed: " + letter);
                parentPanel.reloadTable();
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dispose());
    }
}
