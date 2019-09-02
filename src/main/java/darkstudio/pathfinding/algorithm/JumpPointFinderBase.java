/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author imor / https://github.com/imor
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.algorithm;

import darkstudio.pathfinding.model.Grid;
import darkstudio.pathfinding.model.Node;
import darkstudio.pathfinding.utility.Util;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Base class for the Jump Point Search algorithm
 */
public abstract class JumpPointFinderBase {
    protected Options options;
    private PriorityQueue<Node> openList;
    protected Grid grid;
    protected Node endNode;

    public JumpPointFinderBase(Options options) {
        this.options = options;
    }

    /**
     * Find and return the path.
     *
     * @param startX start x coordinate
     * @param startY start y coordinate
     * @param endX end x coordinate
     * @param endY end y coordinate
     * @param grid the grid to search
     * @return the path, including both start and end positions.
     */
    public List<Point> findPath(int startX, int startY, int endX, int endY, Grid grid) {
        Node startNode = grid.getNodeAt(startX, startY);
        endNode = grid.getNodeAt(endX, endY);
        Node node;

        openList = new PriorityQueue<>();
        this.grid = grid;

        // set the `g` and `f` value of the start node to be 0
        startNode.setGScore(0);
        startNode.setFScore(0);

        // push the start node into the open list
        openList.add(startNode);
        startNode.setOpened(true);

        if (options.checkTeleporter()) {
            // while the open list is not empty
            while (!openList.isEmpty()) {
                // pop the position of node which has the minimum `f` value.
                node = openList.poll();
                node.setClosed(true);

                if (node == endNode) {
                    return Util.expandTeleportPath(Util.backtrace(endNode), grid);
                }
                identifyTeleportSuccessors(node);
            }
        } else {
            // while the open list is not empty
            while (!openList.isEmpty()) {
                // pop the position of node which has the minimum `f` value.
                node = openList.poll();
                node.setClosed(true);

                if (node == endNode) {
                    return Util.expandPath(Util.backtrace(endNode), grid);
                }

                identifySuccessors(node);
            }
        }

        // fail to find the path
        return Collections.emptyList();
    }

    private double getJumpNodeGScore(Node node, Node jumpNode, int teleportType, Grid grid) {
        int dx, dy;
        Node jumpEnd;
        switch (teleportType) {
            case Grid.TELEPORT_TUNNEL_TO_NORMAL:
            case Grid.TELEPORT_TUNNEL_TO_TUNNEL:
            case Grid.TELEPORT_TUNNEL_TO_WORMHOLE:
            case Grid.TELEPORT_WORMHOLE_TO_WORMHOLE:
                return node.getGScore();
            case Grid.TELEPORT_TUNNEL_NORMAL:
            case Grid.TELEPORT_TUNNEL_TUNNEL:
            case Grid.TELEPORT_TUNNEL_OT_TUNNEL:
            case Grid.TELEPORT_TUNNEL_WORMHOLE:
            case Grid.TELEPORT_WORMHOLE_NORMAL:
            case Grid.TELEPORT_WORMHOLE_TUNNEL:
            case Grid.TELEPORT_WORMHOLE_OT_TUNNEL:
            case Grid.TELEPORT_WORMHOLE_WORMHOLE:
                jumpEnd = grid.getFinalEnd(node);
                dx = Math.abs(jumpNode.getX() - jumpEnd.getX());
                dy = Math.abs(jumpNode.getY() - jumpEnd.getY());
                return node.getGScore() + Heuristic.octile(dx, dy);
            case Grid.TELEPORT_NORMAL_NORMAL:
            case Grid.TELEPORT_NORMAL_TUNNEL:
            case Grid.TELEPORT_NORMAL_OT_TUNNEL:
            case Grid.TELEPORT_NORMAL_WORMHOLE:
            default:
                dx = Math.abs(jumpNode.getX() - node.getX());
                dy = Math.abs(jumpNode.getY() - node.getY());
                return node.getGScore() + Heuristic.octile(dx, dy);
        }
    }

