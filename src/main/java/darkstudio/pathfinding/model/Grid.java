/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

import darkstudio.pathfinding.algorithm.DiagonalMovement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The Grid class, which serves as the encapsulation of the layout of the nodes.
 */
public class Grid {
    public static final int TELEPORT_NORMAL_NORMAL = 0; // both nodes are normal ones
    public static final int TELEPORT_NORMAL_TUNNEL = 1; // the left node is normal, while the right node is tunnel node which does NOT teleport to the left one.
    public static final int TELEPORT_NORMAL_OT_TUNNEL = 2; // the left node is normal, while the right node is tunnel node which DOES teleport to the left one.
    public static final int TELEPORT_NORMAL_WORMHOLE = 3; // the left node is normal, while the right node is wormhole node which does NOT teleport to the left one.
    public static final int TELEPORT_TUNNEL_NORMAL = 4; // the left node is tunnel node and does NOT teleport to the right one which is normal one.
    public static final int TELEPORT_TUNNEL_TO_NORMAL = 5; // the left node is tunnel node and DOES teleport to the right one which is normal one.
    public static final int TELEPORT_TUNNEL_TUNNEL = 6; // both nodes are tunnel ones and they do NOT teleport to each other.
    public static final int TELEPORT_TUNNEL_OT_TUNNEL = 7; // both nodes are tunnel ones and the right node teleports to the left one.
    public static final int TELEPORT_TUNNEL_TO_TUNNEL = 8; // both nodes are tunnel ones and the left node teleports to the right one.
    public static final int TELEPORT_TUNNEL_TO_WORMHOLE = 9; // the left node is tunnel node and teleports to the right one which is wormhole node.
    public static final int TELEPORT_TUNNEL_WORMHOLE = 10; // the left node is tunnel node while the right one is wormhole node, and they do NOT teleport to each other.
    public static final int TELEPORT_WORMHOLE_NORMAL = 11; // the left node is wormhole node and does NOT teleport to the right node which is normal one.
    public static final int TELEPORT_WORMHOLE_OT_TUNNEL = 12; // the left node is wormhole node while the right one is tunnel node and teleports to the left one.
    public static final int TELEPORT_WORMHOLE_TUNNEL = 13; // the left node is wormhole node while the right one is tunnel node, and they do NOT teleport to each other.
    public static final int TELEPORT_WORMHOLE_WORMHOLE = 14; // both nodes are wormhole ones and they do NOT teleport to each other.
    public static final int TELEPORT_WORMHOLE_TO_WORMHOLE = 15; // both nodes are wormhole ones and they DO teleport to each other.

    private Node[][] nodes;
    private List<List<Node>> tunnels = new ArrayList<>();

    /**
     * Create grid with all walkable nodes.
     *
     * @param width number of columns of the grid
     * @param height number of rows of the grid.
     */
    public Grid(int width, int height) {
        this(width, height, null);
    }

    /**
     * Create grid with specific walkable nodes.
     *
     * @param width number of columns of the grid
     * @param height number of rows of the grid.
     * @param matrix a 0-1 matrix representing the walkable status of the nodes (0 for walkable). {@code null} indicates
     * all the nodes will be walkable.
     * @throws IllegalArgumentException if {@code matrix} is specified, while its size is not
     * {@code height} x {@code width}
     */
    public Grid(int width, int height, int[][] matrix) {
        if (matrix != null && (matrix.length != height || matrix[0].length != width)) {
            throw new IllegalArgumentException("Matrix size does not fit");
        }
        buildNodes(width, height, matrix);
    }

    /**
     * Create grid with specific walkable nodes.
     *
     * @param matrix a 0-1 matrix representing the walkable status of the nodes (0 for walkable).
     * Must NOT be {@code null}.
     * @throws IllegalArgumentException if {@code matrix} is {@code null}.
     */
    public Grid(int[][] matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix size does not fit");
        }

