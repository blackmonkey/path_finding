/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Elias Nygren
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.algorithm;

import darkstudio.pathfinding.algorithm.Heuristic.HeuristicMethod;

public class Options {
    private HeuristicMethod heuristic = Heuristic::manhattan;
    private boolean trackJumpRecursion;
    private boolean checkTeleporter;

    public Options heuristic(HeuristicMethod heuristic) {
        this.heuristic = heuristic;
        return this;
    }

    public Options trackJumpRecursion(boolean trackJumpRecursion) {
        this.trackJumpRecursion = trackJumpRecursion;
        return this;
    }

    public Options checkTeleporter(boolean checkTeleporter) {
        this.checkTeleporter = checkTeleporter;
        return this;
    }

    public HeuristicMethod getHeuristic() {
        return heuristic;
    }

    public boolean isTrackJumpRecursion() {
        return trackJumpRecursion;
    }

    public boolean doesCheckTeleporter() {
        return checkTeleporter;
    }
}
