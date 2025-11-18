package edu.univ.erp.ui.instructor;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.*;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.util.CSVutil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExportGrades extends JPanel {

    private MainFrame mainFrame;
    private InstructorQueryService queryService;

    public ExportGrades(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new InstructorQueryService();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Export Grades (CSV)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel selectLabel = new JLabel("Select a Section to Export Grades:");
        selectLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JComboBox<String> sectionBox = new JComboBox<>();
        loadSectionChoices(sectionBox);

        JButton exportBtn = new JButton("Download CSV");

        center.add(selectLabel);
        center.add(sectionBox);
        center.add(exportBtn);

        add(center, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainFrame.showScreen("instructor_dashboard"));

        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // EXPORT ACTION
        exportBtn.addActionListener(e -> {
            String selected = (String) sectionBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "No section selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int sectionId = Integer.parseInt(selected.split(" - ")[0].trim());
            exportGrades(sectionId);
        });
    }

    //function to load sections to chososee from
    private void loadSectionChoices(JComboBox<String> box) {
        int instructorId = SessionManager.getCurrentUserId();
        List<Section> list = queryService.getMySections(instructorId);
        for (Section s : list) {
            box.addItem(s.getSectionId() + " - " + s.getDayTime());
        }
    }

    //export for one section
    private void exportGrades(int sectionId) {

        List<Enrollment> enrollments = queryService.getSectionEnrollments(sectionId);
        List<String[]> rows = new ArrayList<>();
        String[] header = {"EnrollmentID", "RollNo", "Component", "Score", "FinalGrade"};
        for (Enrollment e : enrollments) {
            Student st = queryService.getStudentForEnrollment(e.getEnrollmentId());
            List<Grade> grades = queryService.getGradesForEnrollment(e.getEnrollmentId());
            if (grades.isEmpty()) {
                rows.add(new String[]{String.valueOf(e.getEnrollmentId()), st != null ? st.getRollNo() : "N/A", "-", "-", "-"});
            }
            for (Grade g : grades) {
                rows.add(new String[]{String.valueOf(e.getEnrollmentId()), st != null ? st.getRollNo() : "N/A", g.getComponent(), String.valueOf(g.getScore()), g.getFinalGrade() == null ? "" : g.getFinalGrade()});
            }
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("section_" + sectionId + "_grades.csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            boolean ok = CSVutil.writecsv(file, rows, header);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Grades CSV exported!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export CSV.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
