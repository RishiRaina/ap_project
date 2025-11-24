package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.InstructorGradeService;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.access.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class GradeEntryDialog extends JDialog {

    private final int enrollmentId;
    private final InstructorGradeService gradeService;
    private final InstructorSectionStudents parentPanel;

    private static final String[] COMPONENTS = {
            "ASSIGNMENTS", "QUIZZES", "PROJECT", "MID", "END"
    };

    private JComboBox<String> compDropdown;
    private JTextField scoreField;
    private JTextField finalField;

    private HashMap<String, Grade> existingMap = new HashMap<>();

    public GradeEntryDialog(Window owner, int enrollmentId, InstructorSectionStudents parentPanel) {
        super(owner, "Enter Grades", ModalityType.APPLICATION_MODAL);

        this.enrollmentId = enrollmentId;
        this.parentPanel = parentPanel;
        this.gradeService = new InstructorGradeService();

        // SECURITY CHECKS
        if (!SessionManager.isLoggedIn()
                || !"INSTRUCTOR".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied.");
            dispose();
            return;
        }

        if (MaintenanceChecker.isMaintenanceOn()
                && !"ADMIN".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this,
                    "System in Maintenance — Grades cannot be edited.");
            dispose();
            return;
        }

        reloadExistingGrades();
        buildUI(owner);
    }

    private void reloadExistingGrades() {
        existingMap.clear();
        try {
            List<Grade> list =
                    new InstructorQueryService().getGradesForEnrollment(enrollmentId);
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

        // centered component dropdown
        compDropdown = new JComboBox<>(COMPONENTS);
        card.add(centerField("Component:", compDropdown));
        card.add(Box.createVerticalStrut(15));

        scoreField = new JTextField();
        card.add(centerField("Score (0–100):", scoreField));
        card.add(Box.createVerticalStrut(15));

        finalField = new JTextField();
        card.add(centerField("Final Grade (A/B/C/D/F):", finalField));
        card.add(Box.createVerticalStrut(25));

        JPanel btnRow = new JPanel();
        btnRow.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
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


    // Small helper: Label + input stacked neatly
    private JPanel labelField(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    // Nice rounded buttons
    private JButton styledBtn(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(52, 152, 219));
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(52, 152, 219));
            }
        });

        return b;
    }

    private void loadExistingForComponent() {
        String comp = (String) compDropdown.getSelectedItem();
        if (comp == null) return;

        Grade g = existingMap.get(comp);
        scoreField.setText(g != null ? String.valueOf(g.getScore()) : "");

        Grade finalRow = existingMap.get("FINAL");
        finalField.setText(finalRow != null && finalRow.getFinalGrade() != null
                ? finalRow.getFinalGrade()
                : "");
    }

    private void saveComponent() {
        String comp = (String) compDropdown.getSelectedItem();
        String scoreStr = scoreField.getText().trim();
        String finalStr = finalField.getText().trim();

        if (comp == null) {
            JOptionPane.showMessageDialog(this, "Select a component.");
            return;
        }

        try {
            if (!scoreStr.isEmpty()) {
                double score;
                try { score = Double.parseDouble(scoreStr); }
                catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Score must be numeric.");
                    return;
                }

                if (score < 0 || score > 100) {
                    JOptionPane.showMessageDialog(this, "Score must be 0–100.");
                    return;
                }

                gradeService.addOrUpdateComponentGrade(enrollmentId, comp, score);
            }

            if (!finalStr.isEmpty()) {
                String upper = finalStr.toUpperCase();
                if (!upper.matches("[ABCDF]")) {
                    JOptionPane.showMessageDialog(this,
                            "Final grade must be A, B, C, D, or F.");
                    return;
                }
                gradeService.saveFinalGrade(enrollmentId, upper);
            }

            reloadExistingGrades();
            loadExistingForComponent();
            parentPanel.reloadTable();

            JOptionPane.showMessageDialog(this, "Saved successfully.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void computeFinal() {
        try {
            String letter = gradeService.autoComputeFinalLetterGrade(enrollmentId);

            JOptionPane.showMessageDialog(
                    this,
                    "Final Grade Computed: " + letter
            );

            reloadExistingGrades();
            loadExistingForComponent();
            parentPanel.reloadTable();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    private JPanel centerField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(new Dimension(300, 32));
        field.setPreferredSize(new Dimension(300, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(l);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);

        return panel;
    }

}
