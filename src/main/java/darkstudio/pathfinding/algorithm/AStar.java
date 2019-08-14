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
 * Implementation of the A* algorithm with Java built in data structures.
 */
public class AStar {
    private PriorityQueue<Node> heap;
    private Node[][] map;
    private Node startPoint;
    private Node endPoint;
    private String directions;
    private Options heuristics;

    private boolean utilityMode = false;
    private ArrayDeque<Node> utilityStack;

    /**
     * Initializes the AStar so it is ready to run.
     *
     * @param map The node matrix representation of the map used by this routing algorithm
     * @param start The starting point of the route
     * @param end The end point of the route
     * @param heuristics desired heuristic
     * @param diagonalMove {@code true} to allow diagonal moving, {@code false} otherwise.
     */
    public AStar(Node[][] map, Point start, Point end, Options heuristics, boolean diagonalMove) {
        this.map = map;
        this.heuristics = heuristics;
        directions = diagonalMove ? "12345678" : "1357";
        heap = new PriorityQueue<>(map.length * map[0].length);
        startPoint = map[start.y][start.x];
        endPoint = map[end.y][end.x];
        startPoint.setOnPath(true);
        endPoint.setOnPath(true);
    }

    /**
     * Initializes the AStar in utility mode, which finds the closest walkable node with Dijkstra.
     * Uses utilityStack to store changed nodes so their state can be reset.
     *
     * @param map The node matrix representation of the map used by this routing algorithm
     * @param start The starting point of the route
     * @param end The end point of the route
     * @param heuristics desired heuristic
     * @param diagonalMove {@code true} to allow diagonal moving, {@code false} otherwise.
     * @param utilityMode {@code true} to set utility mode ON, {@code false} to set off.
     */
    public AStar(Node[][] map, Point start, Point end, Options heuristics, boolean diagonalMove, boolean utilityMode) {
        this(map, start, end, heuristics, diagonalMove);
        this.utilityMode = utilityMode;
        utilityStack = new ArrayDeque<>();
        utilityStack.push(this.startPoint);
        utilityStack.push(this.endPoint);
    }

    /**
     * Runs the AStar algorithm with the provided map, starting point and the goal.
     * Saves all information to the Node objects on the map.
     *
     * @return the best route as an ArrayList of nodes.
     */
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
            // node is closed when the algorithm has dealt with it
            node.setClosed(true);

            // utilitymode used by LosAlgoritmos.closestValidCoordinate
            if (utilityMode) {
                if (Tools.valid(node.getX(), node.getY(), map)) {
                    ArrayDeque<Node> s = new ArrayDeque<>();
                    s.push(node);
                    return s;
                }
            } else { // no utilitymode-> proceed normally
                // if v == target, stop algo, find the route from path matrix
                if (node.equals(endPoint)) {
                    return Tools.shortestPath(startPoint, endPoint);
                }
            }

            // for all neighbours
            neighbors = Tools.getNeighbors(map, node, directions);
            if (utilityMode) {
                neighbors = Tools.getAllNeighbors(map, node);
                while (!neighbors.isEmpty()) {
                    Node v = neighbors.poll();
                    this.utilityStack.push(v);
                }
                neighbors = Tools.getAllNeighbors(map, node);
            }

            while (!neighbors.isEmpty()) {
                Node neighbor = neighbors.poll();
                // no need to process a node that has already been dealt with
                if (neighbor.isClosed()) {
                    continue;
                }
                // distance == sqrt(2) if diagonal movement to neighbour, else 1
                double distance = node.getDistance() + ((neighbor.getX() - node.getX() == 0 || neighbor.getY() - node.getY() == 0) ? 1 : Math.sqrt(2));

                //relax IF node is not opened (not placed to heap yet) OR shorter distance to it has been found
                if (!neighbor.isOpened() || neighbor.getDistance() > distance) {
                    neighbor.setDistance(distance);
                    //use appropriate heuristic if necessary, -1 is the default value of distance to goal
                    if (neighbor.getToGoal() == -1) {
                        neighbor.setToGoal(Tools.heuristics(neighbor.getX(), neighbor.getY(), this.heuristics, endPoint));
                    }

                    neighbor.setParent(node);

                    // if node was not yet opened, open it and place to heap. Else update its position in heap.
                    if (!neighbor.isOpened()) {
                        heap.add(neighbor);
                        neighbor.setOpened(true);
                    } else if (heap.remove(neighbor)) {
                        heap.add(neighbor);
                    }
                }
            }
        }
        // no route found
        return null;
    }

    /**
     * UtilityStack contains all nodes that were processed during utilityMode.
     * It is sufficient to reset only these nodes.
     *
     * @return utilityStack.
     */
    public ArrayDeque<Node> getUtilityStack() {
        return utilityStack;
    }
}
