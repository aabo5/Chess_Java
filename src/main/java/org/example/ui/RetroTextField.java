package org.example.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RetroTextField extends JTextField {

    private static final Color BG_COLOR = new Color(11, 15, 16); // surface-container-lowest
    private static final Color TEXT_COLOR = new Color(176, 207, 173); // primary sage green
    private static final Color NORMAL_BORDER = new Color(67, 72, 65); // outline-variant
    private static final Color FOCUS_BORDER = new Color(176, 207, 173); // primary sage green

    public RetroTextField(String text) {
        super(text);
        setBackground(BG_COLOR);
        setForeground(TEXT_COLOR);
        setCaretColor(TEXT_COLOR);
        setSelectionColor(new Color(139, 168, 136));
        setSelectedTextColor(new Color(29, 54, 30));
        setOpaque(true);
        
        // Setup border padding and outline
        updateBorder(NORMAL_BORDER);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateBorder(FOCUS_BORDER);
            }

            @Override
            public void focusLost(FocusEvent e) {
                updateBorder(NORMAL_BORDER);
            }
        });
    }

    private void updateBorder(Color color) {
        setBorder(new CompoundBorder(
                new LineBorder(color, 2),
                new EmptyBorder(8, 8, 8, 8)
        ));
    }
}
