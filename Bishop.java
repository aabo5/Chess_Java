package org.example.game.pieces;

import org.example.game.Board;

public class Bishop extends Piece {

    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.getTileSize();
        this.yPos = row * board.getTileSize();
        this.isWhite = isWhite;
        this.name = "Bishop";
    }

    @Override
    public boolean canMove(int col, int row) {
        return Math.abs(col - this.col) == Math.abs(row - this.row);
    }

    @Override
    public boolean isPathBlocked(int col, int row) {
        int colStep = col > this.col ? 1 : -1;
        int rowStep = row > this.row ? 1 : -1;

        int c = this.col + colStep;
        int r = this.row + rowStep;
        while (c != col || r != row) {
            if (board.getPiece(c, r) != null) return true;
            c += colStep;
            r += rowStep;
        }
        return false;
    }
}
