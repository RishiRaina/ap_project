package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.StudentRegistrationService;
import edu.univ.erp.service.SectionService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegisterForSection extends JPanel {

    private MainFrame mainFrame;
    private SectionService sectionService;
    private CourseService courseService;
    private StudentRegistrationService regService;

    public RegisterForSection(MainFrame mainFrame){
        this.mainFrame = mainFrame;
        this.sectionService = new SectionService();
        this.courseService = new CourseService();
        this.regService = new StudentRegistrationService();

        //role check
        if (!SessionManager.isLoggedIn() || !"STUDENT".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Students only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;   // stop with this screen after showing back to login screen
        }

        setLayout(new BorderLayout());
        JLabel banner=null;
        if (MaintenanceChecker.isMaintenanceOn()) {
            banner = new JLabel("System Under Maintenance - VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        JLabel title = new JLabel("Register for a Section", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        if(banner!=null){
            add(title,BorderLayout.CENTER);
        }
        else{
            add(title,BorderLayout.NORTH);
        }

        String[] cols = {"Section ID", "Course Code", "Course Title", "Instructor ID", "Day/Time", "Room", "Capacity", "Deadline"};//table strcuture

        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        //buttons
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.add(registerBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        //load the table
        loadSections(model);

        // register click handling
        registerBtn.addActionListener(e -> {

            //maintenance check
            if (MaintenanceChecker.isMaintenanceOn() && !"ADMIN".equals(SessionManager.getCurrentUserRole())) {
                JOptionPane.showMessageDialog(this, "Can't Register. Maintenance Mode ON ", "Maintenance ON", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a section first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int sectionId = (Integer) model.getValueAt(selectedRow, 0);
            int studentId = SessionManager.getCurrentUserId();

            try {
                regService.register(studentId, sectionId);
                JOptionPane.showMessageDialog(this, "Successfully registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSections(model); // refresh

            } catch (AccessException ex2) {
                JOptionPane.showMessageDialog(this, ex2.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        });


        // back
        backBtn.addActionListener(e ->
                mainFrame.refreshStudentDashboard());

    }

    // load section
    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);

        List<Section> list = sectionService.getAllSections();

        for (Section s : list) {
            Course c = courseService.getCourseById(s.getCourseId());

            model.addRow(new Object[]{
                    s.getSectionId(),
                    c != null ? c.getCode() : "N/A",
                    c != null ? c.getTitle() : "N/A",
                    s.getInstructorId() != null ? s.getInstructorId() : "TBA",
                    s.getDayTime(),
                    s.getRoom(),
                    s.getCapacity(),
                    s.getRegistrationDeadline()
            });
        }
    }

}
