package org.example.game;

import org.example.game.pieces.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the 8x8 Chess Board.
 * Manages the state of active chess pieces, move history, and core chess mechanics 
 * (valid move checks, check/checkmate logic, en passant, and drawing of coordinates).
 */
public class Board extends JPanel {

    public static final int TILE_SIZE = 75;
    public static final int COLS = 8;
    public static final int ROWS = 8;

    private final List<Piece> pieceList = new ArrayList<>();
    private final List<Move> moveHistory = new ArrayList<>();
    private Piece selectedPiece;
    private int enPassantTile = -1;
    private boolean isFlipped = false;

    private static final Color LIGHT = new Color(230, 227, 204);
    private static final Color DARK = new Color(162, 161, 140);
    private static final Color HIGHLIGHT = new Color(176, 207, 173, 100);
    private static final Color LAST_MOVE = new Color(176, 207, 173, 150);

    private Move lastMove;

    // used by check detection
    private int moveCol, moveRow;
    private int kingCol, kingRow;
    private Piece currentKing;

    /**
     * Creates new form Board
     */
    public Board() {
        initComponents();
        Dimension size = new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        addPieces();
    }

    private void initComponents() {
        setLayout(null);
    }                        

    public int getTileSize() { return TILE_SIZE; }
    public int getEnPassantTile() { return enPassantTile; }
    public Piece getSelectedPiece() { return selectedPiece; }
    public void setSelectedPiece(Piece piece) { this.selectedPiece = piece; }
    public List<Piece> getPieceList() { return pieceList; }
    public List<Move> getMoveHistory() { return moveHistory; }

    public boolean isFlipped() { return isFlipped; }
    public void setFlipped(boolean flipped) { this.isFlipped = flipped; }

    public int getScreenX(int col) {
        return isFlipped ? (7 - col) * TILE_SIZE : col * TILE_SIZE;
    }

    public int getScreenY(int row) {
        return isFlipped ? (7 - row) * TILE_SIZE : row * TILE_SIZE;
    }

    public int getBoardCol(int screenX) {
        int col = screenX / TILE_SIZE;
        return isFlipped ? 7 - col : col;
    }

    public int getBoardRow(int screenY) {
        int row = screenY / TILE_SIZE;
        return isFlipped ? 7 - row : row;
    }

