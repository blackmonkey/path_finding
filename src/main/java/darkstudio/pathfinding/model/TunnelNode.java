/*
 * Copyright (c) 2019-present Dark Studio
 * All rights, including trade secret rights, reserved.
 *
 * @author Xueqiao Xu <xueqiaoxu@gmail.com>
 * @author Oscar Cai <blackmuffus@yahoo.com>
 */

package darkstudio.pathfinding.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TunnelNode extends TeleporterNode {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;

    private List<TunnelNode> ins = new ArrayList<>();
    private Node out;
    private int direction = LEFT;

    public TunnelNode(int x, int y) {
        super(x, y);
    }

    public TunnelNode(Node node) {
        super(node);
    }

    /**
     * @return the possible multiple tunnel nodes who teleport right to this one.
     */
    public List<TunnelNode> getIns() {
        return ins;
    }

    /**
     * Set the teleporting source nodes.
     *
     * @param nodes the possible multiple tunnel node who teleport right to this one.
     */
    public void setIns(List<TunnelNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            ins.clear();
        } else {
            ins = nodes;
        }
    }

    /**
     * Add specific tunnel node as this one's incoming node.
     *
     * @param node the incoming tunnel node to add.
     */
    public void addIn(TunnelNode node) {
        if (!ins.contains(node)) {
            ins.add(node);
        }
    }

    /**
     * @return the node to which this tunnel node teleport right, and it is possible another tunnel node or normal
     * walkable node.
     */
    public Node getOut() {
        return out;
    }

    /**
     * Set the teleporting target node.
     *
     * @param node the node to which this tunnel node teleport right, and it is possible another tunnel node or normal
     * walkable node.
     */
    public void setOut(Node node) {
        out = node;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        String inInfo = ins.stream().map(n -> "(" + n.getX() + "," + n.getY() + ")").collect(Collectors.toList()) + "=>";
        String outInfo = out != null ? "=>[" + out.getX() + "," + out.getY() + "]" : "";
        String parentInfo = getParent() != null ? "(" + getParent().getX() + "," + getParent().getY() + ")->" : "";
        return "Tunnel" + parentInfo + inInfo + "(" + getX() + "," + getY() + ")" + outInfo
                + "{walkable:" + isWalkable() + ",opened:" + isOpened() + ",closed:" + isClosed()
                + ",score:" + getGScore() + "+" + getHScore() + "=" + getFScore() + "}";
    }
}
