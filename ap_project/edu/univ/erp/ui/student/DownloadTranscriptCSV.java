package edu.univ.erp.ui.student;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.util.CSVutil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadTranscriptCSV extends JPanel {

    private MainFrame mainFrame;
    private EnrollmentDAO enrollmentDAO;
    private SectionDAO sectionDAO;
    private CourseDAO courseDAO;
    private GradeDAO gradeDAO;

    public DownloadTranscriptCSV(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        enrollmentDAO = new EnrollmentDAO();
        sectionDAO = new SectionDAO();
        courseDAO = new CourseDAO();
        gradeDAO = new GradeDAO();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Download Transcript (CSV)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // TABLE
        String[] cols = {"Section ID", "Course Code", "Course Title", "Final Grade"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // BUTTONS
        JButton exportBtn = new JButton("Download CSV");
        JButton backBtn = new JButton("Back");

        JPanel panel = new JPanel();
        panel.add(exportBtn);
        panel.add(backBtn);
        add(panel, BorderLayout.SOUTH);

        // Load data into table
        loadTranscript(model);

        // Back button
        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.STUDENT_DASH));

        // Export CSV
        exportBtn.addActionListener(e -> exportCSV());
    }

    private void loadTranscript(DefaultTableModel model) {
        model.setRowCount(0);

        int studentId = SessionManager.getCurrentUserId();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);

        for (Enrollment e : enrollments) {

            // Step 1: Get section
            Section s = sectionDAO.getSectionById(e.getSectionId());

            // Step 2: Get course
            Course c = (s != null) ? courseDAO.getCourseById(s.getCourseId()) : null;

            // Step 3: Get final grade
            List<Grade> grades = gradeDAO.getGradesByEnrollment(e.getEnrollmentId());

            String finalGrade = "N/A";
            for (Grade g : grades) {
                if (g.getFinalGrade() != null) {
                    finalGrade = g.getFinalGrade();
                    break;
                }
            }

            model.addRow(new Object[] {
                    e.getSectionId(),
                    (c != null) ? c.getCode() : "N/A",
                    (c != null) ? c.getTitle() : "N/A",
                    finalGrade
            });
        }
    }

    private void exportCSV() {
        int studentId = SessionManager.getCurrentUserId();
        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);

        List<String[]> rows = new ArrayList<>();
        rows.add(new String[] {"Section ID", "Course Code", "Course Title", "Final Grade"});

        for (Enrollment e : enrollments) {

            Section s = sectionDAO.getSectionById(e.getSectionId());
            Course c = (s != null) ? courseDAO.getCourseById(s.getCourseId()) : null;

            List<Grade> grades = gradeDAO.getGradesByEnrollment(e.getEnrollmentId());

            String finalGrade = "N/A";
            for (Grade g : grades) {
                if (g.getFinalGrade() != null) {
                    finalGrade = g.getFinalGrade();
                    break;
                }
            }

            rows.add(new String[] {
                    String.valueOf(e.getSectionId()),
                    (c != null) ? c.getCode() : "N/A",
                    (c != null) ? c.getTitle() : "N/A",
                    finalGrade
            });
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("transcript.csv"));
        int result = chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (CSVutil.writecsv(file.getAbsolutePath(), rows)) {
                JOptionPane.showMessageDialog(this,
                        "Transcript CSV downloaded!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to save CSV.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
