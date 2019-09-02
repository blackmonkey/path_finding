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
import darkstudio.pathfinding.model.TeleporterNode;
import darkstudio.pathfinding.model.TunnelNode;
import darkstudio.pathfinding.model.WormholeNode;
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
import java.util.stream.Collectors;

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
    private Node draggedNode;
    private JButton draggedBtn;
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
        grid.setupTunnels(draggableNodes.stream().filter(node -> node instanceof TunnelNode).map(node -> (TunnelNode) node).collect(Collectors.toList()));

        long startTs = System.currentTimeMillis();
        JumpPointFinderBase finder = Util.jumpPointFinder(DiagonalMovement.TeleportNever, new Options().checkTeleporter(true));
        path = finder.findPath(startNode.getX(), startNode.getY(), endNode.getX(), endNode.getY(), grid.reset());
        long duration = System.currentTimeMillis() - startTs;

        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No path available", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Point point : path) {
            buttons[point.y][point.x].setBackground(PATH_NODE_COLOR);
        }
        draggableNodes.forEach(node -> {
            if (path.contains(new Point(node.getX(), node.getY()))) {
                Color nodeColor;
                if (node instanceof WormholeNode) {
                    nodeColor = WORMHOLE_NODE_COLOR;
                } else if (node instanceof TunnelNode) {
                    nodeColor = TUNNEL_NODE_COLOR;
                } else if (node == startNode) {
                    nodeColor = START_NODE_COLOR;
                } else {
                    nodeColor = END_NODE_COLOR;
                }
                nodeColor = Util.mix(nodeColor, PATH_NODE_COLOR);
                buttons[node.getY()][node.getX()].setBackground(nodeColor);
            }
        });

        infoBar.setText("node count: " + path.size() + ", time: " + duration + "ms");
    }

    private void updateDraggedNode(Node node) {
        if (node instanceof TeleporterNode) {
            onRemovedTeleporterNode((TeleporterNode) draggedNode);
            onCreatedTunnelNode((TunnelNode) node);
        } else {
            draggableNodes.remove(draggedNode);
            draggableNodes.add(node);
        }
        draggedNode = node;
    }

    private void onCreatedTunnelNode(TunnelNode tunnelNode) {
        for (Node node : draggableNodes) {
            if (node instanceof TunnelNode && tunnelNode.equals(((TunnelNode) node).getOut())) {
                ((TunnelNode) node).setOut(tunnelNode);
                tunnelNode.addIn((TunnelNode) node);
            }
        }
        draggableNodes.add(tunnelNode);
    }

    private void onRemovedTeleporterNode(TeleporterNode teleporterNode) {
        Node curNode = grid.getNodeAt(teleporterNode.getX(), teleporterNode.getY());
        for (Node node : draggableNodes) {
            if (node instanceof TunnelNode && teleporterNode.equals(((TunnelNode) node).getOut())) {
                ((TunnelNode) node).setOut(curNode);
            }
        }
        draggableNodes.remove(teleporterNode);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JButton btn = (JButton) e.getSource();
        Node node = (Node) btn.getClientProperty(NODE);

        String info = "(" + node.getX() + ", " + node.getY() + ")";
        Node end = grid.getFinalEnd(node);
        if (end != null) {
            info += "=>(" + end.getX() + ", " + end.getY() + ")";
        }
        btn.setToolTipText(info);

        if (!mouseDragged.get() || draggedBtn == null || draggedNode == null) {
            // If mouse is not dragging, do nothing.
            return;
        }

        if (draggableNodes.contains(node) || !node.isWalkable()) {
            // If drag to another existed wormhole, tunnel or obstacle, do nothing.
            return;
        }

        // To here, the start/end/wormhole/tunnel node is dragged to a normal walkable node.

        btn.setBackground(draggedBtn.getBackground());
        btn.setText(draggedBtn.getText());
        draggedBtn.setBackground(WALKABLE_NODE_COLOR);
        draggedBtn.setText("");

        removePathPoint(node.getX(), node.getY());

        if (draggedNode == startNode) {
            startNode = node;
            updateDraggedNode(node);
        } else if (draggedNode == endNode) {
            endNode = node;
            updateDraggedNode(node);
        } else if (draggedNode instanceof WormholeNode) {
            WormholeNode preWormholeNode = (WormholeNode) draggedNode;
            WormholeNode curWormholeNode = new WormholeNode(node);
            Node preNormalNode = preWormholeNode.toNormalNode();
            grid.replaceNode(node, curWormholeNode);
            grid.replaceNode(preWormholeNode, preNormalNode);
            grid.setupWormhole(curWormholeNode, preWormholeNode.getPeer());
            updateDraggedNode(curWormholeNode);
            draggedBtn.putClientProperty(NODE, preNormalNode);
        } else {
            // draggableNode is a tunnel, but its out is possible null. Therefore, we recalculate its out here.
            TunnelNode preTunnelNode = (TunnelNode) draggedNode;
            TunnelNode curTunnelNode = new TunnelNode(node);
            Node preNormalNode = preTunnelNode.toNormalNode();
            curTunnelNode.setDirection(preTunnelNode.getDirection());
            grid.replaceNode(node, curTunnelNode);
            grid.replaceNode(preTunnelNode, preNormalNode);
            updateDraggedNode(curTunnelNode);
            draggedBtn.putClientProperty(NODE, preNormalNode);

            Node leftNode = grid.getNodeAt(node.getX() - 1, node.getY());
            Node rightNode = grid.getNodeAt(node.getX() + 1, node.getY());
            Node upNode = grid.getNodeAt(node.getX(), node.getY() - 1);
            Node downNode = grid.getNodeAt(node.getX(), node.getY() + 1);
            switch (curTunnelNode.getDirection()) {
                case TunnelNode.LEFT:
                    curTunnelNode.setOut(leftNode);
                    // FIXME: If the moved tunnel is align to map left border, leftNode is null.
                    break;
                case TunnelNode.RIGHT:
                    curTunnelNode.setOut(rightNode);
                    // FIXME: If the moved tunnel is align to map right border, leftNode is null.
                    break;
                case TunnelNode.UP:
                    curTunnelNode.setOut(upNode);
                    // FIXME: If the moved tunnel is align to map top border, leftNode is null.
                    break;
                case TunnelNode.DOWN:
                    curTunnelNode.setOut(downNode);
                    // FIXME: If the moved tunnel is align to map bottom border, leftNode is null.
                    break;
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JButton btn = (JButton) e.getSource();
        btn.setToolTipText(null);

        if (mousePressed.get()) {
            mouseDragged.set(true);
            Node node = (Node) btn.getClientProperty(NODE);
            if (node == draggedNode) {
                draggedBtn = btn;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed.set(true);
        mouseDragged.set(false);
        draggedBtn = null;
        draggedNode = null;

        JButton btn = (JButton) e.getSource();
        Node node = (Node) btn.getClientProperty(NODE);
        if (draggableNodes.contains(node)) {
            draggedNode = node;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed.set(false);
        draggedBtn = null;
        draggedNode = null;

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
                if (node instanceof WormholeNode) {
                    WormholeNode wormholeNode = (WormholeNode) node;
                    WormholeNode peerNode = wormholeNode.getPeer();
                    grid.breakWormhole(wormholeNode, peerNode);
                    onRemovedTeleporterNode(wormholeNode);
                    onRemovedTeleporterNode(peerNode);

                    JButton peerBtn = buttons[peerNode.getY()][peerNode.getX()];
                    peerBtn.setBackground(WALKABLE_NODE_COLOR);
                    peerBtn.setText("");
                    peerBtn.putClientProperty(NODE, grid.getNodeAt(peerNode.getX(), peerNode.getY()));
                } else {
                    grid.replaceNode(node, ((TunnelNode) node).toNormalNode());
                    onRemovedTeleporterNode((TunnelNode) node);
                }
                btn.setBackground(WALKABLE_NODE_COLOR);
                btn.setText("");
                btn.putClientProperty(NODE, grid.getNodeAt(node.getX(), node.getY()));
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
        TunnelNode tunnelNode;
        switch (editMode) {
            case MODE_OBSTACLE:
                node.setWalkable(false);
                btn.setBackground(OBSTACLE_NODE_COLOR);
                break;
            case MODE_WORMHOLE:
                Node toNode = null;
                if (leftNode != null) {
                    toNode = leftNode;
                } else if (rightNode != null) {
                    toNode = rightNode;
                } else if (upNode != null) {
                    toNode = upNode;
                } else if (downNode != null) {
                    toNode = downNode;
                }
                // FIXME: if the added wormhole is surrounded by obstacles, toNode is null.
                removePathPoint(toNode.getX(), toNode.getY());
                grid.setupWormhole(node, toNode);

                WormholeNode oneWormholeNode = (WormholeNode) grid.getNodeAt(node.getX(), node.getY());
                WormholeNode theOtherWormholeNode = (WormholeNode) grid.getNodeAt(toNode.getX(), toNode.getY());
                draggableNodes.add(oneWormholeNode);
                draggableNodes.add(theOtherWormholeNode);

                btn.setBackground(WORMHOLE_NODE_COLOR);
                btn.setText(Integer.toString(wormholeId));
                btn.putClientProperty(NODE, oneWormholeNode);
                JButton toBtn = buttons[toNode.getY()][toNode.getX()];
                toBtn.setBackground(WORMHOLE_NODE_COLOR);
                toBtn.setText(Integer.toString(wormholeId));
                toBtn.putClientProperty(NODE, theOtherWormholeNode);
                wormholeId++;
                break;
            case MODE_TUNNEL_LEFT:
                tunnelNode = new TunnelNode(node);
                tunnelNode.setDirection(TunnelNode.LEFT);
                grid.replaceNode(node, tunnelNode);
                onCreatedTunnelNode(tunnelNode);
                btn.setBackground(TUNNEL_NODE_COLOR);
                btn.setText(TUNNEL_LEFT_TITLE);
                btn.putClientProperty(NODE, tunnelNode);
                tunnelNode.setWalkable(true);
                tunnelNode.setOut(leftNode);
                // FIXME: If the added tunnel is align to map left border, leftNode is null.
                break;
            case MODE_TUNNEL_RIGHT:
                tunnelNode = new TunnelNode(node);
                tunnelNode.setDirection(TunnelNode.RIGHT);
                grid.replaceNode(node, tunnelNode);
                onCreatedTunnelNode(tunnelNode);
                btn.setBackground(TUNNEL_NODE_COLOR);
                btn.setText(TUNNEL_RIGHT_TITLE);
                btn.putClientProperty(NODE, tunnelNode);
                tunnelNode.setWalkable(true);
                tunnelNode.setOut(rightNode);
                // FIXME: If the added tunnel is align to map right border, leftNode is null.
                break;
            case MODE_TUNNEL_UP:
                tunnelNode = new TunnelNode(node);
                tunnelNode.setDirection(TunnelNode.UP);
                grid.replaceNode(node, tunnelNode);
                onCreatedTunnelNode(tunnelNode);
                btn.setBackground(TUNNEL_NODE_COLOR);
                btn.setText(TUNNEL_UP_TITLE);
                btn.putClientProperty(NODE, tunnelNode);
                tunnelNode.setWalkable(true);
                tunnelNode.setOut(upNode);
                // FIXME: If the added tunnel is align to map top border, leftNode is null.
                break;
            case MODE_TUNNEL_DOWN:
                tunnelNode = new TunnelNode(node);
                tunnelNode.setDirection(TunnelNode.DOWN);
                grid.replaceNode(node, tunnelNode);
                onCreatedTunnelNode(tunnelNode);
                btn.setBackground(TUNNEL_NODE_COLOR);
                btn.setText(TUNNEL_DOWN_TITLE);
                btn.putClientProperty(NODE, tunnelNode);
                tunnelNode.setWalkable(true);
                tunnelNode.setOut(downNode);
                // FIXME: If the added tunnel is align to map bottom border, leftNode is null.
                break;
        }
    }
}
