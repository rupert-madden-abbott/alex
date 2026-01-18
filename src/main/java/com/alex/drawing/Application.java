package com.alex.drawing;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.alex.drawing.DrawingPanel.*;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Simple Drawing App");
            DrawingPanel drawingPanel = new DrawingPanel();
            // --- New Menus for Tools and Colours ---
            JPanel controlsPanel = new JPanel();
            controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));

            // Tool menu button
            JButton toolMenuBtn = new JButton("Tool");
            JPopupMenu toolMenu = new JPopupMenu();
            JMenuItem pencilItem = new JMenuItem("Pencil");
            JMenuItem brushItem = new JMenuItem("Brush");
            JMenuItem fillItem = new JMenuItem("Fill");
            JMenuItem eraserItem = new JMenuItem("Eraser");
            JMenuItem sprayCanItem = new JMenuItem("Spray Can");
            JMenu stampMenu = new JMenu("Stamp");
            JMenuItem[] stampItems = new JMenuItem[4];
            String[] stampNames = {"Smiley", "Cat", "Star", "Flower"};
            DrawingPanel.StampType[] stampTypes = {
                DrawingPanel.StampType.SMILEY,
                DrawingPanel.StampType.CAT,
                DrawingPanel.StampType.STAR,
                DrawingPanel.StampType.FLOWER
            };
            for (int i = 0; i < 4; i++) {
                JMenuItem item = new JMenuItem(stampNames[i]);
                int idx = i;
                item.addActionListener(e -> {
                    drawingPanel.setCurrentTool(Tool.STAMP);
                    drawingPanel.setCurrentStampType(stampTypes[idx]);
                    toolMenuBtn.setText("Tool: Stamp - " + stampNames[idx]);
                });
                stampItems[i] = item;
                stampMenu.add(item);
            }
            pencilItem.addActionListener(e -> {
                drawingPanel.setCurrentTool(Tool.PENCIL);
                toolMenuBtn.setText("Tool: Pencil");
            });
            brushItem.addActionListener(e -> {
                drawingPanel.setCurrentTool(Tool.BRUSH);
                toolMenuBtn.setText("Tool: Brush");
            });
            fillItem.addActionListener(e -> {
                drawingPanel.setCurrentTool(Tool.FILL);
                toolMenuBtn.setText("Tool: Fill");
            });
            eraserItem.addActionListener(e -> {
                drawingPanel.setCurrentTool(Tool.ERASER);
                toolMenuBtn.setText("Tool: Eraser");
            });
            sprayCanItem.addActionListener(e -> {
                drawingPanel.setCurrentTool(Tool.SPRAYCAN);
                toolMenuBtn.setText("Tool: Spray Can");
            });
            toolMenu.add(pencilItem);
            toolMenu.add(brushItem);
            toolMenu.add(sprayCanItem);
            toolMenu.add(fillItem);
            toolMenu.add(eraserItem);
            toolMenu.addSeparator();
            toolMenu.add(stampMenu);
            toolMenuBtn.addActionListener(e -> toolMenu.show(toolMenuBtn, 0, toolMenuBtn.getHeight()));
            controlsPanel.add(toolMenuBtn);
            controlsPanel.add(Box.createHorizontalStrut(10));

            // Colour menu button
            JButton colorMenuBtn = new JButton("Colour");
            JPopupMenu colorMenu = new JPopupMenu();
            Color[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA};
            String[] colorNames = {"Black", "Red", "Blue", "Green", "Orange", "Magenta"};
            for (int i = 0; i < colors.length; i++) {
                JMenuItem colorItem = new JMenuItem(colorNames[i]);
                Color c = colors[i];
                colorItem.setForeground(c);
                int finalI = i;
                colorItem.addActionListener(e -> {
                    drawingPanel.setCurrentColor(c);
                    colorMenuBtn.setText("Colour: " + colorNames[finalI]);
                });
                colorMenu.add(colorItem);
            }
            colorMenuBtn.addActionListener(e -> colorMenu.show(colorMenuBtn, 0, colorMenuBtn.getHeight()));
            controlsPanel.add(colorMenuBtn);
            controlsPanel.add(Box.createHorizontalStrut(10));

            // Size slider and clear button
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
            JButton clearBtn = new JButton("Clear");
            clearBtn.addActionListener(e -> drawingPanel.clearCanvas());
            controlsPanel.add(clearBtn);
            JButton openBtn = new JButton("Open");
            JButton saveBtn = new JButton("Save");
            openBtn.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
                int result = chooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    drawingPanel.openImage(chooser.getSelectedFile());
                }
            });
            saveBtn.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
                int result = chooser.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".png")) {
                        file = new File(file.getParentFile(), file.getName() + ".png");
                    }
                    drawingPanel.saveImage(file);
                }
            });
            controlsPanel.add(openBtn);
            controlsPanel.add(saveBtn);

            frame.add(controlsPanel, BorderLayout.NORTH);
            frame.add(drawingPanel, BorderLayout.CENTER);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
