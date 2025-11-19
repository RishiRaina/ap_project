package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorGradeService;
import edu.univ.erp.service.InstructorQueryService;

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

        if (!SessionManager.isLoggedIn() || !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Instructors only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        if (AccessControl.isMaintenanceOn() && !"ADMIN".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "System is under Maintenance — grade editing not allowed.", "Maintenance ON", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        //ownership
        try {
            InstructorQueryService qs = new InstructorQueryService();

            // get section using ID from parent panel (via getter)
            int secId = parentPanel.getSectionId();
            Section sec = qs.getSection(secId);

            if (sec == null || sec.getInstructorId() == null) {
                throw new AccessException("Section or instructor mapping not found.");
            }

            // Make sure logged-in instructor owns this section
            AccessControl.assertInstructorOwnsSection(SessionManager.getCurrentUserId(), sec.getInstructorId(), AccessControl.Actions.ENTER_SCORES);

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(4, 2, 10, 10));

        JTextField componentField = new JTextField();
        JTextField scoreField = new JTextField();
        JTextField finalGradeField = new JTextField();

        add(new JLabel("Component:"));
        add(componentField);

        add(new JLabel("Score:"));
        add(scoreField);

        add(new JLabel("Final Grade (A/B/C...)"));
        add(finalGradeField);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        add(saveBtn);
        add(cancelBtn);

        // ============ SAVE LOGIC ============
        saveBtn.addActionListener(e -> onSave(componentField, scoreField, finalGradeField));
        cancelBtn.addActionListener(e -> dispose());
    }


    private void onSave(JTextField compField, JTextField scoreField, JTextField finalField) {

        String component = compField.getText().trim();
        String scoreStr = scoreField.getText().trim();
        String finalGrade = finalField.getText().trim();
        if (component.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Component is required!");
            return;
        }
        try {
            // access check
            AccessControl.assertAllowedWithMaintenance(AccessControl.Role.INSTRUCTOR, AccessControl.Actions.ENTER_SCORES);

            // final grade
            if (component.equalsIgnoreCase("FINAL")) {
                if (finalGrade.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Final grade must be provided!");
                    return;
                }
                boolean ok = gradeService.saveFinalGrade(enrollmentId, finalGrade);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Final Grade Saved!");
                    dispose();
                    parentPanel.reloadTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Error saving final grade!");
                }
                return;
            }

            // ----- Regular component -----
            if (scoreStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Score required for component!");
                return;
            }

            double score = Double.parseDouble(scoreStr);

            boolean ok = gradeService.addOrUpdateComponentGrade(
                    enrollmentId,
                    component,
                    score
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Component Grade Saved!");
                dispose();
                parentPanel.reloadTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save grade.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Score must be a valid number!");
        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
