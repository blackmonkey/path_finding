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

        // while the open list is not empty
        while (!openList.isEmpty()) {
            // pop the position of node which has the minimum `f` value.
            node = openList.poll();
            node.setClosed(true);

            if (node == endNode) {
                return Util.expandPath(Util.backtrace(endNode), grid, options.checkTeleporter());
            }

            identifySuccessors(node);
        }

        // fail to find the path
        return Collections.emptyList();
    }

    /**
     * Identify successors for the given node. Runs a jump point search in the direction of each available neighbor,
     * adding any points found to the open list.
     *
     * @param node the node to check.
     */
    protected void identifySuccessors(Node node) {
        int endX = endNode.getX();
        int endY = endNode.getY();
        Node jumpNode, finalEndNode;
        Point jumpPoint;
        double d, ng, h, exitH;
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
                if (options.checkTeleporter() && grid.hasTeleporter(node.getX(), node.getY(), jumpPoint.x, jumpPoint.y)) {
                    d = 0; // distance between start/end point of teleporter is 0
                } else {
                    dx = Math.abs(jumpPoint.x - node.getX());
                    dy = Math.abs(jumpPoint.y - node.getY());
                    d = Heuristic.octile(dx, dy);
                }

                ng = node.getGScore() + d; // next `g` value

                if (!jumpNode.isOpened() || ng < jumpNode.getGScore() || ng == node.getGScore()) {
                    jumpNode.setGScore(ng);
                    if (jumpNode.getHScore() == null) {
                        dx = Math.abs(jumpPoint.x - endX);
                        dy = Math.abs(jumpPoint.y - endY);
                        h = options.heuristic().apply(dx, dy);
                        if (options.checkTeleporter() && (finalEndNode = grid.getFinalEnd(jumpNode)) != null) {
                            if (finalEndNode.getHScore() == null) {
                                dx = Math.abs(finalEndNode.getX() - endX);
                                dy = Math.abs(finalEndNode.getY() - endY);
                                finalEndNode.setHScore(options.heuristic().apply(dx, dy));
                            }
                            exitH = finalEndNode.getHScore();
                            jumpNode.setHScore(Math.min(h, exitH));
                        } else {
                            jumpNode.setHScore(h);
                        }
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
