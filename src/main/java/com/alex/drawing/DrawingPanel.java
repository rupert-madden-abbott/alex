package com.alex.drawing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DrawingPanel extends JPanel {
    private Color currentColor = Color.BLACK;
    private Tool currentTool = Tool.PENCIL;
    private BufferedImage canvasImage;
    private int lastX, lastY;
    private boolean drawing = false;
    private int toolSize = 5;

    // Cursor preview state
    private int cursorX = -1, cursorY = -1;
    private boolean cursorInPanel = false;

    public enum StampType { SMILEY, CAT, STAR, FLOWER }
    private StampType currentStamp = StampType.SMILEY;

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
                } else if (currentTool == Tool.SPRAYCAN) {
                    sprayAt(e.getX(), e.getY());
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
                } else if (currentTool == Tool.STAMP) {
                    drawStamp(e.getX(), e.getY(), toolSize, currentColor, currentStamp);
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
                } else if (drawing && currentTool == Tool.SPRAYCAN) {
                    sprayAt(x, y);
                    repaint();
                } else if (drawing && currentTool == Tool.ERASER) {
                    drawBrush(x, y, lastX, lastY, true);
                    lastX = x;
                    lastY = y;
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                cursorX = e.getX();
                cursorY = e.getY();
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                cursorInPanel = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cursorInPanel = false;
                repaint();
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

    public void setCurrentStampType(StampType type) { this.currentStamp = type; }

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

    private void drawStamp(int x, int y, int size, Color color, StampType type) {
        Graphics2D g2 = canvasImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        switch (type) {
            case SMILEY:
                drawSmiley(g2, x, y, size, color);
                break;
            case CAT:
                drawCat(g2, x, y, size, color);
                break;
            case STAR:
                drawStar(g2, x, y, size, color);
                break;
            case FLOWER:
                drawFlower(g2, x, y, size, color);
                break;
        }
        g2.dispose();
    }

    private void drawSmiley(Graphics2D g2, int x, int y, int size, Color color) {
        // Face
        g2.setColor(color);
        g2.fillOval(x - size/2, y - size/2, size, size);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(Math.max(2, size/20)));
        g2.drawOval(x - size/2, y - size/2, size, size);
        // Eyes
        int eyeW = size/7, eyeH = size/5;
        int eyeY = y - size/6;
        g2.setColor(Color.WHITE);
        g2.fillOval(x - size/4 - eyeW/2, eyeY, eyeW, eyeH);
        g2.fillOval(x + size/4 - eyeW/2, eyeY, eyeW, eyeH);
        g2.setColor(Color.BLACK);
        g2.fillOval(x - size/4 - eyeW/4, eyeY + eyeH/2, eyeW/2, eyeH/2);
        g2.fillOval(x + size/4 - eyeW/4, eyeY + eyeH/2, eyeW/2, eyeH/2);
        // Smile (arc)
        int smileW = size/2, smileH = size/3;
        int smileX = x - smileW/2, smileY = y + size/8;
        g2.setStroke(new BasicStroke(Math.max(2, size/18)));
        g2.drawArc(smileX, smileY, smileW, smileH, 0, -180);
    }


    private void drawCat(Graphics2D g2, int x, int y, int size, Color color) {
        // Head
        g2.setColor(color);
        g2.fillOval(x - size/2, y - size/3, size, size*2/3);
        // Ears
        int earH = size/3, earW = size/4;
        Polygon leftEar = new Polygon();
        leftEar.addPoint(x - size/3, y - size/3);
        leftEar.addPoint(x - size/6, y - size/2);
        leftEar.addPoint(x - size/12, y - size/3);
        Polygon rightEar = new Polygon();
        rightEar.addPoint(x + size/3, y - size/3);
        rightEar.addPoint(x + size/6, y - size/2);
        rightEar.addPoint(x + size/12, y - size/3);
        g2.fillPolygon(leftEar);
        g2.fillPolygon(rightEar);
        // Face outline
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(Math.max(2, size/18)));
        g2.drawOval(x - size/2, y - size/3, size, size*2/3);
        g2.drawPolygon(leftEar);
        g2.drawPolygon(rightEar);
        // Eyes
        g2.setColor(Color.WHITE);
        g2.fillOval(x - size/6, y - size/10, size/8, size/8);
        g2.fillOval(x + size/12, y - size/10, size/8, size/8);
        g2.setColor(Color.BLACK);
        g2.fillOval(x - size/6 + size/32, y - size/10 + size/32, size/16, size/16);
        g2.fillOval(x + size/12 + size/32, y - size/10 + size/32, size/16, size/16);
        // Nose
        g2.setColor(new Color(255, 192, 203));
        g2.fillOval(x - size/32, y + size/24, size/16, size/20);
        g2.setColor(Color.BLACK);
        g2.drawOval(x - size/32, y + size/24, size/16, size/20);
        // Mouth
        g2.drawArc(x - size/24, y + size/12, size/12, size/12, 0, -180);
        g2.drawArc(x - size/24, y + size/12, size/12, size/12, 0, 180);
        // Whiskers
        for (int i = -1; i <= 1; i += 2) {
            g2.drawLine(x + i*size/16, y + size/16, x + i*size/4, y + size/16 + size/24);
            g2.drawLine(x + i*size/16, y + size/16, x + i*size/4, y + size/24);
        }
    }
    private void drawStar(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        int r = size/2;
        int n = 5;
        int[] xs = new int[n*2], ys = new int[n*2];
        for (int i = 0; i < n*2; i++) {
            double angle = Math.PI/2 + i * Math.PI/n;
            int rad = (i%2==0) ? r : r/2;
            xs[i] = x + (int)(Math.cos(angle)*rad);
            ys[i] = y - (int)(Math.sin(angle)*rad);
        }
        g2.fillPolygon(xs, ys, n*2);
    }
    private void drawFlower(Graphics2D g2, int x, int y, int size, Color color) {
        g2.setColor(color);
        int petals = 6;
        int r = size/2;
        for (int i = 0; i < petals; i++) {
            double angle = 2*Math.PI*i/petals;
            int px = x + (int)(Math.cos(angle)*r*0.7);
            int py = y + (int)(Math.sin(angle)*r*0.7);
            g2.fillOval(px - r/3, py - r/3, 2*r/3, 2*r/3);
        }
        g2.setColor(Color.YELLOW);
        g2.fillOval(x - r/3, y - r/3, 2*r/3, 2*r/3);
    }

    private void sprayAt(int x, int y) {
        Graphics2D g2 = canvasImage.createGraphics();
        g2.setColor(currentColor);
        int radius = toolSize / 2;
        int dots = Math.max(10, toolSize * 4); // density
        for (int i = 0; i < dots; i++) {
            double angle = Math.random() * 2 * Math.PI;
            double dist = Math.random() * radius;
            int dx = (int) (Math.cos(angle) * dist);
            int dy = (int) (Math.sin(angle) * dist);
            g2.fillRect(x + dx, y + dy, 1, 1);
        }
        g2.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvasImage, 0, 0, null);
        // Draw cursor preview if not drawing and cursor is in panel
        if (!drawing && cursorInPanel && cursorX >= 0 && cursorY >= 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            float alpha = 0.5f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            switch (currentTool) {
                case PENCIL:
                case BRUSH:
                    g2.setColor(currentTool == Tool.ERASER ? Color.WHITE : currentColor);
                    g2.fillOval(cursorX - toolSize/2, cursorY - toolSize/2, toolSize, toolSize);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(cursorX - toolSize/2, cursorY - toolSize/2, toolSize, toolSize);
                    break;
                case SPRAYCAN:
                    g2.setColor(currentColor);
                    g2.drawOval(cursorX - toolSize/2, cursorY - toolSize/2, toolSize, toolSize);
                    for (int i = 0; i < Math.max(10, toolSize * 4); i++) {
                        double angle = Math.random() * 2 * Math.PI;
                        double dist = Math.random() * (toolSize / 2);
                        int dx = (int) (Math.cos(angle) * dist);
                        int dy = (int) (Math.sin(angle) * dist);
                        g2.fillRect(cursorX + dx, cursorY + dy, 1, 1);
                    }
                    break;
                case ERASER:
                    g2.setColor(Color.WHITE);
                    g2.fillOval(cursorX - toolSize/2, cursorY - toolSize/2, toolSize, toolSize);
                    g2.setColor(Color.GRAY);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(cursorX - toolSize/2, cursorY - toolSize/2, toolSize, toolSize);
                    break;
                case FILL:
                    g2.setColor(currentColor);
                    g2.fillOval(cursorX - toolSize/2, cursorY - toolSize/2, toolSize, toolSize);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(cursorX - toolSize/2, cursorY - toolSize/2, toolSize, toolSize);
                    break;
                case STAMP:
                    drawStampPreview(g2, cursorX, cursorY, toolSize, currentColor, currentStamp);
                    break;
            }
            g2.dispose();
        }
    }

    // Draws a semi-transparent preview of the stamp
    private void drawStampPreview(Graphics2D g2, int x, int y, int size, Color color, StampType type) {
        Composite oldComp = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        switch (type) {
            case SMILEY:
                drawSmiley(g2, x, y, size, color);
                break;
            case CAT:
                drawCat(g2, x, y, size, color);
                break;
            case STAR:
                drawStar(g2, x, y, size, color);
                break;
            case FLOWER:
                drawFlower(g2, x, y, size, color);
                break;
        }
        g2.setComposite(oldComp);
    }

    public enum Tool {
        PENCIL, BRUSH, SPRAYCAN, FILL, ERASER, STAMP
    }

    public boolean saveImage(File file) {
        try {
            return ImageIO.write(canvasImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean openImage(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) return false;
            Graphics2D g2 = canvasImage.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
            // Scale image to fit if needed
            int w = canvasImage.getWidth();
            int h = canvasImage.getHeight();
            g2.drawImage(img, 0, 0, w, h, null);
            g2.dispose();
            repaint();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
