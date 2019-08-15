/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

import com.sun.istack.internal.NotNull;
import darkstudio.pathfinding.algorithm.DiagonalMovement;

import java.util.ArrayList;
import java.util.List;

/**
 * The Grid class, which serves as the encapsulation of the layout of the nodes.
 */
public class Grid {
    private Node[][] nodes;

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
    public Grid(@NotNull int[][] matrix) {
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
     * @return the requested neighbors of the node.
     * @throws IllegalArgumentException if diagonalMovement is invalid.
     */
    public List<Node> getNeighbors(Node node, DiagonalMovement diagonalMovement) {
        int x = node.getX();
        int y = node.getY();
        List<Node> neighbors = new ArrayList<>();
        boolean s0 = false, d0 = false,
                s1 = false, d1 = false,
                s2 = false, d2 = false,
                s3 = false, d3 = false;

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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        int height = nodes.length;
        int width = nodes[0].length;
        int[][] matrix = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 0 will be walkable while others will be un-walkable
                matrix[y][x] = nodes[y][x].isWalkable() ? 0 : 1;
            }
        }
        return new Grid(matrix);
    }
}
