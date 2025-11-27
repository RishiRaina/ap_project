package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.table.TableCellEditor;

public class ActionButtonEditor extends DefaultCellEditor {

    private JButton button;
    private boolean clicked;
    private int row;
    private JPanel parentPanel;

    public ActionButtonEditor(JCheckBox checkBox, JPanel parentPanel) {
        super(checkBox);
        this.parentPanel = parentPanel;

        button = new JButton();
        button.addActionListener((ActionEvent e) -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        button.setText(value != null ? value.toString() : "");
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            try {
                if (parentPanel instanceof edu.univ.erp.ui.instructor.InstructorSections) {
                    ((edu.univ.erp.ui.instructor.InstructorSections) parentPanel).viewStudents(row);
                }
                else if (parentPanel instanceof edu.univ.erp.ui.instructor.InstructorSectionStudents) {
                    ((edu.univ.erp.ui.instructor.InstructorSectionStudents) parentPanel).enterGrades(row);
                }
                else if (parentPanel instanceof edu.univ.erp.ui.instructor.InstructorStatsSectionSelect) {
                    ((edu.univ.erp.ui.instructor.InstructorStatsSectionSelect) parentPanel).viewStats(row);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        clicked = false;
        return button.getText();
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
