/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.algorithm;

/**
 * A collection of heuristic functions.
 */
public class Heuristic {
    private static final double F = Math.sqrt(2) - 1;

    /**
     * Manhattan distance.
     *
     * @param dx difference in x.
     * @param dy difference in y.
     * @return dx + dy
     */
    public static double manhattan(int dx, int dy) {
        return dx + dy;
    }

    /**
     * Euclidean distance.
     *
     * @param dx difference in x.
     * @param dy difference in y.
     * @return sqrt(dx * dx + dy * dy)
     */
    public static double euclidean(int dx, int dy) {
        return Math.hypot(dx, dy);
    }

    /**
     * Octile distance.
     *
     * @param dx difference in x.
     * @param dy difference in y.
     * @return sqrt(dx * dx + dy * dy) for grids
     */
    public static double octile(int dx, int dy) {
        return (dx < dy) ? (F * dx + dy) : (F * dy + dx);
    }

    /**
     * Chebyshev distance.
     *
     * @param dx difference in x.
     * @param dy difference in y.
     * @return max(dx, dy)
     */
    public static double chebyshev(int dx, int dy) {
        return Math.max(dx, dy);
    }

    @FunctionalInterface
    interface HeuristicMethod {
        double apply(int dx, int dy);
    }
}
