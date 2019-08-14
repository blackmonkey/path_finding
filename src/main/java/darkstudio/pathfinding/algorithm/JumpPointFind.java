/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Shane
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.algorithm;

import darkstudio.pathfinding.model.Grid;
import darkstudio.pathfinding.model.Node;
import darkstudio.pathfinding.utility.BinaryHeap;
import darkstudio.pathfinding.utility.PriorityQueue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JumpPointFind {
    private static final List<Point> DELTA_DIAGONAL_XY = Arrays.asList(
            new Point(-1, -1), new Point(0, -1), new Point(1, -1),
            new Point(-1, 0), /*new Point(0, 0),*/ new Point(1, 0),
            new Point(-1, 1), new Point(0, 1), new Point(1, 1)
    );

    private static final List<Point> DELTA_ORTHOGONAL_XY = Arrays.asList(
            /*new Point(-1, -1),*/ new Point(0, -1), /*new Point(1, -1),*/
            new Point(-1, 0), /*new Point(0, 0),*/ new Point(1, 0),
            /*new Point(-1, 1),*/ new Point(0, 1)/*, new Point(1, 1)*/
    );

    private Grid grid;
    private Node endNode;
    private PriorityQueue openList;
    private ArrayList<Node> closedList;

    public JumpPointFind(Grid grid) {
        this.grid = grid;
    }

    /**
     * Calculates the given node's score using the Manhattan distance formula
     *
     * @param node The node to calculate
     */
    private void calculateNodeScore(Node node) {
        int manhattan = Math.abs(endNode.getX() - node.getX()) * 10 + Math.abs(endNode.getY() - node.getY()) * 10;
        node.setToGoal(manhattan);

        Node parent = (Node) node.getParent();
        node.setDistance(parent.getDistance() + calculateGScore(node, parent));
    }

    /**
     * Will calculate the gScore between two nodes
     *
     * @param newNode
     * @param oldNode
     * @return The gScore between the nodes
     */
    private int calculateGScore(Node newNode, Node oldNode) {
        int dx = newNode.getX() - oldNode.getX();
        int dy = newNode.getY() - oldNode.getY();

        if (dx == 0 || dy == 0) {
            // Horizontal or vertical
            return Math.abs(10 * Math.max(dx, dy));
        }
        // Diagonal
        return Math.abs(14 * Math.max(dx, dy));
    }

    /**
     * Finds the shortest path between two nodes using the Jump Point Search Algorithm
     *
     * @return A path of nodes
     */
    public ArrayList<Node> jumpPointSearch(Node startNode, Node endNode, boolean orthogonal) {
        closedList = new ArrayList<>();
        openList = new BinaryHeap(startNode);
        this.endNode = endNode;

        while (!openList.isEmpty()) {
            Node curNode = (Node) openList.pop(); // Get the next node with the lowest fScore
            if (curNode.equals(endNode)) { // If this node is the end node we are done
                return backTrace(curNode); // Return the current node to back trace through to find the path
            }
            identifySuccessors(curNode, orthogonal);   // Find the successors to this node (add them to the openList)
            closedList.add(curNode);       // Add the curnode to the closed list (as to not open it again)
        }

        return null;
    }

    /**
     * Finds the successors the the curNode and adds them to the openlist
     *
     * @param curNode The current node to search for
     * @param orthogonal {@code true} to only search orthogonal path, {@code false} to search diagonal as well.
     */
    private void identifySuccessors(Node curNode, boolean orthogonal) {
        List<Point> deltaXy = orthogonal ? DELTA_ORTHOGONAL_XY : DELTA_DIAGONAL_XY;
        deltaXy.forEach(point -> {
            int dx = point.x;
            int dy = point.y;
            // If the neighbor exists at this direction and is valid continue
            if (isValidNeighbor(curNode, grid.getNode(curNode.getX() + dx, curNode.getY() + dy))) {
                // Try to find a jump node (One that is further down the path and has a forced neighbor)
                Node jumpNode = jump(curNode, dx, dy);
                if (jumpNode != null && !openList.contains(jumpNode) && !closedList.contains(jumpNode)) {
                    // If we found one add it to the open list if its not already on it
                    jumpNode.setParent(curNode);  // Set its parent so we can find our way back later
                    calculateNodeScore(jumpNode); // Calculate its score to pull it from the open list later
                    openList.add(jumpNode);       // Add it to the open list for continuing the path
                }
            }
        });
    }

    /**
     * Finds the next node heading in a given direction that has a forced neighbor or is significant
     *
     * @param curNode The node we are starting at
     * @param dx The x direction we are heading
     * @param dy The y direction we are heading
     * @return The node that has a forced neighbor or is significant
     */
    private Node jump(Node curNode, int dx, int dy) {
        // The next nodes details
        int nextX = curNode.getX() + dx;
        int nextY = curNode.getY() + dy;
        Node nextNode = grid.getNode(nextX, nextY);

        // If the nextNode is null or impassable we are done here
        if (nextNode == null || !nextNode.isPassable()) {
            return null;
        }

        // If the nextNode is the endNode we have found our target so return it
        if (nextNode.equals(endNode)) {
            return nextNode;
        }

        if (dx != 0 && dy != 0) { // If we are going in a diagonal direction check for forced neighbors
            // If neighbors do exist and are forced (left and right are impassable)
            Node nbr1 = grid.getNode(nextX - dx, nextY);
            Node nbr2 = grid.getNode(nextX - dx, nextY + dy);
            if (nbr1 != null && !nbr1.isPassable() && nbr2 != null && nbr2.isPassable()) {
                return nextNode;
            }

            // If neighbors do exist and are forced (top and bottom impassable)
            nbr1 = grid.getNode(nextX, nextY - dy);
            nbr2 = grid.getNode(nextX + dx, nextY - dy);
            if (nbr1 != null && !nbr1.isPassable() && nbr2 != null && nbr2.isPassable()) {
                return nextNode;
            }

            if (jump(nextNode, dx, 0) != null || jump(nextNode, 0, dy) != null) {
                // Special Diagonal Case
                return nextNode;
            }
        } else if (dx != 0) { // We are going horizontal
            if (grid.isPassable(nextX + dx, nextY)
                    && !grid.isPassable(nextX, nextY + 1)
                    && grid.isPassable(nextX + dx, nextY + 1)) {
                return nextNode;
            }

            if (grid.isPassable(nextX + dx, nextY)
                    && !grid.isPassable(nextX, nextY - 1)
                    && grid.isPassable(nextX + dx, nextY - 1)) {
                return nextNode;
            }
        } else { // We are going vertical
            if (grid.isPassable(nextX, nextY + dy)
                    && !grid.isPassable(nextX + 1, nextY)
                    && grid.isPassable(nextX + 1, nextY + dy)) {
                return nextNode;
            }

            if (grid.isPassable(nextX, nextY + dy)
                    && !grid.isPassable(nextX - 1, nextY)
                    && grid.isPassable(nextX - 1, nextY + dy)) {
                return nextNode;
            }
        }
        return jump(nextNode, dx, dy); // No forced neighbors so we are continuing down the path
    }

    /**
     * Goes through each parent of each subsequent node and adds them to a list starting with the provided node
     *
     * @param node
     * @return The list of nodes that make up the path
     */
    private ArrayList<Node> backTrace(Node node) {
        ArrayList<Node> path = new ArrayList<>();
        Node parent = node;
        while (parent != null) {
            path.add(parent);
            parent = (Node) parent.getParent();
        }
        return path;
    }

    /**
     * Check if the neighbor of the given node is valid
     *
     * @param node The node to check
     * @param neighbor The neighbor of the node to check
     * @return {@code true} if neighbor is valid, {@code false} otherwise
     */
    private boolean isValidNeighbor(Node node, Node neighbor) {
        return neighbor != null && neighbor.isPassable() && !closedList.contains(neighbor) && !neighbor.equals(node);
    }
}
