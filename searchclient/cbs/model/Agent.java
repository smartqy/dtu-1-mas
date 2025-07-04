package searchclient.cbs.model;

import searchclient.Color;

import java.io.Serializable;
import java.util.Objects;

public class Agent extends MovableObj implements Serializable {

    private Location goalLocation;

    private char agentChar;

    public Agent(char uniqueId, Color color) {
        super(ObjectType.AGENT, String.valueOf(uniqueId), color);
        this.agentChar = uniqueId;
    }

    public char getAgentId() {
        return this.agentChar;
    }

    public Agent deepCopy() {
        Agent agent = new Agent(this.agentChar, this.getColor());
        agent.setInitLocation(this.getInitLocation().deepCopy());
        agent.setCurrentLocation(this.getCurrentLocation().deepCopy());
        Location goalLocation = this.getGoalLocation() != null ? this.getGoalLocation().deepCopy() : null;
        agent.setGoalLocation(goalLocation);
        return agent;
    }

    public int getAgentIdNum() {
        return Character.getNumericValue(this.agentChar);
    }

    @Override
    public String toString() {
        return "Agent {uniqueId=" + this.uniqueId +
                ", objType=" + this.objType +
                ", color=" + this.getColor() +
                ", initLocation=" + this.getInitLocation() +
                ", goalLocation=" + this.getGoalLocation() +
                ", currentLocation=" + this.getCurrentLocation() +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Agent agent = (Agent) obj;
        return (Objects.equals(uniqueId, agent.uniqueId)
                && initLocation.equals(agent.initLocation))
                && currentLocation.equals(agent.currentLocation);
    }

    @Override
    public int hashCode() {
        int result = uniqueId.hashCode();
        result = 31 * result + initLocation.hashCode();
        result = 31 * result + currentLocation.hashCode();
        return result;
    }

    public void setGoalLocation(Location goalLocation) {
        this.goalLocation = goalLocation;
    }

    public Location getGoalLocation() {
        return goalLocation;
    }
}
