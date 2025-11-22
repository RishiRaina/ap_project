package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.util.PDFutil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class SectionStatsView extends JPanel {

    private final MainFrame mainFrame;
    private final int sectionId;
    private final InstructorQueryService queryService;

    // ------- Data container for stats -------
    private static class StatsData {
        int totalStudents;
        int studentsWithFinal;
        Map<String, Integer> letterCount = new TreeMap<>();
        Map<String, ComponentSummary> componentSummaries = new TreeMap<>();
    }

    private static class ComponentSummary {
        double avg;
        double min;
        double max;

        public ComponentSummary(double avg, double min, double max) {
            this.avg = avg;
            this.min = min;
            this.max = max;
        }
    }

    // ------- Rounded card panel -------
    class RoundedPanel extends JPanel {
        private final int cornerRadius = 20;

        public RoundedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    public SectionStatsView(MainFrame mainFrame, int sectionId) {

        this.mainFrame = mainFrame;
        this.sectionId = sectionId;
        this.queryService = new InstructorQueryService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ---------------- ROLE CHECK ----------------
        if (!SessionManager.isLoggedIn() ||
                !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Access Denied.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        // ---------------- MAINTENANCE BANNER ----------------
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY",
                    SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));

            bannerPanel.add(banner, BorderLayout.CENTER);
            add(bannerPanel, BorderLayout.NORTH);
        }

        // ---------------- OWNERSHIP CHECK + STATS ----------------
        StatsData stats;
        try {
            Section sec = queryService.getSection(sectionId);

            AccessControl.assertInstructorOwnsSection(
                    SessionManager.getCurrentUserId(),
                    sec.getInstructorId(),
                    AccessControl.Actions.CLASS_STATS
            );

            // compute stats once
            stats = computeStats();

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
            mainFrame.refreshInstructorDashboard();
            return;
        }

        // ---------------- HEADER ----------------
        JLabel header = new JLabel("Class Statistics — Section " + sectionId, SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(new Color(52, 152, 219));
        header.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(header, BorderLayout.PAGE_START);

        // ---------------- STATS CARD (CENTER) ----------------
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 40, 25, 40));

        JTextPane statsPane = new JTextPane();
        statsPane.setContentType("text/html");
        statsPane.setEditable(false);
        statsPane.setBorder(new EmptyBorder(15, 20, 15, 20));
        statsPane.setText(generateStatsHTML(stats));

        JScrollPane scroll = new JScrollPane(statsPane);
        card.add(scroll, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        // ---------------- FOOTER BUTTONS ----------------
        JButton exportBtn = new JButton("Export Stats PDF");
        JButton backBtn = new JButton("Back");

        styleButton(exportBtn, new Color(52, 152, 219), new Color(41, 128, 185));
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        StatsData finalStats = stats;

        exportBtn.addActionListener(e -> exportPDF(finalStats));

        backBtn.addActionListener(e -> {
            InstructorStatsSectionSelect sel = new InstructorStatsSectionSelect(mainFrame);

            // If we are inside InstructorDashboard, replace its center panel
            InstructorDashboard dash = (InstructorDashboard)
                    SwingUtilities.getAncestorOfClass(InstructorDashboard.class, this);

            if (dash != null) {
                dash.setCenter(sel);
            } else {
                // fallback: use mainFrame card layout
                mainFrame.addScreen("instructor_stats_select_section", sel);
                mainFrame.showScreen("instructor_stats_select_section");
            }
        });

        JPanel footer = new JPanel();
        footer.setBackground(new Color(245, 245, 245));
        footer.add(exportBtn);
        footer.add(backBtn);

        add(footer, BorderLayout.SOUTH);
    }

    // ---------------- COMPUTE STATS ----------------
    private StatsData computeStats() throws AccessException {

        StatsData data = new StatsData();

        List<Enrollment> enrollments = queryService.getSectionEnrollments(sectionId);
        data.totalStudents = enrollments.size();

        Map<String, List<Double>> rawComponentScores = new HashMap<>();

        for (Enrollment e : enrollments) {
            List<Grade> grades = queryService.getGradesForEnrollment(e.getEnrollmentId());

            for (Grade g : grades) {
                String compName = g.getComponent();

                // numeric component scores (exclude FINAL)
                if (!"FINAL".equalsIgnoreCase(compName)) {
                    rawComponentScores
                            .computeIfAbsent(compName, k -> new ArrayList<>())
                            .add(g.getScore());
                }

                // final letter grade
                if ("FINAL".equalsIgnoreCase(compName) &&
                        g.getFinalGrade() != null &&
                        !g.getFinalGrade().isBlank()) {

                    data.letterCount.merge(g.getFinalGrade(), 1, Integer::sum);
                    data.studentsWithFinal++;
                }
            }
        }

        // build avg/min/max per component
        for (Map.Entry<String, List<Double>> entry : rawComponentScores.entrySet()) {
            String comp = entry.getKey();
            List<Double> list = entry.getValue();

            double avg = list.stream().mapToDouble(a -> a).average().orElse(0);
            double min = list.stream().mapToDouble(a -> a).min().orElse(0);
            double max = list.stream().mapToDouble(a -> a).max().orElse(0);

            data.componentSummaries.put(comp, new ComponentSummary(avg, min, max));
        }

        return data;
    }

    // ---------------- HTML FOR ON-SCREEN VIEW ----------------
    private String generateStatsHTML(StatsData stats) {

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Segoe UI; font-size: 14px;'>");

        // Section Summary
        sb.append("<h2 style='color:#3498db;'>Section Summary</h2>");
        sb.append("<b>Total Students:</b> ").append(stats.totalStudents).append("<br>");
        sb.append("<b>Students with Final Grade:</b> ").append(stats.studentsWithFinal).append("<br><br>");

        // Final Grade Distribution
        sb.append("<h2 style='color:#3498db;'>Final Grade Distribution</h2>");
        if (stats.letterCount.isEmpty()) {
            sb.append("<i>No final grades entered yet.</i><br><br>");
        } else {
            for (String grade : stats.letterCount.keySet()) {
                sb.append("<b>").append(grade).append("</b>: ")
                        .append(stats.letterCount.get(grade))
                        .append("<br>");
            }
            sb.append("<br>");
        }

        // Component Statistics
        sb.append("<h2 style='color:#3498db;'>Component Scores</h2>");
        if (stats.componentSummaries.isEmpty()) {
            sb.append("<i>No component scores entered yet.</i>");
        } else {
            for (Map.Entry<String, ComponentSummary> entry : stats.componentSummaries.entrySet()) {
                String comp = entry.getKey();
                ComponentSummary cs = entry.getValue();

                sb.append("<b>").append(comp).append("</b> — ")
                        .append(String.format("Avg: %.2f | Min: %.2f | Max: %.2f<br>",
                                cs.avg, cs.min, cs.max));
            }
        }

        sb.append("</body></html>");
        return sb.toString();
    }

    // ---------------- PLAIN TEXT FOR PDF ----------------
    private String generateStatsPlainText(StatsData stats) {

        StringBuilder sb = new StringBuilder();

        sb.append("Class Statistics — Section ").append(sectionId).append("\n");
        sb.append("====================================\n\n");

        sb.append("Section Summary\n");
        sb.append("---------------------------\n");
        sb.append("Total Students: ").append(stats.totalStudents).append("\n");
        sb.append("Students with Final Grade: ").append(stats.studentsWithFinal).append("\n\n");

        sb.append("Final Grade Distribution\n");
        sb.append("---------------------------\n");
        if (stats.letterCount.isEmpty()) {
            sb.append("No final grades entered yet.\n\n");
        } else {
            for (String grade : stats.letterCount.keySet()) {
                sb.append(grade).append(" : ")
                        .append(stats.letterCount.get(grade))
                        .append("\n");
            }
            sb.append("\n");
        }

        sb.append("Component Scores\n");
        sb.append("---------------------------\n");
        if (stats.componentSummaries.isEmpty()) {
            sb.append("No component scores entered yet.\n");
        } else {
            for (Map.Entry<String, ComponentSummary> entry : stats.componentSummaries.entrySet()) {
                String comp = entry.getKey();
                ComponentSummary cs = entry.getValue();

                sb.append(comp).append(" -> ")
                        .append(String.format("Avg: %.2f, Min: %.2f, Max: %.2f",
                                cs.avg, cs.min, cs.max))
                        .append("\n");
            }
        }

        return sb.toString();
    }

    // ---------------- PDF EXPORT ----------------
    private void exportPDF(StatsData stats) {

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("section_" + sectionId + "_stats.pdf"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();
            String body = generateStatsPlainText(stats);

            // your updated util method: (File, int sectionId, String body)
            boolean ok = PDFutil.writeSectionStatsPDF(file, sectionId, body);

            JOptionPane.showMessageDialog(
                    this,
                    ok ? "Stats PDF exported!" : "Failed to export PDF."
            );
        }
    }

    // ---------------- STYLE BUTTON ----------------
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
