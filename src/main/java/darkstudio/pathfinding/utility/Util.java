/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.utility;

import darkstudio.pathfinding.algorithm.DiagonalMovement;
import darkstudio.pathfinding.algorithm.JPFAlwaysMoveDiagonally;
import darkstudio.pathfinding.algorithm.JPFNeverMoveDiagonally;
import darkstudio.pathfinding.algorithm.JumpPointFinderBase;
import darkstudio.pathfinding.algorithm.Options;
import darkstudio.pathfinding.model.Grid;
import darkstudio.pathfinding.model.Node;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {
    /**
     * Backtrace according to the parent records and return the path including both start and end nodes.
     *
     * @param node the end node
     * @return the path
     */
    public static List<Point> backtrace(Node node) {
        List<Point> path = new ArrayList<>();
        path.add(new Point(node.getX(), node.getY()));
        while (node.getParent() != null) {
            node = node.getParent();
            path.add(new Point(node.getX(), node.getY()));
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Backtrace from start and end node, and return the path including both start and end nodes.
     *
     * @param nodeA the end node
     * @param nodeB the other end node
     * @return the path
     */
    public static List<Point> biBacktrace(Node nodeA, Node nodeB) {
        List<Point> pathA = backtrace(nodeA);
        List<Point> pathB = backtrace(nodeB);
        Collections.reverse(pathB);
        pathA.addAll(pathB);
        return pathA;
    }

    /**
     * Compute the length of the path.
     *
     * @param path the path
     * @return the length of the path
     */
    public static double pathLength(List<Point> path) {
        double sum = 0;
        Point a, b;
        for (int i = 1; i < path.size(); i++) {
            a = path.get(i - 1);
            b = path.get(i);
            sum += Math.hypot(a.x - b.x, a.y - b.y);
        }
        return sum;
    }

    /**
     * Given the start and end coordinates, return all the coordinates lying on the line formed by these coordinates,
     * based on Bresenham's algorithm.
     * http://en.wikipedia.org/wiki/Bresenham's_line_algorithm#Simplification
     *
     * @param x0 start x coordinate
     * @param y0 start y coordinate
     * @param x1 end x coordinate
     * @param y1 end y coordinate
     * @return the coordinates on the line
     */
    public static List<Point> interpolate(int x0, int y0, int x1, int y1) {
        List<Point> line = new ArrayList<>();
        int sx, sy, dx, dy, err, e2;

        dx = Math.abs(x1 - x0);
        dy = Math.abs(y1 - y0);

        sx = (x0 < x1) ? 1 : -1;
        sy = (y0 < y1) ? 1 : -1;

        err = dx - dy;

        while (true) {
            line.add(new Point(x0, y0));

            if (x0 == x1 && y0 == y1) {
                break;
            }

            e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }

        return line;
    }

    /**
     * Given a compressed path, return a new path that has all the segments in it interpolated.
     *
     * @param path the path
     * @param grid the map
     * @param checkTeleporter whether check teleporter nodes.
     * @return expanded path
     */
    public static List<Point> expandPath(List<Point> path, Grid grid, boolean checkTeleporter) {
        List<Point> expanded = new ArrayList<>();
        Point coord0;
        Point coord1;
        List<Point> interpolated;

        if (path.size() < 2) {
            return expanded;
        }

        for (int i = 0; i < path.size() - 1; i++) {
            coord0 = path.get(i);
            coord1 = path.get(i + 1);

            if (checkTeleporter && grid.hasTeleporter(coord0.x, coord0.y, coord1.x, coord1.y)) {
                expanded.add(coord0);
            } else {
                interpolated = interpolate(coord0.x, coord0.y, coord1.x, coord1.y);
                for (int j = 0; j < interpolated.size() - 1; j++) {
                    expanded.add(interpolated.get(j));
                }
            }
        }
        expanded.add(path.get(path.size() - 1));

        return expanded;
    }

    /**
     * Smoothen the give path. The original path will not be modified and a new path will be returned.
     *
     * @param grid
     * @param path The path
     * @return smoothed path
     */
    public static List<Point> smoothenPath(Grid grid, List<Point> path) {
        int x0 = path.get(0).x;                // path start x
        int y0 = path.get(0).y;                // path start y
        int x1 = path.get(path.size() - 1).x;  // path end x
        int y1 = path.get(path.size() - 1).y;  // path end y
        int sx, sy;                            // current start coordinate
        int ex, ey;                            // current end coordinate
        List<Point> newPath = new ArrayList<>();
        List<Point> line;
        Point coord, testCoord, lastValidCoord;
        boolean blocked;

        sx = x0;
        sy = y0;
        newPath.add(new Point(sx, sy));

        for (int i = 2; i < path.size(); i++) {
            coord = path.get(i);
            ex = coord.x;
            ey = coord.y;
            line = interpolate(sx, sy, ex, ey);

            blocked = false;
            for (int j = 1; j < line.size(); j++) {
                testCoord = line.get(j);

                if (!grid.isWalkableAt(testCoord.x, testCoord.y)) {
                    blocked = true;
                    break;
                }
            }
            if (blocked) {
                lastValidCoord = path.get(i - 1);
                newPath.add(lastValidCoord);
                sx = lastValidCoord.x;
                sy = lastValidCoord.y;
            }
        }
        newPath.add(new Point(x1, y1));

        return newPath;
    }

    /**
     * Compress a path, remove redundant nodes without altering the shape. The original path is not modified.
     *
     * @param path the path
     * @return the compressed path
     */
    public static List<Point> compressPath(List<Point> path) {
        // nothing to compress
        if (path.size() < 3) {
            return path;
        }

        List<Point> compressed = new ArrayList<>();
        int sx = path.get(0).x; // start x
        int sy = path.get(0).y; // start y
        int px = path.get(1).x; // second point x
        int py = path.get(1).y; // second point y
        double dx = px - sx; // direction between the two points
        double dy = py - sy; // direction between the two points
        int lx, ly;
        double ldx, ldy;
        double sq;

        // normalize the direction
        sq = Math.hypot(dx, dy);
        dx /= sq;
        dy /= sq;

        // start the new path
        compressed.add(new Point(sx, sy));

        for (int i = 2; i < path.size(); i++) {
            // store the last point
            lx = px;
            ly = py;

            // store the last direction
            ldx = dx;
            ldy = dy;

            // next point
            px = path.get(i).x;
            py = path.get(i).y;

            // next direction
            dx = px - lx;
            dy = py - ly;

            // normalize
            sq = Math.hypot(dx, dy);
            dx /= sq;
            dy /= sq;

            // if the direction has changed, store the point
            if (dx != ldx || dy != ldy) {
                compressed.add(new Point(lx, ly));
            }
        }

        // store the last point
        compressed.add(new Point(px, py));

        return compressed;
    }

    public static JumpPointFinderBase jumpPointFinder(DiagonalMovement diagonalMovement, Options options) {
        switch (diagonalMovement) {
            case Never:
                return new JPFNeverMoveDiagonally(options);
            default:
                return new JPFAlwaysMoveDiagonally(options);
        }
    }
}
