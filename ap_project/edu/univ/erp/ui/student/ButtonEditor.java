package edu.univ.erp.ui.student;

import javax.swing.*;
import java.awt.*;

public class ButtonEditor extends DefaultCellEditor {

    private JButton button;
    private int enrollmentId;
    private boolean clicked;
    private ViewMyEnrollments parent;

    public ButtonEditor(JCheckBox checkBox, ViewMyEnrollments parent) {
        super(checkBox);
        this.parent = parent;

        button = new JButton("Drop");

        button.addActionListener(e -> {
            clicked = true;
            SwingUtilities.invokeLater(() -> {parent.dropEnrollment(enrollmentId);cancelCellEditing();});
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        enrollmentId = (int) table.getValueAt(row, 0);
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        super.cancelCellEditing();
    }
}
