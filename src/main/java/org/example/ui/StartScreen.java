package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Initial Lobby Start Screen UI.
 * Provides Host and Join controls, server IP / Port inputs, latency estimation,
 * and active online player counts with a retro terminal aesthetic.
 */
public class StartScreen extends JPanel {

    public interface StartListener {
        void onHostGame(int port);
        void onJoinGame(String ip, int port);
    }

    private StartListener listener;

    private RetroButton hostButton;
    private RetroButton joinButton;
    private RetroTextField ipField;
    private RetroTextField portField;

    public StartScreen() {
        initComponents();
    }

    public void setStartListener(StartListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        setBackground(new Color(16, 20, 21)); // background `#101415`
        setPreferredSize(new Dimension(680, 720));
        setLayout(null);

        // Fonts
        Font titleFont = FontManager.getHeaderFont(36f);
        Font subtitleFont = FontManager.getMonoFont(Font.PLAIN, 11f);
        Font labelFont = FontManager.getMonoFont(Font.BOLD, 11f);
        Font buttonFont = FontManager.getHeaderFont(16f);

        // Host Game Button
        hostButton = new RetroButton(
                "Host Game (port 5007)",
                new Color(176, 207, 173), // bg primary sage `#b0cfad`
                new Color(29, 54, 30),     // fg on-primary `#1d361e`
                new Color(29, 54, 30),     // hover bg
                new Color(176, 207, 173)   // hover fg
        );
        hostButton.setFont(buttonFont);
        hostButton.setBounds(120, 245, 440, 45);
        hostButton.addActionListener(e -> {
            if (listener != null) {
                int port;
                try {
                    port = Integer.parseInt(portField.getText().trim());
                } catch (NumberFormatException ex) {
                    port = 5007;
                }
                listener.onHostGame(port);
            }
        });
        add(hostButton);

        // IP Field
        ipField = new RetroTextField("localhost");
        ipField.setFont(FontManager.getMonoFont(Font.BOLD, 14f));
        ipField.setBounds(120, 420, 210, 38);
        add(ipField);

        // Port Field
        portField = new RetroTextField("5007");
        portField.setFont(FontManager.getMonoFont(Font.BOLD, 14f));
        portField.setBounds(350, 420, 210, 38);
        add(portField);

        // Join Game Button
        joinButton = new RetroButton(
                "Join Remote Game",
                new Color(71, 75, 47),     // bg secondary-container `#474b2f`
                new Color(183, 187, 151),  // fg on-secondary-container `#b7bb97`
                new Color(183, 187, 151),  // hover bg
                new Color(71, 75, 47)      // hover fg
        );
        joinButton.setFont(buttonFont);
        joinButton.setBounds(120, 490, 440, 45);
        joinButton.addActionListener(e -> {
            if (listener != null) {
                String ip = ipField.getText().trim();
                int port;
                try {
                    port = Integer.parseInt(portField.getText().trim());
                } catch (NumberFormatException ex) {
                    port = 5000;
                }
                listener.onJoinGame(ip, port);
            }
        });
        add(joinButton);
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int cardX = (w - 480) / 2;
        hostButton.setBounds(cardX + 20, 245, 440, 45);
        ipField.setBounds(cardX + 20, 420, 210, 38);
        portField.setBounds(cardX + 250, 420, 210, 38);
        joinButton.setBounds(cardX + 20, 490, 440, 45);
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
        g2.setColor(new Color(49, 53, 54)); // surface-container-highest `#313536`
        g2.fillRect(0, 0, w, 54);
        g2.setColor(new Color(141, 146, 138)); // outline `#8d928a`
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, 52, w, 52);

        // Header Title
        g2.setFont(FontManager.getHeaderFont(18f));
        g2.setColor(new Color(176, 207, 173)); // primary `#b0cfad`
        g2.drawString("CHESS_TERMINAL_v1.0", 24, 33);

        // Header Nav Badge
        g2.setColor(new Color(204, 235, 199)); // primary-fixed `#ccebc7`
        g2.fillRect(w - 110, 15, 80, 24);
        g2.setFont(FontManager.getMonoFont(Font.BOLD, 11f));
        g2.setColor(new Color(7, 32, 11)); // on-primary-fixed `#07200b`
        g2.drawString("LOBBY", w - 90, 31);

