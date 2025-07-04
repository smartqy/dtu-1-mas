package searchclient.cbs.model;

import java.io.Serializable;

/**
 * Model for CBS
 * Represents for the edge or swap conflict
 */
public class EdgeConflict extends AbstractConflict implements Serializable {

    public EdgeConflict(MetaAgentPlan plan1, MetaAgentPlan plan2, Agent agent1, Agent agent2, int timeNow, Location locationOfObj1, Location locationOfObj2) {
        super(plan1, plan2, agent1, agent2, timeNow, locationOfObj1, locationOfObj2, null);
    }

    @Override
    public ConflictType getConflictType() {
        return ConflictType.EdgeConflict;
    }

    @Override
    public Constraint[] getPreventingConstraints() {
        return new Constraint[]{
                //Solution1, add Constraint to Agent1 from own location to location of Agent2
                new Constraint(this.plan2.getMetaId(), this.agent1, this.plan1.getMetaId(), getTimeNow(), getLocationOfObj1(), getLocationOfObj2()),
                //Solution2, add Constraint to Agent2 from own location to location of Agent1
                new Constraint(this.plan1.getMetaId(), this.agent2, this.plan2.getMetaId(), getTimeNow(), getLocationOfObj2(), getLocationOfObj1())
        };
    }

    @Override
    public String toString() {
        return "EdgeConflict{" +
                "agent1.uniqueId=" + this.agent1.getAgentId() +
                ", agent2.uniqueId=" + this.agent2.getAgentId() +
                ", timeNow=" + this.timeNow +
                ", targetLocation=" + this.targetLocation +
                ", locationOfObj1=" + this.locationOfObj1 +
                ", locationOfObj2=" + this.locationOfObj2 +
                '}';
    }
}
