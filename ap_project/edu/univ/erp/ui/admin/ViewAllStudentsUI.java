package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ViewAllStudentsUI extends JPanel {

    private AdminService adminService = new AdminService();


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

    public ViewAllStudentsUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Soft gray background


        JLabel title = new JLabel("All Students", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 152, 219));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);


        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Username", "Roll No", "Program", "Year"}, 0
        );
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(52, 152, 219));
        header.setForeground(Color.WHITE);

        RoundedPanel tablePanel = new RoundedPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        tablePanel.setBackground(new Color(245, 245, 245));

        add(tablePanel, BorderLayout.CENTER);


        JButton back = new JButton("Back");
        styleButton(back, new Color(52, 152, 219), new Color(41, 128, 185));

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 245, 245));
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        back.addActionListener(e -> mainFrame.refreshAdminDashboard());


        List<Student> students = adminService.getAllStudents();
        for (Student s : students) {
            String username = adminService.getUsernameById(s.getUserId()).toUpperCase();;
            model.addRow(new Object[]{
                    username,
                    s.getRollNo(),
                    s.getProgram().toUpperCase(),
                    s.getYear()
            });
        }
    }


    private void styleButton(JButton btn, Color normal, Color hover) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(normal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(hover);
            }

            public void mouseExited(MouseEvent evt) {
                btn.setBackground(normal);
            }
        });
    }
}
