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
    private HeuristicMethod heuristic;
    private boolean trackJumpRecursion;

    public Options() {
        this(false);
    }

    public Options(boolean trackJumpRecursion) {
        this(Heuristic::manhattan, trackJumpRecursion);
    }

    public Options(HeuristicMethod heuristic) {
        this(heuristic, false);
    }

    public Options(HeuristicMethod heuristic, boolean trackJumpRecursion) {
        setHeuristic(heuristic);
        setTrackJumpRecursion(trackJumpRecursion);
    }

    public HeuristicMethod getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(HeuristicMethod heuristic) {
        this.heuristic = heuristic;
    }

    public boolean isTrackJumpRecursion() {
        return trackJumpRecursion;
    }

    public void setTrackJumpRecursion(boolean trackJumpRecursion) {
        this.trackJumpRecursion = trackJumpRecursion;
    }
}
