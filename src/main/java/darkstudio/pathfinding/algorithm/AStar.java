/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.algorithm;

import darkstudio.pathfinding.model.Vertex;
import darkstudio.pathfinding.utility.Tools;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.PriorityQueue;

/**
 * Implementation of the A* algorithm with Java built in data structures.
 */
public class AStar {
    private PriorityQueue<Vertex> heap;
    private Vertex[][] map;
    private Vertex startPoint;
    private Vertex endPoint;
    private String directions;
    private Options heuristics;

    private boolean utilityMode = false;
    private ArrayDeque<Vertex> utilityStack;

    /**
     * Initializes the AStar so it is ready to run.
     *
     * @param map The vertex matrix representation of the map used by this routing algorithm
     * @param start The starting point of the route
     * @param end The end point of the route
     * @param heuristics desired heuristic
     * @param diagonalMove {@code true} to allow diagonal moving, {@code false} otherwise.
     */
    public AStar(Vertex[][] map, Point start, Point end, Options heuristics, boolean diagonalMove) {
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
     * Initializes the AStar in utility mode, which finds the closest walkable vertex with Dijkstra.
     * Uses utilityStack to store changed vertices so their state can be reset.
     *
     * @param map The vertex matrix representation of the map used by this routing algorithm
     * @param start The starting point of the route
     * @param end The end point of the route
     * @param heuristics desired heuristic
     * @param diagonalMove {@code true} to allow diagonal moving, {@code false} otherwise.
     * @param utilityMode {@code true} to set utility mode ON, {@code false} to set off.
     */
    public AStar(Vertex[][] map, Point start, Point end, Options heuristics, boolean diagonalMove, boolean utilityMode) {
        this(map, start, end, heuristics, diagonalMove);
        this.utilityMode = utilityMode;
        utilityStack = new ArrayDeque<>();
        utilityStack.push(this.startPoint);
        utilityStack.push(this.endPoint);
    }

    /**
     * Runs the AStar algorithm with the provided map, starting point and the goal.
     * Saves all information to the Vertex objects on the map.
     *
     * @return the best route as an ArrayList of vertices.
     */
    public ArrayDeque<Vertex> run() {
        // INIT
        startPoint.setDistance(0);
        heap.add(startPoint);
        startPoint.setOpened(true);
        Vertex vertex;
        ArrayDeque<Vertex> neighbors;

        // ALGO
        while (!heap.isEmpty()) {
            vertex = heap.poll();
            // vertex is closed when the algorithm has dealt with it
            vertex.setClosed(true);

            // utilitymode used by LosAlgoritmos.closestValidCoordinate
            if (utilityMode) {
                if (Tools.valid(vertex.getX(), vertex.getY(), map)) {
                    ArrayDeque<Vertex> s = new ArrayDeque<>();
                    s.push(vertex);
                    return s;
                }
            } else { // no utilitymode-> proceed normally
                // if v == target, stop algo, find the route from path matrix
                if (vertex.equals(endPoint)) {
                    return Tools.shortestPath(startPoint, endPoint);
                }
            }

            // for all neighbours
            neighbors = Tools.getNeighbors(map, vertex, directions);
            if (utilityMode) {
                neighbors = Tools.getAllNeighbors(map, vertex);
                while (!neighbors.isEmpty()) {
                    Vertex v = neighbors.poll();
                    this.utilityStack.push(v);
                }
                neighbors = Tools.getAllNeighbors(map, vertex);
            }

            while (!neighbors.isEmpty()) {
                Vertex neighbor = neighbors.poll();
                // no need to process a vertex that has already been dealt with
                if (neighbor.isClosed()) {
                    continue;
                }
                // distance == sqrt(2) if diagonal movement to neighbour, else 1
                double distance = vertex.getDistance() + ((neighbor.getX() - vertex.getX() == 0 || neighbor.getY() - vertex.getY() == 0) ? 1 : Math.sqrt(2));

                //relax IF vertex is not opened (not placed to heap yet) OR shorter distance to it has been found
                if (!neighbor.isOpened() || neighbor.getDistance() > distance) {
                    neighbor.setDistance(distance);
                    //use appropriate heuristic if necessary, -1 is the default value of distance to goal
                    if (neighbor.getToGoal() == -1) {
                        neighbor.setToGoal(Tools.heuristics(neighbor.getX(), neighbor.getY(), this.heuristics, endPoint));
                    }

                    neighbor.setPath(vertex);

                    // if vertex was not yet opened, open it and place to heap. Else update its position in heap.
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
     * UtilityStack contains all vertices that were processed during utilityMode.
     * It is sufficient to reset only these vertices.
     *
     * @return utilityStack.
     */
    public ArrayDeque<Vertex> getUtilityStack() {
        return utilityStack;
    }
}
