/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Shane
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

public class GridNode extends Node {
    private int x, y;
    private int gScore, hScore;

    /**
     * Default constructor
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param isPassable Is the node passable
     */
    public GridNode(int x, int y, boolean isPassable) {
        setLocation(x, y);
        setPassable(isPassable);
        gScore = 0;
        hScore = 0;
    }

    /**
     * Sets this node's location
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return this node's x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * @return this node's y coordinate
     */
    public int getY() {
        return y;
    }

    @Override
    public int getFScore() {
        return hScore + gScore;
    }

    /**
     * Sets this node's heuristic score
     *
     * @param score the heuristic score
     */
    public void setHScore(int score) {
        hScore = score;
    }

    /**
     * Sets this node's gScore
     *
     * @param score
     */
    public void setGScore(int score) {
        gScore = score;
    }

    /**
     * @return this node's Gscore
     */
    public int getGScore() {
        return gScore;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ") F:" + getFScore() + " G:" + getGScore() + " H:" + hScore;
    }
}
