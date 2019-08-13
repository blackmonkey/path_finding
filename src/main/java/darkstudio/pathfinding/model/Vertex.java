/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

/**
 * The class for the graph vertices. Provides functionality for storing information related to that specific vertex.
 */
public class Vertex implements Comparable<Vertex> {
    private int x;
    private int y;
    private double Hx;
    private double Gx;
    private char key;
    private boolean walkable;
    private int index; //used by VertexMinHeap to track index, eliminates need for hashmap
    private boolean onPath;
    private boolean closed;
    private boolean opened;
    private Vertex path;

    /**
     * Initializes values.
     *
     * @param x coordinate
     * @param y coordinate
     * @param key type of coordinate
     */
    public Vertex(int x, int y, char key) {
        this.x = x;
        this.y = y;
        setKey(key);
        index = -1;
        Hx = -1;
        Gx = -1;
        onPath = false;
        closed = false;
        opened = false;
        path = null;
    }

    public boolean isOnPath() {
        return onPath;
    }

    public void setOnPath(boolean onPath) {
        this.onPath = onPath;
    }

    public char getKey() {
        return key;
    }

    public void setKey(char key) {
        this.key = key;
        setWalkable(key == '.');
    }

    public double getToGoal() {
        return Hx;
    }

    public void setToGoal(double toGoal) {
        Hx = toGoal;
    }

    public double getDistance() {
        return Gx;
    }

    public void setDistance(double distance) {
        Gx = distance;
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

    public Vertex getPath() {
        return path;
    }

    public void setPath(Vertex path) {
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    /**
     * Natural order based on the distance values.
     * Formula: distance G(x) + toGoal H(x). Default for toGoal is -1 (ignored if default)
     *
     * @param that to what this object is compared to.
     * @return -1, 0 or 1.
     */
    @Override
    public int compareTo(Vertex that) {
        if (that == null) {
            return 0;
        }

        double thisDist = this.Hx == -1 ? this.Gx : this.Gx + this.Hx;
        double thatDist = that.Hx == -1 ? that.Gx : that.Gx + that.Hx;
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
        if (!(obj instanceof Vertex)) {
            return false;
        }
        Vertex that = (Vertex) obj;
        return x == that.getX() && y == that.getY();
    }

    @Override
    public String toString() {
        return "Vertex{" + "x=" + x + ", y=" + y + ", key=" + key + '}';
    }
}
