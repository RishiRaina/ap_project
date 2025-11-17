package edu.univ.erp.ui.student;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ButtonEditor extends DefaultCellEditor {

    private JButton button;
    private int enrollmentId;
    private boolean clicked;
    private ViewMyEnrollments parent;

    public ButtonEditor(JCheckBox checkBox, ViewMyEnrollments parent) {
        super(checkBox);
        this.parent = parent;

        button = new JButton("Drop");
        button.addActionListener((ActionEvent e) -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        enrollmentId = (int) table.getValueAt(row, 0);
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            parent.dropEnrollment(enrollmentId);
        }
        clicked = false;
        return "Drop";
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
