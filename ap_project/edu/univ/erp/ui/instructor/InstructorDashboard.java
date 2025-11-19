package edu.univ.erp.ui.instructor;

import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JPanel {

    private MainFrame mainFrame;

    public InstructorDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Instructor Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JButton mysecbutton = new JButton("My Sections");
        JButton statssectionbutton = new JButton("View Class Stats");
        JButton exportCsvBtn = new JButton("Export Grades CSV");
        JButton backBtn = new JButton("Back");

        Font btnFont = new Font("Arial", Font.PLAIN, 20);
        mysecbutton.setFont(btnFont);
        statssectionbutton.setFont(btnFont);
        exportCsvBtn.setFont(btnFont);
        backBtn.setFont(btnFont);
        //add all buttons to panel then add panel
        panel.add(mysecbutton);
        panel.add(statssectionbutton);
        panel.add(exportCsvBtn);
        panel.add(backBtn);
        add(panel, BorderLayout.CENTER);

        mysecbutton.addActionListener(e -> {
            mainFrame.addScreen("instructor_my_sections", new InstructorSections(mainFrame));
            mainFrame.showScreen("instructor_my_sections");
        });

        exportCsvBtn.addActionListener(e -> {
            mainFrame.addScreen("instructor_export_csv", new ExportGrades(mainFrame));
            mainFrame.showScreen("instructor_export_csv");
        });

        statssectionbutton.addActionListener(e -> {
            mainFrame.addScreen("instructor_stats_select_section",new InstructorStatsSectionSelect(mainFrame));
            mainFrame.showScreen("instructor_stats_select_section");
        });


        //back button pushes back on to login screen
        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.LOGIN_SCREEN));

    }
}
