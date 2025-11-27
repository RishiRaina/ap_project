package edu.univ.erp.ui.student;

import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.service.TranscriptService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DownloadTranscriptCSV extends JPanel {

    private final MainFrame mainFrame;
    private final TranscriptService transcriptService;

    // Rounded card like other screens
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

    public DownloadTranscriptCSV(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.transcriptService = new TranscriptService();


        if (!SessionManager.isLoggedIn() || !"STUDENT".equalsIgnoreCase(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this, "Access Denied: Students only.", "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));  // soft grey
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bp = new JPanel(new BorderLayout());
            bp.setBackground(new Color(255, 179, 71));
            bp.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY",
                    SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));

            bp.add(banner);
            add(bp, BorderLayout.NORTH);
        }
        JLabel title = new JLabel("Transcript (CSV)", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 15, 0));
        add(title, BorderLayout.PAGE_START);
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 40, 25, 40));
        add(card, BorderLayout.CENTER);
        String[] cols = {"Course Code", "Title", "Credits", "Section", "Status", "Final Grade"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setRowHeight(28);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        card.add(scroll, BorderLayout.CENTER);
        JButton exportBtn = new JButton("Download CSV");
        JButton backBtn = new JButton("Back");
        styleButton(exportBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));
        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(exportBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
        backBtn.addActionListener(e -> mainFrame.refreshStudentDashboard());
        exportBtn.addActionListener(e -> exportCSV());
        loadTranscript(model);
    }

    private void loadTranscript(DefaultTableModel model) {
        model.setRowCount(0);
        int studentId = SessionManager.getCurrentUserId();
        List<String[]> rows = transcriptService.getTranscriptRows(studentId);
        for (String[] row : rows) {
            model.addRow(row);
        }
    }

    private void exportCSV() {
        int studentId = SessionManager.getCurrentUserId();
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("transcript.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            boolean ok = transcriptService.exportTranscriptCSV(studentId,
                    chooser.getSelectedFile());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Transcript CSV downloaded!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save CSV.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(normal);
            }
        });
    }
}
