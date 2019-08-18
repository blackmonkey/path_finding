/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

public class TeleporterNode extends Node {

    /**
     * Create a teleporter node
     *
     * @param x the x coordinate of the node on the grid.
     * @param y the y coordinate of the node on the grid.
     */
    public TeleporterNode(int x, int y) {
        super(x, y, true);
    }

    /**
     * Create a wormhole node by copying basic information from specific node.
     *
     * @param node the node to copy.
     */
    public TeleporterNode(Node node) {
        this(node.getX(), node.getY());
        setParent(node.getParent());
        setFScore(node.getFScore());
        setGScore(node.getGScore());
        if (node.getHScore() != null) {
            setHScore(node.getHScore());
        }
        setOpened(node.isOpened());
        setClosed(node.isClosed());
        setTested(node.isTested());
    }

    /**
     * Convert this wormhole node to normal walkable node.
     *
     * @return the converted node.
     */
    public Node toNormalNode() {
        Node node = new Node(getX(), getY(), true);
        node.setParent(getParent());
        node.setFScore(getFScore());
        node.setGScore(getGScore());
        if (getHScore() != null) {
            node.setHScore(getHScore());
        }
        node.setOpened(isOpened());
        node.setClosed(isClosed());
        node.setTested(isTested());
        return node;
    }
}
