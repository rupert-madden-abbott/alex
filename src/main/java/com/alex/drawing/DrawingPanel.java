package com.alex.drawing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class DrawingPanel extends JPanel {
    private Color currentColor = Color.BLACK;
    private Tool currentTool = Tool.PENCIL;
    private BufferedImage canvasImage;
    private int lastX, lastY;
    private boolean drawing = false;
    private int toolSize = 5;

    public DrawingPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 400));
        canvasImage = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = canvasImage.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 600, 400);
        g2.dispose();
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentTool == Tool.PENCIL) {
                    lastX = e.getX();
                    lastY = e.getY();
                    drawing = true;
                } else if (currentTool == Tool.BRUSH) {
                    lastX = e.getX();
                    lastY = e.getY();
                    drawBrush(lastX, lastY, lastX, lastY, false);
                    drawing = true;
                    repaint();
                } else if (currentTool == Tool.ERASER) {
                    lastX = e.getX();
                    lastY = e.getY();
                    drawBrush(lastX, lastY, lastX, lastY, true);
                    drawing = true;
                    repaint();
                } else if (currentTool == Tool.FILL) {
                    floodFill(e.getX(), e.getY(), currentColor);
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drawing = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (drawing && currentTool == Tool.PENCIL) {
                    Graphics2D g2 = canvasImage.createGraphics();
                    g2.setColor(currentColor);
                    g2.setStroke(new BasicStroke(toolSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(lastX, lastY, x, y);
                    g2.dispose();
                    lastX = x;
                    lastY = y;
                    repaint();
                } else if (drawing && currentTool == Tool.BRUSH) {
                    drawBrush(x, y, lastX, lastY, false);
                    lastX = x;
                    lastY = y;
                    repaint();
                } else if (drawing && currentTool == Tool.ERASER) {
                    drawBrush(x, y, lastX, lastY, true);
                    lastX = x;
                    lastY = y;
                    repaint();
                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setCurrentTool(Tool tool) {
        this.currentTool = tool;
    }

    public void setToolSize(int size) {
        this.toolSize = size;
    }

    public void clearCanvas() {
        Graphics2D g2 = canvasImage.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        g2.dispose();
        repaint();
    }

    private void floodFill(int x, int y, Color fillColor) {
        int targetColor = canvasImage.getRGB(x, y);
        int replacementColor = fillColor.getRGB();
        if (targetColor == replacementColor) return;
        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x, y));
        while (!stack.isEmpty()) {
            Point p = stack.pop();
            int px = p.x, py = p.y;
            if (px < 0 || px >= canvasImage.getWidth() || py < 0 || py >= canvasImage.getHeight()) continue;
            if (canvasImage.getRGB(px, py) != targetColor) continue;
            canvasImage.setRGB(px, py, replacementColor);
            stack.push(new Point(px + 1, py));
            stack.push(new Point(px - 1, py));
            stack.push(new Point(px, py + 1));
            stack.push(new Point(px, py - 1));
        }
    }

    private void drawBrush(int x, int y, int prevX, int prevY, boolean eraser) {
        Graphics2D g2 = canvasImage.createGraphics();
        Color brushColor = eraser ? new Color(255,255,255,255) : new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), 100);
        g2.setColor(brushColor);
        double dist = Point.distance(prevX, prevY, x, y);
        double step = toolSize / 2.5;
        for (double d = 0; d <= dist; d += step) {
            double t = dist == 0 ? 0 : d / dist;
            int cx = (int) (prevX + t * (x - prevX));
            int cy = (int) (prevY + t * (y - prevY));
            g2.fillOval(cx - toolSize / 2, cy - toolSize / 2, toolSize, toolSize);
        }
        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvasImage, 0, 0, null);
    }

    public enum Tool {
        PENCIL, BRUSH, FILL, ERASER
    }
}
