package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
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

        if (!SessionManager.isLoggedIn() || !"INSTRUCTOR".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Instructors only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        // Maintenance banner
        JLabel banner=null;
        if (MaintenanceChecker.isMaintenanceOn()) {
            banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.SOUTH);
        }

        JLabel title = new JLabel("Export Grades (CSV)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        if(banner!=null){
            add(title,BorderLayout.CENTER);
        }
        else{
            add(title,BorderLayout.NORTH);
        }

        JPanel center = new JPanel(new GridLayout(3, 1, 10, 10));

        JLabel selectLabel = new JLabel("Select a Section to Export:", SwingConstants.CENTER);
        JComboBox<String> sectionBox = new JComboBox<>();
        loadSectionChoices(sectionBox);

        JButton exportBtn = new JButton("Download CSV");

        center.add(selectLabel);
        center.add(sectionBox);
        center.add(exportBtn);
        add(center, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> mainFrame.refreshInstructorDashboard());

        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // Export click
        exportBtn.addActionListener(e -> {

            // maintenance not blocked for INSTRUCTORS (CSV export is allowed — non-mutating action)
            String selected = (String) sectionBox.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "No section selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int sectionId = Integer.parseInt(selected.split(" - ")[0].trim());
            try {
                exportGrades(sectionId);
            } catch (AccessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadSectionChoices(JComboBox<String> box) {
        try {
            int instructorId = SessionManager.getCurrentUserId();
            List<Section> list = queryService.getMySections(instructorId);

            for (Section s : list) {
                box.addItem(s.getSectionId() + " - " + s.getDayTime());
            }
        } catch (Exception ignored) {}
    }

    private void exportGrades(int sectionId) throws AccessException {
        // ownership check if instructor owns the section
        Section sec = queryService.getSection(sectionId);
        AccessControl.assertInstructorOwnsSection(SessionManager.getCurrentUserId(), sec.getInstructorId(),AccessControl.Actions.EXPORT_GRADES_CSV);

        // Load data
        List<Enrollment> enrollments = queryService.getSectionEnrollments(sectionId);
        List<String[]> rows = new ArrayList<>();
        String[] header = {"EnrollmentID", "RollNo", "Component", "Score", "FinalGrade"};

        for (Enrollment e : enrollments) {
            Student st = queryService.getStudentForEnrollment(e.getEnrollmentId());
            List<Grade> grades = queryService.getGradesForEnrollment(e.getEnrollmentId());

            if (grades.isEmpty()) {
                rows.add(new String[]{"" + e.getEnrollmentId(), st != null ? st.getRollNo() : "N/A", "-", "-", "-"});
            } else {
                for (Grade g : grades) {
                    rows.add(new String[]{"" + e.getEnrollmentId(), st != null ? st.getRollNo() : "N/A", g.getComponent(), "" + g.getScore(), g.getFinalGrade() != null ? g.getFinalGrade() : ""});
                }
            }
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("section_" + sectionId + "_grades.csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            boolean ok = CSVutil.writecsv(file, rows, header);
            if (ok)
                JOptionPane.showMessageDialog(this, "Grades CSV exported!");
            else
                JOptionPane.showMessageDialog(this, "Failed to export CSV.");
        }
    }
}