    public Piece getPiece(int col, int row) {
        for (Piece piece : pieceList) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    public Piece findKing(boolean isWhite) {
        for (Piece piece : pieceList) {
            if (piece.isWhite == isWhite && piece.name.equals("King")) {
                return piece;
            }
        }
        return null;
    }

    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) return false;
        return p1.isWhite == p2.isWhite;
    }

    public int getTileNum(int col, int row) {
        return row * ROWS + col;
    }

    /**
     * Determines whether a move is legal according to chess rules.
     * Checks team collisions, specific piece movement rules, path obstructions,
     * out of bounds, and check prevention.
     */
    public boolean isValidMove(Move move) {
        if (move.newCol < 0 || move.newCol >= COLS || move.newRow < 0 || move.newRow >= ROWS) return false;
        if (sameTeam(move.piece, move.captured)) return false;
        if (!move.piece.canMove(move.newCol, move.newRow)) return false;
        if (move.piece.isPathBlocked(move.newCol, move.newRow)) return false;
        if (isKingInCheck(move)) return false;
        return true;
    }

    // check detection (was in CheckScanner)
    /**
     * Checks if the player's King is in check after simulating a move.
     * Computes vectors of attack (lines of sight, Knight spots, Pawn captures) 
     * to determine if the King's coordinate is attacked.
     */
    public boolean isKingInCheck(Move move) {
        currentKing = findKing(move.piece.isWhite);
        if (currentKing == null) return false;

        kingCol = currentKing.col;
        kingRow = currentKing.row;

        if (selectedPiece != null && selectedPiece.name.equals("King")) {
            kingCol = move.newCol;
            kingRow = move.newRow;
        }

        moveCol = move.newCol;
        moveRow = move.newRow;

        return checkLine(0, 1, "Rook") || checkLine(1, 0, "Rook") ||
               checkLine(0, -1, "Rook") || checkLine(-1, 0, "Rook") ||
               checkLine(-1, -1, "Bishop") || checkLine(1, -1, "Bishop") ||
               checkLine(1, 1, "Bishop") || checkLine(-1, 1, "Bishop") ||
               checkKnight() || checkPawn() || checkKing();
    }

    private boolean checkLine(int colDir, int rowDir, String type) {
        for (int i = 1; i < 8; i++) {
            int c = kingCol + i * colDir;
            int r = kingRow + i * rowDir;
            if (c < 0 || c >= COLS || r < 0 || r >= ROWS) break;
            if (c == moveCol && r == moveRow) break;

            Piece p = getPiece(c, r);
            if (p != null && p != selectedPiece) {
                if (!sameTeam(p, currentKing) && (p.name.equals(type) || p.name.equals("Queen")))
                    return true;
                break;
            }
        }
        return false;
    }

    private boolean checkKnight() {
        int[][] spots = {
            {-1,-2},{1,-2},{2,-1},{2,1},{1,2},{-1,2},{-2,1},{-2,-1}
        };
        for (int[] s : spots) {
            Piece p = getPiece(kingCol + s[0], kingRow + s[1]);
            if (p != null && !sameTeam(p, currentKing) && p.name.equals("Knight")
                    && !(p.col == moveCol && p.row == moveRow)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPawn() {
        int dir = currentKing.isWhite ? -1 : 1;
        Piece left = getPiece(kingCol - 1, kingRow + dir);
        Piece right = getPiece(kingCol + 1, kingRow + dir);

        if (left != null && !sameTeam(left, currentKing) && left.name.equals("Pawn")
                && !(left.col == moveCol && left.row == moveRow))
            return true;
        if (right != null && !sameTeam(right, currentKing) && right.name.equals("Pawn")
                && !(right.col == moveCol && right.row == moveRow))
            return true;
        return false;
    }

    private boolean checkKing() {
        for (int dc = -1; dc <= 1; dc++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (dc == 0 && dr == 0) continue;
                Piece p = getPiece(kingCol + dc, kingRow + dr);
                if (p != null && !sameTeam(p, currentKing) && p.name.equals("King"))
                    return true;
            }
        }
        return false;
    }

    public void makeMove(Move move) {
        if (move.piece.name.equals("Pawn")) {
            movePawn(move);
        } else {
            move.piece.col = move.newCol;
            move.piece.row = move.newRow;
            move.piece.xPos = move.newCol * TILE_SIZE;
            move.piece.yPos = move.newRow * TILE_SIZE;
            move.piece.isFirstMove = false;
            capturePiece(move.captured);
            enPassantTile = -1;
        }
        lastMove = move;
        moveHistory.add(move);
    }

    private void movePawn(Move move) {
        // handle special pawn rules like en passant
        int dir = move.piece.isWhite ? 1 : -1;

        // en passant capture
        if (getTileNum(move.newCol, move.newRow) == enPassantTile) {
            move.captured = getPiece(move.newCol, move.newRow + dir);
        }

        if (Math.abs(move.piece.row - move.newRow) == 2) {
            enPassantTile = getTileNum(move.newCol, move.newRow + dir);
        } else {
            enPassantTile = -1;
        }

        // promotion
        if (move.newRow == (move.piece.isWhite ? 0 : 7)) {
            pieceList.add(new Queen(this, move.newCol, move.newRow, move.piece.isWhite));
            capturePiece(move.piece);
        }

        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.xPos = move.newCol * TILE_SIZE;
        move.piece.yPos = move.newRow * TILE_SIZE;
        move.piece.isFirstMove = false;
        capturePiece(move.captured);
    }

    public void capturePiece(Piece piece) {
        pieceList.remove(piece);
    }

    public void addPieces() {
        // set up initial pieces for both teams
        pieceList.clear();

        // black pieces
        pieceList.add(new Rook(this, 0, 0, false));
        pieceList.add(new Knight(this, 1, 0, false));
        pieceList.add(new Bishop(this, 2, 0, false));
        pieceList.add(new Queen(this, 3, 0, false));
        pieceList.add(new King(this, 4, 0, false));
        pieceList.add(new Bishop(this, 5, 0, false));
        pieceList.add(new Knight(this, 6, 0, false));
        pieceList.add(new Rook(this, 7, 0, false));
        for (int c = 0; c < 8; c++) {
            pieceList.add(new Pawn(this, c, 1, false));
        }

        // white pieces
        pieceList.add(new Rook(this, 0, 7, true));
        pieceList.add(new Knight(this, 1, 7, true));
        pieceList.add(new Bishop(this, 2, 7, true));
        pieceList.add(new Queen(this, 3, 7, true));
        pieceList.add(new King(this, 4, 7, true));
        pieceList.add(new Bishop(this, 5, 7, true));
        pieceList.add(new Knight(this, 6, 7, true));
        pieceList.add(new Rook(this, 7, 7, true));
        for (int c = 0; c < 8; c++) {
            pieceList.add(new Pawn(this, c, 6, true));
        }
    }

    public void reset() {
        selectedPiece = null;
        enPassantTile = -1;
        lastMove = null;
        moveHistory.clear();
        addPieces();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        // draw the checkerboard pattern
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                g2.setColor((c + r) % 2 == 0 ? LIGHT : DARK);
                g2.fillRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if (lastMove != null) {
            g2.setColor(LAST_MOVE);
            g2.fillRect(getScreenX(lastMove.oldCol), getScreenY(lastMove.oldRow), TILE_SIZE, TILE_SIZE);
            g2.fillRect(getScreenX(lastMove.newCol), getScreenY(lastMove.newRow), TILE_SIZE, TILE_SIZE);
        }

        if (selectedPiece != null) {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    Move testMove = new Move(this, selectedPiece, c, r);
                    if (isValidMove(testMove)) {
                        g2.setColor(HIGHLIGHT);
                        int circleSize = TILE_SIZE / 3;
                        int offset = (TILE_SIZE - circleSize) / 2;
                        if (testMove.captured != null) {
                            g2.setStroke(new BasicStroke(3));
                            g2.drawOval(getScreenX(c) + 4, getScreenY(r) + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                        } else {
                            g2.fillOval(getScreenX(c) + offset, getScreenY(r) + offset, circleSize, circleSize);
                        }
                    }
                }
            }
        }

        // Coordinates drawing is removed from here since it is drawn on the board's wrapper panels.

        for (Piece piece : pieceList) {
            if (piece != selectedPiece) {
                piece.paint(g2);
            }
        }
        if (selectedPiece != null) {
            selectedPiece.paint(g2);
        }
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
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                javax.swing.JFrame frame = new javax.swing.JFrame();
                frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                frame.getContentPane().add(new Board());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    // End of variables declaration                   
}
