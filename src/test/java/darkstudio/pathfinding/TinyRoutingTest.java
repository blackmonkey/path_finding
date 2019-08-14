/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding;

import darkstudio.pathfinding.algorithm.Options;
import darkstudio.pathfinding.model.Vertex;
import darkstudio.pathfinding.utility.GridNav;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tiny routing tests.
 */
public class TinyRoutingTest {
    private static final double EPSILON = 0.005;

    private GridNav gridNav;
    private Point pt1;
    private Point pt2;
    private ArrayDeque<Vertex> bestRoute;

    @Before
    public void setUp() {
        gridNav = new GridNav();
        assertTrue(gridNav.loadMap(new File(getClass().getClassLoader().getResource("test4.map").getFile())));

        pt1 = new Point(0, 0);
        pt2 = new Point(2, 2);
    }

    /**
     * Route should find correct vertices.
     */
    @Test
    public void testFindRoute() {
        List<Point> route1 = Arrays.asList(new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2));
        List<Point> route2 = Arrays.asList(new Point(0, 0), new Point(2, 2));

        gridNav.clearMap();
        bestRoute = gridNav.route(pt1, pt2, Options.ASTAR, Options.MANHATTAN_HEURISTIC, false);
        assertEquals(5, bestRoute.size());
        testRoute(route1);

        gridNav.clearMap();
        bestRoute = gridNav.route(pt1, pt2, Options.DIJKSTRA, Options.MANHATTAN_HEURISTIC, false);
        assertEquals(5, bestRoute.size());
        testRoute(route1);

        gridNav.clearMap();
        bestRoute = gridNav.route(pt1, pt2, Options.JPS, Options.EUCLIDEAN_HEURISTIC, true);
        assertEquals(2, bestRoute.size());
        testRoute(route2);

    }

    private void testRoute(List<Point> route) {
        for (Point pt : route) {
            Vertex v = bestRoute.pop();
            assertEquals(pt.x, v.getX());
            assertEquals(pt.y, v.getY());
        }
    }

    /**
     * Route should set vertex distances correctly.
     */
    @Test
    public void testSetDistance() {
        double[] distances1 = {0, 1, 2, 3, 4};
        double[] distances2 = {0, 2 * Math.sqrt(2)}; // two times diagonal == 2*sqrt(2)

        gridNav.clearMap();
        bestRoute = gridNav.route(pt1, pt2, Options.ASTAR, Options.MANHATTAN_HEURISTIC, false);
        assertEquals(5, bestRoute.size());
        testDistances(distances1);

        gridNav.clearMap();
        bestRoute = gridNav.route(pt1, pt2, Options.DIJKSTRA, Options.MANHATTAN_HEURISTIC, false);
        assertEquals(5, bestRoute.size());
        testDistances(distances1);

        gridNav.clearMap();
        bestRoute = gridNav.route(pt1, pt2, Options.JPS, Options.EUCLIDEAN_HEURISTIC, true);
        assertEquals(2, bestRoute.size());
        testDistances(distances2);
    }

    private void testDistances(double[] distances) {
        for (double d : distances) {
            Vertex v = bestRoute.pop();
            assertEquals(d, v.getDistance(), EPSILON);
        }
    }
}