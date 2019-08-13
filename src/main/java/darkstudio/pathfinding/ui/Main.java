/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Shane
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.ui;

import darkstudio.pathfinding.model.Grid;
import darkstudio.pathfinding.model.GridNode;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JFrame implements MouseListener {
    private static final int ROWS = 30;
    private static final int COLS = 40;
    private static final int NODE_SIZE = 25;
    private static final int GRID_WIDTH = COLS * NODE_SIZE;
    private static final int GRID_HEIGHT = ROWS * NODE_SIZE;

    private static final Color PATH_COLOR = Color.YELLOW;
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
    private ArrayList<JButton> path;
    private JButton[][] buttons = new JButton[ROWS][COLS];
    private int[][] map = new int[ROWS][COLS];
    private GridNode[][] nodes = new GridNode[ROWS][COLS];
    private GridNode startNode;
    private GridNode endNode;
    private Grid grid = new Grid(nodes);
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
        JPanel grid = new JPanel(new GridLayout(ROWS, COLS));

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                JButton btn = new JButton();
                btn.putClientProperty(COORD, new Point(x, y));
                btn.putClientProperty(COLOR_IDX, PASSABLE_COLOR_IDX);
                btn.setBackground(MARK_COLORS[PASSABLE_COLOR_IDX]);
                btn.addMouseListener(this);
                grid.add(btn);

                buttons[y][x] = btn;
                nodes[y][x] = new GridNode(x, y, true);
            }
        }

        JButton button = new JButton("Search");
        button.addActionListener(evt -> searchPath());

        infoBar = new JLabel(" ", SwingConstants.CENTER);

        main.add(infoBar, BorderLayout.NORTH);
        main.add(grid, BorderLayout.CENTER);
        main.add(button, BorderLayout.SOUTH);
        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return main;
    }

    private void searchPath() {
        if (startNode == null || endNode == null) {
            JOptionPane.showMessageDialog(this, "Start and/or ending point undefined", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long startTs = System.currentTimeMillis();
        ArrayList<GridNode> result = grid.searchJPS(startNode, endNode);
        long duration = System.currentTimeMillis() - startTs;
        if (result == null) {
            JOptionPane.showMessageDialog(this, "No path available", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Iterator<GridNode> resultIt = result.iterator();
        path = new ArrayList<>();
        GridNode aux = null;
        while (resultIt.hasNext()) {
            GridNode node = resultIt.next();
            drawLine(aux, node);
            aux = node;
        }

        resultIt = result.iterator();
        while (resultIt.hasNext()) {
            GridNode node = resultIt.next();
            buttons[node.getY()][node.getX()].setBackground(PATH_NODE_COLOR);
        }

        buttons[startNode.getY()][startNode.getX()].setBackground(MARK_COLORS[START_COLOR_IDX]);
        buttons[endNode.getY()][endNode.getX()].setBackground(MARK_COLORS[END_COLOR_IDX]);

        infoBar.setText("path length: " + path.size() + ", time: " + duration + "ms");
    }

    private void setPathNode(int x, int y) {
        buttons[y][x].setBackground(PATH_COLOR);
        path.add(buttons[y][x]);
    }

    private void drawLine(GridNode from, GridNode to) {
        if (from == null || to == null) {
            return;
        }

        int x = from.getX(), y = from.getY(), toX = to.getX(), toY = to.getY();

        if (y == toY) { // vertical lines
            if (x < toX) {
                while (x != toX) {
                    setPathNode(x, y);
                    x++;
                }
            } else { // x > toX
                while (x != toX) {
                    setPathNode(x, y);
                    x--;
                }
            }
        } else if (x == toX) { // horizontal lines
            if (y < toY) {
                while (y != toY) {
                    setPathNode(x, y);
                    y++;
                }
            } else { // y > toY
                while (y != toY) {
                    setPathNode(x, y);
                    y--;
                }
            }
        } else if (x < toX) { // diagonals lines
            if (y < toY) {
                while (x != toX && y != toY) {
                    setPathNode(x, y);
                    y++;
                    x++;
                }
            } else {
                while (x != toX && y != toY) {
                    setPathNode(x, y);
                    y--;
                    x++;
                }
            }
        } else { // diagonals lines
            if (y < toY) {
                while (x != toX && y != toY) {
                    setPathNode(x, y);
                    y++;
                    x--;
                }
            } else {
                while (x != toX && y != toY) {
                    setPathNode(x, y);
                    y--;
                    x--;
                }
            }
        }
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
                startNode = nodes[y][x];
            } else if (endNode == null) {
                curIdx = END_COLOR_IDX;
                endNode = nodes[y][x];
            } else {
                curIdx = PASSABLE_COLOR_IDX;
            }
        } else if (curIdx == END_COLOR_IDX) {
            if (endNode == null) {
                endNode = nodes[y][x];
            } else {
                curIdx = PASSABLE_COLOR_IDX;
            }
        }
        nodes[y][x].setPassable(curIdx != OBSTACLE_COLOR_IDX);
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

    @Override
    public void mousePressed(MouseEvent e) {
        if (path != null) {
            for (JButton btn : path) {
                btn.putClientProperty(COLOR_IDX, PASSABLE_COLOR_IDX);
                btn.setBackground(MARK_COLORS[PASSABLE_COLOR_IDX]);
            }
            path = null;
            startNode = null;
            endNode = null;
        }
        JButton btn = (JButton) e.getSource();
        changeBackground(btn);
        mousePressed.set(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed.set(false);
    }
}
