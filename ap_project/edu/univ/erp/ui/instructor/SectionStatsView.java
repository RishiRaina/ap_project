package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.*;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.util.CSVutil;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class SectionStatsView extends JPanel {

    private MainFrame mainFrame;
    private int sectionId;
    private InstructorQueryService queryService;

    public SectionStatsView(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;
        this.queryService = new InstructorQueryService();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Class Stats for Section " + sectionId, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));

        add(title, BorderLayout.NORTH);

        // made a display area for displaying all the stats of hte section selected
        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);

        add(scroll, BorderLayout.CENTER);

        // Load stats into text area ( function defined below )
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

                // Final grades
                if (g.getComponent().equalsIgnoreCase("FINAL") && g.getFinalGrade() != null) {
                    letterCount.merge(g.getFinalGrade(), 1, Integer::sum);
                    withFinal++;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total Students: ").append(totalStudents).append("\n");
        sb.append("Students with Final Grade: ").append(withFinal).append("\n\n");
        sb.append("Letter Grade Distribution \n");
        for (String grade : letterCount.keySet()) {
            sb.append(String.format("%-5s : %d\n", grade, letterCount.get(grade)));
        }

        sb.append("\n=== Component Statistics ===\n");

        for (String comp : componentScores.keySet()) {

            List<Double> list = componentScores.get(comp);
            double avg = list.stream().mapToDouble(a -> a).average().orElse(0);
            double min = list.stream().mapToDouble(a -> a).min().orElse(0);
            double max = list.stream().mapToDouble(a -> a).max().orElse(0);

            sb.append(String.format(
                    "%s -> Avg: %.2f  Min: %.2f  Max: %.2f\n",
                    comp, avg, min, max
            ));
        }

        return sb.toString();
    }

    //export stats csv
    private void exportStatsCSV() {

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("section_" + sectionId + "_stats.csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            List<String[]> rows = new ArrayList<>();
            rows.add(new String[]{"Statistic", "Value"});
            rows.add(new String[]{"Section ID", String.valueOf(sectionId)});
            String[] textLines = generateStatsText().split("\n");
            for (String line : textLines) {
                rows.add(new String[]{line, ""});
            }
            String[] header = {"Stat", "Value"};
            boolean ok = CSVutil.writecsv(file, rows, header);

            if (ok)
                JOptionPane.showMessageDialog(this, "Stats CSV exported!");
            else
                JOptionPane.showMessageDialog(this, "Failed to export CSV.");
        }
    }
}
