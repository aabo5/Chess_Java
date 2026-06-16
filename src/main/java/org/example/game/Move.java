package org.example.game;

import org.example.game.pieces.Piece;

public class Move {

    public int oldCol;
    public int oldRow;
    public int newCol;
    public int newRow;

    public Piece piece;
    public Piece captured;

    public Move(Board board, Piece piece, int newCol, int newRow) {
        this.oldCol = piece.col;
        this.oldRow = piece.row;
        this.newCol = newCol;
        this.newRow = newRow;
        this.piece = piece;
        this.captured = board.getPiece(newCol, newRow);
    }
}
