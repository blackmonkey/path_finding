/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding;

import darkstudio.pathfinding.model.Vertex;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Vertex class.
 */
public class VertexTest {
    private Vertex v;

    @Before
    public void setUp() {
        v = new Vertex(0, 0, '.');
    }

    /**
     * Constructor should set correct initial values.
     * Do not change initial values, search algorithms and ImageBuilder depend on them.
     */
    @Test
    public void testConstructor() {
        assertEquals(0, v.getX());
        assertEquals(0, v.getY());
        assertEquals('.', v.getKey());
        assertEquals(-1, v.getDistance(), 0.002);
        assertEquals(-1, v.getToGoal(), 0.002);
        assertFalse(v.isOnPath());
        assertNull(v.getPath());
    }

    /**
     * Equals should return true if coordinates match.
     */
    @Test
    public void testEquals() {
        Vertex comp = new Vertex(0, 0, '.');
        assertEquals(v, comp);
        comp.setX(1);
        assertNotEquals(v, comp);
    }

    /**
     * Compare to should order items based on whose distance is smaller.
     * Smaller distance -> smaller in natural ordering -> return -1 when smaller distance compared to larger.
     */
    @Test
    public void testCompareTo() {
        Vertex comp = new Vertex(0, 0, '.');

        v.setDistance(1);
        comp.setDistance(2);
        assertTrue(v.compareTo(comp) < 0);

        comp.setDistance(0);
        assertTrue(v.compareTo(comp) > 0);

        comp.setDistance(1);
        assertEquals(0, v.compareTo(comp));
    }

    /**
     * CompareTo must work correctly with astar manhattan when distance is Integer.MAX_VALUE.
     */
    @Test
    public void testCompareToInSpecialCases() {
        Vertex comp = new Vertex(0, 0, '.');

        v.setDistance(Integer.MAX_VALUE);
        comp.setDistance(Integer.MAX_VALUE);

        assertEquals(0, v.compareTo(comp));

        v.setToGoal(1);
        comp.setToGoal(2);
        assertTrue(v.compareTo(comp) < 0);

        comp.setToGoal(0);
        assertTrue(v.compareTo(comp) > 0);

        comp.setDistance(100);
        comp.setToGoal(0);
        assertTrue(v.compareTo(comp) > 0);
    }
}