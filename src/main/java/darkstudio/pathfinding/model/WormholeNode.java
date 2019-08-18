/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

public class WormholeNode extends TeleporterNode {
    private WormholeNode peer;

    public WormholeNode(int x, int y) {
        super(x, y);
    }

    public WormholeNode(Node node) {
        super(node);
    }

    public WormholeNode getPeer() {
        return peer;
    }

    public void setPeer(WormholeNode node) {
        peer = node;
    }

    @Override
    public String toString() {
        String peerInfo = peer != null ? "<=>[" + peer.getX() + "," + peer.getY() + "]" : "";
        String parentInfo = getParent() != null ? "(" + getParent().getX() + "," + getParent().getY() + ")->" : "";
        return "Wormhole" + parentInfo + "(" + getX() + "," + getY() + ")" + peerInfo
                + "{walkable:" + isWalkable() + ",opened:" + isOpened() + ",closed:" + isClosed()
                + ",score:" + getGScore() + "+" + getHScore() + "=" + getFScore() + "}";
    }
}
