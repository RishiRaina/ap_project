package edu.univ.erp.ui;

import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.ui.auth.LoginScreen;
import edu.univ.erp.ui.instructor.InstructorDashboard;
import edu.univ.erp.ui.student.*;
import edu.univ.erp.ui.instructor.*;


public class MainLauncher {

    public static void main(String[] args) {

        MainFrame frame = new MainFrame();

        // Add screens
        frame.addScreen(MainFrame.LOGIN_SCREEN, new LoginScreen(frame));
        frame.addScreen(MainFrame.STUDENT_DASH, new StudentDashboard(frame));
        frame.addScreen(MainFrame.INSTRUCTOR_DASH, new InstructorDashboard(frame));
        frame.addScreen(MainFrame.ADMIN_DASH, new AdminDashboard(frame));


        frame.setVisible(true);
        frame.showScreen(MainFrame.LOGIN_SCREEN);
    }
}
