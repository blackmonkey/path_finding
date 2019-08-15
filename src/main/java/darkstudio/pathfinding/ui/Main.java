/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Shane
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.ui;

import darkstudio.pathfinding.algorithm.DiagonalMovement;
import darkstudio.pathfinding.algorithm.JumpPointFinderBase;
import darkstudio.pathfinding.algorithm.Options;
import darkstudio.pathfinding.model.Grid;
import darkstudio.pathfinding.model.Node;
import darkstudio.pathfinding.utility.Util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JFrame implements MouseListener {
    private static final int ROWS = 30;
    private static final int COLS = 40;
    private static final int NODE_SIZE = 25;
    private static final int GRID_WIDTH = COLS * NODE_SIZE;
    private static final int GRID_HEIGHT = ROWS * NODE_SIZE;

    private static final Color PATH_NODE_COLOR = Color.ORANGE;
    private static final Color[] MARK_COLORS = new Color[]{
            Color.LIGHT_GRAY, // passable node
            Color.DARK_GRAY,  // obstacle node
            Color.GREEN,      // start node
            Color.RED         // end node
    };
    private static final int PASSABLE_COLOR_IDX = 0;
    private static final int OBSTACLE_COLOR_IDX = 1;
    private static final int START_COLOR_IDX = 2;
    private static final int END_COLOR_IDX = 3;

    private static final String COORD = "COORD";
    private static final String COLOR_IDX = "COLOR_IDX";

    private AtomicBoolean mousePressed = new AtomicBoolean(false);
    private List<Point> path;
    private JButton[][] buttons = new JButton[ROWS][COLS];
    private Grid grid = new Grid(COLS, ROWS);
    private Node startNode;
    private Node endNode;
    private JLabel infoBar;

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        super();
        setTitle("Jump Point Search");
        setSize(GRID_WIDTH, GRID_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createMainPanel());
        setVisible(true);
    }

    private Container createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(ROWS + 2, COLS + 2));

        gridPanel.add(new JLabel());
        for (int x = 0; x < COLS; x++) {
            gridPanel.add(new JLabel(Integer.toString(x), SwingConstants.CENTER));
        }
        gridPanel.add(new JLabel());

        for (int y = 0; y < ROWS; y++) {
            gridPanel.add(new JLabel(Integer.toString(y), SwingConstants.CENTER));
            for (int x = 0; x < COLS; x++) {
                JButton btn = new JButton();
                btn.putClientProperty(COORD, new Point(x, y));
                btn.putClientProperty(COLOR_IDX, PASSABLE_COLOR_IDX);
                btn.setBackground(MARK_COLORS[PASSABLE_COLOR_IDX]);
                btn.addMouseListener(this);
                gridPanel.add(btn);

                buttons[y][x] = btn;
            }
            gridPanel.add(new JLabel(Integer.toString(y), SwingConstants.CENTER));
        }

        gridPanel.add(new JLabel());
        for (int x = 0; x < COLS; x++) {
            gridPanel.add(new JLabel(Integer.toString(x), SwingConstants.CENTER));
        }
        gridPanel.add(new JLabel());

        JButton button = new JButton("Search");
        button.addActionListener(evt -> searchPath());

        infoBar = new JLabel(" ", SwingConstants.CENTER);

        main.add(infoBar, BorderLayout.NORTH);
        main.add(gridPanel, BorderLayout.CENTER);
        main.add(button, BorderLayout.SOUTH);
        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return main;
    }

    private void searchPath() {
        if (startNode == null || endNode == null) {
            JOptionPane.showMessageDialog(this, "Start and/or ending point undefined", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        clearPath();
        long startTs = System.currentTimeMillis();
        JumpPointFinderBase finder = Util.jumpPointFinder(DiagonalMovement.Never, new Options());
        path = finder.findPath(startNode.getX(), startNode.getY(), endNode.getX(), endNode.getY(), grid.reset());
        long duration = System.currentTimeMillis() - startTs;
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No path available", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Point point : path) {
            buttons[point.y][point.x].setBackground(PATH_NODE_COLOR);
        }

        buttons[startNode.getY()][startNode.getX()].setBackground(MARK_COLORS[START_COLOR_IDX]);
        buttons[endNode.getY()][endNode.getX()].setBackground(MARK_COLORS[END_COLOR_IDX]);

        infoBar.setText("node count: " + path.size() + ", time: " + duration + "ms");
    }

    private void changeBackground(JButton btn) {
        Point coord = (Point) btn.getClientProperty(COORD);
        int idx = (int) btn.getClientProperty(COLOR_IDX);
        idx = getNextColor(coord.x, coord.y, idx);
        btn.putClientProperty(COLOR_IDX, idx);
        btn.setBackground(MARK_COLORS[idx]);
    }

    private int getNextColor(int x, int y, int curIdx) {
        if (curIdx == START_COLOR_IDX) {
            startNode = null;
        } else if (curIdx == END_COLOR_IDX) {
            endNode = null;
        }

        curIdx++;

        if (curIdx >= MARK_COLORS.length) {
            curIdx = PASSABLE_COLOR_IDX;
        } else if (curIdx == START_COLOR_IDX) {
            if (startNode == null) {
                startNode = grid.getNodeAt(x, y);
            } else if (endNode == null) {
                curIdx = END_COLOR_IDX;
                endNode = grid.getNodeAt(x, y);
            } else {
                curIdx = PASSABLE_COLOR_IDX;
            }
        } else if (curIdx == END_COLOR_IDX) {
            if (endNode == null) {
                endNode = grid.getNodeAt(x, y);
            } else {
                curIdx = PASSABLE_COLOR_IDX;
            }
        }
        grid.setWalkableAt(x, y, curIdx != OBSTACLE_COLOR_IDX);
        return curIdx;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (mousePressed.get()) {
            JButton b = (JButton) e.getSource();
            changeBackground(b);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void clearPath() {
        if (path != null) {
            for (int i = 1; i < path.size() - 1; i++) {
                int x = path.get(i).x;
                int y = path.get(i).y;
                buttons[y][x].putClientProperty(COLOR_IDX, PASSABLE_COLOR_IDX);
                buttons[y][x].setBackground(MARK_COLORS[PASSABLE_COLOR_IDX]);
            }
            path = null;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        clearPath();
        JButton btn = (JButton) e.getSource();
        changeBackground(btn);
        mousePressed.set(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed.set(false);
    }
}
