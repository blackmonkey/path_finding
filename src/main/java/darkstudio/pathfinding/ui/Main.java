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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JFrame implements MouseListener {
    private static final int ROWS = 20;
    private static final int COLS = 20;
    private static final int NODE_SIZE = 50;
    private static final int GRID_WIDTH = COLS * NODE_SIZE;
    private static final int GRID_HEIGHT = ROWS * NODE_SIZE;

    private static final Color PATH_NODE_COLOR = Color.ORANGE;
    private static final Color START_NODE_COLOR = Color.GREEN;
    private static final Color END_NODE_COLOR = Color.RED;
    private static final Color WALKABLE_NODE_COLOR = Color.LIGHT_GRAY;
    private static final Color OBSTACLE_NODE_COLOR = Color.DARK_GRAY;
    private static final Color WORMHOLE_NODE_COLOR = Color.PINK;
    private static final Color TUNNEL_NODE_COLOR = Color.CYAN;

    private static final String TUNNEL_LEFT_TITLE = "<";
    private static final String TUNNEL_RIGHT_TITLE = ">";
    private static final String TUNNEL_UP_TITLE = "^";
    private static final String TUNNEL_DOWN_TITLE = "v";

    private static final int MODE_OBSTACLE = 1;
    private static final int MODE_WORMHOLE = 2;
    private static final int MODE_TUNNEL_LEFT = 3;
    private static final int MODE_TUNNEL_RIGHT = 4;
    private static final int MODE_TUNNEL_UP = 5;
    private static final int MODE_TUNNEL_DOWN = 6;

    private static final String NODE = "NODE";

    private AtomicBoolean mousePressed = new AtomicBoolean(false);
    private AtomicBoolean mouseDragged = new AtomicBoolean(false);
    private List<Point> path;
    private JButton[][] buttons = new JButton[ROWS][COLS];
    private Grid grid = new Grid(COLS, ROWS);
    private Node startNode = grid.getNodeAt(COLS / 3, ROWS / 2);
    private Node endNode = grid.getNodeAt(2 * COLS / 3, ROWS / 2);
    private JLabel infoBar;
    private int editMode = MODE_OBSTACLE;
    private List<Node> draggableNodes = new ArrayList<>();
    private Node draggableNode;
    private Color draggableBtnColor;
    private String draggableBtnText;
    private int wormholeId = 1;

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
        showStartEndNodes();
        draggableNodes.add(startNode);
        draggableNodes.add(endNode);
    }

    private Container createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());

        infoBar = new JLabel(" ", SwingConstants.CENTER);
        main.add(infoBar, BorderLayout.NORTH);
        main.add(createMapPanel(), BorderLayout.CENTER);
        main.add(createToolBar(), BorderLayout.SOUTH);
        main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return main;
    }

    private JPanel createMapPanel() {
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
                btn.putClientProperty(NODE, grid.getNodeAt(x, y));
                btn.setBackground(WALKABLE_NODE_COLOR);
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

        return gridPanel;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar("Tool", SwingConstants.HORIZONTAL);
        toolBar.setFloatable(false);

        JRadioButton obstacle = new JRadioButton("Obstacle");
        JRadioButton wormhole = new JRadioButton("Wormhole");
        JRadioButton tunnelLeft = new JRadioButton("Left");
        JRadioButton tunnelRight = new JRadioButton("Right");
        JRadioButton tunnelUp = new JRadioButton("Up");
        JRadioButton tunnelDown = new JRadioButton("Down");

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(obstacle);
        btnGroup.add(wormhole);
        btnGroup.add(tunnelLeft);
        btnGroup.add(tunnelRight);
        btnGroup.add(tunnelUp);
        btnGroup.add(tunnelDown);

        obstacle.setSelected(true);
        obstacle.addActionListener(evt -> editMode = MODE_OBSTACLE);
        wormhole.addActionListener(evt -> editMode = MODE_WORMHOLE);
        tunnelLeft.addActionListener(evt -> editMode = MODE_TUNNEL_LEFT);
        tunnelRight.addActionListener(evt -> editMode = MODE_TUNNEL_RIGHT);
        tunnelUp.addActionListener(evt -> editMode = MODE_TUNNEL_UP);
        tunnelDown.addActionListener(evt -> editMode = MODE_TUNNEL_DOWN);

        toolBar.add(obstacle);
        toolBar.add(wormhole);
        toolBar.addSeparator(new Dimension(50, 0));
        toolBar.add(new JLabel("Tunnel: "));
        toolBar.add(tunnelLeft);
        toolBar.add(tunnelRight);
        toolBar.add(tunnelUp);
        toolBar.add(tunnelDown);

        toolBar.addSeparator(new Dimension(100, 0));

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(evt -> searchPath());
        toolBar.add(searchBtn);
        return toolBar;
    }

    private void showStartEndNodes() {
        buttons[startNode.getY()][startNode.getX()].setBackground(START_NODE_COLOR);
        buttons[endNode.getY()][endNode.getX()].setBackground(END_NODE_COLOR);
    }

    private void clearPath() {
        if (path != null) {
            path.forEach(point -> buttons[point.y][point.x].setBackground(WALKABLE_NODE_COLOR));
            path = null;
        }
    }

    private void removePathPoint(int x, int y) {
        if (path != null) {
            path.remove(new Point(x, y));
        }
    }

    private void searchPath() {
        clearPath();
        draggableNodes.forEach(Node::digExit);

        long startTs = System.currentTimeMillis();
        JumpPointFinderBase finder = Util.jumpPointFinder(DiagonalMovement.Never, new Options().checkTeleporter(true));
        path = finder.findPath(startNode.getX(), startNode.getY(), endNode.getX(), endNode.getY(), grid.reset());
        long duration = System.currentTimeMillis() - startTs;

        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No path available", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        draggableNodes.forEach(node -> removePathPoint(node.getX(), node.getY()));
        for (Point point : path) {
            buttons[point.y][point.x].setBackground(PATH_NODE_COLOR);
        }

        infoBar.setText("node count: " + path.size() + ", time: " + duration + "ms");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JButton btn = (JButton) e.getSource();
        Node node = (Node) btn.getClientProperty(NODE);

        String info = "(" + node.getX() + ", " + node.getY() + ")";
        if (node.getExit() != null) {
            info += "=>(" + node.getExit().getX() + ", " + node.getExit().getY() + ")";
        }
        btn.setToolTipText(info);

        if (!mouseDragged.get() || draggableBtnColor == null || draggableBtnText == null || draggableNode == null) {
            // If mouse is not dragging, do nothing.
            return;
        }

        if (draggableNodes.contains(node) || !node.isWalkable()) {
            // If drag to another existed wormhole, tunnel or obstacle, do nothing.
            return;
        }

        btn.setBackground(draggableBtnColor);
        btn.setText(draggableBtnText);

        removePathPoint(node.getX(), node.getY());
        node.setWalkable(draggableNode.isWalkable());
        node.setExit(null); // reset exit at first.
        if (draggableNode == startNode) {
            startNode = node;
        } else if (draggableNode == endNode) {
            endNode = node;
        } else if (draggableNode.isWormhole()) {
            node.setExit(draggableNode.getExit());
            draggableNode.getExit().setExit(node);
        } else {
            // draggableNode is a tunnel, but its exit is possible null.
            Node leftNode = grid.getNodeAt(node.getX() - 1, node.getY());
            Node rightNode = grid.getNodeAt(node.getX() + 1, node.getY());
            Node upNode = grid.getNodeAt(node.getX(), node.getY() - 1);
            Node downNode = grid.getNodeAt(node.getX(), node.getY() + 1);
            switch (draggableBtnText) {
                case TUNNEL_LEFT_TITLE:
                    node.setExit(leftNode);
                    // FIXME: If the moved tunnel is align to map left border, leftNode is null.
                    break;
                case TUNNEL_RIGHT_TITLE:
                    node.setExit(rightNode);
                    // FIXME: If the moved tunnel is align to map right border, leftNode is null.
                    break;
                case TUNNEL_UP_TITLE:
                    node.setExit(upNode);
                    // FIXME: If the moved tunnel is align to map top border, leftNode is null.
                    break;
                case TUNNEL_DOWN_TITLE:
                    node.setExit(downNode);
                    // FIXME: If the moved tunnel is align to map bottom border, leftNode is null.
                    break;
            }
        }

        draggableNodes.remove(draggableNode);
        draggableNodes.add(node);
        draggableNode.setWalkable(true);
        draggableNode.setExit(null);
        draggableNode = node;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JButton btn = (JButton) e.getSource();
        btn.setToolTipText(null);

        if (mousePressed.get()) {
            mouseDragged.set(true);
            Node node = (Node) btn.getClientProperty(NODE);
            if (node == draggableNode) {
                draggableBtnColor = btn.getBackground();
                draggableBtnText = btn.getText();
                btn.setBackground(WALKABLE_NODE_COLOR);
                btn.setText("");
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed.set(true);
        mouseDragged.set(false);
        draggableBtnColor = null;
        draggableBtnText = null;
        draggableNode = null;

        JButton btn = (JButton) e.getSource();
        Node node = (Node) btn.getClientProperty(NODE);
        if (draggableNodes.contains(node)) {
            draggableNode = node;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed.set(false);
        draggableBtnColor = null;
        draggableBtnText = null;
        draggableNode = null;

        if (mouseDragged.get()) {
            mouseDragged.set(false);
            return;
        }

        // to here, it is mouse clicking.

        JButton btn = (JButton) e.getSource();
        Node node = (Node) btn.getClientProperty(NODE);
        if (node == startNode || node == endNode) {
            // if clicking on start/end node, do nothing.
            return;
        }

        if (draggableNodes.contains(node)) {
            // Clicked on wormhole or tunnel node: change it to normal walkable node when in editing mode.
            if (editMode == MODE_WORMHOLE || editMode == MODE_TUNNEL_LEFT || editMode == MODE_TUNNEL_RIGHT
                    || editMode == MODE_TUNNEL_UP || editMode == MODE_TUNNEL_DOWN) {
                if (node.isWormhole()) {
                    Node exit = node.getExit();
                    JButton exitBtn = buttons[exit.getY()][exit.getX()];
                    exitBtn.setBackground(WALKABLE_NODE_COLOR);
                    exitBtn.setText("");
                    exit.setWalkable(true);
                    exit.setExit(null);
                    draggableNodes.remove(exit);
                }
                btn.setBackground(WALKABLE_NODE_COLOR);
                btn.setText("");
                node.setWalkable(true);
                node.setExit(null);
                draggableNodes.remove(node);
            }
            return;
        }

        if (!node.isWalkable()) {
            // Clicked on obstacle node: change it to normal walkable node when in editing mode.
            if (editMode == MODE_OBSTACLE) {
                btn.setBackground(WALKABLE_NODE_COLOR);
                node.setWalkable(true);
            }
            return;
        }

        // to here, it is clicking on normal walkable node.
        removePathPoint(node.getX(), node.getY());

        Node leftNode = grid.getNodeAt(node.getX() - 1, node.getY());
        Node rightNode = grid.getNodeAt(node.getX() + 1, node.getY());
        Node upNode = grid.getNodeAt(node.getX(), node.getY() - 1);
        Node downNode = grid.getNodeAt(node.getX(), node.getY() + 1);
        Node exitNode = null;
        switch (editMode) {
            case MODE_OBSTACLE:
                node.setWalkable(false);
                btn.setBackground(OBSTACLE_NODE_COLOR);
                break;
            case MODE_WORMHOLE:
                if (leftNode != null) {
                    exitNode = leftNode;
                } else if (rightNode != null) {
                    exitNode = rightNode;
                } else if (upNode != null) {
                    exitNode = upNode;
                } else if (downNode != null) {
                    exitNode = downNode;
                }
                // FIXME: if the added wormhole is surrounded by obstacles, exitNode is null.
                removePathPoint(exitNode.getX(), exitNode.getY());
                JButton exitBtn = buttons[exitNode.getY()][exitNode.getX()];

                btn.setBackground(WORMHOLE_NODE_COLOR);
                btn.setText(Integer.toString(wormholeId));
                node.setWalkable(true);
                node.setExit(exitNode);
                exitBtn.setBackground(WORMHOLE_NODE_COLOR);
                exitBtn.setText(Integer.toString(wormholeId));
                exitNode.setWalkable(true);
                exitNode.setExit(node);
                draggableNodes.add(node);
                draggableNodes.add(exitNode);
                wormholeId++;
                break;
            case MODE_TUNNEL_LEFT:
                draggableNodes.add(node);
                btn.setBackground(TUNNEL_NODE_COLOR);
                btn.setText(TUNNEL_LEFT_TITLE);
                node.setWalkable(true);
                node.setExit(leftNode);
                // FIXME: If the added tunnel is align to map left border, leftNode is null.
                break;
            case MODE_TUNNEL_RIGHT:
                draggableNodes.add(node);
                btn.setBackground(TUNNEL_NODE_COLOR);
                btn.setText(TUNNEL_RIGHT_TITLE);
                node.setWalkable(true);
                node.setExit(rightNode);
                // FIXME: If the added tunnel is align to map right border, leftNode is null.
                break;
            case MODE_TUNNEL_UP:
                draggableNodes.add(node);
                btn.setBackground(TUNNEL_NODE_COLOR);
                btn.setText(TUNNEL_UP_TITLE);
                node.setWalkable(true);
                node.setExit(upNode);
                // FIXME: If the added tunnel is align to map top border, leftNode is null.
                break;
            case MODE_TUNNEL_DOWN:
                draggableNodes.add(node);
                btn.setBackground(TUNNEL_NODE_COLOR);
                btn.setText(TUNNEL_DOWN_TITLE);
                node.setWalkable(true);
                node.setExit(downNode);
                // FIXME: If the added tunnel is align to map bottom border, leftNode is null.
                break;
        }
    }
}
