/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.utility;

import darkstudio.pathfinding.algorithm.AStar;
import darkstudio.pathfinding.algorithm.Options;
import darkstudio.pathfinding.model.Node;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Gathered tools used by routing algorithms, tests and GUI.
 * With Java built in data structures.
 */
public class Tools {
    /**
     * Implementations of A* heuristics.
     * When Dijkstra (no heuristics) selected, returns -1
     *
     * @param x x coordinate of current node
     * @param y y coordinate of current node
     * @param heuristic selected heuristic
     * @return estimate distance to goal
     */
    public static double heuristics(int x, int y, Options heuristic, Node whereTo) {
        int dx = Math.abs(y - whereTo.getY());
        int dy = Math.abs(x - whereTo.getX());
        if (heuristic == Options.MANHATTAN_HEURISTIC) {
            return dy + dx;
        }
        if (heuristic == Options.DIAGONAL_HEURISTIC) {
            return (dx + dy) + (Math.sqrt(2) - 2) * Math.min(dx, dy);
        }
        if (heuristic == Options.DIAGONAL_EQUAL_COST_HEURISTIC) {
            return Math.max(dx, dy);
        }
        if (heuristic == Options.EUCLIDEAN_HEURISTIC) {
            return Math.hypot(dx, dy);
        }
        return -1;
    }

    /**
     * Finds the shortest path from Node[][] path.
     * Used by the run() method after finding the goal.
     *
     * @param start The node at the start.
     * @param goal The node at the goal (end point).
     * @return the best route as an ArrayList of nodes.
     */
    public static ArrayDeque<Node> shortestPath(Node start, Node goal) {
        ArrayDeque<Node> pino = new ArrayDeque<>();
        pino.push(goal);

        if (goal.equals(start)) {
            return pino;
        }

        Node u = goal.getParent();
        while (!u.equals(start)) {
            u.setOnPath(true);
            pino.push(u);
            u = u.getParent();
        }
        pino.push(u);
        start.setOnPath(true);
        return pino;
    }

    /**
     * Gets the walkable neighbors of a given node on the map.
     *
     * @param u the node whose neighbors are desired.
     * @return a list of the neighbors
     */
    public static ArrayDeque<Node> getNeighbors(Node[][] map, Node u, String directions) {
        ArrayDeque<Node> ngbrs = new ArrayDeque<>();
        for (char c : directions.toCharArray()) {
            Node v = getNeighbor(map, u, c);
            // if v valid (within the map)
            if (v != null && v.isPassable()) {
                ngbrs.add(v);
            }
        }
        return ngbrs;
    }

    /**
     * Gets the all, walkable or not, neighbors of a given node on the map.
     *
     * @param u the node whose neighbors are desired.
     * @return a list of the neighbors
     */
    public static ArrayDeque<Node> getAllNeighbors(Node[][] map, Node u) {
        ArrayDeque<Node> ngbrs = new ArrayDeque<>();
        for (char c : "12345678".toCharArray()) {
            Node v = getNeighbor(map, u, c);
            if (v != null) {
                ngbrs.add(v);
            }
        }
        return ngbrs;
    }

    /**
     * Gets the neighbor in the specified direction - left, up, right, down,
     *
     * @param u the node whose neighbor is desired
     * @param c the direction from which the neighbor is desired, format: L/U/R/D
     * @return null if the direction is out of map, otherwise the neighbor node.
     */
    public static Node getNeighbor(Node[][] map, Node u, char c) {
        int dy = 0;
        int dx = 0;
        if (c == '1') { // left
            --dx;
        } else if (c == '2') { // left & up
            --dx;
            --dy;
        } else if (c == '3') { // up
            --dy;
        } else if (c == '4') { // right & up
            --dy;
            ++dx;
        } else if (c == '5') { // right
            ++dx;
        } else if (c == '6') { // right & down
            ++dy;
            ++dx;
        } else if (c == '7') { // down
            ++dy;
        } else if (c == '8') { // left & down
            ++dy;
            --dx;
        }

        int x = u.getX() + dx;
        int y = u.getY() + dy;
        return valid(x, y, map) ? map[y][x] : null;
    }

    /**
     * @param x x coordinate.
     * @param y y coordinate.
     * @param map Node[][] map.
     * @return {@code true} if coordinate is walkable and within the map, {@code false} otherwise.
     */
    public static boolean valid(int x, int y, Node[][] map) {
        if (x < 0 || y < 0 || x >= map[0].length || y >= map.length) {
            return false;
        }
        return map[y][x].isPassable();
    }

    /**
     * Returns a random, valid, point on the map.
     *
     * @param map
     * @return
     */
    public static Point randomPoint(Node[][] map) {
        Random r = new Random();
        int x = r.nextInt(map[0].length);
        int y = r.nextInt(map.length);
        return closestValidCoordinate(map, new Point(x, y));
    }

    /**
     * Checks that coordinate is valid, if not, returns the closest valid coordinate.
     * Moves coordinate to within the map and uses Dijkstra(AStar, NO_HEURISTIC) with utilitymode.
     *
     * @param coord coordinate to be validated.
     * @return closest valid coordinate.
     */
    public static Point closestValidCoordinate(Node[][] nodes, Point coord) {
        if (nodes == null) {
            return null;
        }
        if (valid(coord.x, coord.y, nodes)) {
            return coord;
        }

        // move coordinate within the map, if outside
        if (coord.y < 0) {
            coord.y = 0;
        }
        if (coord.y >= nodes.length) {
            coord.y = nodes.length - 1;
        }
        if (coord.x < 0) {
            coord.x = 0;
        }
        if (coord.x >= nodes[0].length) {
            coord.x = nodes[0].length - 1;
        }

        // dijkstra
        AStar A = new AStar(nodes, coord, coord, Options.NO_HEURISTIC, true, true);
        ArrayDeque<Node> s = A.run();
        if (s != null) { // FIXME: how A.run() returns null?
            Node v = s.pop();
            coord.x = v.getX();
            coord.y = v.getY();
        }

        // undo any changes made by dijkstra
        ArrayDeque<Node> utilityStack = A.getUtilityStack();
        while (!utilityStack.isEmpty()) {
            Node node = utilityStack.pop();
            node.setOnPath(false);
            node.setClosed(false);
            node.setOpened(false);
            node.setDistance(-1);
            node.setToGoal(-1);
        }
        return coord;
    }

    /**
     * Loads a .map file to a char matrix and returns it.
     *
     * @param file the .map file
     * @return the provided .map as a char matrix if the file was loaded successfully, {@code null} otherwise.
     */
    public static Node[][] loadMap(File file) {
        if (file == null) {
            return null;
        }
        try {
            String heightLine;
            String widthLine;
            Scanner scanner = new Scanner(file);

            scanner.nextLine(); // skip typeline
            heightLine = scanner.nextLine();
            widthLine = scanner.nextLine();
            Node[][] nodes =
                    new Node[Integer.parseInt(heightLine.substring(7))]
                            [Integer.parseInt(widthLine.substring(6))];
            scanner.nextLine(); // skip mapline

            int y = 0;
            while (y < nodes.length) {
                char[] chars = scanner.nextLine().toCharArray();
                for (int x = 0; x < chars.length; x++) {
                    nodes[y][x] = new Node(x, y, chars[x]);
                }
                y++;
            }
            return nodes;
        } catch (FileNotFoundException | NoSuchElementException | IllegalStateException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }
}
