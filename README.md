# Chess Game (Client-Server)

This project is a multiplayer chess game using Java.

## Features
- Client-server architecture
- AWS-hosted server
- GUI for players

## Directory Structure

```
src/main/java/org/example/
├── main/
│   └── Main.java                 # Application entry point
├── ui/
│   ├── StartScreen.java          # Game start screen
│   ├── EndScreen.java            # Game end screen
│   └── GamePanel.java            # Game board UI
├── network/
│   ├── Server.java               # Server logic
│   ├── Client.java               # Client logic
│   └── ClientHandler.java        # Server-side client handling
└── game/
    ├── GameManager.java          # Game state management
    ├── Board.java                # Chess board logic
    ├── Move.java                 # Move representation
    └── pieces/
        ├── Piece.java            # Base piece class
        ├── Pawn.java
        ├── Rook.java
        ├── Knight.java
        ├── Bishop.java
        ├── Queen.java
        └── King.java
```

## How to Run
1. Run server on AWS
2. Run client on local machine
3. Connect using server IP
