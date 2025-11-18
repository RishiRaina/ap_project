package edu.univ.erp.ui.common;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ActionButtonRenderer extends JButton implements TableCellRenderer {

    public ActionButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        setText(value != null ? value.toString() : "");

        if (isSelected) {
            setForeground(Color.white);
            setBackground(new Color(0, 120, 215));  // selection color
        } else {
            setForeground(Color.black);
            setBackground(UIManager.getColor("Button.background"));
        }

        return this;
    }
}
