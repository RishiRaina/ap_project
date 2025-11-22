package edu.univ.erp.ui;
import edu.univ.erp.ui.auth.LoginScreen;
import edu.univ.erp.ui.common.MainFrame;



public class MainLauncher {

    public static void main(String[] args) {

        MainFrame frame = new MainFrame();

        // Only load login screen at startup
        frame.addScreen(MainFrame.LOGIN_SCREEN, new LoginScreen(frame));

        frame.setVisible(true);
        frame.showScreen(MainFrame.LOGIN_SCREEN);
    }
}
