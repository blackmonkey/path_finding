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
    private boolean considerTeleporter;

    public Options heuristic(HeuristicMethod heuristic) {
        this.heuristic = heuristic;
        return this;
    }

    public Options trackJumpRecursion(boolean trackJumpRecursion) {
        this.trackJumpRecursion = trackJumpRecursion;
        return this;
    }

    public Options considerTeleporter(boolean considerTeleporter) {
        this.considerTeleporter = considerTeleporter;
        return this;
    }

    public HeuristicMethod getHeuristic() {
        return heuristic;
    }

    public boolean isTrackJumpRecursion() {
        return trackJumpRecursion;
    }

    public boolean doesConsiderTeleporter() {
        return considerTeleporter;
    }
}
