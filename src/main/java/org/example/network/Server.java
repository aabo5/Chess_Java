package org.example.network;

import java.io.*;
import java.net.*;

/**
 * Thread-safe, multi-threaded TCP server for Chess Online.
 * Listens on a target port, accepts exactly 2 client connections (White and Black),
 * spins up concurrent reader loops, relays move and rematch coordinates, and cleans up
 * on disconnect to prepare for the next session.
 */
public class Server {

    private static final int DEFAULT_PORT = 5007;

    private ClientHandler white;
    private ClientHandler black;
    private boolean whiteTurn = true;

    private boolean playAgainWhite = false;
    private boolean playAgainBlack = false;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); }
            catch (NumberFormatException e) { System.out.println("Bad port, using " + DEFAULT_PORT); }
        }
        new Server().start(port);
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("=== Chess Server started on port " + port + " ===");
            
            while (true) {
                System.out.println("Waiting for Player 1 (White)...");
                try {
                    Socket socket1 = serverSocket.accept();
                    white = new ClientHandler(socket1);
                    white.sendMessage("COLOR white");
                    System.out.println("Player 1 (White) connected: " + socket1.getInetAddress());
                    white.sendMessage("WAIT");

                    System.out.println("Waiting for Player 2 (Black)...");
                    Socket socket2 = serverSocket.accept();
                    black = new ClientHandler(socket2);
                    black.sendMessage("COLOR black");
                    System.out.println("Player 2 (Black) connected: " + socket2.getInetAddress());

                    white.sendMessage("START");
                    black.sendMessage("START");
                    System.out.println("Game started!");

                    // Reset session state variables
                    whiteTurn = true;
                    playAgainWhite = false;
                    playAgainBlack = false;

                    // Start reader threads for both clients
                    Thread t1 = startClientReader(white, true);
                    Thread t2 = startClientReader(black, false);

                    // Wait for both threads to finish
                    t1.join();
                    t2.join();

                } catch (Exception e) {
                    System.out.println("Lobby session error or interrupted: " + e.getMessage());
                } finally {
                    System.out.println("Cleaning up game session...");
                    closeGame();
                }
            }
        } catch (IOException e) {
            System.out.println("Server socket error: " + e.getMessage());
        }
    }

    private Thread startClientReader(ClientHandler client, boolean isWhite) {
        Thread t = new Thread(() -> {
            try {
                String message;
                while ((message = client.readMessage()) != null) {
                    handleClientMessage(isWhite, message);
                }
            } catch (IOException e) {
                System.out.println((isWhite ? "White" : "Black") + " read error: " + e.getMessage());
            } finally {
                handleDisconnect(isWhite);
            }
        });
        t.start();
        return t;
    }

    private synchronized void handleClientMessage(boolean isWhite, String message) {
        System.out.println("[" + (isWhite ? "White" : "Black") + "] " + message);

        if (message.startsWith("MOVE ")) {
            ClientHandler opponent = isWhite ? black : white;
            if (opponent != null) {
                opponent.sendMessage(message);
            }
            whiteTurn = !whiteTurn;

        } else if (message.startsWith("GAMEOVER ")) {
            ClientHandler opponent = isWhite ? black : white;
            if (opponent != null) {
                opponent.sendMessage(message);
            }
            System.out.println("Game over: " + message);
            playAgainWhite = false;
            playAgainBlack = false;

        } else if (message.equals("PLAYAGAIN")) {
            if (isWhite) playAgainWhite = true;
            else playAgainBlack = true;

            System.out.println((isWhite ? "White" : "Black") + " wants to play again");

            if (playAgainWhite && playAgainBlack) {
                if (white != null) white.sendMessage("RESET");
                if (black != null) black.sendMessage("RESET");
                whiteTurn = true;
                playAgainWhite = false;
                playAgainBlack = false;
                System.out.println("Both players agreed. New game!");
            }
        }
    }

    private synchronized void handleDisconnect(boolean isWhite) {
        System.out.println((isWhite ? "White" : "Black") + " connection closed.");
        // Close both player connections to end the session and notify clients
        closeGame();
    }

    private synchronized void closeGame() {
        if (white != null) {
            white.close();
            white = null;
        }
        if (black != null) {
            black.close();
            black = null;
        }
    }
}
