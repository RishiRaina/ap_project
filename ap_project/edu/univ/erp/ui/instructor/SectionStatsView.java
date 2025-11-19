package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.util.CSVutil;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import java.io.File;
import java.util.*;

public class SectionStatsView extends JPanel {

    private MainFrame mainFrame;
    private int sectionId;
    private InstructorQueryService queryService;

    public SectionStatsView(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;
        this.queryService = new InstructorQueryService();

        setLayout(new BorderLayout());

        // ROLE CHECK
        if (!SessionManager.isLoggedIn() ||
                !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied.", "Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        // Maintenance banner
        if (AccessControl.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.SOUTH);
        }

        // OWNERSHIP CHECK
        try {
            Section sec = queryService.getSection(sectionId);
            AccessControl.assertInstructorOwnsSection(SessionManager.getCurrentUserId(), sec.getInstructorId(), AccessControl.Actions.CLASS_STATS);
        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.INSTRUCTOR_DASH);
            return;
        }

        JLabel title = new JLabel("Class Stats for Section " + sectionId, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        add(scroll, BorderLayout.CENTER);

        // Load stats
        area.setText(generateStatsText());

        JPanel bottom = new JPanel();
        JButton exportCsv = new JButton("Export Stats CSV");
        JButton back = new JButton("Back");
        bottom.add(exportCsv);
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        exportCsv.addActionListener(e -> exportStatsCSV());
        back.addActionListener(e -> mainFrame.showScreen("instructor_stats_select_section"));
    }

    private String generateStatsText() {
        StringBuilder sb = new StringBuilder();

        try {
            List<Enrollment> enrollments = queryService.getSectionEnrollments(sectionId);
            int totalStudents = enrollments.size();
            Map<String, Integer> letterCount = new LinkedHashMap<>();
            Map<String, List<Double>> componentScores = new HashMap<>();
            int withFinal = 0;
            for (Enrollment e : enrollments) {
                List<Grade> grades = queryService.getGradesForEnrollment(e.getEnrollmentId());
                for (Grade g : grades) {
                    // Component scores
                    if (!g.getComponent().equalsIgnoreCase("FINAL")) {
                        componentScores.computeIfAbsent(g.getComponent(), k -> new ArrayList<>()).add(g.getScore());
                    }
                    // Final grade
                    if (g.getComponent().equalsIgnoreCase("FINAL") && g.getFinalGrade() != null) {
                        letterCount.merge(g.getFinalGrade(), 1, Integer::sum);
                        withFinal++;
                    }
                }
            }
            sb.append("Total Students: ").append(totalStudents).append("\n");
            sb.append("Students with Final Grade: ").append(withFinal).append("\n\n");
            sb.append("Letter Grade Distribution\n");
            for (String grade : letterCount.keySet()) {
                sb.append(String.format("%-5s : %d\n", grade, letterCount.get(grade)));
            }

            sb.append("\nComponent Statistics\n");
            for (String comp : componentScores.keySet()) {
                List<Double> list = componentScores.get(comp);
                double avg = list.stream().mapToDouble(a -> a).average().orElse(0);
                double min = list.stream().mapToDouble(a -> a).min().orElse(0);
                double max = list.stream().mapToDouble(a -> a).max().orElse(0);
                sb.append(String.format("%s -> Avg: %.2f   Min: %.2f   Max: %.2f\n", comp, avg, min, max));
            }
        } catch (AccessException ex) {
            sb.append("AccessError:\n").append(ex.getMessage());
        }
        return sb.toString();
    }


    private void exportStatsCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("section_" + sectionId + "_stats.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            List<String[]> rows = new ArrayList<>();
            String[] header = {"Statistic", "Value"};
            rows.add(new String[]{"Section ID", "" + sectionId});
            String[] lines = generateStatsText().split("\n");
            for (String line : lines) {
                rows.add(new String[]{line, ""});
            }
            boolean ok = CSVutil.writecsv(file, rows, header);
            if (ok)
                JOptionPane.showMessageDialog(this, "Stats CSV exported!");
            else
                JOptionPane.showMessageDialog(this, "Failed to export CSV.");
        }
    }
}
