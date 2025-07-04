package searchclient.cbs.model;

import java.io.Serializable;

/**
 * Model for CBS
 * Represents for the vertex conflict
 */
public class VertexConflict extends AbstractConflict implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean isSingle;
    private boolean needForStop = false;
    private int timeForStop;

    public VertexConflict(MetaAgentPlan plan1, MetaAgentPlan plan2, Agent agent1, Agent agent2, int timeNow, Location locationOfAgent1, Location locationOfAgent2, Location targetLocation, boolean isSingle) {
        super(plan1, plan2, agent1, agent2, timeNow, locationOfAgent1, locationOfAgent2, targetLocation);
        this.isSingle = isSingle;
    }

    public VertexConflict(MetaAgentPlan plan1, MetaAgentPlan plan2, Agent agent1, Agent agent2, int timeNow, Location locationOfAgent1, Location locationOfAgent2, Location targetLocation, int timeForStop) {
        super(plan1, plan2, agent1, agent2, timeNow, locationOfAgent1, locationOfAgent2, targetLocation);
        this.isSingle = false;
        this.needForStop = true;
        this.timeForStop = timeForStop;
    }

    @Override
    public ConflictType getConflictType() {
        return ConflictType.VertexConflict;
    }

    @Override
    public Constraint[] getPreventingConstraints() {
        if (this.isSingle) {
            return new Constraint[]{
                    //Solution1 add a Constraint to Agent1
                    new Constraint(plan2.getMetaId(), agent1, plan1.getMetaId(), getTimeNow(), null, getTargetLocation())
            };
        } else if (!this.needForStop) {
            return new Constraint[]{
                    //Solution1 add a Constraint to Agent1
                    new Constraint(plan2.getMetaId(), agent1, plan1.getMetaId(), getTimeNow(), null, getTargetLocation()),
                    //Solution2 add a Constraint to Agent2
                    new Constraint(plan1.getMetaId(), agent2, plan2.getMetaId(), getTimeNow(), null, getTargetLocation())
            };
        } else {
            return new Constraint[]{
                    //Solution1 add a Constraint to Agent1
                    new Constraint(plan2.getMetaId(), agent1, plan1.getMetaId(), getTimeNow(), null, getTargetLocation()),
//                    //Solution2 add a Constraint to Agent2
//                    new Constraint(plan1.getMetaId(), agent2, plan2.getMetaId(), this.timeForStop, null, getTargetLocation())
            };
        }
    }

    @Override
    public String toString() {
        return "VertexConflict{" +
                "Agent1.uniqueId=" + this.agent1.getAgentId() +
                ", Agent2.uniqueId=" + this.agent2.getAgentId() +
                ", timeNow=" + this.timeNow +
                ", targetLocation=" + this.targetLocation +
                ", locationOfObj1=" + this.locationOfObj1 +
                ", locationOfObj2=" + this.locationOfObj2 +
                ", isSingle=" + this.isSingle +
                '}';
    }
}
