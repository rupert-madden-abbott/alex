package com.alex.game;

import java.awt.*;

public class MainCharacter {
    private int x, y;
    private int width = 40, height = 60;

    public MainCharacter(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // --- Body ---
        g2.setColor(new Color(34, 139, 34)); // Forest green dress
        g2.fillRoundRect(x + 10, y + 30, 20, 28, 12, 18);
        g2.setColor(Color.BLACK); // Outline
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + 10, y + 30, 20, 28, 12, 18);
        // --- Head ---
        g2.setColor(new Color(255, 224, 189)); // Light skin tone
        g2.fillOval(x + 8, y + 8, 24, 24);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x + 8, y + 8, 24, 24);
        // --- Hair (purple) ---
        g2.setColor(new Color(160, 32, 240)); // Purple hair
        g2.fillArc(x + 8, y + 8, 24, 24, 0, 180); // Top hair
        g2.fillOval(x + 6, y + 18, 8, 12); // Left side hair
        g2.fillOval(x + 26, y + 18, 8, 12); // Right side hair
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawArc(x + 8, y + 8, 24, 24, 0, 180);
        g2.drawOval(x + 6, y + 18, 8, 12);
        g2.drawOval(x + 26, y + 18, 8, 12);
        // --- Outdoorsman's Hat ---
        g2.setColor(new Color(120, 100, 60)); // Brown hat
        g2.fillArc(x + 10, y + 5, 20, 12, 0, 180); // Hat top
        g2.setColor(new Color(90, 70, 40)); // Darker brim
        g2.fillRoundRect(x + 7, y + 16, 26, 5, 6, 6); // Hat brim
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawArc(x + 10, y + 5, 20, 12, 0, 180);
        g2.drawRoundRect(x + 7, y + 16, 26, 5, 6, 6);
        // --- Eyes ---
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 15, y + 18, 3, 3);
        g2.fillOval(x + 22, y + 18, 3, 3);
        // --- Smile ---
        g2.setColor(new Color(180, 80, 80));
        g2.drawArc(x + 15, y + 23, 10, 6, 0, -180);
        // --- Arms ---
        g2.setStroke(new BasicStroke(3));
        g2.setColor(new Color(255, 224, 189));
        g2.drawLine(x + 10, y + 38, x, y + 55); // Left arm
        g2.drawLine(x + 30, y + 38, x + 40, y + 55); // Right arm
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x + 10, y + 38, x, y + 55);
        g2.drawLine(x + 30, y + 38, x + 40, y + 55);
        // --- Legs ---
        g2.setStroke(new BasicStroke(4));
        g2.setColor(new Color(120, 80, 40)); // Brown boots
        g2.drawLine(x + 16, y + 58, x + 16, y + 70); // Left leg
        g2.drawLine(x + 24, y + 58, x + 24, y + 70); // Right leg
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x + 16, y + 58, x + 16, y + 70);
        g2.drawLine(x + 24, y + 58, x + 24, y + 70);
        // --- Hands ---
        g2.setStroke(new BasicStroke(1));
        g2.setColor(new Color(255, 224, 189));
        g2.fillOval(x - 3, y + 53, 6, 6); // Left hand
        g2.fillOval(x + 37, y + 53, 6, 6); // Right hand
        g2.setColor(Color.BLACK);
        g2.drawOval(x - 3, y + 53, 6, 6);
        g2.drawOval(x + 37, y + 53, 6, 6);
    }

    public void move(int dx, int dy, int minX, int minY, int maxX) {
        x = Math.max(minX, Math.min(x + dx, maxX - width));
        y = minY; // Always on the floor
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
