package org.example.game;

import org.example.game.pieces.Piece;
import org.example.network.Client;
import javax.swing.*;

public class GameManager {

    private final Board board;
    private boolean whiteTurn = true;
    private boolean gameOver = false;
    private String result = null;
    private Client client;
    private boolean playerIsWhite = true;
    private boolean myTurn = true;
    private JLabel statusLabel;

    public GameManager(Board board) {
        this.board = board;
    }

    public void setStatusLabel(JLabel label) {
        this.statusLabel = label;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setPlayerColor(boolean isWhite) {
        this.playerIsWhite = isWhite;
        this.myTurn = isWhite;
    }

    public boolean isWhiteTurn() { return whiteTurn; }
    public boolean isGameOver() { return gameOver; }
    public boolean isMyTurn() { return myTurn; }
    public boolean isPlayerWhite() { return playerIsWhite; }
    public String getResult() { return result; }
    public Board getBoard() { return board; }

    private void updateStatus(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    public boolean tryMove(int oldCol, int oldRow, int newCol, int newRow) {
        // check if it's our turn and the right color
        if (gameOver || !myTurn) return false;

        Piece piece = board.getPiece(oldCol, oldRow);
        if (piece == null) return false;
        if (piece.isWhite != playerIsWhite) return false;
        if (piece.isWhite != whiteTurn) return false;

        Move move = new Move(board, piece, newCol, newRow);
        board.setSelectedPiece(piece);

        if (!board.isValidMove(move)) {
            board.setSelectedPiece(null);
            return false;
        }

        board.makeMove(move);
        board.setSelectedPiece(null);
        whiteTurn = !whiteTurn;
        myTurn = false;

        if (client != null) {
            client.sendMove(oldCol, oldRow, newCol, newRow);
        }

        updateStatus("Opponent's turn...");
        checkGameOver();
        board.repaint();
        return true;
    }

    public void applyOpponentMove(int oldCol, int oldRow, int newCol, int newRow) {
        Piece piece = board.getPiece(oldCol, oldRow);
        if (piece == null) return;

        Move move = new Move(board, piece, newCol, newRow);
        board.setSelectedPiece(piece);
        board.makeMove(move);
        board.setSelectedPiece(null);

        whiteTurn = !whiteTurn;
        myTurn = true;

        updateStatus("Your turn (" + (playerIsWhite ? "White" : "Black") + ")");
        checkGameOver();
        board.repaint();
    }

    private void checkGameOver() {
        // see if there are any valid moves left
        boolean hasValidMove = false;

        for (Piece piece : board.getPieceList()) {
            if (piece.isWhite != whiteTurn) continue;

            for (int r = 0; r < Board.ROWS && !hasValidMove; r++) {
                for (int c = 0; c < Board.COLS && !hasValidMove; c++) {
                    board.setSelectedPiece(piece);
                    Move testMove = new Move(board, piece, c, r);
                    if (board.isValidMove(testMove)) {
                        hasValidMove = true;
                    }
                }
            }
            if (hasValidMove) break;
        }
        board.setSelectedPiece(null);

        if (!hasValidMove) {
            gameOver = true;

            Piece king = board.findKing(whiteTurn);
            if (king != null) {
                Move dummyMove = new Move(board, king, king.col, king.row);
                board.setSelectedPiece(king);
                boolean inCheck = board.isKingInCheck(dummyMove);
                board.setSelectedPiece(null);

                if (inCheck) {
                    result = (whiteTurn ? "Black" : "White") + " Wins!";
                } else {
                    result = "Draw (Stalemate)";
                }
            } else {
                result = (whiteTurn ? "Black" : "White") + " Wins!";
            }

            if (client != null) {
                client.sendGameOver(result);
            }

            updateStatus("Game Over - " + result);
        }
    }

    public void reset() {
        whiteTurn = true;
        gameOver = false;
        result = null;
        myTurn = playerIsWhite;
        board.reset();
    }
}
