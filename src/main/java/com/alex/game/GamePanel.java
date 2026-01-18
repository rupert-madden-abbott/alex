package com.alex.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel {
    private enum GameState { OUTSIDE, INSIDE_HOME }
    private GameState state = GameState.OUTSIDE;
    private int worldX = 100; // Character's world position
    private int cameraX = 0; // Camera offset
    private final int HOME_X = 0; // Home's world X position
    private final int DOOR_X = HOME_X + 60; // Door's world X (relative to home)
    private final int CHARACTER_WIDTH = 40;
    private final int CHARACTER_HEIGHT = 60;
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 480;
    private final int FLOOR_Y = 400;
    private MainCharacter character;
    private List<Tree> trees;
    private Random worldRand = new Random(42); // Deterministic seed

    // For inside house movement
    private int playerRoomX = 400; // Centered at door
    private int playerRoomY = FLOOR_Y - 60;
    private int playerRoomMinX = 120;
    private int playerRoomMaxX = 680;
    // Family member positions (inside house)
    private int husbandX = 250, husbandY = FLOOR_Y - 60;
    private int childX = 520, childY = FLOOR_Y - 50;
    private int husbandTargetX = 250;
    private int childTargetX = 520;
    private javax.swing.Timer wanderTimer;
    private Random rand = new Random();

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(new Color(120, 180, 120));
        character = new MainCharacter(worldX, FLOOR_Y - CHARACTER_HEIGHT);
        setFocusable(true);
        requestFocusInWindow();
        // Key listener for movement and state
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (state == GameState.OUTSIDE) {
                    if (key == KeyEvent.VK_LEFT) {
                        int newWorldX = Math.max(worldX - 10, HOME_X);
                        if (!isBlockedByLake(newWorldX)) {
                            worldX = newWorldX;
                            updateCamera();
                            repaint();
                        }
                    } else if (key == KeyEvent.VK_RIGHT) {
                        int newWorldX = worldX + 10;
                        if (!isBlockedByLake(newWorldX)) {
                            worldX = newWorldX;
                            updateCamera();
                            repaint();
                        }
                    } else if (key == KeyEvent.VK_UP) {
                        if (isAtDoor()) {
                            state = GameState.INSIDE_HOME;
                            repaint();
                        }
                    }
                } else if (state == GameState.INSIDE_HOME) {
                    if (key == KeyEvent.VK_LEFT) {
                        playerRoomX = Math.max(playerRoomMinX, playerRoomX - 10);
                        repaint();
                    } else if (key == KeyEvent.VK_RIGHT) {
                        playerRoomX = Math.min(playerRoomMaxX, playerRoomX + 10);
                        repaint();
                    } else if (key == KeyEvent.VK_ENTER) {
                        // Only allow exit if at door
                        if (playerRoomX >= 370 && playerRoomX <= 430) {
                            state = GameState.OUTSIDE;
                            repaint();
                        }
                    }
                }
            }
        });
        wanderTimer = new javax.swing.Timer(1200, e -> {
            if (state == GameState.INSIDE_HOME) {
                // Occasionally wander
                if (rand.nextDouble() < 0.5) husbandTargetX = 140 + rand.nextInt(520);
                if (rand.nextDouble() < 0.7) childTargetX = 140 + rand.nextInt(520);
                // Move towards target
                if (Math.abs(husbandX - husbandTargetX) > 2) husbandX += (int)Math.signum(husbandTargetX - husbandX) * 6;
                if (Math.abs(childX - childTargetX) > 2) childX += (int)Math.signum(childTargetX - childX) * 8;
                repaint();
            }
        });
        wanderTimer.start();
    }

    private void updateCamera() {
        // Keep character near center, but don't scroll past home
        cameraX = worldX - SCREEN_WIDTH / 2 + CHARACTER_WIDTH / 2;
        if (cameraX < 0) cameraX = 0;
    }

    private boolean isAtDoor() {
        // Character is at the door if her worldX overlaps the door area
        return (worldX + CHARACTER_WIDTH/2) >= DOOR_X && (worldX + CHARACTER_WIDTH/2) <= (DOOR_X + 20);
    }

    private List<Tree> getVisibleTrees() {
        // Generate trees for visible area using deterministic positions
        List<Tree> visible = new ArrayList<>();
        int start = (cameraX / 80) - 2;
        int end = ((cameraX + SCREEN_WIDTH) / 80) + 2;
        for (int i = start; i <= end; i++) {
            if (i < 1) continue; // No trees at home
            int tx = i * 80 + 40;
            Random r = new Random(i * 1000L + 42); // Deterministic per tree
            int trunkHeight = 120 + r.nextInt(220);
            if (r.nextDouble() < 0.3) trunkHeight += 100 + r.nextInt(100);
            int trunkWidth = 18 + r.nextInt(8);
            int canopyWidth = trunkWidth * (3 + r.nextInt(2));
            int canopyHeight = trunkHeight / 2 + r.nextInt(trunkHeight / 2);
            visible.add(new Tree(tx, trunkHeight, trunkWidth, canopyWidth, canopyHeight));
        }
        return visible;
    }

    private List<Lake> getVisibleLakes() {
        List<Lake> lakes = new ArrayList<>();
        int start = (cameraX / 400) - 1;
        int end = ((cameraX + SCREEN_WIDTH) / 400) + 1;
        for (int i = start; i <= end; i++) {
            if (i < 1) continue; // No lakes at home
            Random r = new Random(i * 7777L + 99);
            if (r.nextDouble() < 0.25) { // 25% chance for a lake in this segment
                int lx = i * 400 + 120 + r.nextInt(80);
                int width = 120 + r.nextInt(60);
                int depth = 30 + r.nextInt(20);
                lakes.add(new Lake(lx, width, depth));
            }
        }
        return lakes;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (state == GameState.OUTSIDE) {
            // Draw the floor
            g.setColor(new Color(80, 60, 40));
            g.fillRect(0, FLOOR_Y, SCREEN_WIDTH, SCREEN_HEIGHT - FLOOR_Y);
            // Draw home
            drawHome(g, HOME_X - cameraX, FLOOR_Y);
            // Draw lakes
            for (Lake lake : getVisibleLakes()) {
                lake.draw(g, FLOOR_Y, -cameraX);
            }
            // Draw trees
            for (Tree tree : getVisibleTrees()) {
                tree.draw(g, FLOOR_Y, -cameraX);
            }
            // Draw character
            character.setPosition(worldX - cameraX, FLOOR_Y - CHARACTER_HEIGHT);
            character.draw(g);
        } else if (state == GameState.INSIDE_HOME) {
            drawHomeInterior(g);
        }
    }

    private void drawHome(Graphics g, int x, int floorY) {
        // Log cabin body (logs)
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(156, 102, 31));
            g.fillRoundRect(x, floorY - 120 + i * 20, 120, 20, 18, 18);
            g.setColor(Color.BLACK);
            g.drawRoundRect(x, floorY - 120 + i * 20, 120, 20, 18, 18);
        }
        // Log ends
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(205, 133, 63));
            g.fillOval(x - 10, floorY - 120 + i * 20, 20, 20);
            g.fillOval(x + 110, floorY - 120 + i * 20, 20, 20);
            g.setColor(Color.BLACK);
            g.drawOval(x - 10, floorY - 120 + i * 20, 20, 20);
            g.drawOval(x + 110, floorY - 120 + i * 20, 20, 20);
        }
        // Roof
        g.setColor(new Color(150, 80, 40));
        int[] rx = {x - 10, x + 60, x + 130};
        int[] ry = {floorY - 120, floorY - 170, floorY - 120};
        g.fillPolygon(rx, ry, 3);
        g.setColor(Color.BLACK);
        g.drawPolygon(rx, ry, 3);
        // Door
        g.setColor(new Color(120, 80, 40));
        g.fillRect(x + 60, floorY - 50, 20, 50);
        g.setColor(Color.BLACK);
        g.drawRect(x + 60, floorY - 50, 20, 50);
        // Door knob
        g.fillOval(x + 75, floorY - 25, 5, 5);
    }

    private void drawHomeInterior(Graphics g) {
        // Room background
        g.setColor(new Color(230, 210, 170));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        // Floor
        g.setColor(new Color(180, 140, 90));
        g.fillRect(0, FLOOR_Y, SCREEN_WIDTH, SCREEN_HEIGHT - FLOOR_Y);
        // Furniture
        drawFurniture(g);
        // Door (to outside)
        g.setColor(new Color(120, 80, 40));
        g.fillRect(370, FLOOR_Y - 50, 60, 50);
        g.setColor(Color.BLACK);
        g.drawRect(370, FLOOR_Y - 50, 60, 50);
        g.fillOval(420, FLOOR_Y - 25, 8, 8);
        // Husband
        drawFamilyMember(g, husbandX, husbandY, new Color(80, 120, 200), new Color(80, 40, 20), 36, 54, true);
        // Daughter (smaller, fixed head/body/eyes/legs)
        drawFamilyMember(g, childX, childY, new Color(220, 120, 180), new Color(160, 80, 180), 24, 36, false);
        // Main character (smaller, to show she's home)
        character.setPosition(playerRoomX, playerRoomY);
        character.draw(g);
        // Labels
        g.setColor(Color.BLACK);
        g.drawString("Press ENTER at the door to go outside", 300, FLOOR_Y - 60);
    }

    private void drawFamilyMember(Graphics g, int x, int y, Color bodyColor, Color hairColor) {
        drawFamilyMember(g, x, y, bodyColor, hairColor, 36, 54, true);
    }
    private void drawFamilyMember(Graphics g, int x, int y, Color bodyColor, Color hairColor, int width, int height, boolean hasLegs) {
        Graphics2D g2 = (Graphics2D) g;
        // Body
        g2.setColor(bodyColor);
        g2.fillRoundRect(x + 8, y + 24, width - 16, height - 24, 12, 18);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + 8, y + 24, width - 16, height - 24, 12, 18);
        // Head (child: smaller, lower, connected)
        int headW = width - 12, headH = width - 12;
        int headX = x + (width - headW) / 2;
        int headY = y + 8;
        if (!hasLegs) headY = y + 12;
        g2.setColor(new Color(255, 224, 189));
        g2.fillOval(headX, headY, headW, headH);
        g2.setColor(Color.BLACK);
        g2.drawOval(headX, headY, headW, headH);
        // Hair
        g2.setColor(hairColor);
        g2.fillArc(headX, headY, headW, headH, 0, 180);
        g2.setColor(Color.BLACK);
        g2.drawArc(headX, headY, headW, headH, 0, 180);
        // Eyes (child: closer together)
        g2.setColor(Color.BLACK);
        int eyeY = headY + headH / 3;
        if (!hasLegs) {
            g2.fillOval(headX + headW / 3, eyeY, 3, 3);
            g2.fillOval(headX + headW * 2 / 3 - 3, eyeY, 3, 3);
        } else {
            g2.fillOval(headX + 8, eyeY, 4, 4);
            g2.fillOval(headX + headW - 12, eyeY, 4, 4);
        }
        // Smile
        g2.setColor(new Color(180, 80, 80));
        g2.drawArc(headX + 8, headY + headH - 12, headW - 16, 8, 0, -180);
        // Legs
        if (hasLegs) {
            g2.setStroke(new BasicStroke(4));
            g2.setColor(new Color(120, 80, 40));
            g2.drawLine(x + 16, y + height, x + 16, y + height + 14);
            g2.drawLine(x + width - 16, y + height, x + width - 16, y + height + 14);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x + 16, y + height, x + 16, y + height + 14);
            g2.drawLine(x + width - 16, y + height, x + width - 16, y + height + 14);
        } else {
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(120, 80, 40));
            g2.drawLine(x + width / 3, y + height, x + width / 3, y + height + 10);
            g2.drawLine(x + width * 2 / 3, y + height, x + width * 2 / 3, y + height + 10);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x + width / 3, y + height, x + width / 3, y + height + 10);
            g2.drawLine(x + width * 2 / 3, y + height, x + width * 2 / 3, y + height + 10);
        }
    }

    // Prevent player from entering lakes
    @Override
    public boolean isFocusable() { return true; }
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
    // Check for collision with lakes before moving
    private boolean isBlockedByLake(int newWorldX) {
        for (Lake lake : getVisibleLakes()) {
            int lakeStart = lake.getX();
            int lakeEnd = lake.getX() + lake.getWidth();
            if (newWorldX + CHARACTER_WIDTH > lakeStart && newWorldX < lakeEnd) {
                return true;
            }
        }
        return false;
    }

    private void drawFurniture(Graphics g) {
        // Rug
        g.setColor(new Color(200, 80, 80));
        g.fillOval(220, FLOOR_Y - 30, 360, 60);
        g.setColor(Color.BLACK);
        g.drawOval(220, FLOOR_Y - 30, 360, 60);
        // Table
        g.setColor(new Color(180, 120, 60));
        g.fillRoundRect(340, FLOOR_Y - 70, 120, 30, 18, 18);
        g.setColor(Color.BLACK);
        g.drawRoundRect(340, FLOOR_Y - 70, 120, 30, 18, 18);
        // Chairs
        g.setColor(new Color(160, 100, 60));
        g.fillRect(320, FLOOR_Y - 50, 20, 40);
        g.fillRect(460, FLOOR_Y - 50, 20, 40);
        g.setColor(Color.BLACK);
        g.drawRect(320, FLOOR_Y - 50, 20, 40);
        g.drawRect(460, FLOOR_Y - 50, 20, 40);
        // Bookshelf
        g.setColor(new Color(140, 90, 50));
        g.fillRect(140, FLOOR_Y - 120, 30, 80);
        g.setColor(Color.BLACK);
        g.drawRect(140, FLOOR_Y - 120, 30, 80);
        // Books
        g.setColor(new Color(220, 60, 60));
        g.fillRect(145, FLOOR_Y - 110, 8, 20);
        g.setColor(new Color(60, 120, 220));
        g.fillRect(155, FLOOR_Y - 110, 8, 30);
        g.setColor(new Color(60, 180, 100));
        g.fillRect(165, FLOOR_Y - 110, 8, 15);
        // Lamp
        g.setColor(new Color(240, 220, 120));
        g.fillOval(600, FLOOR_Y - 100, 30, 30);
        g.setColor(new Color(180, 140, 90));
        g.fillRect(612, FLOOR_Y - 70, 6, 40);
        g.setColor(Color.BLACK);
        g.drawOval(600, FLOOR_Y - 100, 30, 30);
        g.drawRect(612, FLOOR_Y - 70, 6, 40);
        // Window
        g.setColor(new Color(180, 220, 255));
        g.fillRect(250, FLOOR_Y - 110, 60, 40);
        g.setColor(Color.BLACK);
        g.drawRect(250, FLOOR_Y - 110, 60, 40);
        // Curtains
        g.setColor(new Color(220, 120, 180));
        g.fillRect(250, FLOOR_Y - 110, 15, 40);
        g.fillRect(295, FLOOR_Y - 110, 15, 40);
        g.setColor(Color.BLACK);
        g.drawRect(250, FLOOR_Y - 110, 15, 40);
        g.drawRect(295, FLOOR_Y - 110, 15, 40);
    }
}
