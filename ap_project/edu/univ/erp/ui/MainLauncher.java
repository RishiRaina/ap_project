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

        //sub parts of student dashboard
        frame.addScreen("view_catalog", new ViewCourseCatalog(frame));
        frame.addScreen("view_my_enrollments", new ViewMyEnrollments(frame));
        frame.addScreen("view_register_forsection", new RegisterForSection(frame));
        frame.addScreen("timetable", new ViewTimeTable(frame));
        frame.addScreen("grades",new ViewGrades(frame));
        frame.addScreen("download_csv", new DownloadTranscriptCSV(frame));
        frame.addScreen("download_pdf", new DownloadTranscriptPDF(frame));


        // sub parts of instrcutor dashboard
        frame.addScreen("instructor_my_sections", new InstructorSections(frame));
        frame.addScreen("instructor_export_csv", new ExportGrades(frame));
        frame.addScreen("instructor_stats_select_section", new InstructorStatsSectionSelect(frame));
        frame.addScreen("instructor_stats_view", new SectionStatsView(frame, -1)); // dummy, replaced dynamically









        frame.setVisible(true);
        frame.showScreen(MainFrame.LOGIN_SCREEN);
    }
}
