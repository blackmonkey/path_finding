/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.algorithm;

import darkstudio.pathfinding.model.Node;
import darkstudio.pathfinding.utility.Tools;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.PriorityQueue;

/**
 * Implementation of the Jump Point Search algorithm with self-made data structures.
 */
public class JPS {
    private PriorityQueue<Node> heap;
    private Node[][] map;
    private Node startPoint;
    private Node endPoint;
    private String directions;
    private Options heuristics;

    /**
     * Initializes the Jump Point Search so it is ready to run.
     *
     * @param map The node matrix representation of the map used by this routing algorithm
     * @param start The starting point of the route
     * @param end The end point of the route
     * @param heuristics desired heuristic
     * @param diagonalMove {@code true} to allow diagonal moving, {@code false} otherwise.
     */
    public JPS(Node[][] map, Point start, Point end, Options heuristics, boolean diagonalMove) {
        this.map = map;
        this.heuristics = heuristics;
        directions = diagonalMove ? "12345678" : "1357";
        heap = new PriorityQueue<>(map.length * map[0].length);
        startPoint = map[start.y][start.x];
        endPoint = map[end.y][end.x];
        startPoint.setOnPath(true);
        endPoint.setOnPath(true);
    }

    public ArrayDeque<Node> run() {
        // INIT
        startPoint.setDistance(0);
        heap.add(startPoint);
        startPoint.setOpened(true);
        Node node;
        ArrayDeque<Node> neighbors;

        // ALGO
        while (!heap.isEmpty()) {
            node = heap.poll();
//            System.out.println(node);
            //node is closed when the algorithm has dealt with it
            node.setClosed(true);
            //if v == target, stop algo, find the route from path matrix
            if (node.equals(endPoint)) {
                return Tools.shortestPath(startPoint, endPoint);
            }

            // IDENTIFY SUCCESSORS:

            // for all neighbours
            neighbors = getNeighbors(node);
            while (!neighbors.isEmpty()) {
                Node neighbor = neighbors.poll();
                // find next jumpPoint
                int[] jumpCoord = jump(neighbor.getX(), neighbor.getY(), node.getX(), node.getY());

                if (jumpCoord != null) {
                    Node jumpPoint = map[jumpCoord[1]][jumpCoord[0]];

                    //no need to process a jumpPoint that has already been dealt with
                    if (jumpPoint.isClosed()) {
                        continue;
                    }

                    //distance == distance of parent and from parent to jumpPoint
                    double distance = Tools.heuristics(jumpPoint.getX(), jumpPoint.getY(), Options.DIAGONAL_HEURISTIC, node) + node.getDistance();

                    //relax IF node is not opened (not placed to heap yet) OR shorter distance to it has been found
                    if (!jumpPoint.isOpened() || jumpPoint.getDistance() > distance) {
                        jumpPoint.setDistance(distance);

                        //use appropriate heuristic if necessary (-1 is the default value of distance to goal, so heuristic not used if still -1)
                        if (jumpPoint.getToGoal() == -1) {
                            jumpPoint.setToGoal(Tools.heuristics(jumpPoint.getX(), jumpPoint.getY(), this.heuristics, endPoint));
                        }

                        jumpPoint.setParent(node);

                        //if node was not yet opened, open it and place to heap. Else update its position in heap.
                        if (!jumpPoint.isOpened()) {
                            heap.add(jumpPoint);
                            jumpPoint.setOpened(true);
                        } else {
                            heap.remove(jumpPoint);
                            heap.add(jumpPoint);
                        }
                    }
                }
            }
        }
        //no route found
        return null;
    }

