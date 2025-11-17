package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout visiblecard;
    private JPanel cardholder;

    public static final String LOGIN_SCREEN = "login_screen";
    public static final String STUDENT_DASH = "student_dashboard";
    public static final String INSTRUCTOR_DASH = "instructor_dashboard";
    public static final String ADMIN_DASH = "admin_dashboard";

    public MainFrame() {
        setTitle("University ERP System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        visiblecard = new CardLayout();
        cardholder = new JPanel(visiblecard);

        add(cardholder);
    }

    public void addScreen(String name, JPanel screen) {
        cardholder.add(screen, name);
    }

    public void showScreen(String name) {
        visiblecard.show(cardholder, name);
    }
}
