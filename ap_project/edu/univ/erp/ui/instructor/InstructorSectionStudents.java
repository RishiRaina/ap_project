package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.*;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.common.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorSectionStudents extends JPanel {

    private MainFrame mainFrame;
    private int sectionId;
    private InstructorQueryService queryserv;
    //keep a reference to the table
    private JTable studentTable;

    public InstructorSectionStudents(MainFrame mainFrame, int sectionId) {
        this.mainFrame = mainFrame;
        this.sectionId = sectionId;
        this.queryserv = new InstructorQueryService();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Students in Section " + sectionId, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        String[] cols = {
                "Enrollment ID",
                "Roll No",
                "Program",
                "Year",
                "Final Grade",
                "Action"
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5;     // Only Action column editable (button)
            }
        };

        // 🔴 Save table in a field
        studentTable = new JTable(model);
        studentTable.setRowHeight(28);

        studentTable.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        studentTable.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Load initial data
        loadStudents(model);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        backBtn.addActionListener(e ->
                mainFrame.showScreen("instructor_my_sections")
        );

        JPanel bottom = new JPanel();
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    // Load students of this section
    public void loadStudents(DefaultTableModel model) {
        model.setRowCount(0);
        List<Enrollment> enrollments = queryserv.getSectionEnrollments(sectionId);
        for (Enrollment e : enrollments) {
            Student s = queryserv.getStudentForEnrollment(e.getEnrollmentId());
            if (s == null) continue;
            //to locate final grade,- if no
            List<Grade> gradeList = queryserv.getGradesForEnrollment(e.getEnrollmentId());
            String finalGrade = "-";
            for (Grade g : gradeList) {
                if (g.getFinalGrade() != null && !g.getFinalGrade().isBlank()) {
                    finalGrade = g.getFinalGrade();
                    break;
                }
            }
            model.addRow(new Object[]{e.getEnrollmentId(), s.getRollNo(), s.getProgram(), s.getYear(), finalGrade, "Enter Grades"});
        }
    }

    // Called by ButtonEditor when "Enter Grades" clicked
    public void enterGrades(int row) {
        int enrollmentId = (Integer) studentTable.getValueAt(row, 0);
        new GradeEntryDialog(SwingUtilities.getWindowAncestor(this), enrollmentId, this).setVisible(true);
    }

    // 🔴 This is what GradeEntryDialog calls after saving grades
    public void reloadTable() {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        loadStudents(model);
    }
}
