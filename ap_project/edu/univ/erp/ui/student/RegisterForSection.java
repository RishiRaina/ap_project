package edu.univ.erp.ui.student;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class RegisterForSection extends JPanel {

    private MainFrame mainFrame;
    private SectionDAO sectionDAO;
    private CourseDAO courseDAO;
    private EnrollmentDAO enrollmentDAO;

    public RegisterForSection(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.sectionDAO = new SectionDAO();
        this.courseDAO = new CourseDAO();
        this.enrollmentDAO = new EnrollmentDAO();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Register for a Section", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);


        // Table model

        String[] cols = {"Section ID", "Course Code", "Course Title", "Instructor ID",
                "Day/Time", "Room", "Capacity", "Deadline"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        JTable table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // register or back button in a panel
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.add(registerBtn);
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        //load data
        loadSections(model);

        // event: register button clicked
        registerBtn.addActionListener(e -> {

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a section first.");
                return;
            }

            int sectionId = (Integer) model.getValueAt(selectedRow, 0);
            int studentId = SessionManager.getCurrentUserId();

            // Create enrollment object
            Enrollment enroll = new Enrollment(studentId, sectionId);

            boolean ok = enrollmentDAO.enrollStudent(enroll);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Successfully registered!");
                loadSections(model); // refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Could not register (maybe already registered or capacity full).");
            }
        });

        // EVENT: BACK
        backBtn.addActionListener(e ->
                mainFrame.showScreen(MainFrame.STUDENT_DASH)
        );
    }

    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);

        List<Section> list = sectionDAO.getAllSections();

        for (Section s : list) {
            Course c = courseDAO.getCourseById(s.getCourseId());

            model.addRow(new Object[]{
                    s.getSectionId(),
                    (c != null ? c.getCode() : "N/A"),
                    (c != null ? c.getTitle() : "N/A"),
                    s.getInstructorId(),
                    s.getDayTime(),
                    s.getRoom(),
                    s.getCapacity(),
                    s.getRegistrationDeadline()
            });
        }
    }
}
