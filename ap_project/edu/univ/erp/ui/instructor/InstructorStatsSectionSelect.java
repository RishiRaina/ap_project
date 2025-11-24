package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.common.ActionButtonEditor;
import edu.univ.erp.ui.common.ActionButtonRenderer;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorStatsSectionSelect extends JPanel {

    private final MainFrame mainFrame;
    private final InstructorQueryService queryService;

    private JTable sectionTable;

    // -------- Rounded card --------
    class RoundedPanel extends JPanel {
        private final int radius = 20;
        public RoundedPanel() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    public InstructorStatsSectionSelect(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new InstructorQueryService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // -------- ROLE CHECK --------
        if (!SessionManager.isLoggedIn() ||
                !"INSTRUCTOR".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Access Denied: Instructors only.",
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        // -------- Maintenance Banner --------
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY",
                    SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));

            bannerPanel.add(banner, BorderLayout.CENTER);
            add(bannerPanel, BorderLayout.NORTH);
        }

        // -------- Header --------
        JLabel title = new JLabel("Select Section for Statistics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.PAGE_START);

        // -------- Card Container --------
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 40, 20, 40));

        // -------- Table Columns (Same as InstructorSections) --------
        String[] cols = {
                "Section", "Day/Time", "Room",
                "Action", "SECTION_ID" // hidden
        };

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;  // Only Action column editable
            }
        };

        sectionTable = new JTable(model);
        sectionTable.setRowHeight(30);
        sectionTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sectionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Action Button
        sectionTable.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        sectionTable.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        // Load actual data
        loadSections(model);

        // Hide the SECTION_ID column properly
        sectionTable.removeColumn(sectionTable.getColumnModel().getColumn(4));

        JScrollPane scroll = new JScrollPane(sectionTable);
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        // -------- Back Button --------
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));
        backBtn.addActionListener(e -> mainFrame.refreshInstructorDashboard());

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    // -------- Load Sections (same style as InstructorSections) --------
    private void loadSections(DefaultTableModel model) {
        model.setRowCount(0);

        try {
            int instructorId = SessionManager.getCurrentUserId();
            List<Section> list = queryService.getMySections(instructorId);

            for (Section s : list) {

                model.addRow(new Object[]{
                        s.toString().toUpperCase(),    // Example: "12 | CS201 - MON 10:00 @ C102"
                        s.getDayTime().toUpperCase(),
                        s.getRoom().toUpperCase(),
                        "View Stats",
                        s.getSectionId()               // hidden real ID
                });
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // -------- Action when clicking View Stats --------
    public void viewStats(int row) {
        try {
            int sectionId = (int) sectionTable.getModel().getValueAt(row, 4);

            SectionStatsView statsView = new SectionStatsView(mainFrame, sectionId);

            InstructorDashboard dash =
                    (InstructorDashboard) SwingUtilities.getAncestorOfClass(
                            InstructorDashboard.class, this);

            if (dash != null) {
                dash.setCenter(statsView);
            } else {
                mainFrame.addScreen("instructor_stats_view", statsView);
                mainFrame.showScreen("instructor_stats_view");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE
            );
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