    /**
     * Find next jump point
     *
     * @param y neighbor y.
     * @param x neighbor x.
     * @param py node (parent of neighbor) y.
     * @param px node (parent of neighbor) x.
     * @return jump point y, x in array
     */
    private int[] jump(int x, int y, int px, int py) {
        if (!Tools.valid(x, y, map)) {
            return null;
        }

        if (map[y][x].equals(endPoint)) {
            return new int[]{x, y};
        }

        int dx = x - px;
        int dy = y - py;

        // diagonal search
        if (dx != 0 && dy != 0) {
            if ((Tools.valid(x - dx, y + dy, map) && !Tools.valid(x - dx, y, map)) ||
                    (Tools.valid(x + dx, y - dy, map) && !Tools.valid(x, y - dy, map))) {
                return new int[]{x, y};
            }
        } else { //vertical search
            if (dx != 0) {
                // jumpnode if has forced neighbor
                if ((Tools.valid(x + dx, y + 1, map) && !Tools.valid(x, y + 1, map)) ||
                        (Tools.valid(x + dx, y - 1, map) && !Tools.valid(x, y - 1, map))) {
                    return new int[]{x, y};
                }
            } else { //horizontal search
                // jupmnode if has forced neighbor
                if ((Tools.valid(x + 1, y + dy, map) && !Tools.valid(x + 1, y, map)) ||
                        (Tools.valid(x - 1, y + dy, map) && !Tools.valid(x - 1, y, map))) {
                    return new int[]{x, y};
                }
            }
        }

        // when moving diagonally, must perform horizontal and vertical search
        if (dx != 0 && dy != 0) {
            if (jump(x + dx, y, x, y) != null) {
                return new int[]{x, y};
            }
            if (jump(x, y + dy, x, y) != null) {
                return new int[]{x, y};
            }
        }

        // diagonal search recursively
        if (Tools.valid(x + dx, y, map) || Tools.valid(x, y + dy, map)) {
            return jump(x + dx, y + dy, x, y);
        }
        return null;
    }

    /**
     * Get the neighbors of the node.
     * No parent (first node) -> return all neighbors, otherwise prune.
     *
     * @return neighbors in queue.
     */
    private ArrayDeque<Node> getNeighbors(Node u) {
        ArrayDeque<Node> ngbrs = new ArrayDeque<>();
        Node parent = u.getParent();

        if (parent != null) {
            // get direction of movement
            int dy = (u.getY() - parent.getY()) / Math.max(Math.abs(u.getY() - parent.getY()), 1);
            int dx = (u.getX() - parent.getX()) / Math.max(Math.abs(u.getX() - parent.getX()), 1);
            int y = u.getY();
            int x = u.getX();

            // helper booleans, optimization
            boolean validY = false;
            boolean validX = false;

            // CHECK NEIGHBORS

            // diagonally
            if (dx != 0 && dy != 0) {
                // natural neighbors
                if (Tools.valid(x, y + dy, map)) {
                    ngbrs.add(map[y + dy][x]);
                    validY = true;
                }
                if (Tools.valid(x + dx, y, map)) {
                    ngbrs.add(map[y][x + dx]);
                    validX = true;
                }
                if (validY || validX) {
                    if (Tools.valid(x + dx, y + dy, map)) { // caused nullpointer without check at one point, no harm in making sure...
                        ngbrs.add(map[y + dy][x + dx]);
                    }
                }

                // forced neighbors
                if (!Tools.valid(x - dx, y, map) && validY) {
                    ngbrs.add(map[y + dy][x - dx]);
                }
                if (!Tools.valid(x, y - dy, map) && validX) {
                    ngbrs.add(map[y - dy][x + dx]);
                }
            } else { // vertically
                if (dx == 0) {
                    if (Tools.valid(x, y + dy, map)) {
                        // natural neighbor
//                        if (Tools.valid(y + dy,x, map)) {
                        ngbrs.add(map[y + dy][x]);
//                        }
                        // forced neighbors
                        if (!Tools.valid(x + 1, y, map)) {
                            ngbrs.add(map[y + dy][x + 1]);
                        }
                        if (!Tools.valid(x - 1, y, map)) {
                            ngbrs.add(map[y + dy][x - 1]);
                        }
                    }
                } else { //horizontally
                    // natural neighbors
                    if (Tools.valid(x + dx, y, map)) {
//                        if (Tools.valid(y,x+dx, map)) {
                        ngbrs.add(map[y][x + dx]);
//                        }

                        // forced neighbors
                        if (!Tools.valid(x, y + 1, map)) {
                            ngbrs.add(map[y + 1][x + dx]);
                        }
                        if (!Tools.valid(x, y - 1, map)) {
                            ngbrs.add(map[y - 1][x + dx]);
                        }
                    }
                }
            }
        } else {
            // no pruning - get all ngbrs normally
            ngbrs = Tools.getNeighbors(map, u, directions);
        }
        return ngbrs;
    }
}