        // 2. Hero Header
        g2.setFont(FontManager.getHeaderFont(34f));
        g2.setColor(new Color(176, 207, 173));
        g2.drawString("CHESS ONLINE", cardX, 115);

        g2.setFont(FontManager.getMonoFont(Font.PLAIN, 10f));
        g2.setColor(new Color(195, 200, 191)); // on-surface-variant `#c3c8bf`
        g2.drawString("SYSTEM STATUS: READY // 2-PLAYER NETWORK PROTOCOL", cardX, 138);

        // 3. Host Card
        // Box: (cardX, 165, 480, 145)
        g2.setColor(new Color(24, 28, 29)); // surface-container-low `#181c1d`
        g2.fillRect(cardX, 165, 480, 145);
        g2.setColor(new Color(141, 146, 138)); // outline `#8d928a`
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(cardX, 165, 480, 145);

        // Host card header text
        g2.setFont(FontManager.getHeaderFont(14f));
        g2.setColor(new Color(224, 227, 228)); // on-surface `#e0e3e4`
        g2.drawString("INITIATE HOST", cardX + 20, 198);

        g2.setFont(FontManager.getMonoFont(Font.PLAIN, 11f));
        g2.setColor(new Color(195, 200, 191));
        g2.drawString("Create a secure channel on local node", cardX + 20, 220);

        // 4. Divider
        g2.setColor(new Color(67, 72, 65)); // outline-variant `#434841`
        g2.drawLine(cardX, 342, cardX + 120, 342);
        g2.drawLine(cardX + 360, 342, cardX + 480, 342);
        g2.setFont(FontManager.getMonoFont(Font.BOLD, 10f));
        g2.setColor(new Color(141, 146, 138));
        g2.drawString("- OR JOIN A REMOTE NODE -", cardX + 144, 345);

        // 5. Join Card
        // Box: (cardX, 370, 480, 185)
        g2.setColor(new Color(24, 28, 29)); // surface-container-low `#181c1d`
        g2.fillRect(cardX, 370, 480, 185);
        g2.setColor(new Color(141, 146, 138)); // outline `#8d928a`
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(cardX, 370, 480, 185);

        // Labels
        g2.setFont(FontManager.getMonoFont(Font.BOLD, 10f));
        g2.setColor(new Color(176, 207, 173)); // primary `#b0cfad`
        g2.drawString("SERVER IP ADDRESS:", cardX + 20, 405);
        g2.drawString("COMMUNICATION PORT:", cardX + 250, 405);

        // 6. Stats Footer
        // Left Box: (cardX, 585, 225, 65)
        g2.setColor(new Color(28, 32, 33)); // surface-container `#1c2021`
        g2.fillRect(cardX, 585, 225, 65);
        g2.setColor(new Color(141, 146, 138));
        g2.drawRect(cardX, 585, 225, 65);

        g2.setFont(FontManager.getMonoFont(Font.BOLD, 9f));
        g2.setColor(new Color(195, 200, 191));
        g2.drawString("LATENCY", cardX + 20, 608);
        g2.setFont(FontManager.getMonoFont(Font.BOLD, 12f));
        g2.setColor(new Color(176, 207, 173));
        g2.drawString("0.02ms (STABLE)", cardX + 20, 632);

        // Right Box: (cardX + 255, 585, 225, 65)
        g2.setColor(new Color(28, 32, 33));
        g2.fillRect(cardX + 255, 585, 225, 65);
        g2.setColor(new Color(141, 146, 138));
        g2.drawRect(cardX + 255, 585, 225, 65);

        g2.setFont(FontManager.getMonoFont(Font.BOLD, 9f));
        g2.setColor(new Color(195, 200, 191));
        g2.drawString("ACTIVE USERS", cardX + 275, 608);
        g2.setFont(FontManager.getMonoFont(Font.BOLD, 12f));
        g2.setColor(new Color(198, 202, 165)); // secondary `#c6caa5`
        g2.drawString("1,204 ONLINE", cardX + 275, 632);

        g2.dispose();
    }
}
