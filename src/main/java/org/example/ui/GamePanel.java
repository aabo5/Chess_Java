package org.example.ui;

import org.example.game.Board;
import org.example.game.GameManager;
import org.example.game.Move;
import org.example.game.pieces.Piece;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Main Game Panel container UI.
 * Arranges the 600x600 Chess Board, left and bottom notation rails, ELO stat panels,
 * a move log scrollpane, and game resign/offer/undo control decks.
 */
public class GamePanel extends JPanel {

    private Board board;
    private GameManager gameManager;

    private MarqueePanel marqueePanel;
    private JTextArea historyTextArea;
    private JLabel opponentNameLabel;
    private JLabel playerNameLabel;
    private JPanel leftRail;
    private JPanel bottomRail;

    public GamePanel() {
        initComponents();
        setupGame();
    }

    private void initComponents() {
        setBackground(new Color(16, 20, 21)); // background `#101415`
        setLayout(new BorderLayout());

        // 1. Top Marquee Bar
        marqueePanel = new MarqueePanel();
        marqueePanel.setText("CONNECTING...");
        add(marqueePanel, BorderLayout.NORTH);

        // 2. Main Content Split Panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        add(mainContent, BorderLayout.CENTER);

        // 3. Sidebar (West)
        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(16, 16, 16, 8));
        sidebar.setLayout(new GridBagLayout());
        mainContent.add(sidebar, BorderLayout.WEST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Opponent Panel
        JPanel opponentPanel = createStatsPanel("OPPONENT", "GM_KASPAROV", "ELO: 2850", "09:42", new Color(105, 0, 5));
        opponentNameLabel = (JLabel) opponentPanel.getClientProperty("nameLabel");
        gbc.gridy = 0;
        gbc.weighty = 0.25;
        gbc.insets = new Insets(0, 0, 12, 0);
        sidebar.add(opponentPanel, gbc);

        // Move History Panel
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(11, 15, 16)); // surface-lowest
        historyPanel.setBorder(new LineBorder(new Color(141, 146, 138), 2)); // outline
        
        JLabel historyHeader = new JLabel(" MOVE_HISTORY_LOG");
        historyHeader.setFont(FontManager.getMonoFont(Font.BOLD, 10f));
        historyHeader.setForeground(new Color(176, 207, 173)); // primary
        historyHeader.setBorder(new EmptyBorder(6, 6, 6, 6));
        historyPanel.add(historyHeader, BorderLayout.NORTH);

