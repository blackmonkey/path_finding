/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding;

import darkstudio.pathfinding.algorithm.Options;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Heavy testing for all routing algorithms.
 * Main idea: results of all three algorithms should be the same, always.
 * Warning: important to reload the map after every search, as the algorithms change the state of the VertexMatrix (the map)
 */
public class ExtensiveRoutingTest {
    private static final double EPSILON = 0.5;

//    private GridNav gridNav;
//
//    @Before
//    public void setUp() {
//        gridNav = new GridNav();
//        assertTrue(gridNav.loadMap(new File(getClass().getClassLoader().getResource("combat.map").getFile())));
//    }
//
//
//    /**
//     * Compares the results of all routing algorithms with diagonal allowed.
//     * Uses EPSILON in comparing the double values.
//     * Manhattan is not tested as it is not consistent when diagonal is allowed.
//     */
//    @Test
//    public void testDiagonalRouting() {
//        testDiagonalRouting(Options.DIAGONAL_HEURISTIC);
//        testDiagonalRouting(Options.EUCLIDEAN_HEURISTIC);
//    }
//
//    private void testDiagonalRouting(Options heuristic) {
//        for (int i = 0; i < 100; i++) {
//            // two random valid points on the map
//            Point pt1 = gridNav.getRandomCoordinate();
//            Point pt2 = gridNav.getRandomCoordinate();
//
//            gridNav.clearMap();
//            gridNav.route(pt1, pt2, Options.DIJKSTRA, Options.NO_HEURISTIC, true);
//            double dijkstraDist = gridNav.getDistance();
//
//            gridNav.clearMap();
//            gridNav.route(pt1, pt2, Options.ASTAR, heuristic, true);
//            double astarDist = gridNav.getDistance();
//
//            gridNav.clearMap();
//            gridNav.route(pt1, pt2, Options.JPS, heuristic, true);
//            double jpsDist = gridNav.getDistance();
//
//            assertEquals(dijkstraDist, astarDist, EPSILON);
//            assertEquals(dijkstraDist, jpsDist, EPSILON);
//        }
//    }
//
//    /**
//     * Dijkstra and Astar should have same MANHATTAN scores when diagonal not allowed.
//     * JPS is not tested as it always uses 8 directions, MANHATTAN is consistent with only 4.
//     */
//    @Test
//    public void testManhattan() {
//        for (int i = 0; i < 100; i++) {
//            // two random valid points on the map
//            Point pt1 = gridNav.getRandomCoordinate();
//            Point pt2 = gridNav.getRandomCoordinate();
//
//            gridNav.clearMap();
//            gridNav.route(pt1, pt2, Options.DIJKSTRA, Options.NO_HEURISTIC, false);
//            double dijkstra = gridNav.getDistance();
//
//            gridNav.clearMap();
//            gridNav.route(pt1, pt2, Options.ASTAR, Options.MANHATTAN_HEURISTIC, false);
//            double astar = gridNav.getDistance();
//
//            assertEquals(dijkstra, astar, EPSILON);
//        }
//    }
}