    private void setJumpNodeHScore(Node node, Node jumpNode, int teleportType, Grid grid) {
        int dx, dy;
        Node jumpEnd;
        switch (teleportType) {
            case Grid.TELEPORT_NORMAL_NORMAL:
            case Grid.TELEPORT_TUNNEL_NORMAL:
            case Grid.TELEPORT_TUNNEL_TO_NORMAL:
            case Grid.TELEPORT_WORMHOLE_NORMAL:
                dx = Math.abs(jumpNode.getX() - endNode.getX());
                dy = Math.abs(jumpNode.getY() - endNode.getY());
                jumpNode.setHScore(options.heuristic().apply(dx, dy));
                break;
            case Grid.TELEPORT_NORMAL_TUNNEL:
            case Grid.TELEPORT_NORMAL_WORMHOLE:
            case Grid.TELEPORT_TUNNEL_TUNNEL:
            case Grid.TELEPORT_TUNNEL_WORMHOLE:
            case Grid.TELEPORT_WORMHOLE_TUNNEL:
            case Grid.TELEPORT_WORMHOLE_WORMHOLE:
                jumpEnd = grid.getFinalEnd(jumpNode);
                if (jumpEnd.getHScore() == null) {
                    dx = Math.abs(jumpEnd.getX() - endNode.getX());
                    dy = Math.abs(jumpEnd.getY() - endNode.getY());
                    jumpEnd.setHScore(options.heuristic().apply(dx, dy));
                }
                jumpNode.setHScore(jumpEnd.getHScore());
                break;
            case Grid.TELEPORT_NORMAL_OT_TUNNEL:
            case Grid.TELEPORT_TUNNEL_OT_TUNNEL:
            case Grid.TELEPORT_TUNNEL_TO_TUNNEL:
            case Grid.TELEPORT_TUNNEL_TO_WORMHOLE:
            case Grid.TELEPORT_WORMHOLE_OT_TUNNEL:
                jumpNode.setHScore(node.getHScore());
                break;
            case Grid.TELEPORT_WORMHOLE_TO_WORMHOLE:
                dx = Math.abs(node.getX() - endNode.getX());
                dy = Math.abs(node.getY() - endNode.getY());
                jumpNode.setHScore(options.heuristic().apply(dx, dy));
                break;
        }
    }

    private void setJumpNodeParent(Node node, Node jumpNode, int teleportType, Grid grid) {
        switch (teleportType) {
            case Grid.TELEPORT_NORMAL_NORMAL:
            case Grid.TELEPORT_NORMAL_TUNNEL:
            case Grid.TELEPORT_NORMAL_OT_TUNNEL:
            case Grid.TELEPORT_NORMAL_WORMHOLE:
            case Grid.TELEPORT_TUNNEL_TO_NORMAL:
            case Grid.TELEPORT_TUNNEL_TO_TUNNEL:
            case Grid.TELEPORT_TUNNEL_TO_WORMHOLE:
            case Grid.TELEPORT_WORMHOLE_TO_WORMHOLE:
                jumpNode.setParent(node);
                break;
            case Grid.TELEPORT_TUNNEL_NORMAL:
            case Grid.TELEPORT_TUNNEL_TUNNEL:
            case Grid.TELEPORT_TUNNEL_OT_TUNNEL:
            case Grid.TELEPORT_TUNNEL_WORMHOLE:
            case Grid.TELEPORT_WORMHOLE_NORMAL:
            case Grid.TELEPORT_WORMHOLE_OT_TUNNEL:
            case Grid.TELEPORT_WORMHOLE_TUNNEL:
            case Grid.TELEPORT_WORMHOLE_WORMHOLE:
                jumpNode.setParent(grid.getFinalEnd(node));
                break;
        }
    }

