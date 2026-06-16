package org.example.ui;

import javax.swing.*;
import java.awt.*;

public class CRTPanel extends JPanel {

    private static final Color BG_DARK = new Color(16, 20, 21); // background slate

    public CRTPanel(LayoutManager layout) {
        super(layout);
        setBackground(BG_DARK);
    }

    @Override
    public void paint(Graphics g) {
        // First paint all child components normally
        super.paint(g);

        // Now paint the CRT overlay on top of everything!
        Graphics2D g2d = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        // 1. Repeating Horizontal Scanlines
        g2d.setColor(new Color(255, 255, 255, 6)); // ~2% opacity white
        for (int y = 0; y < h; y += 4) {
            g2d.fillRect(0, y, w, 1);
        }

        // 2. Repeating Vertical Aperture Grille simulation
        g2d.setColor(new Color(0, 0, 0, 10)); // ~4% opacity black
        for (int x = 0; x < w; x += 3) {
            g2d.fillRect(x, 0, 1, h);
        }

        // 3. CRT Vignette & Glass Glow overlay
        float[] fractions = {0.0f, 0.7f, 1.0f};
        Color[] colors = {
                new Color(176, 207, 173, 10), // subtle sage green glow in center
                new Color(0, 0, 0, 30),        // soft dark gradient
                new Color(0, 0, 0, 110)        // heavier CRT edge shadow
        };

        try {
            RadialGradientPaint rgp = new RadialGradientPaint(
                    new Point(w / 2, h / 2),
                    (float) Math.sqrt(w * w + h * h) * 0.7f,
                    fractions,
                    colors
            );
            g2d.setPaint(rgp);
            g2d.fillRect(0, 0, w, h);
        } catch (Exception e) {
            // Fallback: draw dark borders if RadialGradientPaint fails for some reason
            g2d.setColor(new Color(0, 0, 0, 60));
            g2d.setStroke(new BasicStroke(20));
            g2d.drawRect(0, 0, w, h);
        }

        g2d.dispose();
    }
}
