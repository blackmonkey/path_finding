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
    private GridNode[][] nodes;
    private JumpPointFind pathFinder;

    /**
     * Set ups the resources to perform a JPS.
     *
     * @param nodes node matrix
     */
    public Grid(GridNode nodes[][]) {
        this.nodes = nodes;
        pathFinder = new JumpPointFind(this);
    }

    public ArrayList<GridNode> searchJPS(GridNode start, GridNode end) {
        return pathFinder.jumpPointSearch(start, end);
    }

    /**
     * Gets a node at the specified location in the grid
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return The {@link GridNode} at the specific coordinates
     */
    public GridNode getNode(int x, int y) {
        if (x >= nodes.length || x < 0 || y >= nodes[0].length || y < 0) {
            return null;
        }
        return nodes[x][y];
    }

    /**
     * Checks if a node is passable at the specified location in the grid
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return {@code true} if this node is passable, {@code false} otherwise.
     */
    public boolean isPassable(int x, int y) {
        GridNode node = getNode(x, y);
        return node != null && node.isPassable();
    }
}
