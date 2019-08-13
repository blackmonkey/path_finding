/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Shane
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

public abstract class Node implements Comparable<Object> {
    private Node parent;
    private boolean passable = true;

    /**
     * @return this node's FScore
     */
    public abstract int getFScore();

    /**
     * @return this node's parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets this node's parent
     *
     * @param node the parent node
     */
    public void setParent(Node node) {
        parent = node;
    }

    /**
     * @return {@code true} if this node is passable, {@code false} otherwise.
     */
    public boolean isPassable() {
        return passable;
    }

    /**
     * Sets whether this node is passable
     *
     * @param passable {@code true} if this node is passable, {@code false} otherwise.
     */
    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    @Override
    public int compareTo(Object t) {
        if (!(t instanceof Node)) {
            return 0;
        }
        Node other = (Node) t;
        return getFScore() - other.getFScore();
    }
}
