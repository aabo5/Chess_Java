package org.example.game.pieces;

import org.example.game.Board;
import java.awt.*;

/**
 * Base abstract representation of a Chess Piece.
 * Defines position coordinates, color ownership, movement rules (canMove),
 * path obstruction verification (isPathBlocked), and Swing rendering logic (paint).
 */
public class Piece {

    public int col, row;
    public int xPos, yPos;
    public boolean isWhite;
    public String name;
    public int value;
    public boolean isFirstMove = true;

    protected Board board;

    public Piece(Board board) {
        this.board = board;
    }

    public boolean canMove(int col, int row) {
        return true;
    }

    public boolean isPathBlocked(int col, int row) {
        return false;
    }

    public void paint(Graphics2D g2d) {
        // Disable anti-aliasing to maintain sharp, pixelated borders
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        g2d.setFont(new Font("Segoe UI Symbol", Font.PLAIN, board.getTileSize() - 20));
        
        // Match the HTML mockup: hollow unicode for white, solid for black
        String symbol = "";
        switch (name) {
            case "King": symbol = isWhite ? "♔" : "♚"; break;
            case "Queen": symbol = isWhite ? "♕" : "♛"; break;
            case "Rook": symbol = isWhite ? "♖" : "♜"; break;
            case "Bishop": symbol = isWhite ? "♗" : "♝"; break;
            case "Knight": symbol = isWhite ? "♘" : "♞"; break;
            case "Pawn": symbol = isWhite ? "♙" : "♟"; break;
        }
        
        FontMetrics fm = g2d.getFontMetrics();
        int strWidth = fm.stringWidth(symbol);
        int strHeight = fm.getAscent();
        
        int x = (board.getSelectedPiece() == this) ? xPos : board.getScreenX(col);
        int y = (board.getSelectedPiece() == this) ? yPos : board.getScreenY(row);

        int drawX = x + (board.getTileSize() - strWidth) / 2;
        int drawY = y + (board.getTileSize() + strHeight) / 2 - 12;

        // Draw retro 4-directional outline to make pieces stand out on checkerboard
        g2d.setColor(isWhite ? new Color(11, 15, 16) : new Color(224, 227, 228)); // black outline for white pieces, light outline for black pieces
        g2d.drawString(symbol, drawX + 1, drawY);
        g2d.drawString(symbol, drawX - 1, drawY);
        g2d.drawString(symbol, drawX, drawY + 1);
        g2d.drawString(symbol, drawX, drawY - 1);

        // Draw main piece fill
        // White: `#e0e3e4` (on-surface), Black: `#0b0f10` (surface-lowest)
        g2d.setColor(isWhite ? new Color(224, 227, 228) : new Color(11, 15, 16));
        g2d.drawString(symbol, drawX, drawY);
    }
}
