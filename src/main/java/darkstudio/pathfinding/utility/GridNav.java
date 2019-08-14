/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.utility;

import darkstudio.pathfinding.algorithm.AStar;
import darkstudio.pathfinding.algorithm.JPS;
import darkstudio.pathfinding.algorithm.Options;
import darkstudio.pathfinding.model.Node;

import java.awt.Point;
import java.io.File;
import java.util.ArrayDeque;

/**
 * Functionality for managing the different routing algorithms.
 * The routing algorithms save all necessary information to the nodes on the VertexMatrix (the map)
 */
public class GridNav {
    private Node[][] map;
    private Point startPoint;
    private Point endPoint;

    public GridNav() {
    }

    /**
     * Set ups the resources to perform path finding.
     *
     * @param map node matrix
     */
    public GridNav(Node[][] map) {
        this.map = map;
    }

    /**
     * Routing with specified settings, returns the shortest route.
     *
     * @param start start point of the route.
     * @param end end point of the route.
     * @param algorithm chosen algorithm, algorithms provided in GridNav as static final integers.
     * @param heuristic chosen heuristic, heuristics provided in GridNav as static final integers.
     * @param diagonalMove {@code true} -> diagonal movement allowed. {@code false} otherwise.
     * @return null if no map loaded or invalid coordinates
     */
    public ArrayDeque<Node> route(Point start, Point end, Options algorithm, Options heuristic, boolean diagonalMove) {
        if (map == null || !Tools.valid(start.x, start.y, map) || !Tools.valid(end.x, end.y, map)) {
            return null;
        }

        //set default heuristic if none was chosen for non-DIJKSTRA algorithm
        if (algorithm != Options.DIJKSTRA && heuristic == Options.NO_HEURISTIC) {
            heuristic = Options.MANHATTAN_HEURISTIC;
        }

        startPoint = start;
        endPoint = end;

        //select correct algorithm & settings
        if (algorithm == Options.DIJKSTRA) {
            return new AStar(map, start, end, Options.NO_HEURISTIC, diagonalMove).run();
        }
        if (algorithm == Options.ASTAR) {
            return new AStar(map, start, end, heuristic, diagonalMove).run();
        }
        if (algorithm == Options.JPS) {
            return new JPS(map, start, end, heuristic, diagonalMove).run();
        }
        return null;
    }

    /**
     * Return the distance of the shortest route found by the last run algorithm.
     *
     * @return distance from start to goal.
     */
    public double getDistance() {
        return map[endPoint.y][endPoint.x].getDistance();
    }

    /**
     * Load .map file to desired map to be used by the routing algorithms.
     *
     * @param file the .map file
     * @return {@code true} if load successfully, {@code false} otherwise.
     */
    public boolean loadMap(File file) {
        map = Tools.loadMap(file);
        return map != null;
    }

    /**
     * Return the number of comparisons done by the routing algorithm that was last run.
     *
     * @return comparisons.
     */
    public int comparisons() {
        int comps = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x].getDistance() != -1) {
                    comps++;
                }
            }
        }
        return comps;
    }

    /**
     * Get currently loaded node matrix (map).
     *
     * @return node matrix.
     */
    public Node[][] getMap() {
        return map;
    }

    /**
     * @return a random validate coordinate on the map. {@code null} if no charMatrix is loaded.
     */
    public Point getRandomCoordinate() {
        return map != null ? Tools.randomPoint(map) : null;
    }

    /**
     * @return the same coordinate if already valid, otherwise finds the closes valid. {@code null} if no charMatrix is loaded.
     */
    public Point getClosestValidCoordinate(Point coord) {
        return map != null ? Tools.closestValidCoordinate(map, coord) : null;
    }

    public void clearMap() {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                map[y][x] = new Node(x, y, map[y][x].getKey());
            }
        }
    }
}
