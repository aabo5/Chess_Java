package org.example.main;

import org.example.network.Client;
import org.example.ui.EndScreen;
import org.example.ui.GamePanel;
import org.example.ui.StartScreen;

import javax.swing.*;
import java.awt.*;

public class Main {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private StartScreen startScreen;
    private GamePanel gamePanel;
    private EndScreen endScreen;

    private Client client;

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
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
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().start();
            }
        });
    }

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
        cardPanel.add(endScreen,  "END");

        startScreen.setStartListener(new StartScreen.StartListener() {
            @Override
            public void onHostGame() {
                connectToServer("localhost", 5000);
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

        frame.setContentPane(cardPanel);
        frame.pack();
        frame.setMinimumSize(new Dimension(700, 750));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

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
                    JOptionPane.showMessageDialog(frame,
                        "Lost connection to server.",
                        "Disconnected", JOptionPane.WARNING_MESSAGE);
                    cardLayout.show(cardPanel, "START");
                });
            }
        });

        client.connect(host, port);
    }
}
