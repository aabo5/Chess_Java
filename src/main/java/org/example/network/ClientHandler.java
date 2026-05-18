package org.example.network;

import java.io.*;
import java.net.*;

public class ClientHandler {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String readMessage() throws IOException {
        return in.readLine();
    }

    public void close() {
        try { socket.close(); } catch (IOException e) { /* ignore */ }
    }
}
