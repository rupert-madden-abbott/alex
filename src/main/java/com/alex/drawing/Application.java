package com.alex.drawing;

import javax.swing.*;
import java.awt.*;

import static com.alex.drawing.DrawingPanel.*;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simple Drawing App");
            DrawingPanel drawingPanel = new DrawingPanel();
            JPanel colorPanel = new JPanel();
            Color[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA};
            String[] colorNames = {"Black", "Red", "Blue", "Green", "Orange", "Magenta"};
            JButton[] colorButtons = new JButton[colors.length];
            for (int i = 0; i < colors.length; i++) {
                JButton btn = new JButton(colorNames[i]);
                Color c = colors[i];
                btn.setForeground(c);
                btn.setFocusPainted(false);
                int idx = i;
                btn.addActionListener(e -> {
                    drawingPanel.setCurrentColor(c);
                    for (int j = 0; j < colorButtons.length; j++) {
                        colorButtons[j].setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
                    }
                    btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                });
                colorButtons[i] = btn;
                colorPanel.add(btn);
            }
            // Set initial color highlight
            colorButtons[0].setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

            JPanel toolPanel = new JPanel();
            JButton pencilBtn = new JButton("Pencil");
            JButton fillBtn = new JButton("Fill");
            JButton brushBtn = new JButton("Brush");
            JButton eraserBtn = new JButton("Eraser");
            eraserBtn.setFocusPainted(false);
            JButton clearBtn = new JButton("Clear");
            JButton[] toolButtons = {pencilBtn, fillBtn, brushBtn, eraserBtn};
            pencilBtn.setFocusPainted(false);
            fillBtn.setFocusPainted(false);
            brushBtn.setFocusPainted(false);
            pencilBtn.addActionListener(e -> {
                drawingPanel.setCurrentTool(Tool.PENCIL);
                for (JButton b : toolButtons) b.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
                pencilBtn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
            });
            fillBtn.addActionListener(e -> {
                drawingPanel.setCurrentTool(Tool.FILL);
                for (JButton b : toolButtons) b.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
                fillBtn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
            });
            brushBtn.addActionListener(e -> {
                drawingPanel.setCurrentTool(Tool.BRUSH);
                for (JButton b : toolButtons) b.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
                brushBtn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
            });
            eraserBtn.addActionListener(e -> {
                drawingPanel.setCurrentTool(Tool.ERASER);
                for (JButton b : toolButtons) b.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
                eraserBtn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
            });
            clearBtn.addActionListener(e -> drawingPanel.clearCanvas());
            toolPanel.add(pencilBtn);
            toolPanel.add(fillBtn);
            toolPanel.add(brushBtn);
            toolPanel.add(eraserBtn);
            // Set initial tool highlight
            pencilBtn.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));

            JPanel controlsPanel = new JPanel();
            controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));
            controlsPanel.add(toolPanel);
            controlsPanel.add(Box.createHorizontalStrut(10));
            controlsPanel.add(colorPanel);
            controlsPanel.add(Box.createHorizontalStrut(10));
            // Add size selection slider
            JPanel sizePanel = new JPanel();
            JLabel sizeLabel = new JLabel("Size:");
            JSlider sizeSlider = new JSlider(1, 30, 5);
            sizeSlider.setMajorTickSpacing(5);
            sizeSlider.setMinorTickSpacing(1);
            sizeSlider.setPaintTicks(true);
            sizeSlider.setPaintLabels(true);
            sizeSlider.addChangeListener(e -> drawingPanel.setToolSize(sizeSlider.getValue()));
            sizePanel.add(sizeLabel);
            sizePanel.add(sizeSlider);
            controlsPanel.add(sizePanel);
            controlsPanel.add(Box.createHorizontalStrut(10));
            controlsPanel.add(clearBtn);
            frame.add(controlsPanel, BorderLayout.NORTH);
            frame.add(drawingPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
