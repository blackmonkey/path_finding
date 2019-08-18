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
import java.util.stream.Collectors;

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
        System.out.println("identifySuccessors() openList inited=" + openList);

        // while the open list is not empty
        while (!openList.isEmpty()) {
            // pop the position of node which has the minimum `f` value.
            node = openList.poll();
            node.setClosed(true);
            System.out.println("===== findPath() openList poll node=" + node);

            if (node == endNode) {
                System.out.println("findPath() expand path");
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
        Node jumpNode;
        Point jumpPoint;
        double d, ng, h, exitH;
        int dx, dy;

        System.out.println("----- identifySuccessors() node=" + node);

        List<Point> neighbors = findNeighbors(node);
        System.out.println("identifySuccessors() neighbors=" + neighbors.stream().map(pt -> grid.getNodeAt(pt.x, pt.y)).collect(Collectors.toList()));
        for (Point neighbor : neighbors) {
            System.out.println("identifySuccessors() check neighbor=" + grid.getNodeAt(neighbor.x, neighbor.y));
            jumpPoint = jump(neighbor.x, neighbor.y, node.getX(), node.getY());
            System.out.println("identifySuccessors() jumpPoint=" + jumpPoint);
            if (jumpPoint != null) {
                jumpNode = grid.getNodeAt(jumpPoint.x, jumpPoint.y);
                System.out.println("identifySuccessors() jumpNode=" + jumpNode);
                if (jumpNode.isClosed()) {
                    continue;
                }

                // include distance, as parent may not be immediately adjacent:
                if (options.checkTeleporter() && node.hasExit(jumpPoint.x, jumpPoint.y)) {
                    d = 0; // distance between start/end point of teleporter is 0
                    System.out.println("identifySuccessors() node=>jumpNode, d=0");
                } else {
                    dx = Math.abs(jumpPoint.x - node.getX());
                    dy = Math.abs(jumpPoint.y - node.getY());
                    d = Heuristic.octile(dx, dy);
                    System.out.println("identifySuccessors() node#jumpNode, d=" + d);
                }

                ng = node.getGScore() + d; // next `g` value
                System.out.println("identifySuccessors() ng=" + ng);

                if (!jumpNode.isOpened() || ng < jumpNode.getGScore() || ng == node.getGScore()) {
                    jumpNode.setGScore(ng);
                    if (jumpNode.getHScore() == null) {
                        dx = Math.abs(jumpPoint.x - endX);
                        dy = Math.abs(jumpPoint.y - endY);
                        h = options.heuristic().apply(dx, dy);
                        if (options.checkTeleporter() && jumpNode.getExit() != null) {
                            if (jumpNode.getExit().getHScore() == null) {
                                dx = Math.abs(jumpNode.getExit().getX() - endX);
                                dy = Math.abs(jumpNode.getExit().getY() - endY);
                                jumpNode.getExit().setHScore(options.heuristic().apply(dx, dy));
                            }
                            exitH = jumpNode.getExit().getHScore();
                            jumpNode.setHScore(Math.min(h, exitH));
                            System.out.println("identifySuccessors() jumpNode hScore=" + h + ", exit hScore=" + exitH);
                        } else {
                            jumpNode.setHScore(h);
                            System.out.println("identifySuccessors() jumpNode hScore=" + h);
                        }
                        System.out.println("identifySuccessors() jumpNode update hScore=" + jumpNode);
                    }
                    jumpNode.setFScore(jumpNode.getGScore() + jumpNode.getHScore());
                    jumpNode.setParent(node);
                    System.out.println("identifySuccessors() jumpNode update parent, fScore=" + jumpNode);

                    if (!jumpNode.isOpened()) {
                        openList.add(jumpNode);
                        jumpNode.setOpened(true);
                    } else {
                        // update the position of jump node
                        openList.remove(jumpNode);
                        openList.add(jumpNode);
                    }
                    System.out.println("identifySuccessors() openList updated=" + openList);
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
