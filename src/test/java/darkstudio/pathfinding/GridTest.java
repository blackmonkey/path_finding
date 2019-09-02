/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding;

import darkstudio.pathfinding.model.Grid;
import darkstudio.pathfinding.model.Node;
import darkstudio.pathfinding.model.TunnelNode;
import darkstudio.pathfinding.model.WormholeNode;
import javafx.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GridTest {
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 10;

    private List<TunnelNode> given1TunnelNodes(Grid grid) {
        TunnelNode tn1 = new TunnelNode(1, 1);
        TunnelNode tn2 = new TunnelNode(1, 2);
        TunnelNode tn3 = new TunnelNode(1, 3);

        grid.replaceNode(1, 1, tn1);
        grid.replaceNode(1, 2, tn2);
        grid.replaceNode(1, 3, tn3);

        tn1.setOut(tn2);
        tn2.addIn(tn1);
        tn2.setOut(tn3);
        tn3.addIn(tn2);
        tn3.setOut(grid.getNodeAt(1, 4));

        List<TunnelNode> nodes = new ArrayList<>();
        nodes.add(tn1);
        nodes.add(tn2);
        nodes.add(tn3);
        return nodes;
    }

    private List<TunnelNode> given2SeparatedTunnelNodes(Grid grid) {
        TunnelNode tn11 = new TunnelNode(1, 1);
        TunnelNode tn12 = new TunnelNode(1, 2);
        TunnelNode tn13 = new TunnelNode(1, 3);
        TunnelNode tn21 = new TunnelNode(2, 1);
        TunnelNode tn22 = new TunnelNode(2, 2);
        TunnelNode tn23 = new TunnelNode(2, 3);

        grid.replaceNode(1, 1, tn11);
        grid.replaceNode(1, 2, tn12);
        grid.replaceNode(1, 3, tn13);
        grid.replaceNode(2, 1, tn21);
        grid.replaceNode(2, 2, tn22);
        grid.replaceNode(2, 3, tn23);

        tn11.setOut(tn12);
        tn12.addIn(tn11);
        tn12.setOut(tn13);
        tn13.addIn(tn12);
        tn13.setOut(grid.getNodeAt(1, 4));

        tn21.setOut(tn22);
        tn22.addIn(tn21);
        tn22.setOut(tn23);
        tn23.addIn(tn22);
        tn23.setOut(grid.getNodeAt(2, 4));

        List<TunnelNode> nodes = new ArrayList<>();
        nodes.add(tn11);
        nodes.add(tn12);
        nodes.add(tn13);
        nodes.add(tn21);
        nodes.add(tn22);
        nodes.add(tn23);
        return nodes;
    }

    private List<TunnelNode> given2MergedTunnelNodes(Grid grid) {
        TunnelNode tn11 = new TunnelNode(1, 1);
        TunnelNode tn21 = new TunnelNode(2, 1);
        TunnelNode tn31 = new TunnelNode(3, 1);
        TunnelNode tn41 = new TunnelNode(4, 1);
        TunnelNode tn51 = new TunnelNode(5, 1);
        TunnelNode tn32 = new TunnelNode(3, 2);

        grid.replaceNode(1, 1, tn11);
        grid.replaceNode(2, 1, tn21);
        grid.replaceNode(3, 1, tn31);
        grid.replaceNode(4, 1, tn41);
        grid.replaceNode(5, 1, tn51);
        grid.replaceNode(3, 2, tn32);

        tn11.setOut(tn21);
        tn21.addIn(tn11);
        tn21.setOut(tn31);
        tn31.addIn(tn21);
        tn31.setOut(tn32);
        tn32.addIn(tn31);
        tn32.setOut(grid.getNodeAt(3, 3));
        tn41.setOut(tn31);
        tn31.addIn(tn41);
        tn51.setOut(tn41);
        tn41.addIn(tn51);

        List<TunnelNode> nodes = new ArrayList<>();
        nodes.add(tn11);
        nodes.add(tn21);
        nodes.add(tn31);
        nodes.add(tn41);
        nodes.add(tn51);
        nodes.add(tn32);
        return nodes;
    }

    private Pair<List<TunnelNode>, WormholeNode> givenTunnelToWormholeNodes(Grid grid) {
        TunnelNode tn1 = new TunnelNode(1, 1);
        TunnelNode tn2 = new TunnelNode(1, 2);
        WormholeNode wn1 = new WormholeNode(1, 3);
        WormholeNode wn2 = new WormholeNode(2, 5);

        grid.replaceNode(1, 1, tn1);
        grid.replaceNode(1, 2, tn2);
        grid.replaceNode(1, 3, wn1);
        grid.replaceNode(2, 5, wn2);

        tn1.setOut(tn2);
        tn2.addIn(tn1);
        tn2.setOut(wn1);
        wn1.setPeer(wn2);
        wn2.setPeer(wn1);

        List<TunnelNode> nodes = new ArrayList<>();
        nodes.add(tn1);
        nodes.add(tn2);
        return new Pair<>(nodes, wn1);
    }

    private void then1TunnelShouldBeOk(List<TunnelNode> nodes, List<List<Node>> tunnels) {
        assertEquals(1, tunnels.size());

        List<Node> tunnel = tunnels.get(0);
        assertEquals(nodes.size() + 1, tunnel.size());
        for (int i = 0; i < nodes.size(); i++) {
            assertEquals(nodes.get(i), tunnel.get(i));
        }
        assertEquals(nodes.get(nodes.size() - 1).getOut(), tunnel.get(nodes.size()));
    }

    private void then2SeparatedTunnelShouldBeOk(List<TunnelNode> nodes, List<List<Node>> tunnels) {
        assertEquals(2, tunnels.size());

        List<Node> tunnel = tunnels.get(0);
        assertEquals(4, tunnel.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(nodes.get(i), tunnel.get(i));
        }
        assertEquals(nodes.get(2).getOut(), tunnel.get(3));

        tunnel = tunnels.get(1);
        assertEquals(4, tunnel.size());
        for (int i = 3; i < 6; i++) {
            assertEquals(nodes.get(i), tunnel.get(i - 3));
        }
        assertEquals(nodes.get(5).getOut(), tunnel.get(3));
    }

    private void then2MergedTunnelShouldBeOk(List<TunnelNode> nodes, List<List<Node>> tunnels) {
        assertEquals(2, tunnels.size());

        List<Node> tunnel = tunnels.get(0);
        assertEquals(5, tunnel.size());
        assertEquals(nodes.get(0), tunnel.get(0)); // (1, 1)
        assertEquals(nodes.get(1), tunnel.get(1)); // (2, 1)
        assertEquals(nodes.get(2), tunnel.get(2)); // (3, 1)
        assertEquals(nodes.get(5), tunnel.get(3)); // (3, 2)
        assertEquals(nodes.get(5).getOut(), tunnel.get(4)); // (3, 3)

        tunnel = tunnels.get(1);
        assertEquals(5, tunnel.size());
        assertEquals(nodes.get(4), tunnel.get(0)); // (5, 1)
        assertEquals(nodes.get(3), tunnel.get(1)); // (4, 1)
        assertEquals(nodes.get(2), tunnel.get(2)); // (3, 1)
        assertEquals(nodes.get(5), tunnel.get(3)); // (3, 2)
        assertEquals(nodes.get(5).getOut(), tunnel.get(4)); // (3, 3)
    }

    private void thenTunnelToWormholeShouldBeOk(List<TunnelNode> nodes, WormholeNode wormholeNode, List<List<Node>> tunnels) {
        assertEquals(1, tunnels.size());

        List<Node> tunnel = tunnels.get(0);
        assertEquals(4, tunnel.size());
        assertEquals(nodes.get(0), tunnel.get(0)); // (1, 1)
        assertEquals(nodes.get(1), tunnel.get(1)); // (1, 2)
        assertEquals(wormholeNode, tunnel.get(2)); // (1, 3)
        assertEquals(wormholeNode.getPeer(), tunnel.get(3)); // (2, 5)
    }

    @Test
    public void testSetup1Tunnel() {
        Grid grid = new Grid(GRID_WIDTH, GRID_HEIGHT);
        List<TunnelNode> nodes = given1TunnelNodes(grid);
        grid.setupTunnels(new ArrayList<>(nodes));
        then1TunnelShouldBeOk(nodes, grid.getTunnels());

        List<TunnelNode> massNodes = new ArrayList<>();
        massNodes.add(nodes.get(0));
        massNodes.add(nodes.get(2));
        massNodes.add(nodes.get(1));
        grid.setupTunnels(massNodes);
        assertTrue(massNodes.isEmpty());
        then1TunnelShouldBeOk(nodes, grid.getTunnels());

        massNodes.clear();
        massNodes.add(nodes.get(2));
        massNodes.add(nodes.get(1));
        massNodes.add(nodes.get(0));
        grid.setupTunnels(massNodes);
        assertTrue(massNodes.isEmpty());
        then1TunnelShouldBeOk(nodes, grid.getTunnels());
    }

    @Test
    public void testSetup2SeparatedTunnels() {
        Grid grid = new Grid(GRID_WIDTH, GRID_HEIGHT);
        List<TunnelNode> nodes = given2SeparatedTunnelNodes(grid);
        grid.setupTunnels(new ArrayList<>(nodes));
        then2SeparatedTunnelShouldBeOk(nodes, grid.getTunnels());

        List<TunnelNode> massNodes = new ArrayList<>();
        massNodes.add(nodes.get(0));
        massNodes.add(nodes.get(2));
        massNodes.add(nodes.get(1));
        massNodes.add(nodes.get(3));
        massNodes.add(nodes.get(5));
        massNodes.add(nodes.get(4));
        grid.setupTunnels(massNodes);
        assertTrue(massNodes.isEmpty());
        then2SeparatedTunnelShouldBeOk(nodes, grid.getTunnels());

        massNodes.clear();
        massNodes.add(nodes.get(2));
        massNodes.add(nodes.get(1));
        massNodes.add(nodes.get(0));
        massNodes.add(nodes.get(5));
        massNodes.add(nodes.get(4));
        massNodes.add(nodes.get(3));
        grid.setupTunnels(massNodes);
        assertTrue(massNodes.isEmpty());
        then2SeparatedTunnelShouldBeOk(nodes, grid.getTunnels());
    }

    @Test
    public void testSetup2MergedTunnels() {
        Grid grid = new Grid(GRID_WIDTH, GRID_HEIGHT);
        List<TunnelNode> nodes = given2MergedTunnelNodes(grid);
        grid.setupTunnels(new ArrayList<>(nodes));
        then2MergedTunnelShouldBeOk(nodes, grid.getTunnels());

        List<TunnelNode> massNodes = new ArrayList<>();
        massNodes.add(nodes.get(0));
        massNodes.add(nodes.get(2));
        massNodes.add(nodes.get(1));
        massNodes.add(nodes.get(3));
        massNodes.add(nodes.get(5));
        massNodes.add(nodes.get(4));
        grid.setupTunnels(massNodes);
        assertTrue(massNodes.isEmpty());
        then2MergedTunnelShouldBeOk(nodes, grid.getTunnels());

        massNodes.clear();
        massNodes.add(nodes.get(2));
        massNodes.add(nodes.get(1));
        massNodes.add(nodes.get(0));
        massNodes.add(nodes.get(5));
        massNodes.add(nodes.get(4));
        massNodes.add(nodes.get(3));
        grid.setupTunnels(massNodes);
        assertTrue(massNodes.isEmpty());
        then2MergedTunnelShouldBeOk(nodes, grid.getTunnels());
    }

    @Test
    public void testSetupTunnelToWormhole() {
        Grid grid = new Grid(GRID_WIDTH, GRID_HEIGHT);
        Pair<List<TunnelNode>, WormholeNode> nodes = givenTunnelToWormholeNodes(grid);
        List<TunnelNode> tunnelNodes = nodes.getKey();
        WormholeNode wormholeNode = nodes.getValue();
        grid.setupTunnels(new ArrayList<>(tunnelNodes));
        thenTunnelToWormholeShouldBeOk(tunnelNodes, wormholeNode, grid.getTunnels());
    }

    @Test
    public void testGetTeleportType() {
        Grid grid = new Grid(GRID_WIDTH, GRID_HEIGHT);

        /*
         * Prepare the following nodes:
         *     0   1   2   3
         * 0       T
         *         v
         * 1       T
         *         v
         * 2   N   N   W = W
         *
         * 3   W = W < T
         */
        TunnelNode t10 = new TunnelNode(1, 0);
        TunnelNode t11 = new TunnelNode(1, 1);
        TunnelNode t23 = new TunnelNode(2, 3);
        WormholeNode w22 = new WormholeNode(2, 2);
        WormholeNode w32 = new WormholeNode(3, 2);
        WormholeNode w03 = new WormholeNode(0, 3);
        WormholeNode w13 = new WormholeNode(1, 3);
        Node n02 = grid.getNodeAt(0, 2);
        Node n12 = grid.getNodeAt(1, 2);

        grid.replaceNode(1, 0, t10);
        grid.replaceNode(1, 1, t11);
        grid.replaceNode(2, 3, t23);
        grid.replaceNode(2, 2, w22);
        grid.replaceNode(3, 2, w32);
        grid.replaceNode(0, 3, w03);
        grid.replaceNode(1, 3, w13);

        t10.setOut(t11);
        t11.addIn(t10);
        t11.setOut(n12);

        t23.setOut(w13);

        w13.setPeer(w03);
        w03.setPeer(w13);
        w22.setPeer(w32);
        w32.setPeer(w22);

        List<TunnelNode> tunnelNodes = new ArrayList<>();
        tunnelNodes.add(t10);
        tunnelNodes.add(t11);
        tunnelNodes.add(t23);
        grid.setupTunnels(tunnelNodes);

        assertEquals(Grid.TELEPORT_NORMAL_NORMAL, grid.getTeleporterType(n02, n12));
        assertEquals(Grid.TELEPORT_NORMAL_TUNNEL, grid.getTeleporterType(n02, t10));
        assertEquals(Grid.TELEPORT_NORMAL_OT_TUNNEL, grid.getTeleporterType(n12, t10));
        assertEquals(Grid.TELEPORT_NORMAL_OT_TUNNEL, grid.getTeleporterType(n12, t11));
        assertEquals(Grid.TELEPORT_NORMAL_WORMHOLE, grid.getTeleporterType(n02, w03));
        assertEquals(Grid.TELEPORT_NORMAL_WORMHOLE, grid.getTeleporterType(n02, w13));
        assertEquals(Grid.TELEPORT_NORMAL_WORMHOLE, grid.getTeleporterType(n02, w32));
        assertEquals(Grid.TELEPORT_NORMAL_WORMHOLE, grid.getTeleporterType(n02, w22));
        assertEquals(Grid.TELEPORT_TUNNEL_NORMAL, grid.getTeleporterType(t10, n02));
        assertEquals(Grid.TELEPORT_TUNNEL_NORMAL, grid.getTeleporterType(t11, n02));
        assertEquals(Grid.TELEPORT_TUNNEL_NORMAL, grid.getTeleporterType(t23, n02));
        assertEquals(Grid.TELEPORT_TUNNEL_TO_NORMAL, grid.getTeleporterType(t10, n12));
        assertEquals(Grid.TELEPORT_TUNNEL_TO_NORMAL, grid.getTeleporterType(t11, n12));
        assertEquals(Grid.TELEPORT_TUNNEL_TUNNEL, grid.getTeleporterType(t10, t23));
        assertEquals(Grid.TELEPORT_TUNNEL_TUNNEL, grid.getTeleporterType(t11, t23));
        assertEquals(Grid.TELEPORT_TUNNEL_OT_TUNNEL, grid.getTeleporterType(t11, t10));
        assertEquals(Grid.TELEPORT_TUNNEL_TO_TUNNEL, grid.getTeleporterType(t10, t11));
        assertEquals(Grid.TELEPORT_TUNNEL_TO_WORMHOLE, grid.getTeleporterType(t23, w13));
        assertEquals(Grid.TELEPORT_TUNNEL_TO_WORMHOLE, grid.getTeleporterType(t23, w03));
        assertEquals(Grid.TELEPORT_TUNNEL_WORMHOLE, grid.getTeleporterType(t10, w22));
        assertEquals(Grid.TELEPORT_TUNNEL_WORMHOLE, grid.getTeleporterType(t11, w32));
        assertEquals(Grid.TELEPORT_WORMHOLE_NORMAL, grid.getTeleporterType(w03, n02));
        assertEquals(Grid.TELEPORT_WORMHOLE_NORMAL, grid.getTeleporterType(w13, n02));
        assertEquals(Grid.TELEPORT_WORMHOLE_NORMAL, grid.getTeleporterType(w32, n02));
        assertEquals(Grid.TELEPORT_WORMHOLE_NORMAL, grid.getTeleporterType(w22, n02));
        assertEquals(Grid.TELEPORT_WORMHOLE_OT_TUNNEL, grid.getTeleporterType(w13, t23));
        assertEquals(Grid.TELEPORT_WORMHOLE_OT_TUNNEL, grid.getTeleporterType(w03, t23));
        assertEquals(Grid.TELEPORT_WORMHOLE_TUNNEL, grid.getTeleporterType(w13, t10));
        assertEquals(Grid.TELEPORT_WORMHOLE_TUNNEL, grid.getTeleporterType(w03, t11));
        assertEquals(Grid.TELEPORT_WORMHOLE_WORMHOLE, grid.getTeleporterType(w03, w32));
        assertEquals(Grid.TELEPORT_WORMHOLE_TO_WORMHOLE, grid.getTeleporterType(w03, w13));
        assertEquals(Grid.TELEPORT_WORMHOLE_TO_WORMHOLE, grid.getTeleporterType(w13, w03));
    }
}
