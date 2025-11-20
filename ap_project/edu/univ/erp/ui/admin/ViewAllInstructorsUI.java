package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewAllInstructorsUI extends JPanel {

    private AdminService adminService = new AdminService();

    public ViewAllInstructorsUI(MainFrame mainFrame) {

        setLayout(new BorderLayout());

        JLabel title = new JLabel("All Instructors", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Instructor ID", "Department"}, 0
        );
        JTable table = new JTable(model);
        table.setRowHeight(25);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton back = new JButton("Back");
        JPanel bottom = new JPanel();
        bottom.add(back);
        add(bottom, BorderLayout.SOUTH);

        back.addActionListener(e -> mainFrame.showScreen(MainFrame.ADMIN_DASH));

        // load
        List<Instructor> list = adminService.getAllInstructors();
        for (Instructor i : list) {
            model.addRow(new Object[]{
                    i.getUserId(),
                    i.getDepartment()
            });
        }
    }
}
