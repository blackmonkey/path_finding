/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

/**
 * A node in grid.
 * <p/>
 * This class holds some basic information about a node and custom attributes may be added, depending on the
 * algorithms' needs.
 */
public class Node implements Comparable<Node> {
    private int x;
    private int y;
    private boolean walkable = true;
    private Node parent;
    private Node exit;
    private double fScore;
    private double gScore;
    private Double hScore;
    private boolean opened;
    private boolean closed;
    private boolean tested;

    /**
     * @param x the x coordinate of the node on the grid.
     * @param y the y coordinate of the node on the grid.
     * @param walkable whether this node is walkable.
     */
    public Node(int x, int y, boolean walkable) {
        setX(x);
        setY(y);
        setWalkable(walkable);
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

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getExit() {
        return exit;
    }

    public void setExit(Node exit) {
        this.exit = exit;
    }

    public double getFScore() {
        return fScore;
    }

    public void setFScore(double score) {
        fScore = score;
    }

    public double getGScore() {
        return gScore;
    }

    public void setGScore(double score) {
        gScore = score;
    }

    public Double getHScore() {
        return hScore;
    }

    public void setHScore(double score) {
        hScore = score;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isTested() {
        return tested;
    }

    public void setTested(boolean tested) {
        this.tested = tested;
    }

    public void reset() {
        parent = null;
        fScore = 0;
        gScore = 0;
        hScore = null;
        opened = false;
        closed = false;
        tested = false;
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
        return "Node{" + "x=" + x + ", y=" + y + ", walkable=" + walkable + "}";
    }

    @Override
    public int compareTo(Node o) {
        return o != null ? Double.compare(fScore, o.getFScore()) : 0;
    }
}
