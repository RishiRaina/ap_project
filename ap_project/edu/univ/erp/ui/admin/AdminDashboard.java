package edu.univ.erp.ui.admin;

import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.access.AccessControl;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JPanel {

    private MainFrame mainFrame;

    public AdminDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // maintenance banner
        if (MaintenanceChecker.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        // title
        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        // buttons panel
        JPanel panel = new JPanel(new GridLayout(14, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 250, 20, 250));

        JButton addCourse = btn("Add Course");
        JButton updateCourse = btn("Update Course");
        JButton deleteCourse = btn("Delete Course");

        JButton addSection = btn("Add Section");
        JButton assignInstructor = btn("Assign Instructor");
        JButton changeCapacity = btn("Change Capacity");

        JButton addStudent = btn("Add Student");
        JButton addInstructor = btn("Add Instructor");
        JButton dropStudent = btn("Drop Student Forcefully");

        JButton viewStudents = btn("View All Students");
        JButton viewInstructors = btn("View All Instructors");

        JButton toggleMaintenance = btn("Toggle Maintenance Mode");
        JButton logout = btn("Logout");

        panel.add(addCourse);
        panel.add(updateCourse);
        panel.add(deleteCourse);

        panel.add(addSection);
        panel.add(assignInstructor);
        panel.add(changeCapacity);

        panel.add(addStudent);
        panel.add(addInstructor);
        panel.add(dropStudent);

        panel.add(viewStudents);
        panel.add(viewInstructors);

        panel.add(toggleMaintenance);
        panel.add(logout);

        add(panel, BorderLayout.CENTER);

        // ACTIONS
        addCourse.addActionListener(e -> open("add_course", new AddCourseUI(mainFrame)));
        updateCourse.addActionListener(e -> open("update_course", new UpdateCourseUI(mainFrame)));
        deleteCourse.addActionListener(e -> open("delete_course", new DeleteCourseUI(mainFrame)));

        addSection.addActionListener(e -> open("add_section", new AddSectionUI(mainFrame)));
        assignInstructor.addActionListener(e -> open("assign_instructor", new AssignInstructorUI(mainFrame)));
        changeCapacity.addActionListener(e -> open("change_capacity", new ChangeCapacityUI(mainFrame)));

        addStudent.addActionListener(e -> open("add_student", new AddStudentUI(mainFrame)));
        addInstructor.addActionListener(e -> open("add_instructor", new AddInstructorUI(mainFrame)));
        dropStudent.addActionListener(e -> open("drop_student", new DropStudentForcefullyUI(mainFrame)));

        viewStudents.addActionListener(e -> open("view_students", new ViewAllStudentsUI(mainFrame)));
        viewInstructors.addActionListener(e -> open("view_instructors", new ViewAllInstructorsUI(mainFrame)));

        toggleMaintenance.addActionListener(e -> open("toggle_maintenance", new ToggleMaintenanceUI(mainFrame)));

        logout.addActionListener(e -> {
            SessionManager.clear();
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
        });
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 16));
        b.setFocusPainted(false);
        return b;
    }

    private void open(String name, JPanel panel) {
        mainFrame.addScreen(name, panel);
        mainFrame.showScreen(name);
    }
}
