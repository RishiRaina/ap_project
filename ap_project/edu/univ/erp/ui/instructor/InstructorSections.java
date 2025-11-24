package edu.univ.erp.ui.instructor;

import edu.univ.erp.access.AccessException;
import edu.univ.erp.access.MaintenanceChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.InstructorQueryService;
import edu.univ.erp.ui.common.MainFrame;
import edu.univ.erp.ui.common.ActionButtonEditor;
import edu.univ.erp.ui.common.ActionButtonRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorSections extends JPanel {

    private final MainFrame mainFrame;
    private final InstructorQueryService queryService;
    private final CourseService courseService;

    private JTable sectionTable;  // reference for action buttons

    // ------------------ Rounded Card Panel ------------------
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

    // ------------------ Constructor ------------------
    public InstructorSections(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.queryService = new InstructorQueryService();
        this.courseService = new CourseService();

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

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ------------------ MAINTENANCE BANNER ------------------
        if (MaintenanceChecker.isMaintenanceOn()) {
            JPanel bannerPanel = new JPanel(new BorderLayout());
            bannerPanel.setBackground(new Color(255, 179, 71));
            bannerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JLabel banner = new JLabel("System Under Maintenance — VIEW ONLY", SwingConstants.CENTER);
            banner.setFont(new Font("Segoe UI", Font.BOLD, 16));
            bannerPanel.add(banner, BorderLayout.CENTER);

            add(bannerPanel, BorderLayout.NORTH);
        }

        // ------------------ HEADER ------------------
        JLabel title = new JLabel("My Sections", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.PAGE_START);

        // ------------------ TABLE CARD ------------------
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 40, 20, 40));

        // ------- IMPORTANT: Hidden ID column added at the end -------
        String[] cols = {"Section", "Course", "Title", "Day/Time", "Room", "Action", "SECTION_ID"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;  // ONLY action column editable
            }
        };

        sectionTable = new JTable(model);
        sectionTable.setRowHeight(30);
        sectionTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sectionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Action button styling
        sectionTable.getColumn("Action").setCellRenderer(new ActionButtonRenderer());
        sectionTable.getColumn("Action").setCellEditor(new ActionButtonEditor(new JCheckBox(), this));

        // Load content
        loadMySections(model);

        // ----- Hide INTERNAL column (SECTION_ID) from view -----
        sectionTable.removeColumn(sectionTable.getColumnModel().getColumn(6));

        JScrollPane scroll = new JScrollPane(sectionTable);
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        // ------------------ BACK BUTTON ------------------
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));
        backBtn.addActionListener(e -> mainFrame.refreshInstructorDashboard());

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    // ------------------ Load Sections ------------------
    public void loadMySections(DefaultTableModel model) {
        model.setRowCount(0);

        try {
            int instructorId = SessionManager.getCurrentUserId();
            List<Section> sections = queryService.getMySections(instructorId);

            for (Section s : sections) {
                Course c = courseService.getCourseById(s.getCourseId());

                model.addRow(new Object[]{
                        s.toString().toUpperCase(),
                        c != null ? c.getCode().toUpperCase() : "N/A",
                        c != null ? c.getTitle().toUpperCase() : "N/A",
                        s.getDayTime().toUpperCase(),
                        s.getRoom().toUpperCase(),
                        "View Students",

                        // REAL SECTION ID stored here (hidden column)
                        s.getSectionId()
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

    // ------------------ VIEW STUDENTS HANDLER ------------------
    public void viewStudents(int row) {
        try {
            // Retrieve REAL sectionId from hidden column
            int sectionId = (int) sectionTable.getModel().getValueAt(row, 6);

            InstructorSectionStudents panel = new InstructorSectionStudents(mainFrame, sectionId);

            // Display inside dashboard frame if possible
            InstructorDashboard dash =
                    (InstructorDashboard) SwingUtilities.getAncestorOfClass(InstructorDashboard.class, this);

            if (dash != null) {
                dash.setCenter(panel);
            } else {
                mainFrame.addScreen("instructor_section_students", panel);
                mainFrame.showScreen("instructor_section_students");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------ Button Styling ------------------
    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(normal); }
        });
    }
}
