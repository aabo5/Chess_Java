package org.example.main;

import org.example.network.Client;
import org.example.ui.EndScreen;
import org.example.ui.GamePanel;
import org.example.ui.StartScreen;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for the Chess Online client.
 * Sets up the JFrame container, configures the CardLayout screens,
 * and handles the transitions and connection logic between screen components.
 */
public class Main {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private StartScreen startScreen;
    private GamePanel gamePanel;
    private EndScreen endScreen;

    private Client client;

    /**
     * Launch the chess client application.
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().start();
            }
        });
    }

    /**
     * Initializes and positions the UI frame, card container, and screen views.
     */
    private void start() {
        frame = new JFrame("Chess Online");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        startScreen = new StartScreen();
        gamePanel = new GamePanel();
        endScreen = new EndScreen();

        cardPanel.add(startScreen, "START");
        cardPanel.add(gamePanel, "GAME");
        cardPanel.add(endScreen, "END");

        startScreen.setStartListener(new StartScreen.StartListener() {
            @Override
            public void onHostGame(int port) {
                // Spin up a server thread on target port in background
                new Thread(() -> {
                    try {
                        new org.example.network.Server().start(port);
                    } catch (Exception e) {
                        System.out.println("Host server thread error: " + e.getMessage());
                    }
                }).start();

                // Give the server socket a moment to bind
                try { Thread.sleep(200); } catch (InterruptedException e) {}

                connectToServer("localhost", port);
            }

            @Override
            public void onJoinGame(String ip, int port) {
                connectToServer(ip, port);
            }
        });

        endScreen.setEndListener(() -> {
            if (client != null) {
                client.sendPlayAgain();
            }
        });

        org.example.ui.CRTPanel crtWrapper = new org.example.ui.CRTPanel(new BorderLayout());
        crtWrapper.add(cardPanel, BorderLayout.CENTER);
        crtWrapper.setPreferredSize(new Dimension(920, 780));
        frame.setContentPane(crtWrapper);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Establishes a socket connection to the server on the target port,
     * wires up client event listeners, and transitions to the main gameplay screen.
     */
    private void connectToServer(String host, int port) {
        // establish socket connection to AWS server
        cardLayout.show(cardPanel, "GAME");
        gamePanel.setStatus("Connecting to " + host + ":" + port + "...");

        client = new Client();
        gamePanel.getGameManager().setClient(client);

        client.setListener(new Client.ClientListener() {
            @Override
            public void onColorAssigned(boolean isWhite) {
                SwingUtilities.invokeLater(() -> {
                    gamePanel.getGameManager().setPlayerColor(isWhite);
                    gamePanel.getBoard().setFlipped(!isWhite);
                    gamePanel.updateNotation(isWhite);
                    gamePanel.setStatus("You are " + (isWhite ? "White" : "Black"));
                });
            }

            @Override
            public void onWaiting() {
                SwingUtilities.invokeLater(() -> {
                    gamePanel.setStatus("Waiting for opponent to connect...");
                });
            }

            @Override
            public void onGameStart() {
                SwingUtilities.invokeLater(() -> {
                    cardLayout.show(cardPanel, "GAME");
                    if (gamePanel.getGameManager().isMyTurn()) {
                        gamePanel.setStatus("Game started! Your turn (White)");
                    } else {
                        gamePanel.setStatus("Game started! Opponent's turn...");
                    }
                });
            }

            @Override
            public void onOpponentMove(int oldCol, int oldRow, int newCol, int newRow) {
                SwingUtilities.invokeLater(() -> {
                    gamePanel.getGameManager().applyOpponentMove(oldCol, oldRow, newCol, newRow);
                });
            }

            @Override
            public void onGameOver(String result) {
                SwingUtilities.invokeLater(() -> {
                    endScreen.setResult(result);
                    cardLayout.show(cardPanel, "END");
                });
            }

            @Override
            public void onReset() {
                SwingUtilities.invokeLater(() -> {
                    gamePanel.getGameManager().reset();
                    gamePanel.getBoard().setFlipped(!gamePanel.getGameManager().isPlayerWhite());
                    gamePanel.updateNotation(gamePanel.getGameManager().isPlayerWhite());
                    cardLayout.show(cardPanel, "GAME");
                    if (gamePanel.getGameManager().isMyTurn()) {
                        gamePanel.setStatus("New game! Your turn");
                    } else {
                        gamePanel.setStatus("New game! Opponent's turn...");
                    }
                });
            }

            @Override
            public void onDisconnected() {
                SwingUtilities.invokeLater(() -> {
                    String msg;
                    if (client.isProtocolError()) {
                        msg = "Connected to a non-chess server.\n" +
                              "Please free the port, or change the Port field to a different value (e.g. 5001).";
                    } else if (client.isConnected()) {
                        msg = "Lost connection to server.";
                    } else {
                        msg = "Could not connect to server.";
                    }
                    JOptionPane.showMessageDialog(frame,
                            msg,
                            "Disconnected", JOptionPane.WARNING_MESSAGE);
                    cardLayout.show(cardPanel, "START");
                });
            }
        });

        client.connect(host, port);
    }
}
