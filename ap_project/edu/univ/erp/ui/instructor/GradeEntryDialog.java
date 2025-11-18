package edu.univ.erp.ui.instructor;

import edu.univ.erp.service.InstructorGradeService;

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
        add(new JLabel("Final Grade (A/B/C... optional):"));
        add(finalGradeField);
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        add(saveBtn);
        add(cancelBtn);

        saveBtn.addActionListener(e -> {
            String component = componentField.getText().trim();
            String scoreStr = scoreField.getText().trim();
            String finalGrade = finalGradeField.getText().trim();

            if (component.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Component is required!");
                return;
            }
            // FINAL grade logic
            if (component.equalsIgnoreCase("FINAL")) {
                if (finalGrade.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Final grade must be entered!");
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

            // Component score logic
            if (scoreStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Score required for this component!");
                return;
            }

            try {
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
                JOptionPane.showMessageDialog(this, "Score must be a number!");
            }

        });

        cancelBtn.addActionListener(e -> dispose());
    }
}
