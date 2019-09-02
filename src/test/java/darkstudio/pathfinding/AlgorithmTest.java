/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding;

import darkstudio.pathfinding.model.Node;
import darkstudio.pathfinding.model.TunnelNode;
import org.junit.Test;

import java.util.PriorityQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AlgorithmTest {
    @Test
    public void testPriorityQueue() {
        PriorityQueue<Node> queue = new PriorityQueue<>();

        TunnelNode tn1 = new TunnelNode(6, 8);
        tn1.setGScore(2);
        tn1.setHScore(1);
        tn1.setFScore(3);

        Node n2 = new Node(13, 10, true);
        n2.setGScore(7);
        n2.setHScore(0);
        n2.setFScore(7);

        TunnelNode tn3 = new TunnelNode(6, 13);
        tn3.setGScore(3);
        tn3.setHScore(1);
        tn3.setFScore(4);

        for (int i = 0; i < 100; i++) {
            queue.add(tn1);
            queue.add(n2);
            queue.add(tn3);

            assertEquals(tn1, queue.poll());
            assertEquals(tn3, queue.poll());
            assertEquals(n2, queue.poll());
            assertNull(queue.poll());
        }
    }
}
