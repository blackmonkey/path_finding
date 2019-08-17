/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author imor / https://github.com/imor
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.algorithm;

import darkstudio.pathfinding.model.Node;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class JPFNeverMoveDiagonally extends JumpPointFinderBase {
    public JPFNeverMoveDiagonally(Options options) {
        super(options);
    }

    @Override
    protected List<Point> findNeighbors(Node node) {
        Node parent = node.getParent();
        int x = node.getX(), y = node.getY();
        int px, py, dx, dy;
        List<Point> neighbors = new ArrayList<>();

        // Directed pruning: can ignore most neighbors, unless forced.
        // If need to check teleporter nodes, we shouldn't prune direction.
        if (parent != null && !options.checkTeleporter()) {
            px = parent.getX();
            py = parent.getY();
            // get the normalized direction of travel
            dx = (x - px) / Math.max(Math.abs(x - px), 1);
            dy = (y - py) / Math.max(Math.abs(y - py), 1);

            if (dx != 0) {
                if (grid.isWalkableAt(x, y - 1)) {
                    neighbors.add(new Point(x, y - 1));
                }
                if (grid.isWalkableAt(x, y + 1)) {
                    neighbors.add(new Point(x, y + 1));
                }
                if (grid.isWalkableAt(x + dx, y)) {
                    neighbors.add(new Point(x + dx, y));
                }
            } else if (dy != 0) {
                if (grid.isWalkableAt(x - 1, y)) {
                    neighbors.add(new Point(x - 1, y));
                }
                if (grid.isWalkableAt(x + 1, y)) {
                    neighbors.add(new Point(x + 1, y));
                }
                if (grid.isWalkableAt(x, y + dy)) {
                    neighbors.add(new Point(x, y + dy));
                }
            }
        } else { // return all neighbors
            List<Node> neighborNodes = grid.getNeighbors(node, DiagonalMovement.Never, options.checkTeleporter());
            for (Node neighborNode : neighborNodes) {
                neighbors.add(new Point(neighborNode.getX(), neighborNode.getY()));
            }
        }

        return neighbors;
    }

    @Override
    protected Point jump(int x0, int y0, int x1, int y1) {
        int dx = x0 - x1, dy = y0 - y1;

        if (!grid.isWalkableAt(x0, y0)) {
            return null;
        }

        if (options.trackJumpRecursion()) {
            grid.getNodeAt(x0, y0).setTested(true);
        }

        if (grid.getNodeAt(x0, y0) == endNode) {
            return new Point(x0, y0);
        }

        if (options.checkTeleporter() && (grid.hasTeleporter(x1, y1, x0, y0) || grid.isTeleporterAt(x0, y0))) {
            return new Point(x0, y0);
        }

        if (dx != 0) { // moving horizontally
            if ((grid.isWalkableAt(x0, y0 - 1) && !grid.isWalkableAt(x1, y0 - 1)) ||
                    (grid.isWalkableAt(x0, y0 + 1) && !grid.isWalkableAt(x1, y0 + 1))) {
                return new Point(x0, y0);
            }
        } else if (dy != 0) { // moving vertically
            if ((grid.isWalkableAt(x0 - 1, y0) && !grid.isWalkableAt(x0 - 1, y1)) ||
                    (grid.isWalkableAt(x0 + 1, y0) && !grid.isWalkableAt(x0 + 1, y1))) {
                return new Point(x0, y0);
            }
            // when moving vertically, must check for horizontal jump points
            if (jump(x0 + 1, y0, x0, y0) != null || jump(x0 - 1, y0, x0, y0) != null) {
                return new Point(x0, y0);
            }
        } else {
            throw new RuntimeException("Only horizontal and vertical movements are allowed");
        }

        return jump(x0 + dx, y0 + dy, x0, y0);
    }
}
