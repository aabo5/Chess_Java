package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RetroButton extends JButton {
    private Color normalBg;
    private Color normalFg;
    private Color hoverBg;
    private Color hoverFg;
    private boolean hover = false;

    public RetroButton(String text, Color bg, Color fg, Color hoverBg, Color hoverFg) {
        super(text);
        this.normalBg = bg;
        this.normalFg = fg;
        this.hoverBg = hoverBg;
        this.hoverFg = hoverFg;

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = getWidth();
        int h = getHeight();

        boolean pressed = getModel().isPressed();

        // Offset for tactile press effect
        int dx = pressed ? 2 : 0;
        int dy = pressed ? 2 : 0;

        // Draw flat 3D shadow if not pressed
        if (!pressed) {
            g2.setColor(new Color(11, 15, 16)); // surface-container-lowest
            g2.fillRect(3, 3, w - 3, h - 3);
        }

        // Draw button background
        g2.setColor(hover ? hoverBg : normalBg);
        g2.fillRect(dx, dy, w - 3, h - 3);

        // Draw solid outline border
        g2.setColor(new Color(141, 146, 138)); // outline color
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(dx, dy, w - 3, h - 3);

        // Draw text
        g2.setFont(getFont());
        g2.setColor(hover ? hoverFg : normalFg);
        FontMetrics fm = g2.getFontMetrics();
        int stringWidth = fm.stringWidth(getText());
        int stringHeight = fm.getAscent();
        int textX = dx + (w - 3 - stringWidth) / 2;
        int textY = dy + (h - 3 + stringHeight) / 2 - 2;
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}
