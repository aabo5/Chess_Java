package org.example.game.pieces;

import org.example.game.Board;

public class Rook extends Piece {

    public Rook(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.getTileSize();
        this.yPos = row * board.getTileSize();
        this.isWhite = isWhite;
        this.name = "Rook";
    }

    @Override
    public boolean canMove(int col, int row) {
        return this.col == col || this.row == row;
    }

    @Override
    public boolean isPathBlocked(int col, int row) {
        int colStep = 0, rowStep = 0;
        if (col > this.col) colStep = 1;
        else if (col < this.col) colStep = -1;
        if (row > this.row) rowStep = 1;
        else if (row < this.row) rowStep = -1;

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
