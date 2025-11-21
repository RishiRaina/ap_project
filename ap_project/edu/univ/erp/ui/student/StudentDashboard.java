package edu.univ.erp.ui.student;

import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.access.*;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JPanel {

    private final MainFrame mainFrame;

    public StudentDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        if (MaintenanceChecker.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance - VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.SOUTH);
        }



        //title
        JLabel title = new JLabel("Student Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        //button panel
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(9, 1, 12, 12));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 250, 20, 250));
        btnPanel.setBackground(Color.WHITE);

        JButton CatalogBtn = createButton("View Course Catalog");
        JButton enrollmentsBtn = createButton("My Enrollments");
        JButton registerBtn = createButton("Register for Section");
        JButton timetableBtn = createButton("View Timetable");
        JButton gradesBtn = createButton("View Grades");
        JButton csvBtn = createButton("Download Transcript (CSV)");
        JButton pdfBtn = createButton("Download Transcript (PDF)");
        JButton logoutBtn = createButton("Logout");

        btnPanel.add(CatalogBtn);
        btnPanel.add(enrollmentsBtn);
        btnPanel.add(registerBtn);
        btnPanel.add(timetableBtn);
        btnPanel.add(gradesBtn);
        btnPanel.add(csvBtn);
        btnPanel.add(pdfBtn);
        btnPanel.add(logoutBtn);

        add(btnPanel, BorderLayout.CENTER);

        // back to login when logout pressed
        logoutBtn.addActionListener(e -> {
            SessionManager.clear();
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);});


        CatalogBtn.addActionListener(e -> {
            mainFrame.addScreen("view_catalog", new ViewCourseCatalog(mainFrame));
            mainFrame.showScreen("view_catalog");});

        enrollmentsBtn.addActionListener(e -> {
            mainFrame.addScreen("view_my_enrollments", new ViewMyEnrollments(mainFrame));
            mainFrame.showScreen("view_my_enrollments");
        });

        registerBtn.addActionListener(e -> {
            mainFrame.addScreen("view_register_forsection", new RegisterForSection(mainFrame));
            mainFrame.showScreen("view_register_forsection");
        });

        timetableBtn.addActionListener(e -> {
            mainFrame.addScreen("timetable", new ViewTimeTable(mainFrame));
            mainFrame.showScreen("timetable");
        });

        gradesBtn.addActionListener(e -> {
            mainFrame.addScreen("grades", new ViewGrades(mainFrame));
            mainFrame.showScreen("grades");
        });

        csvBtn.addActionListener(e -> {
            mainFrame.addScreen("download_csv", new DownloadTranscriptCSV(mainFrame));
            mainFrame.showScreen("download_csv");
        });

        pdfBtn.addActionListener(e -> {
            mainFrame.addScreen("download_pdf", new DownloadTranscriptPDF(mainFrame));
            mainFrame.showScreen("download_pdf");
        });


    }

    // Reusable button style
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        return btn;
    }


}