    /**
     * Identify successors for the given node. Runs a jump point search in the direction of each available neighbor,
     * adding any points found to the open list.
     *
     * @param node the node to check.
     */
    private void identifyTeleportSuccessors(Node node) {
        Node jumpNode;
        Point jumpPoint;
        double ng;
        int teleportType;

        List<Point> neighbors = findNeighbors(node);
        for (Point neighbor : neighbors) {
            jumpPoint = jump(neighbor.x, neighbor.y, node.getX(), node.getY());

            if (jumpPoint != null) {
                jumpNode = grid.getNodeAt(jumpPoint.x, jumpPoint.y);
                if (jumpNode.isClosed()) {
                    continue;
                }

                teleportType = grid.getTeleporterType(node, jumpNode);
                ng = getJumpNodeGScore(node, jumpNode, teleportType, grid);

                if (!jumpNode.isOpened() || ng < jumpNode.getGScore() || ng == node.getGScore()) {
                    jumpNode.setGScore(ng);
                    if (jumpNode.getHScore() == null) {
                        setJumpNodeHScore(node, jumpNode, teleportType, grid);
                    }

                    jumpNode.setFScore(jumpNode.getGScore() + jumpNode.getHScore());
                    setJumpNodeParent(node, jumpNode, teleportType, grid);

                    if (!jumpNode.isOpened()) {
                        openList.add(jumpNode);
                        jumpNode.setOpened(true);
                    } else {
                        // update the position of jump node
                        openList.remove(jumpNode);
                        openList.add(jumpNode);
                    }
                }
            }
        }
    }

    /**
     * Identify successors for the given node. Runs a jump point search in the direction of each available neighbor,
     * adding any points found to the open list.
     *
     * @param node the node to check.
     */
    private void identifySuccessors(Node node) {
        int endX = endNode.getX();
        int endY = endNode.getY();
        Node jumpNode;
        Point jumpPoint;
        double d, ng, h;
        int dx, dy;

        List<Point> neighbors = findNeighbors(node);
        for (Point neighbor : neighbors) {
            jumpPoint = jump(neighbor.x, neighbor.y, node.getX(), node.getY());
            if (jumpPoint != null) {
                jumpNode = grid.getNodeAt(jumpPoint.x, jumpPoint.y);
                if (jumpNode.isClosed()) {
                    continue;
                }

                // include distance, as parent may not be immediately adjacent:
                dx = Math.abs(jumpPoint.x - node.getX());
                dy = Math.abs(jumpPoint.y - node.getY());
                d = Heuristic.octile(dx, dy);
                ng = node.getGScore() + d; // next `g` value

                if (!jumpNode.isOpened() || ng < jumpNode.getGScore()) {
                    jumpNode.setGScore(ng);
                    if (jumpNode.getHScore() == null) {
                        dx = Math.abs(jumpPoint.x - endX);
                        dy = Math.abs(jumpPoint.y - endY);
                        h = options.heuristic().apply(dx, dy);
                        jumpNode.setHScore(h);
                    }
                    jumpNode.setFScore(jumpNode.getGScore() + jumpNode.getHScore());
                    jumpNode.setParent(node);

                    if (!jumpNode.isOpened()) {
                        openList.add(jumpNode);
                        jumpNode.setOpened(true);
                    } else {
                        // update the position of jump node
                        openList.remove(jumpNode);
                        openList.add(jumpNode);
                    }
                }
            }
        }
    }

    /**
     * Find the neighbors for the given node. If the node has a parent, prune the neighbors based on the jump point
     * search algorithm, otherwise return all available neighbors.
     *
     * @param node the node to check.
     * @return the found neighbors or an empty list, must NOT be {@code null}.
     */
    protected abstract List<Point> findNeighbors(Node node);

    /**
     * Search recursively in the direction (parent -> child), stopping only when a jump point is found.
     *
     * @param x0 child x coordinate
     * @param y0 child y coordinate
     * @param x1 parent x coordinate
     * @param y1 parent y coordinate
     * @return The x, y coordinate of the jump point found, or {@code null} if not found.
     */
    protected abstract Point jump(int x0, int y0, int x1, int y1);
}
