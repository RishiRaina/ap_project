package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentQueryService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ViewGrades extends JPanel {

    private final MainFrame mainFrame;
    private final StudentQueryService queryService;

    // Rounded card panel (like Instructor UI)
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

    // Renderer for multi-line cell
    class MultilineCellRenderer extends JTextArea implements TableCellRenderer {
        public MultilineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            setText(value != null ? value.toString() : "");

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }

            // auto-adjust row height
            int preferredHeight = getPreferredSize().height;
            if (table.getRowHeight(row) < preferredHeight) {
                table.setRowHeight(row, preferredHeight);
            }

            return this;
        }
    }

    public ViewGrades(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new StudentQueryService();

        // ------------ ROLE CHECK ------------
        if (!SessionManager.isLoggedIn()
                || !"STUDENT".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Students only!", "Access Error",
                    JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ------------ MAINTENANCE BANNER ------------
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY",
                    SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));
            bannerPanel.add(banner);

            add(bannerPanel, BorderLayout.NORTH);
        }

        // ------------ TITLE ------------
        JLabel title = new JLabel("My Grades", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));

        add(title, BorderLayout.PAGE_START);

        // ------------ MAIN CARD ------------
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 40, 25, 40));

        // ------------ TABLE MODEL ------------
        String[] cols = {"Course", "Breakdown", "Final Grade"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setRowHeight(28);

        // Breakdown column uses multiline renderer
        table.getColumnModel().getColumn(1).setCellRenderer(new MultilineCellRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(350);

        JScrollPane scroll = new JScrollPane(table);
        card.add(scroll, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        // ------------ BACK BUTTON ------------
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.refreshStudentDashboard());

        // Load rows
        loadGrades(model);
    }

    private void loadGrades(DefaultTableModel model) {
        model.setRowCount(0);
        int studentId = SessionManager.getCurrentUserId();

        try {
            List<Enrollment> enrollments = queryService.getMyEnrollments(studentId);

            for (Enrollment e : enrollments) {

                Section sec = queryService.getSection(e.getSectionId());
                if (sec == null) continue;

                Course c = queryService.getCourseById(sec.getCourseId());
                if (c == null) continue;

                List<Grade> grades = queryService.getGradesForEnrollment(e.getEnrollmentId());

                StringBuilder breakdown = new StringBuilder();
                String finalGrade = "-";

                for (Grade g : grades) {
                    breakdown.append(g.getComponent().toUpperCase())
                            .append(": ").append(g.getScore()).append("\n");

                    if (g.getFinalGrade() != null && !g.getFinalGrade().isBlank()) {
                        finalGrade = g.getFinalGrade();
                    }
                }

                model.addRow(new Object[]{
                        c.getCode().toUpperCase() + " - " + c.getTitle(),
                        breakdown.toString().trim(),
                        finalGrade
                });
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Access Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------ BUTTON STYLE ------------
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
