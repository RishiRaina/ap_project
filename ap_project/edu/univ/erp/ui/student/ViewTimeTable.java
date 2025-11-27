package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentQueryService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ViewTimeTable extends JPanel {

    private final MainFrame mainFrame;
    private final StudentQueryService queryService = new StudentQueryService();

    // Rounded panel for consistency with other student screens
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

    public ViewTimeTable(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        if (!SessionManager.isLoggedIn() || !"STUDENT".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Students only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            bannerPanel.setBackground(new Color(255, 179, 71));
            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));
            bannerPanel.add(banner);
            add(bannerPanel, BorderLayout.NORTH);
        }
        JLabel title = new JLabel("Timetable", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.PAGE_START);

        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 40, 20, 40));
        add(card, BorderLayout.CENTER);
        String[] cols = {"Day/Time", "Room", "Course Code", "Course Title"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(backBtn);

        add(bottom, BorderLayout.SOUTH);
        backBtn.addActionListener(e -> mainFrame.refreshStudentDashboard());

        // Load Timetable Data
        loadTimetable(model);
    }

    private void loadTimetable(DefaultTableModel model) {
        model.setRowCount(0);
        int studentId = SessionManager.getCurrentUserId();
        // DAY ORDER (uppercase keys)
        Map<String, Integer> dayOrder = Map.of("MON", 1, "TUE", 2, "WED", 3, "THU", 4, "FRI", 5, "SAT", 6, "SUN", 7);
        class Entry {
            String dayLabel, timeLabel, room, code, title;
            int dayIdx, timeIdx;
        }
        List<Entry> rows = new ArrayList<>();
        try {
            List<Enrollment> list = queryService.getMyEnrollments(studentId);
            for (Enrollment en : list) {
                Section sec = queryService.getSection(en.getSectionId());
                Course c = queryService.getCourseById(sec.getCourseId());

                String raw = sec.getDayTime().trim().toUpperCase();
                String day = raw.substring(0, 3);  // "MON"
                int dayIdx = dayOrder.getOrDefault(day, 99);
                String rawTime = raw.substring(3).trim();
                if (rawTime.contains("@")) {
                    rawTime = rawTime.substring(0, rawTime.indexOf("@")).trim();
                }
                String start = rawTime.split("-")[0].trim();
                start = start.replace(":", "");  // "10:00" → "1000"
                if (start.length() == 3) start = "0" + start; // 900 → 0900
                int timeIdx = 0;
                try { timeIdx = Integer.parseInt(start); } catch (Exception ignored) {}
                Entry e = new Entry();
                e.dayLabel = day;
                e.timeLabel = rawTime;
                e.room = sec.getRoom();
                e.code = c.getCode();
                e.title = c.getTitle();
                e.dayIdx = dayIdx;
                e.timeIdx = timeIdx;
                rows.add(e);
            }
            rows.sort((a, b) -> {
                if (a.dayIdx != b.dayIdx) return a.dayIdx - b.dayIdx;
                return a.timeIdx - b.timeIdx;
            });
            for (Entry e : rows) {
                model.addRow(new Object[]{
                        e.dayLabel + " " + e.timeLabel,
                        e.room,
                        e.code,
                        e.title
                });
            }

        } catch (AccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(normal); }
        });
    }
}
