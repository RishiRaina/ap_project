package edu.univ.erp.ui.student;

import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.auth.SessionManager;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JPanel {

    private final MainFrame mainFrame;

    public StudentDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        //title

        JLabel title = new JLabel("Student Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        // ==========================
        // BUTTON PANEL
        // ==========================
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
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
        });


        //when course catalog button is clicked
        CatalogBtn.addActionListener(e -> mainFrame.showScreen("view_catalog")
        );

        //when my enrollments clicked
        enrollmentsBtn.addActionListener(e -> mainFrame.showScreen("view_my_enrollments")
        );
        //when register section button clicked
        registerBtn.addActionListener(e-> mainFrame.showScreen("view_register_forsection"));


        //when view timetables is clicked
        timetableBtn.addActionListener(e -> mainFrame.showScreen("timetable"));

        //when grades button is clicked
        gradesBtn.addActionListener(e-> mainFrame.showScreen("grades"));

        //when csv download button clicked
        csvBtn.addActionListener(e->mainFrame.showScreen("download_csv"));



        //helper till future buttons work
        pdfBtn.addActionListener(e -> showNotReady("PDF Download"));
    }

    // Reusable button style
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        return btn;
    }

    // only and only till all ui panels made
    private void showNotReady(String feature) {
        JOptionPane.showMessageDialog(
                this,
                feature + " screen is not built yet!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
