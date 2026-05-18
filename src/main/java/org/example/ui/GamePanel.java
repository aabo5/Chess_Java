/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package org.example.ui;

import org.example.game.Board;
import org.example.game.GameManager;
import org.example.game.pieces.Piece;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Acer
 */
public class GamePanel extends javax.swing.JPanel {

    private Board board;
    private GameManager gameManager;

    /**
     * Creates new form GamePanel
     */
    public GamePanel() {
        initComponents();
        setupGame();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(40, 40, 40));
        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Connecting...");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 0, 8, 0));

        jPanel1.setBackground(new java.awt.Color(50, 50, 50));
        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>                        

    private void setupGame() {
        board = new Board();
        gameManager = new GameManager(board);
        gameManager.setStatusLabel(jLabel1);

        board.addMouseListener(new MouseAdapter() {
            // handle mouse clicks on the board
            @Override
            public void mousePressed(MouseEvent evt) {
                if (gameManager.isGameOver()) return;
                if (!gameManager.isMyTurn()) return;

                int col = evt.getX() / Board.TILE_SIZE;
                int row = evt.getY() / Board.TILE_SIZE;

                Piece piece = board.getPiece(col, row);
                if (piece != null && piece.isWhite == gameManager.isPlayerWhite()) {
                    board.setSelectedPiece(piece);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                Piece selected = board.getSelectedPiece();
                if (selected == null) return;

                int newCol = evt.getX() / Board.TILE_SIZE;
                int newRow = evt.getY() / Board.TILE_SIZE;

                boolean ok = gameManager.tryMove(selected.col, selected.row, newCol, newRow);

                if (!ok) {
                    selected.xPos = selected.col * Board.TILE_SIZE;
                    selected.yPos = selected.row * Board.TILE_SIZE;
                }

                board.setSelectedPiece(null);
                board.repaint();
            }
        });

        board.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                Piece selected = board.getSelectedPiece();
                if (selected != null) {
                    selected.xPos = evt.getX() - Board.TILE_SIZE / 2;
                    selected.yPos = evt.getY() - Board.TILE_SIZE / 2;
                    board.repaint();
                }
            }
        });

        add(board, java.awt.BorderLayout.CENTER);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setStatus(String text) {
        javax.swing.SwingUtilities.invokeLater(() -> jLabel1.setText(text));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GamePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GamePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GamePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GamePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                javax.swing.JFrame frame = new javax.swing.JFrame();
                frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                frame.getContentPane().add(new GamePanel());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration                   
}
