package com.alex.game;

import java.awt.*;
import java.util.Random;

public class Tree {
    private int x;
    private int trunkHeight;
    private int trunkWidth;
    private int canopyWidth;
    private int canopyHeight;
    private Color trunkColor = new Color(90, 60, 30);
    private Color leafColor = new Color(34, 139, 34);

    public Tree(int x, int trunkHeight, int trunkWidth, int canopyWidth, int canopyHeight) {
        this.x = x;
        this.trunkHeight = trunkHeight;
        this.trunkWidth = trunkWidth;
        this.canopyWidth = canopyWidth;
        this.canopyHeight = canopyHeight;
    }

    public void draw(Graphics g, int floorY, int offsetX) {
        Graphics2D g2 = (Graphics2D) g;
        // Draw trunk
        g2.setColor(trunkColor);
        int trunkY = floorY - trunkHeight;
        g2.fillRect(x + offsetX, trunkY, trunkWidth, trunkHeight);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x + offsetX, trunkY, trunkWidth, trunkHeight);
        // Draw canopy (leaves)
        g2.setColor(leafColor);
        g2.fillOval(x + offsetX - (canopyWidth - trunkWidth) / 2, trunkY - canopyHeight / 2, canopyWidth, canopyHeight);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x + offsetX - (canopyWidth - trunkWidth) / 2, trunkY - canopyHeight / 2, canopyWidth, canopyHeight);
    }
}
