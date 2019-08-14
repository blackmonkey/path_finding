/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding;

import darkstudio.pathfinding.model.Node;
import darkstudio.pathfinding.utility.VertexMinHeap;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for VertexMinHeap.
 */
public class NodeMinHeapTest {
    private VertexMinHeap heap;

    @Before
    public void setUp() {
        heap = new VertexMinHeap(10);
        Random r = new Random();

        for (int i = 0; i < 100; i++) {
            Node v = new Node(0, i, Node.PASSABLE);
            v.setDistance(r.nextDouble() + 1);
            heap.add(v);
        }

        Node b = new Node(0, -1, Node.PASSABLE);
        b.setDistance(0);
        heap.add(b);

        assertEquals(101, heap.size());
    }

    /**
     * Heap should have correct size, that should decrease when polled.
     */
    @Test
    public void testSize() {
        assertEquals(0, heap.poll().getDistance(), 0.002);
        assertEquals(100, heap.size());
    }

    /**
     * Heap should have every item in correct order.
     */
    @Test
    public void testOrder() {
        Node[] arr = heap.getHeap();

        for (int i = 1; i < 51; i++) {
            assertTrue(arr[i].compareTo(arr[2 * i]) < 0);
            assertTrue(arr[i].compareTo(arr[2 * i + 1]) < 0);
        }
    }

    /**
     * Update should should preserve order and update the index of the node.
     */
    @Test
    public void testUpdate() {
        heap = new VertexMinHeap(10);
        Node a = new Node(0, 0, Node.PASSABLE);
        Node b = new Node(0, 0, Node.PASSABLE);
        Node c = new Node(0, 0, Node.PASSABLE);
        Node d = new Node(0, 0, Node.PASSABLE);
        a.setDistance(1);
        b.setDistance(3);
        c.setDistance(5);
        d.setDistance(7);
        heap.add(a);
        heap.add(b);
        heap.add(c);
        heap.add(d);

        assertEquals(a, heap.poll());
        a.setDistance(2);
        heap.add(a);
        d.setDistance(1);
        heap.update(d);
        assertEquals(d, heap.poll());
    }
}