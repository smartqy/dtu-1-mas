package searchclient.cbs.model;

import searchclient.Action;
import searchclient.ActionType;

import java.util.Objects;

/**
 * Base Model
 * Agent move at timeNow, who is at currentLocation
 */
public class Move {
    private final Agent agent;
    private final Box box;
    private final int timeNow;
    private final Action action;

    public Move(Agent agent, int timeNow, Action action, Box box) {
        this.agent = agent;
        this.timeNow = timeNow;
        this.action = action;
        this.box = box;
    }

    public Move deepCopy() {
        return new Move(this.agent.deepCopy(), this.timeNow, this.action, this.box != null ? this.box.deepCopy() : null);
    }

    public Agent getAgent() {
        return agent;
    }

    public int getTimeNow() {
        return timeNow;
    }


    public Action getAction() {
        return action;
    }

    /**
     * Get the location to move to
     * if action is NoOp, return null
     * if action is Push, return the box location after the push
     * if action is Move or Pull, return the agent location after the move
     *
     * @return
     */
    public Location getMoveTo() {
        if (this.action == Action.NoOp) {
            return this.agent.getCurrentLocation();
        } else if (this.action.type == ActionType.Push) {
            return new Location(this.box.getCurrentLocation().getRow() + this.action.boxRowDelta, this.box.getCurrentLocation().getCol() + this.action.boxColDelta);
        } else {
            // move or pull
            return new Location(this.agent.getCurrentLocation().getRow() + this.action.agentRowDelta, this.agent.getCurrentLocation().getCol() + this.action.agentColDelta);
        }
    }

    /**
     * This function is used for detecting the collision
     *
     * @return
     */
    public Location getCurrentLocation() {
        if (this.action.type == ActionType.Move) {
            return this.agent.getCurrentLocation();
        } else if (this.action.type == ActionType.Pull) {
            return this.box.getCurrentLocation();//改成边界
        } else if (this.action.type == ActionType.Push) {
            return this.agent.getCurrentLocation();
        } else {
            return this.agent.getCurrentLocation();
        }
    }

    public Box getBox() {
        return box;
    }

    public Location getBoxTargetLocation() {
        switch (this.action.type) {
            case Move:
            case NoOp:
                return null;
            case Push:
            case Pull:
                return new Location(this.box.getCurrentLocation().getRow() + this.action.boxRowDelta, this.box.getCurrentLocation().getCol() + this.action.boxColDelta);
            default:
                throw new IllegalArgumentException("Unknown action : " + this.action);
        }
    }

    public Location getAgentTargetLocation() {
        switch (this.action.type) {
            case Move:
            case Push:
            case Pull:
            case NoOp:
                return new Location(this.agent.getCurrentLocation().getRow() + this.action.agentRowDelta, this.agent.getCurrentLocation().getCol() + this.action.agentColDelta);
            default:
                throw new IllegalArgumentException("Unknown action : " + this.action);
        }
    }

    @Override
    public String toString() {
        return "Move{" + "agent=" + agent + ", box=" + box + ", timeNow=" + timeNow + ", action=" + action + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Move move = (Move) obj;

        if (timeNow != move.timeNow) return false;
        if (!agent.equals(move.agent)) return false;
        if (!action.equals(move.action)) return false;
        return Objects.equals(box, move.box);

    }

    @Override
    public int hashCode() {
        return Objects.hash(agent, timeNow, action, box);
    }
}
