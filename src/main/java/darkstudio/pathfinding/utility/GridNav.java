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
import darkstudio.pathfinding.model.Vertex;

import java.awt.Point;
import java.io.File;
import java.util.ArrayDeque;

/**
 * Functionality for managing the different routing algorithms.
 * The routing algorithms save all necessary information to the vertices on the VertexMatrix (the map)
 */
public class GridNav {
    private Vertex[][] vertexMatrix;
    private boolean clearMap;
    private Point startPoint;
    private Point endPoint;

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
    public ArrayDeque<Vertex> route(Point start, Point end, Options algorithm, Options heuristic, boolean diagonalMove) {
        if (vertexMatrix == null || !Tools.valid(start.x, start.y, vertexMatrix) || !Tools.valid(end.x, end.y, vertexMatrix)) {
            return null;
        }

        //set default heuristic if none was chosen for non-DIJKSTRA algorithm
        if (algorithm != Options.DIJKSTRA && heuristic == Options.NO_HEURISTIC) {
            heuristic = Options.MANHATTAN_HEURISTIC;
        }

        startPoint = start;
        endPoint = end;

        //clear map only if it has been used in routing.
        if (clearMap) {
            clearMap();
        }
        clearMap = true;

        //select correct algorithm & settings
        if (algorithm == Options.DIJKSTRA) {
            return new AStar(vertexMatrix, start, end, Options.NO_HEURISTIC, diagonalMove).run();
        }
        if (algorithm == Options.ASTAR) {
            return new AStar(vertexMatrix, start, end, heuristic, diagonalMove).run();
        }
        if (algorithm == Options.JPS) {
            return new JPS(vertexMatrix, start, end, heuristic, diagonalMove).run();
        }
        return null;
    }

    /**
     * Return the distance of the shortest route found by the last run algorithm.
     *
     * @return distance from start to goal.
     */
    public double getDistance() {
        return vertexMatrix[endPoint.y][endPoint.x].getDistance();
    }

    /**
     * Load .map file to desired map to be used by the routing algorithms.
     *
     * @param file the .map file
     */
    public void loadCharMatrix(File file) {
        vertexMatrix = Tools.loadMap(file);
        clearMap = false; // no need to clear map in GridNav.route();
    }

    /**
     * Return the number of comparisons done by the routing algorithm that was last run.
     *
     * @return comparisons.
     */
    public int comparisons() {
        int comps = 0;
        for (int y = 0; y < vertexMatrix.length; y++) {
            for (int x = 0; x < vertexMatrix[0].length; x++) {
                if (vertexMatrix[y][x].getDistance() != -1) {
                    comps++;
                }
            }
        }
        return comps;
    }

    /**
     * Get currently loaded vertex matrix (map).
     *
     * @return vertexMatrix.
     */
    public Vertex[][] getVertexMatrix() {
        return vertexMatrix;
    }

    /**
     * @return a random validate coordinate on the map. {@code null} if no charMatrix is loaded.
     */
    public Point getRandomCoordinate() {
        return vertexMatrix != null ? Tools.randomPoint(vertexMatrix) : null;
    }

    /**
     * @return the same coordinate if already valid, otherwise finds the closes valid. {@code null} if no charMatrix is loaded.
     */
    public Point getClosestValidCoordinate(Point coord) {
        return vertexMatrix != null ? Tools.closestValidCoordinate(vertexMatrix, coord) : null;
    }

    private void clearMap() {
        for (int y = 0; y < vertexMatrix.length; y++) {
            for (int x = 0; x < vertexMatrix[0].length; x++) {
                vertexMatrix[y][x] = new Vertex(x, y, vertexMatrix[y][x].getKey());
            }
        }
    }
}
