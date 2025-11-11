package edu.univ.erp.ui;

import edu.univ.erp.service.CourseService;
import edu.univ.erp.service.EnrollmentService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {

    private final int studentId;
    private JTable courseTable;
    private JTable myCoursesTable;
    private DefaultTableModel tableModel;
    private DefaultTableModel myCoursesModel;
    private CourseService courseService;
    private EnrollmentService enrollmentService;

    public StudentPanel(int studentId) {
        this.studentId = studentId;
        courseService = new CourseService();
        enrollmentService = new EnrollmentService();
        setLayout(new BorderLayout());

        JPanel catalogPanel = createCatalogPanel();
        JPanel myCoursesPanel = createMyCoursesPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, catalogPanel, myCoursesPanel);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        loadCourseCatalog();
        loadRegisteredCourses();
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Course Catalog"));
        String[] columnNames = {"Section ID", "Course Code", "Title", "Credits", "Instructor Dept", "Time", "Room", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0);
        courseTable = new JTable(tableModel);
        courseTable.setDefaultEditor(Object.class, null);
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton registerButton = new JButton("Register for Selected Course");
        buttonPanel.add(registerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        registerButton.addActionListener(e -> onRegisterButtonClick());
        return panel;
    }

    private JPanel createMyCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("My Registered Courses"));
        String[] columnNames = {"Enrollment ID", "Course Code", "Title", "Time", "Room"};
        myCoursesModel = new DefaultTableModel(columnNames, 0);
        myCoursesTable = new JTable(myCoursesModel);
        myCoursesTable.setDefaultEditor(Object.class, null);
        panel.add(new JScrollPane(myCoursesTable), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton dropButton = new JButton("Drop Selected Course");
        JButton exportButton = new JButton("Download Transcript (CSV)");
        buttonPanel.add(dropButton);
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dropButton.addActionListener(e -> onDropButtonClick());
        exportButton.addActionListener(e -> onExportTranscriptClick());
        return panel;
    }

    private void loadCourseCatalog() {
        List<Object[]> catalogData = courseService.getCourseCatalog();
        tableModel.setRowCount(0);
        for (Object[] rowData : catalogData) {
            tableModel.addRow(rowData);
        }
    }
    
    private void loadRegisteredCourses() {
        List<Object[]> registeredData = enrollmentService.getRegisteredCourses(this.studentId);
        myCoursesModel.setRowCount(0);
        for (Object[] rowData : registeredData) {
            myCoursesModel.addRow(rowData);
        }
    }

    private void onRegisterButtonClick() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course from the catalog to register.", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int sectionId = (int) tableModel.getValueAt(selectedRow, 0);
        
        String resultMessage = enrollmentService.enrollStudent(this.studentId, sectionId);
        JOptionPane.showMessageDialog(this, resultMessage);

        loadRegisteredCourses();
        loadCourseCatalog();
    }

    private void onDropButtonClick() {
        int selectedRow = myCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course from your list to drop.", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int enrollmentId = (int) myCoursesModel.getValueAt(selectedRow, 0);
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to drop this course?", "Confirm Drop", 
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            String resultMessage = enrollmentService.dropCourse(enrollmentId);
            JOptionPane.showMessageDialog(this, resultMessage);
            
            loadRegisteredCourses();
            loadCourseCatalog();
        }
    }

    private void onExportTranscriptClick() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new java.io.File("transcript.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String result = enrollmentService.exportTranscript(this.studentId, fileToSave);
            JOptionPane.showMessageDialog(this, result);
        }
    }
}