package edu.univ.erp.ui;

import edu.univ.erp.service.InstructorService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class InstructorPanel extends JPanel {

    private final int instructorId;
    private JList<Map<String, Object>> sectionList;
    private DefaultListModel<Map<String, Object>> sectionListModel;
    private JTable gradebookTable;
    private DefaultTableModel gradebookTableModel;
    private InstructorService instructorService;

    public InstructorPanel(int instructorId) {
        this.instructorId = instructorId;
        this.instructorService = new InstructorService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Instructor Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        sectionListModel = new DefaultListModel<>();
        sectionList = new JList<>(sectionListModel);
        sectionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Map<String, Object> sectionData = (Map<String, Object>) value;
                return super.getListCellRendererComponent(list, sectionData.get("displayText"), index, isSelected, cellHasFocus);
            }
        });
        JScrollPane sectionScrollPane = new JScrollPane(sectionList);
        sectionScrollPane.setBorder(BorderFactory.createTitledBorder("My Sections"));
        sectionScrollPane.setPreferredSize(new Dimension(250, 0));

        JPanel gradebookPanel = new JPanel(new BorderLayout(10, 10));
        gradebookPanel.setBorder(BorderFactory.createTitledBorder("Gradebook"));
        String[] gradebookColumns = {"Student ID", "Roll No", "Program"};
        gradebookTableModel = new DefaultTableModel(gradebookColumns, 0);
        gradebookTable = new JTable(gradebookTableModel);
        gradebookPanel.add(new JScrollPane(gradebookTable), BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sectionScrollPane, gradebookPanel);
        splitPane.setResizeWeight(0.3);
        add(splitPane, BorderLayout.CENTER);

        sectionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Map<String, Object> selectedSection = sectionList.getSelectedValue();
                if (selectedSection != null) {
                    int sectionId = (int) selectedSection.get("id");
                    loadEnrolledStudents(sectionId);
                }
            }
        });

        loadAssignedSections();
    }

    private void loadAssignedSections() {
        List<Map<String, Object>> sections = instructorService.getAssignedSections(instructorId);
        sectionListModel.clear();
        for (Map<String, Object> section : sections) {
            sectionListModel.addElement(section);
        }
    }

    private void loadEnrolledStudents(int sectionId) {
        List<Object[]> students = instructorService.getEnrolledStudents(sectionId);
        gradebookTableModel.setRowCount(0);
        for (Object[] student : students) {
            gradebookTableModel.addRow(student);
        }
    }
}