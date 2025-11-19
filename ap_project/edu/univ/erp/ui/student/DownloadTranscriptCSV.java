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

public class DownloadTranscriptCSV extends JPanel {

    private MainFrame mainFrame;
    private TranscriptService transcriptService;

    public DownloadTranscriptCSV(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.transcriptService = new TranscriptService();

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
        JLabel title = new JLabel("Transcript (CSV)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"Course Code", "Title", "Credits", "Status", "Final Grade"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(25);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JButton exportBtn = new JButton("Download CSV");
        JButton backBtn = new JButton("Back");

        JPanel bottom = new JPanel();
        bottom.add(exportBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.STUDENT_DASH));
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
            File file = chooser.getSelectedFile();

            boolean ok = transcriptService.exportTranscriptCSV(studentId, file);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Transcript CSV downloaded!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to save CSV.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
