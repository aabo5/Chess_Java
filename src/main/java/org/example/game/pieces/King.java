package org.example.game.pieces;

import org.example.game.Board;

public class King extends Piece {

    public King(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.getTileSize();
        this.yPos = row * board.getTileSize();
        this.isWhite = isWhite;
        this.name = "King";
    }

    @Override
    public boolean canMove(int col, int row) {
        int colDiff = Math.abs(col - this.col);
        int rowDiff = Math.abs(row - this.row);
        return colDiff <= 1 && rowDiff <= 1 && (colDiff + rowDiff) > 0;
    }
}
