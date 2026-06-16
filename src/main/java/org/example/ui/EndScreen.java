package org.example.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Game Over End Screen UI.
 * Displays the final game result (checkmate winner/stalemate draw), status updates,
 * and a rematch "PLAY AGAIN" button inside a dynamic centered terminal box.
 */
public class EndScreen extends JPanel {

    public interface EndListener {
        void onPlayAgain();
    }

    private EndListener listener;

    private JLabel crownLabel;
    private JLabel gameOverLabel;
    private JLabel resultLabel;
    private JLabel statusLabel;
    private RetroButton playAgainButton;

    public EndScreen() {
        initComponents();
    }

    public void setEndListener(EndListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        setBackground(new Color(16, 20, 21)); // background `#101415`
        setPreferredSize(new Dimension(680, 720));
        setLayout(null);

        // Fonts
        Font headerFont = FontManager.getHeaderFont(32f);
        Font resultFont = FontManager.getHeaderFont(24f);
        Font statusFont = FontManager.getMonoFont(Font.PLAIN, 12f);
        Font buttonFont = FontManager.getHeaderFont(16f);

        // Crown Icon (Unicode)
        crownLabel = new JLabel("♛");
        crownLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 90));
        crownLabel.setForeground(new Color(176, 207, 173)); // primary sage green
        crownLabel.setHorizontalAlignment(SwingConstants.CENTER);
        crownLabel.setBounds(190, 120, 300, 100);
        add(crownLabel);

        // Game Over Label
        gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(headerFont);
        gameOverLabel.setForeground(new Color(224, 227, 228)); // on-surface
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setBounds(190, 240, 300, 40);
        add(gameOverLabel);

        // Result Label
        resultLabel = new JLabel(" ");
        resultLabel.setFont(resultFont);
        resultLabel.setForeground(new Color(198, 202, 165)); // secondary creamy sage
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setBounds(140, 295, 400, 35);
        add(resultLabel);

        // Status Label (Waiting for opponent...)
        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusFont);
        statusLabel.setForeground(new Color(195, 200, 191)); // on-surface-variant
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBounds(190, 345, 300, 20);
        add(statusLabel);

        // Play Again Button (RetroButton)
        playAgainButton = new RetroButton(
                "PLAY AGAIN",
                new Color(176, 207, 173), // bg `#b0cfad`
                new Color(29, 54, 30),     // fg `#1d361e`
                new Color(29, 54, 30),
                new Color(176, 207, 173)
        );
        playAgainButton.setFont(buttonFont);
        playAgainButton.setBounds(190, 400, 300, 50);
        playAgainButton.addActionListener(e -> {
            statusLabel.setText("Waiting for opponent...");
            if (listener != null) listener.onPlayAgain();
        });
        add(playAgainButton);
    }

    public void setResult(String result) {
        resultLabel.setText(result.toUpperCase());
        statusLabel.setText(" ");
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int cardX = (w - 480) / 2;
        crownLabel.setBounds(cardX + 90, 120, 300, 100);
        gameOverLabel.setBounds(cardX + 90, 240, 300, 40);
        resultLabel.setBounds(cardX + 40, 295, 400, 35);
        statusLabel.setBounds(cardX + 90, 345, 300, 20);
        playAgainButton.setBounds(cardX + 90, 400, 300, 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        int w = getWidth();
        int h = getHeight();
        int cardX = (w - 480) / 2;

        // 1. Draw Top Header Bar
        g2.setColor(new Color(49, 53, 54)); // surface-container-highest
        g2.fillRect(0, 0, w, 54);
        g2.setColor(new Color(141, 146, 138)); // outline
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, 52, w, 52);

        // Header Title
        g2.setFont(FontManager.getHeaderFont(18f));
        g2.setColor(new Color(176, 207, 173));
        g2.drawString("CHESS_TERMINAL_v1.0", 24, 33);

        // Header Nav Badge (GAME OVER red badge)
        g2.setColor(new Color(147, 0, 10)); // error-container `#93000a`
        g2.fillRect(w - 140, 15, 110, 24);
        g2.setFont(FontManager.getMonoFont(Font.BOLD, 10f));
        g2.setColor(new Color(255, 218, 214)); // on-error-container `#ffdad6`
        g2.drawString("GAME OVER", w - 122, 31);

        // Draw central terminal card around game over details
        g2.setColor(new Color(24, 28, 29)); // surface-container-low
        g2.fillRect(cardX, 100, 480, 400);
        g2.setColor(new Color(141, 146, 138)); // outline
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(cardX, 100, 480, 400);

        g2.dispose();
    }
}
