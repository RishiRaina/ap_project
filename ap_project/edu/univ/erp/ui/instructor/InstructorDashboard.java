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

        JButton mySectionsBtn = new JButton("My Sections");
        JButton statsBtn = new JButton("View Class Stats");
        JButton exportCsvBtn = new JButton("Export Grades CSV");
        JButton backBtn = new JButton("Back");

        Font btnFont = new Font("Arial", Font.PLAIN, 20);
        mySectionsBtn.setFont(btnFont);
        statsBtn.setFont(btnFont);
        exportCsvBtn.setFont(btnFont);
        backBtn.setFont(btnFont);

        panel.add(mySectionsBtn);
        panel.add(statsBtn);
        panel.add(exportCsvBtn);
        panel.add(backBtn);

        add(panel, BorderLayout.CENTER);

        mySectionsBtn.addActionListener(e -> mainFrame.showScreen("instructor_my_sections"));
        statsBtn.addActionListener(e -> mainFrame.showScreen("instructor_stats_select_section"));
        exportCsvBtn.addActionListener(e -> mainFrame.showScreen("instructor_export_csv"));
        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.LOGIN_SCREEN));

    }
}
