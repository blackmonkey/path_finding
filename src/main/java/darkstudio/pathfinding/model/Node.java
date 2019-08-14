/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

/**
 * The class for the graph nodes. Provides functionality for storing information related to that specific node.
 */
public class Node implements Comparable<Node> {
    public static final char PASSABLE = '.';

    private int x;
    private int y;
    private double gScore = -1;
    private double hScore = -1;
    private Node parent = null;
    private boolean passable;

    private char key;
    private int index = -1; // used by VertexMinHeap to track index, eliminates need for hashmap
    private boolean onPath = false;
    private boolean closed = false;
    private boolean opened = false;

    /**
     * Initializes values.
     *
     * @param x coordinate
     * @param y coordinate
     * @param key type of coordinate
     */
    public Node(int x, int y, char key) {
        setX(x);
        setY(y);
        setKey(key);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getDistance() {
        return gScore;
    }

    public void setDistance(double distance) {
        gScore = distance;
    }

    public double getToGoal() {
        return hScore;
    }

    public void setToGoal(double toGoal) {
        hScore = toGoal;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isPassable() {
        return passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public char getKey() {
        return key;
    }

    public void setKey(char key) {
        this.key = key;
        setPassable(key == PASSABLE);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isOnPath() {
        return onPath;
    }

    public void setOnPath(boolean onPath) {
        this.onPath = onPath;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    /**
     * Natural order based on the distance values.
     * Formula: distance G(x) + toGoal H(x). Default for toGoal is -1 (ignored if default)
     *
     * @param that to what this object is compared to.
     * @return -1, 0 or 1.
     */
    @Override
    public int compareTo(Node that) {
        if (that == null) {
            return 0;
        }

        double thisDist = hScore == -1 ? gScore : gScore + hScore;
        double thatDist = that.hScore == -1 ? that.gScore : that.gScore + that.hScore;
        return Double.compare(thisDist, thatDist);
    }

    /**
     * Checks if coordinates match.
     *
     * @param obj to what this is compared.
     * @return {@code true} if coordinates match, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        }
        Node that = (Node) obj;
        return x == that.getX() && y == that.getY();
    }

    @Override
    public String toString() {
        return "Node{" + "x=" + x + ", y=" + y + ", key=" + key + '}';
    }
}
