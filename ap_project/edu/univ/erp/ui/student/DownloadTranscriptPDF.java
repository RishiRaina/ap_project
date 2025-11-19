package edu.univ.erp.ui.student;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.service.TranscriptService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DownloadTranscriptPDF extends JPanel {

    private MainFrame mainFrame;
    private TranscriptService ts;

    public DownloadTranscriptPDF(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.ts = new TranscriptService();

        // Role check
        if (!SessionManager.isLoggedIn() || !"STUDENT".equals(SessionManager.getCurrentUserRole())) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied: Students only.",
                    "Access Error", JOptionPane.ERROR_MESSAGE);
            mainFrame.showScreen(MainFrame.LOGIN_SCREEN);
            return;
        }

        setLayout(new BorderLayout());

        // Banner
        if (AccessControl.isMaintenanceOn()) {
            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setOpaque(true);
            banner.setBackground(Color.ORANGE);
            banner.setForeground(Color.BLACK);
            banner.setFont(new Font("Arial", Font.BOLD, 16));
            add(banner, BorderLayout.NORTH);
        }

        // Title
        JLabel title = new JLabel("Transcript (PDF)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"Course Code", "Title", "Credits", "Status", "Final Grade"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JButton exportBtn = new JButton("Download PDF");
        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.add(exportBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.STUDENT_DASH));
        exportBtn.addActionListener(e -> exportPDF());

        loadTranscript(model);
    }

    private void loadTranscript(DefaultTableModel model) {
        model.setRowCount(0);

        int studentId = SessionManager.getCurrentUserId();
        List<String[]> rows = ts.getTranscriptRows(studentId);

        for (String[] row : rows) {
            model.addRow(row);
        }
    }

    private void exportPDF() {
        int studentId = SessionManager.getCurrentUserId();

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("transcript.pdf"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            boolean ok = ts.exportTranscriptPDF(studentId, file);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Transcript PDF downloaded!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save PDF.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
