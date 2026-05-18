package org.example.game.pieces;

import org.example.game.Board;

public class Knight extends Piece {

    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.getTileSize();
        this.yPos = row * board.getTileSize();
        this.isWhite = isWhite;
        this.name = "Knight";
    }

    @Override
    public boolean canMove(int col, int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }
}