        int height = matrix.length;
        int width = matrix[0].length;
        buildNodes(width, height, matrix);
    }

    /**
     * Build the nodes.
     *
     * @param width number of columns of the grid
     * @param height number of rows of the grid.
     * @param matrix a 0-1 matrix representing the walkable status of the nodes (0 for walkable). {@code null} indicates
     * all the nodes will be walkable.
     */
    private void buildNodes(int width, int height, int[][] matrix) {
        nodes = new Node[height][width];
        boolean hasMatrix = matrix != null;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 0 will be walkable while others will be un-walkable
                nodes[y][x] = new Node(x, y, !hasMatrix || matrix[y][x] == 0);
            }
        }
    }

    /**
     * Gets a node at the specified location in the grid
     *
     * @param x the x coordinate of the node.
     * @param y the y coordinate of the node.
     * @return The {@link Node} at the specific coordinates
     */
    public Node getNodeAt(int x, int y) {
        return isInside(x, y) ? nodes[y][x] : null;
    }

    /**
     * Replace oldNode with newNode.
     *
     * @param oldNode the node to be replaced.
     * @param newNode the node to replace.
     */
    public void replaceNode(Node oldNode, Node newNode) {
        nodes[oldNode.getY()][oldNode.getX()] = newNode;
    }

    /**
     * Set specific node to specific place, and replace the one there.
     *
     * @param x the x coordinate of the node.
     * @param y the y coordinate of the node.
     * @param node the node to replace.
     */
    public void replaceNode(int x, int y, Node node) {
        if (isInside(x, y)) {
            nodes[y][x] = node;
        }
    }

    /**
     * Determine whether the node at the given position is walkable. Also returns {@code false} if the position is
     * outside the grid.
     *
     * @param x the x coordinate of the node.
     * @param y the y coordinate of the node.
     * @return {@code true} if this node is inside the grid and walkable, {@code false} otherwise.
     */
    public boolean isWalkableAt(int x, int y) {
        Node node = getNodeAt(x, y);
        return node != null && node.isWalkable();
    }

    /**
     * Determine whether the position is inside the grid.
     *
     * @param x the x coordinate of the node.
     * @param y the y coordinate of the node.
     * @return {@code true} if this node is inside the grid, {@code false} otherwise.
     */
    public boolean isInside(int x, int y) {
        return y >= 0 && y < nodes.length && x >= 0 && x < nodes[0].length;
    }

    /**
     * Set whether the node on the given position is walkable. Do nothing if the coordinate is not inside the grid.
     *
     * @param x the x coordinate of the node.
     * @param y the y coordinate of the node.
     * @param walkable whether the position is walkable.
     */
    public void setWalkableAt(int x, int y, boolean walkable) {
        if (isInside(x, y)) {
            nodes[y][x].setWalkable(walkable);
        }
    }

    /**
     * Link two nodes as wormhole.
     *
     * @param one one end of the wormhole.
     * @param theOther the other end of the wormhole.
     */
    public void setupWormhole(Node one, Node theOther) {
        WormholeNode oneWormholeNode;
        if (one instanceof WormholeNode) {
            oneWormholeNode = (WormholeNode) one;
        } else {
            oneWormholeNode = new WormholeNode(one);
            replaceNode(one, oneWormholeNode);
        }

        WormholeNode theOtherWormholeNode;
        if (theOther instanceof WormholeNode) {
            theOtherWormholeNode = (WormholeNode) theOther;
        } else {
            theOtherWormholeNode = new WormholeNode(theOther);
            replaceNode(theOther, theOtherWormholeNode);
        }

        oneWormholeNode.setWalkable(true);
        oneWormholeNode.setPeer(theOtherWormholeNode);
        theOtherWormholeNode.setWalkable(true);
        theOtherWormholeNode.setPeer(oneWormholeNode);
    }

    /**
     * Break wormhole to two individual walkable nodes.
     *
     * @param one one end of the wormhole.
     * @param theOther the other end of the wormhole.
     */
    public void breakWormhole(WormholeNode one, WormholeNode theOther) {
        replaceNode(one, one.toNormalNode());
        replaceNode(theOther, theOther.toNormalNode());
        one.setWalkable(true);
        one.setPeer(null);
        theOther.setWalkable(true);
        theOther.setPeer(null);
    }

    /**
     * Analyze and cache all teleport nodes link information
     *
     * @param nodes the nodes to check which are possible normal walkable, wormhole or tunnel nodes.
     */
    public void setupTunnels(List<TunnelNode> nodes) {
        tunnels.clear();
        while (!nodes.isEmpty()) {
            TunnelNode node = nodes.get(0);
            int[] idx = indexOfTunnelLink(node);
            nodes.remove(node);
            if (idx == null) {
                List<Node> newLink = new ArrayList<>();
                while (true) {
                    newLink.add(node);
                    Node out = node.getOut();
                    if (out instanceof WormholeNode) {
                        newLink.add(out);
                        nodes.remove(out);
                        newLink.add(((WormholeNode) out).getPeer());
                        nodes.remove(((WormholeNode) out).getPeer());
                        break;
                    } else if (out instanceof TunnelNode) {
                        idx = indexOfTunnelLink(out);
                        if (idx == null) {
                            node = (TunnelNode) out;
                        } else {
                            int linkIdx = idx[0];
                            int nodeIdx = idx[1];
                            List<Node> preLink = tunnels.get(linkIdx);
                            newLink.addAll(preLink.subList(nodeIdx, preLink.size()));
                            if (nodeIdx == 0) {
                                tunnels.remove(linkIdx); // replace preLink cause it is subsection of current newLink.
                            }
                            break;
                        }
                    } else { // out is normal node
                        newLink.add(out);
                        nodes.remove(out);
                        break;
                    }
                }
                tunnels.add(newLink);
            }
        }
    }

    public List<List<Node>> getTunnels() {
        return tunnels;
    }

    private int[] indexOfTunnelLink(Node node) {
        for (int i = 0; i < tunnels.size(); i++) {
            int pos = tunnels.get(i).indexOf(node);
            if (pos != -1) {
                return new int[]{i, pos};
            }
        }
        return null;
    }

    private int[] indexOfTunnelLink(Node startNode, Node endNode) {
        List<Node> tunnel;
        int startPos, endPos;
        for (int i = 0; i < tunnels.size(); i++) {
            tunnel = tunnels.get(i);
            startPos = tunnel.indexOf(startNode);
            endPos = tunnel.indexOf(endNode);
            if (startPos != -1 && endPos != -1) {
                return new int[]{i, startPos, endPos};
            }
        }
        return null;
    }

    /**
     * Get the final target node of specific wormhole/tunnel node.
     * <ul>
     * <li>If the specific node is a wormhole node, returns its peer node.</li>
     * <li>If the specific node is a tunnel node:
     * <ul>
     * <li>if the node is contained in certain tunnel link, then returns the target of the last tunnel node in the link.</li>
     * <li>otherwise, returns the target of the specific tunnel node.</li>
     * </ul>
     * </li>
     * </ul>
     *
     * @param node the tunnel node to check.
     * @return the found final target node, or {@code null} otherwise.
     */
    public Node getFinalEnd(Node node) {
        if (node instanceof WormholeNode) {
            return ((WormholeNode) node).getPeer();
        }
        if (node instanceof TunnelNode) {
            List<Node> tunnelLink = tunnels.stream().filter(link -> link.contains(node)).findFirst().orElse(Collections.emptyList());
            return tunnelLink.isEmpty() ? null : tunnelLink.get(tunnelLink.size() - 1);
        }
        return null;
    }

    public Node getFinalEnd(int x, int y) {
        return getFinalEnd(getNodeAt(x, y));
    }

    /**
     * Determine whether the node at the given position is a teleporter. Also returns {@code false} if the position is
     * outside the grid.
     *
     * @param x the x coordinate of the node.
     * @param y the y coordinate of the node.
     * @return {@code true} if this node is a teleporter, {@code false} otherwise.
     */
    public boolean isTeleporterAt(int x, int y) {
        return isTeleporterNode(getNodeAt(x, y));
    }

    /**
     * Determine whether the specific node is a teleporter.
     *
     * @param node the node to check.
     * @return {@code true} if this node is a teleporter, {@code false} otherwise.
     */
    public boolean isTeleporterNode(Node node) {
        return node instanceof TunnelNode || node instanceof WormholeNode;
    }

    /**
     * Determine whether there is a teleporter from specific start position to specific end position. Also returns
     * {@code false} if either position is outside the grid.
     *
     * @param startX the x coordinate of the start node.
     * @param startY the y coordinate of the start node.
     * @param endX the x coordinate of the end node.
     * @param endY the y coordinate of the end node.
     * @return one of TELEPORT_* constants.
     */
    public int getTeleporterType(int startX, int startY, int endX, int endY) {
        Node startNode = getNodeAt(startX, startY);
        Node endNode = getNodeAt(endX, endY);
        return getTeleporterType(startNode, endNode);
    }

    public int getTeleporterType(Node startNode, Node endNode) {
        if (startNode instanceof WormholeNode) {
            if (endNode instanceof WormholeNode) {
                WormholeNode peer = ((WormholeNode) startNode).getPeer();
                return peer.equals(endNode) ? TELEPORT_WORMHOLE_TO_WORMHOLE : TELEPORT_WORMHOLE_WORMHOLE;
            }
            if (endNode instanceof TunnelNode) {
                int[] idx = indexOfTunnelLink(startNode, endNode);
                return idx != null ? TELEPORT_WORMHOLE_OT_TUNNEL : TELEPORT_WORMHOLE_TUNNEL;
            }
            return TELEPORT_WORMHOLE_NORMAL;
        }

        if (startNode instanceof TunnelNode) {
            int[] idx = indexOfTunnelLink(startNode, endNode);
            if (endNode instanceof WormholeNode) {
                return idx != null ? TELEPORT_TUNNEL_TO_WORMHOLE : TELEPORT_TUNNEL_WORMHOLE;
            }
            if (endNode instanceof TunnelNode) {
                if (idx != null) {
                    int startPos = idx[1];
                    int endPos = idx[2];
                    return startPos > endPos ? TELEPORT_TUNNEL_OT_TUNNEL : TELEPORT_TUNNEL_TO_TUNNEL;
                }
                return TELEPORT_TUNNEL_TUNNEL;
            }
            return idx != null ? TELEPORT_TUNNEL_TO_NORMAL : TELEPORT_TUNNEL_NORMAL;
        }

        if (endNode instanceof WormholeNode) {
            return TELEPORT_NORMAL_WORMHOLE;
        }

        if (endNode instanceof TunnelNode) {
            int[] idx = indexOfTunnelLink(startNode, endNode);
            return idx != null ? TELEPORT_NORMAL_OT_TUNNEL : TELEPORT_NORMAL_TUNNEL;
        }

        return TELEPORT_NORMAL_NORMAL;
    }

    public Set<Node> getNeighbors(Node node, DiagonalMovement diagonalMovement) {
        return getNeighbors(node, diagonalMovement, false);
    }

    /**
     * Get the neighbors of the given node.
     * <pre>
     *     offsets      diagonalOffsets:
     *  +---+---+---+    +---+---+---+
     *  |   | 0 |   |    | 0 |   | 1 |
     *  +---+---+---+    +---+---+---+
     *  | 3 |   | 1 |    |   |   |   |
     *  +---+---+---+    +---+---+---+
     *  |   | 2 |   |    | 3 |   | 2 |
     *  +---+---+---+    +---+---+---+
     * </pre>
     * When allowDiagonal is {@code true}, if offsets[i] is valid, then diagonalOffsets[i] and
     * diagonalOffsets[(i + 1) % 4] is valid.
     *
     * @param node
     * @param diagonalMovement
     * @param checkTeleporter whether check teleporter nodes.
     * @return the requested neighbors of the node.
     * @throws IllegalArgumentException if diagonalMovement is invalid.
     */
    public Set<Node> getNeighbors(Node node, DiagonalMovement diagonalMovement, boolean checkTeleporter) {
        int x = node.getX();
        int y = node.getY();
        Set<Node> neighbors = new LinkedHashSet<>();
        boolean s0 = false, d0 = false,
                s1 = false, d1 = false,
                s2 = false, d2 = false,
                s3 = false, d3 = false;

        // teleporter node has only one logical neighbor, i.e. its final end.
        if (checkTeleporter && isTeleporterNode(node)) {
            Node end = getFinalEnd(node);
            if (end != null) {
                neighbors.add(end);
            }
            return neighbors;
        }

        // neighbor above
        if (isWalkableAt(x, y - 1)) {
            neighbors.add(nodes[y - 1][x]);
            s0 = true;
        }
        // neighbor on right
        if (isWalkableAt(x + 1, y)) {
            neighbors.add(nodes[y][x + 1]);
            s1 = true;
        }
        // neighbor below
        if (isWalkableAt(x, y + 1)) {
            neighbors.add(nodes[y + 1][x]);
            s2 = true;
        }
        // neighbor on left
        if (isWalkableAt(x - 1, y)) {
            neighbors.add(nodes[y][x - 1]);
            s3 = true;
        }

        switch (diagonalMovement) {
            case Never:
                return neighbors;
            case OnlyWhenNoObstacles:
                d0 = s3 && s0;
                d1 = s0 && s1;
                d2 = s1 && s2;
                d3 = s2 && s3;
                break;
            case IfAtMostOneObstacle:
                d0 = s3 || s0;
                d1 = s0 || s1;
                d2 = s1 || s2;
                d3 = s2 || s3;
                break;
            case Always:
                d0 = true;
                d1 = true;
                d2 = true;
                d3 = true;
                break;
            default:
                throw new IllegalArgumentException("Incorrect value of diagonalMovement");
        }

        // neighbor left above
        if (d0 && isWalkableAt(x - 1, y - 1)) {
            neighbors.add(nodes[y - 1][x - 1]);
        }
        // neighbor right above
        if (d1 && isWalkableAt(x + 1, y - 1)) {
            neighbors.add(nodes[y - 1][x + 1]);
        }
        // neighbor right below
        if (d2 && isWalkableAt(x + 1, y + 1)) {
            neighbors.add(nodes[y + 1][x + 1]);
        }
        // neighbor left below
        if (d3 && isWalkableAt(x - 1, y + 1)) {
            neighbors.add(nodes[y + 1][x - 1]);
        }

        return neighbors;
    }

    public Grid reset() {
        int height = nodes.length;
        int width = nodes[0].length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                nodes[y][x].reset();
            }
        }
        return this;
    }
}
