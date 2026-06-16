package org.example.ui;

import javax.swing.*;
import java.awt.*;

public class MarqueePanel extends JPanel {

    private String text = "";
    private int offset = 0;
    private Timer timer;

    public MarqueePanel() {
        setBackground(new Color(28, 32, 33)); // surface-container
        setPreferredSize(new Dimension(0, 32)); // height 32px
        
        timer = new Timer(30, e -> {
            offset -= 2;
            repaint();
        });
        timer.start();
    }

    public void setText(String text) {
        this.text = text.toUpperCase() + "  ***  ";
        this.offset = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        int w = getWidth();
        int h = getHeight();

        // Draw bottom outline border
        g2d.setColor(new Color(141, 146, 138)); // outline color
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0, h - 1, w, h - 1);

        // Draw text
        g2d.setFont(FontManager.getMonoFont(Font.BOLD, 12f));
        g2d.setColor(new Color(176, 207, 173)); // primary sage green

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        if (textWidth == 0) return;

        // Wrap offset
        if (offset <= -textWidth) {
            offset = 0;
        }

        // Draw repeating text for continuous scroll effect
        int drawX = offset;
        while (drawX < w) {
            g2d.drawString(text, drawX, (h + fm.getAscent()) / 2 - 2);
            drawX += textWidth;
        }

        g2d.dispose();
    }
}
