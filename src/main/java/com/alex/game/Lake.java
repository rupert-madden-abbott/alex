package com.alex.game;

import java.awt.*;
import java.util.Random;

public class Lake {
    private int x, width, depth;
    public Lake(int x, int width, int depth) {
        this.x = x;
        this.width = width;
        this.depth = depth;
    }
    public int getX() { return x; }
    public int getWidth() { return width; }
    public void draw(Graphics g, int floorY, int offsetX) {
        Graphics2D g2 = (Graphics2D) g;
        int y = floorY - depth + 10;
        // Water
        g2.setColor(new Color(70, 180, 230));
        g2.fillOval(x + offsetX, y, width, depth);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x + offsetX, y, width, depth);
        // Draw fish (random, deterministic)
        Random r = new Random(x * 12345L + width * 99L);
        int fishCount = 1 + r.nextInt(3);
        for (int i = 0; i < fishCount; i++) {
            int fx = x + offsetX + 10 + r.nextInt(Math.max(1, width - 30));
            int fy = y + 10 + r.nextInt(Math.max(1, depth - 20));
            drawFish(g2, fx, fy, r.nextBoolean());
        }
    }
    private void drawFish(Graphics2D g2, int x, int y, boolean flip) {
        g2.setColor(new Color(255, 180, 60));
        int w = 18, h = 8;
        if (flip) x += 8;
        g2.fillOval(x, y, w, h);
        Polygon tail = new Polygon();
        if (!flip) {
            tail.addPoint(x, y + h/2);
            tail.addPoint(x - 8, y);
            tail.addPoint(x - 8, y + h);
        } else {
            tail.addPoint(x + w, y + h/2);
            tail.addPoint(x + w + 8, y);
            tail.addPoint(x + w + 8, y + h);
        }
        g2.setColor(new Color(255, 120, 40));
        g2.fillPolygon(tail);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(x, y, w, h);
        g2.drawPolygon(tail);
        g2.fillOval(x + (flip ? w-3 : 2), y + 2, 3, 3); // Eye
    }
}

