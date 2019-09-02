/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author imor / https://github.com/imor
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.algorithm;

import darkstudio.pathfinding.model.Grid;
import darkstudio.pathfinding.model.Node;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

public class JPFTeleportMoveOrthogonally extends JumpPointFinderBase {
    public JPFTeleportMoveOrthogonally(Options options) {
        super(options);
    }

    @Override
    protected List<Point> findNeighbors(Node node) {
        return grid.getNeighbors(node, DiagonalMovement.Never, true)
                .stream().map(n -> new Point(n.getX(), n.getY())).collect(Collectors.toList());
    }

    @Override
    protected Point jump(int x0, int y0, int x1, int y1) {
        int dx = x0 - x1, dy = y0 - y1;

        if (!grid.isWalkableAt(x0, y0)) {
            return null;
        }

        if (grid.getNodeAt(x0, y0) == endNode) {
            return new Point(x0, y0);
        }

        int teleportType = grid.getTeleporterType(x1, y1, x0, y0);

        if (teleportType == Grid.TELEPORT_NORMAL_TUNNEL
                || teleportType == Grid.TELEPORT_NORMAL_WORMHOLE
                || teleportType == Grid.TELEPORT_TUNNEL_TO_NORMAL
                || teleportType == Grid.TELEPORT_TUNNEL_TO_TUNNEL
                || teleportType == Grid.TELEPORT_TUNNEL_TO_WORMHOLE
                || teleportType == Grid.TELEPORT_WORMHOLE_NORMAL
                || teleportType == Grid.TELEPORT_WORMHOLE_TO_WORMHOLE) {
            return new Point(x0, y0);
        }

        if (teleportType != Grid.TELEPORT_NORMAL_NORMAL) {
            /*
             * i.e. teleportType is one of the following value:
             *   Grid.TELEPORT_TUNNEL_TUNNEL
             *   Grid.TELEPORT_TUNNEL_WORMHOLE
             *   Grid.TELEPORT_WORMHOLE_TUNNEL
             *   Grid.TELEPORT_WORMHOLE_WORMHOLE
             *   Grid.TELEPORT_NORMAL_OT_TUNNEL
             *   Grid.TELEPORT_TUNNEL_NORMAL
             *   Grid.TELEPORT_TUNNEL_OT_TUNNEL
             *   Grid.TELEPORT_WORMHOLE_OT_TUNNEL
             *
             * In any of above case, there must be other jump point between two nodes. For this finding of jump point,
             * the method should return null, so that the method will be invoked again to find the intermediate jump
             * point.
             */
            return null;
        }

        if (dx != 0) { // moving horizontally
            if ((grid.isWalkableAt(x0, y0 - 1) && !grid.isWalkableAt(x1, y0 - 1)) ||
                    (grid.isWalkableAt(x0, y0 + 1) && !grid.isWalkableAt(x1, y0 + 1))) {
                return new Point(x0, y0);
            }
        } else if (dy != 0) { // moving vertically
            if ((grid.isWalkableAt(x0 - 1, y0) && !grid.isWalkableAt(x0 - 1, y1)) ||
                    (grid.isWalkableAt(x0 + 1, y0) && !grid.isWalkableAt(x0 + 1, y1))) {
                return new Point(x0, y0);
            }
        } else {
            throw new RuntimeException("Only horizontal and vertical movements are allowed");
        }

        return jump(x0 + dx, y0 + dy, x0, y0);
    }
}
