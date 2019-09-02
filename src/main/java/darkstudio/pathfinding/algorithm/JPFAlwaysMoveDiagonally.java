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
import java.util.Set;

public class JPFAlwaysMoveDiagonally extends JumpPointFinderBase {
    public JPFAlwaysMoveDiagonally(Options options) {
        super(options);
    }

    @Override
    protected List<Point> findNeighbors(Node node) {
        Node parent = node.getParent();
        int x = node.getX(), y = node.getY();
        int px, py, dx, dy;
        List<Point> neighbors = new ArrayList<>();

        // Directed pruning: can ignore most neighbors, unless forced.
        if (parent != null) {
            px = parent.getX();
            py = parent.getY();
            // get the normalized direction of travel
            dx = (x - px) / Math.max(Math.abs(x - px), 1);
            dy = (y - py) / Math.max(Math.abs(y - py), 1);

            // search diagonally
            if (dx != 0 && dy != 0) {
                if (grid.isWalkableAt(x, y + dy)) {
                    neighbors.add(new Point(x, y + dy));
                }
                if (grid.isWalkableAt(x + dx, y)) {
                    neighbors.add(new Point(x + dx, y));
                }
                if (grid.isWalkableAt(x + dx, y + dy)) {
                    neighbors.add(new Point(x + dx, y + dy));
                }
                if (!grid.isWalkableAt(x - dx, y)) {
                    neighbors.add(new Point(x - dx, y + dy));
                }
                if (!grid.isWalkableAt(x, y - dy)) {
                    neighbors.add(new Point(x + dx, y - dy));
                }
            } else if (dx == 0) { // search horizontally
                if (grid.isWalkableAt(x, y + dy)) {
                    neighbors.add(new Point(x, y + dy));
                }
                if (!grid.isWalkableAt(x + 1, y)) {
                    neighbors.add(new Point(x + 1, y + dy));
                }
                if (!grid.isWalkableAt(x - 1, y)) {
                    neighbors.add(new Point(x - 1, y + dy));
                }
            } else {  // search vertically
                if (grid.isWalkableAt(x + dx, y)) {
                    neighbors.add(new Point(x + dx, y));
                }
                if (!grid.isWalkableAt(x, y + 1)) {
                    neighbors.add(new Point(x + dx, y + 1));
                }
                if (!grid.isWalkableAt(x, y - 1)) {
                    neighbors.add(new Point(x + dx, y - 1));
                }
            }
        } else { // return all neighbors
            Set<Node> neighborNodes = grid.getNeighbors(node, DiagonalMovement.Always);
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

        // check for forced neighbors
        // along the diagonal
        if (dx != 0 && dy != 0) {
            if ((grid.isWalkableAt(x1, y0 + dy) && !grid.isWalkableAt(x1, y0)) ||
                    (grid.isWalkableAt(x0 + dx, y1) && !grid.isWalkableAt(x0, y1))) {
                return new Point(x0, y0);
            }
            // when moving diagonally, must check for vertical/horizontal jump points
            if (jump(x0 + dx, y0, x0, y0) != null || jump(x0, y0 + dy, x0, y0) != null) {
                return new Point(x0, y0);
            }
        } else if (dx != 0) { // horizontally
            if ((grid.isWalkableAt(x0 + dx, y0 + 1) && !grid.isWalkableAt(x0, y0 + 1)) ||
                    (grid.isWalkableAt(x0 + dx, y0 - 1) && !grid.isWalkableAt(x0, y0 - 1))) {
                return new Point(x0, y0);
            }
        } else { // vertically
            if ((grid.isWalkableAt(x0 + 1, y0 + dy) && !grid.isWalkableAt(x0 + 1, y0)) ||
                    (grid.isWalkableAt(x0 - 1, y0 + dy) && !grid.isWalkableAt(x0 - 1, y0))) {
                return new Point(x0, y0);
            }
        }

        return jump(x0 + dx, y0 + dy, x0, y0);
    }
}
