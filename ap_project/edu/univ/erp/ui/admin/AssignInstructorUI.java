package edu.univ.erp.ui.admin;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignInstructorUI extends JPanel {

    private CourseDAO courseDAO = new CourseDAO();
    private SectionDAO sectionDAO = new SectionDAO();
    private InstructorDAO instructorDAO = new InstructorDAO();

    // ---------- Rounded Panel for modern design ----------
    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;

        public RoundedPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    public AssignInstructorUI(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // ---------- Header ----------
        JLabel title = new JLabel("Assign Instructor to Section", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ---------- Form Panel ----------
        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(3, 2, 20, 20));
        form.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);

        // --- Course Dropdown ---
        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(labelFont);
        JComboBox<Course> courseDropdown = new JComboBox<>();
        courseDropdown.setFont(inputFont);
        courseDropdown.setPreferredSize(new Dimension(150, 30));

        courseDropdown.addItem(null); // Placeholder
        for (Course c : courseDAO.getAllCourses()) {
            courseDropdown.addItem(c);
        }

        courseDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    setText(((Course) value).getCode().toUpperCase());
                } else if (value == null) {
                    setText("Select Course...");
                }
                return this;
            }
        });

        // --- Section Dropdown ---
        JLabel sectionLabel = new JLabel("Select Section:");
        sectionLabel.setFont(labelFont);
        JComboBox<Section> sectionDropdown = new JComboBox<>();
        sectionDropdown.setFont(inputFont);
        sectionDropdown.setPreferredSize(new Dimension(150, 30));
        sectionDropdown.addItem(null); // Placeholder

        // When course selected → load sections
        courseDropdown.addActionListener(e -> {
            sectionDropdown.removeAllItems();
            sectionDropdown.addItem(null); // Placeholder

            Course selectedCourse = (Course) courseDropdown.getSelectedItem();
            if (selectedCourse == null) return;

            List<Section> secs = sectionDAO.getSectionsByCourse(selectedCourse.getCourseId());
            for (Section s : secs) {
                sectionDropdown.addItem(s);
            }
        });

        sectionDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Section) {
                    setText("Section " + ((Section) value).getSectionId());
                } else if (value == null) {
                    setText("Select Section...");
                }
                return this;
            }
        });

        // --- Instructor Dropdown ---
        JLabel instructorLabel = new JLabel("Select Instructor:");
        instructorLabel.setFont(labelFont);
        JComboBox<Integer> instructorDropdown = new JComboBox<>();
        instructorDropdown.setFont(inputFont);
        instructorDropdown.setPreferredSize(new Dimension(150, 30));
        instructorDropdown.addItem(null); // Placeholder

        for (Integer instId : instructorDAO.getAllInstructorIds()) {
            instructorDropdown.addItem(instId);
        }

        instructorDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Integer) {
                    String username = instructorDAO.getUsernameById((Integer) value);
                    setText(username != null ? username.toUpperCase() : "UNKNOWN");
                } else if (value == null) {
                    setText("Select Instructor...");
                }

                return this;
            }
        });

        // Add components
        form.add(courseLabel);
        form.add(courseDropdown);

        form.add(sectionLabel);
        form.add(sectionDropdown);

        form.add(instructorLabel);
        form.add(instructorDropdown);

        // ---------- Buttons ----------
        JButton assignBtn = new JButton("Assign");
        JButton backBtn = new JButton("Back");

        styleButton(assignBtn, new Color(46, 204, 113), new Color(39, 174, 96)); // Green
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185)); // Blue

        JPanel btnPanel = new JPanel();
        btnPanel.add(assignBtn);
        btnPanel.add(backBtn);

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // ---------- Button Actions ----------
        assignBtn.addActionListener(e -> {
            Section selectedSec = (Section) sectionDropdown.getSelectedItem();
            Integer instId = (Integer) instructorDropdown.getSelectedItem();

            if (selectedSec == null) {
                JOptionPane.showMessageDialog(this, "No section selected.");
                return;
            }

            if (instId == null) {
                JOptionPane.showMessageDialog(this, "No instructor selected.");
                return;
            }

            boolean ok = sectionDAO.assignInstructor(selectedSec.getSectionId(), instId);

            if (ok)
                JOptionPane.showMessageDialog(this, "Instructor assigned successfully!");
            else
                JOptionPane.showMessageDialog(this, "Assignment failed.");
        });

        backBtn.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));
    }

    // ---------- Button Styling ----------
    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
