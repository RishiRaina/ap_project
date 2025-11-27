package edu.univ.erp.ui.student;

import edu.univ.erp.domain.Course;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.access.MaintenanceChecker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewCourseCatalog extends JPanel {

    private final MainFrame mainFrame;
    private final CourseService courseService;

    // Same instructor-style rounded card
    class RoundedPanel extends JPanel {
        private final int cornerRadius = 20;
        public RoundedPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    public ViewCourseCatalog(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.courseService = new CourseService();
        if (!SessionManager.isLoggedIn() || !"STUDENT".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {

            JOptionPane.showMessageDialog(this, "Access Denied: Students Only!", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));
            bannerPanel.add(banner, BorderLayout.CENTER);
            add(bannerPanel, BorderLayout.NORTH);
        }
        JLabel title = new JLabel("Course Catalog", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.PAGE_START);

        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 40, 25, 40));
        String[] cols = {"Code", "Title", "Credits"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
        backBtn.addActionListener(e -> mainFrame.refreshStudentDashboard());
        loadCourses(model);
    }

    private void loadCourses(DefaultTableModel model) {
        model.setRowCount(0);
        for (Course c : courseService.getAllCourses()) {
            model.addRow(new Object[]{c.getCode().toUpperCase(), c.getTitle(), c.getCredits()});
        }
    }

    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(normal); }
        });
    }
}
