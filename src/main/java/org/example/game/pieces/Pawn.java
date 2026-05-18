package org.example.game.pieces;

import org.example.game.Board;

public class Pawn extends Piece {

    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.col = col;
        this.row = row;
        this.xPos = col * board.getTileSize();
        this.yPos = row * board.getTileSize();
        this.isWhite = isWhite;
        this.name = "Pawn";
    }

    @Override
    public boolean canMove(int col, int row) {
        int dir = isWhite ? 1 : -1;

        if (this.col == col && this.row - dir == row && board.getPiece(col, row) == null) {
            return true;
        }
        if (isFirstMove && this.col == col && this.row - dir * 2 == row
                && board.getPiece(col, row) == null && board.getPiece(col, row + dir) == null) {
            return true;
        }
        if (col == this.col - 1 && row == this.row - dir && board.getPiece(col, row) != null) {
            return true;
        }
        if (col == this.col + 1 && row == this.row - dir && board.getPiece(col, row) != null) {
            return true;
        }
        // en passant
        if (board.getTileNum(col, row) == board.getEnPassantTile()
                && col == this.col - 1 && row == this.row - dir
                && board.getPiece(col, row + dir) != null) {
            return true;
        }
        if (board.getTileNum(col, row) == board.getEnPassantTile()
                && col == this.col + 1 && row == this.row - dir
                && board.getPiece(col, row + dir) != null) {
            return true;
        }
        return false;
    }
}