        historyTextArea = new JTextArea();
        historyTextArea.setBackground(new Color(11, 15, 16));
        historyTextArea.setForeground(new Color(176, 207, 173));
        historyTextArea.setFont(FontManager.getMonoFont(Font.PLAIN, 12f));
        historyTextArea.setEditable(false);
        historyTextArea.setFocusable(false);
        historyTextArea.setMargin(new Insets(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(11, 15, 16));
        scrollPane.getVerticalScrollBar().setBackground(new Color(11, 15, 16));
        scrollPane.getHorizontalScrollBar().setBackground(new Color(11, 15, 16));
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        gbc.gridy = 1;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(0, 0, 12, 0);
        sidebar.add(historyPanel, gbc);

        // Player Panel
        JPanel playerPanel = createStatsPanel("YOU", "PLAYER_01", "ELO: 1420", "07:15", new Color(29, 54, 30));
        playerNameLabel = (JLabel) playerPanel.getClientProperty("nameLabel");
        gbc.gridy = 2;
        gbc.weighty = 0.25;
        gbc.insets = new Insets(0, 0, 0, 0);
        sidebar.add(playerPanel, gbc);

        // 4. Board wrapper (Center)
        JPanel boardArea = new JPanel(new GridBagLayout());
        boardArea.setOpaque(false);
        boardArea.setBorder(new EmptyBorder(16, 8, 16, 16));
        mainContent.add(boardArea, BorderLayout.CENTER);

        // Grid positions inside boardArea:
        // Left rail (cols 0, row 0)
        // Board (cols 1, row 0)
        // Bottom rail (cols 1, row 1)
        // Controls (cols 1, row 2)

        GridBagConstraints boardGbc = new GridBagConstraints();

        // Left notation rail
        leftRail = new JPanel(new GridLayout(8, 1));
        leftRail.setOpaque(false);
        leftRail.setPreferredSize(new Dimension(24, 600));
        for (int i = 8; i >= 1; i--) {
            JLabel lbl = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            lbl.setFont(FontManager.getMonoFont(Font.BOLD, 12f));
            lbl.setForeground(new Color(141, 146, 138)); // outline
            leftRail.add(lbl);
        }
        boardGbc.gridx = 0;
        boardGbc.gridy = 0;
        boardGbc.fill = GridBagConstraints.VERTICAL;
        boardGbc.insets = new Insets(0, 0, 8, 4);
        boardArea.add(leftRail, boardGbc);

        // Chess Board Component
        board = new Board();
        board.setBorder(new LineBorder(new Color(141, 146, 138), 3)); // 3px border
        boardGbc.gridx = 1;
        boardGbc.gridy = 0;
        boardGbc.fill = GridBagConstraints.NONE;
        boardGbc.insets = new Insets(0, 0, 0, 0);
        boardArea.add(board, boardGbc);

        // Bottom notation rail
        bottomRail = new JPanel(new GridLayout(1, 8));
        bottomRail.setOpaque(false);
        bottomRail.setPreferredSize(new Dimension(600, 24));
        char[] files = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        for (char f : files) {
            JLabel lbl = new JLabel(String.valueOf(f), SwingConstants.CENTER);
            lbl.setFont(FontManager.getMonoFont(Font.BOLD, 12f));
            lbl.setForeground(new Color(141, 146, 138));
            bottomRail.add(lbl);
        }
        boardGbc.gridx = 1;
        boardGbc.gridy = 1;
        boardGbc.fill = GridBagConstraints.HORIZONTAL;
        boardGbc.insets = new Insets(4, 0, 16, 0);
        boardArea.add(bottomRail, boardGbc);

        // Controls Deck
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        controls.setOpaque(false);

        RetroButton resignBtn = new RetroButton("RESIGN", new Color(39, 43, 44), new Color(224, 227, 228), new Color(224, 227, 228), new Color(39, 43, 44));
        resignBtn.setFont(FontManager.getMonoFont(Font.BOLD, 11f));
        resignBtn.setPreferredSize(new Dimension(130, 36));
        resignBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "COMMUNICATION PROTOCOL: Resignation not supported by remote server.", "Info", JOptionPane.INFORMATION_MESSAGE));
        controls.add(resignBtn);

        RetroButton drawBtn = new RetroButton("OFFER DRAW", new Color(39, 43, 44), new Color(224, 227, 228), new Color(224, 227, 228), new Color(39, 43, 44));
        drawBtn.setFont(FontManager.getMonoFont(Font.BOLD, 11f));
        drawBtn.setPreferredSize(new Dimension(130, 36));
        drawBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "COMMUNICATION PROTOCOL: Draw offer not supported by remote server.", "Info", JOptionPane.INFORMATION_MESSAGE));
        controls.add(drawBtn);

        RetroButton undoBtn = new RetroButton("UNDO", new Color(176, 207, 173), new Color(29, 54, 30), new Color(29, 54, 30), new Color(176, 207, 173));
        undoBtn.setFont(FontManager.getMonoFont(Font.BOLD, 11f));
        undoBtn.setPreferredSize(new Dimension(130, 36));
        undoBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "COMMUNICATION PROTOCOL: Undo not supported in network matches.", "Info", JOptionPane.INFORMATION_MESSAGE));
        controls.add(undoBtn);

        boardGbc.gridx = 1;
        boardGbc.gridy = 2;
        boardGbc.fill = GridBagConstraints.NONE;
        boardGbc.insets = new Insets(0, 0, 0, 0);
        boardArea.add(controls, boardGbc);
    }

    private JPanel createStatsPanel(String header, String name, String elo, String timer, Color timerColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(28, 32, 33)); // surface-container `#1c2021`
        panel.setBorder(new LineBorder(new Color(141, 146, 138), 2)); // outline

        JPanel inner = new JPanel(new GridBagLayout());
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(8, 12, 8, 12));
        panel.add(inner, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;

        JLabel lblHeader = new JLabel(header);
        lblHeader.setFont(FontManager.getMonoFont(Font.BOLD, 9f));
        lblHeader.setForeground(new Color(195, 200, 191)); // on-surface-variant
        c.gridy = 0;
        inner.add(lblHeader, c);

        JLabel lblName = new JLabel(name);
        lblName.setFont(FontManager.getHeaderFont(18f));
        lblName.setForeground(new Color(224, 227, 228)); // on-surface
        c.gridy = 1;
        c.insets = new Insets(2, 0, 2, 0);
        inner.add(lblName, c);
        panel.putClientProperty("nameLabel", lblName);

        JLabel lblElo = new JLabel(elo);
        lblElo.setFont(FontManager.getMonoFont(Font.PLAIN, 10f));
        lblElo.setForeground(new Color(176, 207, 173)); // primary
        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 0);
        inner.add(lblElo, c);

        JLabel lblTimer = new JLabel(timer);
        lblTimer.setFont(FontManager.getMonoFont(Font.BOLD, 18f));
        lblTimer.setForeground(new Color(224, 227, 228));
        lblTimer.setOpaque(true);
        lblTimer.setBackground(timerColor);
        lblTimer.setHorizontalAlignment(SwingConstants.CENTER);
        lblTimer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(141, 146, 138), 1),
                new EmptyBorder(2, 8, 2, 8)
        ));
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(6, 0, 0, 0);
        inner.add(lblTimer, c);

        return panel;
    }

    private void setupGame() {
        board = getBoard(); // Re-use the instantiated board
        gameManager = new GameManager(board);
        gameManager.setStatusLabel(new JLabel() {
            @Override
            public void setText(String text) {
                setStatus(text);
            }
        });

        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (gameManager.isGameOver()) return;
                if (!gameManager.isMyTurn()) return;

                int col = board.getBoardCol(evt.getX());
                int row = board.getBoardRow(evt.getY());

                Piece piece = board.getPiece(col, row);
                if (piece != null && piece.isWhite == gameManager.isPlayerWhite()) {
                    board.setSelectedPiece(piece);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                Piece selected = board.getSelectedPiece();
                if (selected == null) return;

                int newCol = board.getBoardCol(evt.getX());
                int newRow = board.getBoardRow(evt.getY());

                boolean ok = gameManager.tryMove(selected.col, selected.row, newCol, newRow);

                if (!ok) {
                    selected.xPos = selected.col * Board.TILE_SIZE;
                    selected.yPos = selected.row * Board.TILE_SIZE;
                } else {
                    updateMoveHistoryLog();
                }

                board.setSelectedPiece(null);
                board.repaint();
            }
        });

        board.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                Piece selected = board.getSelectedPiece();
                if (selected != null) {
                    selected.xPos = evt.getX() - Board.TILE_SIZE / 2;
                    selected.yPos = evt.getY() - Board.TILE_SIZE / 2;
                    board.repaint();
                }
            }
        });
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public Board getBoard() {
        return board;
    }

    public void setStatus(String text) {
        SwingUtilities.invokeLater(() -> {
            marqueePanel.setText(text);
            updateMoveHistoryLog();
            
            // Adjust player tags dynamically based on assigned colors
            if (gameManager != null) {
                if (gameManager.isPlayerWhite()) {
                    playerNameLabel.setText("PLAYER_01 (W)");
                    opponentNameLabel.setText("GM_KASPAROV (B)");
                } else {
                    playerNameLabel.setText("PLAYER_01 (B)");
                    opponentNameLabel.setText("GM_KASPAROV (W)");
                }
            }
        });
    }

    public void updateMoveHistoryLog() {
        List<Move> history = board.getMoveHistory();
        StringBuilder sb = new StringBuilder();
        sb.append("MOVE_HISTORY_LOG\n");
        sb.append("================\n");
        for (int i = 0; i < history.size(); i += 2) {
            int moveNum = (i / 2) + 1;
            Move whiteMove = history.get(i);
            String whiteStr = formatMove(whiteMove);
            String blackStr = "";
            if (i + 1 < history.size()) {
                Move blackMove = history.get(i + 1);
                blackStr = formatMove(blackMove);
            }
            sb.append(String.format("%d. %-8s %s\n", moveNum, whiteStr, blackStr));
        }
        historyTextArea.setText(sb.toString());
        historyTextArea.setCaretPosition(historyTextArea.getDocument().getLength());
    }

    private String formatMove(Move m) {
        if (m == null || m.piece == null) return "";
        String pName = m.piece.name;
        String prefix = "";
        if (pName.equals("Knight")) prefix = "N";
        else if (pName.equals("Bishop")) prefix = "B";
        else if (pName.equals("Rook")) prefix = "R";
        else if (pName.equals("Queen")) prefix = "Q";
        else if (pName.equals("King")) prefix = "K";

        char colChar = (char) ('a' + m.newCol);
        int rowNum = 8 - m.newRow;

        String action = m.captured != null ? "x" : "";

        return prefix + action + colChar + rowNum;
    }

    public void updateNotation(boolean isWhite) {
        leftRail.removeAll();
        if (isWhite) {
            for (int i = 8; i >= 1; i--) {
                JLabel lbl = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                lbl.setFont(FontManager.getMonoFont(Font.BOLD, 12f));
                lbl.setForeground(new Color(141, 146, 138));
                leftRail.add(lbl);
            }
        } else {
            for (int i = 1; i <= 8; i++) {
                JLabel lbl = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                lbl.setFont(FontManager.getMonoFont(Font.BOLD, 12f));
                lbl.setForeground(new Color(141, 146, 138));
                leftRail.add(lbl);
            }
        }
        
        bottomRail.removeAll();
        char[] files = isWhite ? new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'} : new char[]{'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        for (char f : files) {
            JLabel lbl = new JLabel(String.valueOf(f), SwingConstants.CENTER);
            lbl.setFont(FontManager.getMonoFont(Font.BOLD, 12f));
            lbl.setForeground(new Color(141, 146, 138));
            bottomRail.add(lbl);
        }
        
        leftRail.revalidate();
        leftRail.repaint();
        bottomRail.revalidate();
        bottomRail.repaint();
    }
}
