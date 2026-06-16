package org.example.network;

import java.io.*;
import java.net.*;

/**
 * Socket client for the Chess Online application.
 * Manages socket connection to the server, reads messages asynchronously in a background
 * thread, parses received command tokens, and alerts UI screen listeners.
 */
public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected = false;
    private boolean protocolError = false;

    public boolean isProtocolError() { return protocolError; }

    public void close() {
        connected = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            // ignore
        }
    }

    public interface ClientListener {
        void onColorAssigned(boolean isWhite);
        void onWaiting();
        void onGameStart();
        void onOpponentMove(int oldCol, int oldRow, int newCol, int newRow);
        void onGameOver(String result);
        void onReset();
        void onDisconnected();
    }

    private ClientListener listener;

    public void setListener(ClientListener listener) {
        this.listener = listener;
    }

    public void connect(String host, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                connected = true;
                System.out.println("Connected to server at " + host + ":" + port);

                String message;
                while ((message = in.readLine()) != null) {
                    handleMessage(message);
                }

            } catch (IOException e) {
                System.out.println("Connection failed: " + e.getMessage());
            } finally {
                connected = false;
                if (listener != null) listener.onDisconnected();
            }
        }).start();
    }

    private void handleMessage(String message) {
        // process data received from server
        System.out.println("[Client received] " + message);

        String upper = message.toUpperCase();
        if (upper.contains("<HTML") || upper.startsWith("<!DOCTYPE") || upper.startsWith("HTTP/")) {
            System.out.println("Non-chess server detected, closing connection.");
            protocolError = true;
            close();
            return;
        }

        if (message.startsWith("COLOR ")) {
            boolean isWhite = message.equals("COLOR white");
            if (listener != null) listener.onColorAssigned(isWhite);

        } else if (message.equals("WAIT")) {
            if (listener != null) listener.onWaiting();

        } else if (message.equals("START")) {
            if (listener != null) listener.onGameStart();

        } else if (message.startsWith("MOVE ")) {
            // parse "MOVE 2,6,2,4"
            String[] parts = message.substring(5).split(",");
            int oldCol = Integer.parseInt(parts[0]);
            int oldRow = Integer.parseInt(parts[1]);
            int newCol = Integer.parseInt(parts[2]);
            int newRow = Integer.parseInt(parts[3]);
            if (listener != null) listener.onOpponentMove(oldCol, oldRow, newCol, newRow);

        } else if (message.startsWith("GAMEOVER ")) {
            String result = message.substring(9);
            if (listener != null) listener.onGameOver(result);

        } else if (message.equals("RESET")) {
            if (listener != null) listener.onReset();
        }
    }

    public void sendMove(int oldCol, int oldRow, int newCol, int newRow) {
        if (connected) {
            out.println("MOVE " + oldCol + "," + oldRow + "," + newCol + "," + newRow);
        }
    }

    public void sendGameOver(String result) {
        if (connected) {
            out.println("GAMEOVER " + result);
        }
    }

    public void sendPlayAgain() {
        if (connected) {
            out.println("PLAYAGAIN");
        }
    }

    public boolean isConnected() { return connected; }
}
