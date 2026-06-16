package org.example.ui;

import java.awt.Font;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FontManager {
    public static Font spaceGrotesk;
    public static Font jetBrainsMono;

    static {
        spaceGrotesk = loadCachedFont("SpaceGrotesk-Bold.ttf", "https://fonts.gstatic.com/s/spacegrotesk/v15/V8mQoQDjQSkFtoMM3T6r8E7mF71Q-g.ttf", "Dialog", Font.BOLD);
        jetBrainsMono = loadCachedFont("JetBrainsMono-Regular.ttf", "https://fonts.gstatic.com/s/jetbrainsmono/v18/tU3A0oU1YOW37pqQ_4tZaA.ttf", "Monospaced", Font.PLAIN);
    }

    private static Font loadCachedFont(String filename, String urlStr, String fallbackFamily, int fallbackStyle) {
        Path cacheDir = Paths.get(System.getProperty("user.home"), ".chess_terminal_cache");
        Path fontPath = cacheDir.resolve(filename);
        try {
            if (!Files.exists(fontPath)) {
                Files.createDirectories(cacheDir);
                System.out.println("Downloading font " + filename + " from Google Fonts...");
                try (InputStream in = new URL(urlStr).openStream()) {
                    Files.copy(in, fontPath);
                }
            }
            return Font.createFont(Font.TRUETYPE_FONT, fontPath.toFile());
        } catch (Exception e) {
            System.err.println("Could not load/download " + filename + ", using system fallback: " + fallbackFamily);
            return new Font(fallbackFamily, fallbackStyle, 12);
        }
    }

    public static Font getHeaderFont(float size) {
        return spaceGrotesk.deriveFont(size);
    }

    public static Font getMonoFont(int style, float size) {
        return jetBrainsMono.deriveFont(style, size);
    }
}
