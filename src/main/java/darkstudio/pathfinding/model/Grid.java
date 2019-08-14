/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Shane
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

import darkstudio.pathfinding.algorithm.JumpPointFind;

import java.util.ArrayList;

public class Grid {
    private Node[][] map;
    private JumpPointFind pathFinder;

    /**
     * Set ups the resources to perform a JPS.
     *
     * @param map node matrix
     */
    public Grid(Node[][] map) {
        this.map = map;
        pathFinder = new JumpPointFind(this);
    }

    public ArrayList<Node> searchJPS(Node start, Node end) {
        return pathFinder.jumpPointSearch(start, end, true);
    }

    /**
     * Gets a node at the specified location in the grid
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return The {@link Node} at the specific coordinates
     */
    public Node getNode(int x, int y) {
        if (y >= map.length || y < 0 || x >= map[0].length || x < 0) {
            return null;
        }
        return map[y][x];
    }

    /**
     * Checks if a node is passable at the specified location in the grid
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return {@code true} if this node is passable, {@code false} otherwise.
     */
    public boolean isPassable(int x, int y) {
        Node node = getNode(x, y);
        return node != null && node.isPassable();
    }
}
