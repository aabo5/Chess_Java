package org.example.game.pieces;

import org.example.game.Board;

import java.awt.*;

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
        g2d.setFont(new Font("Segoe UI Symbol", Font.PLAIN, board.getTileSize() - 20));
        String symbol = "";
        switch (name) {
            case "King": symbol = "♚"; break;
            case "Queen": symbol = "♛"; break;
            case "Rook": symbol = "♜"; break;
            case "Bishop": symbol = "♝"; break;
            case "Knight": symbol = "♞"; break;
            case "Pawn": symbol = "♟"; break;
        }
        FontMetrics fm = g2d.getFontMetrics();
        int strWidth = fm.stringWidth(symbol);
        int strHeight = fm.getAscent();
        
        int drawX = xPos + (board.getTileSize() - strWidth) / 2;
        int drawY = yPos + (board.getTileSize() + strHeight) / 2 - 12;

        // Draw outline/shadow
        g2d.setColor(Color.GRAY);
        g2d.drawString(symbol, drawX + 1, drawY + 1);

        // Draw main character
        g2d.setColor(isWhite ? Color.WHITE : Color.BLACK);
        g2d.drawString(symbol, drawX, drawY);
    }
}
