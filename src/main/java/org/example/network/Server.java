package org.example.network;

import java.io.*;
import java.net.*;

public class Server {

    private static final int DEFAULT_PORT = 5000;

    private ClientHandler white;
    private ClientHandler black;
    private boolean whiteTurn = true;

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
            System.out.println("Waiting for 2 players...");

            // wait for player 1
            Socket socket1 = serverSocket.accept();
            white = new ClientHandler(socket1);
            white.sendMessage("COLOR white");
            System.out.println("Player 1 (White) connected: " + socket1.getInetAddress());
            white.sendMessage("WAIT");

            // wait for player 2
            Socket socket2 = serverSocket.accept();
            black = new ClientHandler(socket2);
            black.sendMessage("COLOR black");
            System.out.println("Player 2 (Black) connected: " + socket2.getInetAddress());

            white.sendMessage("START");
            black.sendMessage("START");
            System.out.println("Game started!");

            gameLoop();

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private void gameLoop() {
        // keep reading messages until someone disconnects
        boolean playAgainWhite = false;
        boolean playAgainBlack = false;

        while (true) {
            try {
                ClientHandler current = whiteTurn ? white : black;
                ClientHandler opponent = whiteTurn ? black : white;

                String message = current.readMessage();
                if (message == null) {
                    System.out.println("A player disconnected.");
                    break;
                }

                System.out.println("[" + (whiteTurn ? "White" : "Black") + "] " + message);

                if (message.startsWith("MOVE ")) {
                    opponent.sendMessage(message);
                    whiteTurn = !whiteTurn;

                } else if (message.startsWith("GAMEOVER ")) {
                    opponent.sendMessage(message);
                    System.out.println("Game over: " + message);
                    playAgainWhite = false;
                    playAgainBlack = false;

                } else if (message.equals("PLAYAGAIN")) {
                    if (current == white) playAgainWhite = true;
                    else playAgainBlack = true;

                    System.out.println((current == white ? "White" : "Black") + " wants to play again");

                    if (playAgainWhite && playAgainBlack) {
                        white.sendMessage("RESET");
                        black.sendMessage("RESET");
                        whiteTurn = true;
                        playAgainWhite = false;
                        playAgainBlack = false;
                        System.out.println("Both players agreed. New game!");
                    }
                }

            } catch (IOException e) {
                System.out.println("Connection lost: " + e.getMessage());
                break;
            }
        }
    }
}
