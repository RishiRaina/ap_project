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

    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;
        public RoundedPanel() { setOpaque(false); }
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

        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        JLabel title = new JLabel("Assign Instructor to Section");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        RoundedPanel form = new RoundedPanel();
        form.setLayout(new GridLayout(3, 2, 15, 15)); // match AddSection spacing
        form.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50)); // match AddSection width

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 15);


        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(labelFont);

        JComboBox<Course> courseDropdown = new JComboBox<>();
        courseDropdown.setFont(inputFont);
        courseDropdown.addItem(null);

        List<Course> courses = courseDAO.getAllCourses();
        for (Course c : courses) courseDropdown.addItem(c);

        courseDropdown.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    Course c = (Course) value;
                    setText(c.getCode().toUpperCase() + " - " + c.getTitle());
                } else {
                    setText("Select Course...");
                }
                return this;
            }
        });


        JLabel sectionLabel = new JLabel("Select Section:");
        sectionLabel.setFont(labelFont);

        JComboBox<Section> sectionDropdown = new JComboBox<>();
        sectionDropdown.setFont(inputFont);
        sectionDropdown.addItem(null);

        courseDropdown.addActionListener(e -> {
            sectionDropdown.removeAllItems();
            sectionDropdown.addItem(null);

            Course selected = (Course) courseDropdown.getSelectedItem();
            if (selected == null) return;

            List<Section> secs = sectionDAO.getSectionsByCourse(selected.getCourseId());
            for (Section s : secs) sectionDropdown.addItem(s);
        });

        sectionDropdown.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Section) {
                    setText(value.toString().toUpperCase());
                } else {
                    setText("Select Section...");
                }
                return this;
            }
        });


        JLabel instructorLabel = new JLabel("Select Instructor:");
        instructorLabel.setFont(labelFont);

        JComboBox<Integer> instructorDropdown = new JComboBox<>();
        instructorDropdown.setFont(inputFont);
        instructorDropdown.addItem(null);

        for (Integer id : instructorDAO.getAllInstructorIds()) instructorDropdown.addItem(id);

        instructorDropdown.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Integer) {
                    String username = instructorDAO.getUsernameById((Integer) value);
                    setText(username != null ? username.toUpperCase() : "UNKNOWN");
                } else {
                    setText("Select Instructor...");
                }
                return this;
            }
        });


        form.add(courseLabel); form.add(courseDropdown);
        form.add(sectionLabel); form.add(sectionDropdown);
        form.add(instructorLabel); form.add(instructorDropdown);

        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(245, 245, 245));

        JButton assignBtn = new JButton("Assign");
        JButton backBtn = new JButton("Back");

        styleButton(assignBtn, new Color(46, 204, 113), new Color(39, 174, 96));
        styleButton(backBtn, new Color(52, 152, 219), new Color(41, 128, 185));

        buttons.add(assignBtn);
        buttons.add(backBtn);

        wrapper.add(form);
        add(wrapper, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

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

            JOptionPane.showMessageDialog(this,
                    ok ? "Instructor assigned successfully!" : "Assignment failed.");
        });

        backBtn.addActionListener(e -> mainFrame.refreshAdminDashboard());
    }

    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(normal); }
        });
    }
}
