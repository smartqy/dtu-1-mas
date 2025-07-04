package searchclient.cbs.model;

import searchclient.ActionType;

import java.util.Objects;

/**
 * Model for CBS
 */
public abstract class AbstractConflict {

    protected MetaAgentPlan plan1;
    protected MetaAgentPlan plan2;
    protected Agent agent1;
    protected Agent agent2;
    protected int timeNow;
    //Used for vertex conflict
    protected Location targetLocation;
    //The origin loc of Agent1
    protected Location locationOfObj1;
    //The origin loc of Agent2
    protected Location locationOfObj2;

    enum ConflictType {
        VertexConflict,
        EdgeConflict,
        FollowConflict
    }

    public AbstractConflict(MetaAgentPlan plan1, MetaAgentPlan plan2, Agent agent1, Agent agent2,
                            int timeNow, Location locationOfObj1, Location locationOfObj2, Location targetLocation) {
        this.plan1 = plan1;
        this.plan2 = plan2;
        this.agent1 = agent1;
        this.agent2 = agent2;
        this.timeNow = timeNow;
        this.locationOfObj1 = locationOfObj1;
        this.locationOfObj2 = locationOfObj2;
        this.targetLocation = targetLocation;
    }

    public abstract ConflictType getConflictType();

    //todo 还需要再考虑一下有box的情况，这个是否可行 - box 在外面处理了
    public static AbstractConflict conflictBetween(MetaAgentPlan plan1, MetaAgentPlan plan2, Move move1, Move move2) {
        if (move1.getAction().type == ActionType.NoOp && move2.getAction().type == ActionType.NoOp) {
            // NoOp action, no conflict
            return null;
        }
        if (move1.getAction().type == ActionType.NoOp) {
            if (move2.getMoveTo().equals(move1.getMoveTo())) {
                //只需要给move2加一个冲突
                return new VertexConflict(plan2, plan1, move2.getAgent(), move1.getAgent(), move2.getTimeNow(),
                        move2.getCurrentLocation(), move1.getCurrentLocation(), move2.getMoveTo(), true);
            }
        }
        if (move2.getAction().type == ActionType.NoOp) {
            if (move1.getMoveTo().equals(move2.getMoveTo())) {
                //只需要给move1加一个冲突
                return new VertexConflict(plan1, plan2, move1.getAgent(), move2.getAgent(), move1.getTimeNow(),
                        move1.getCurrentLocation(), move2.getCurrentLocation(), move1.getMoveTo(), true);
            }
        }
        if (move1.getMoveTo().equals(move2.getMoveTo())) {
            return new VertexConflict(plan1, plan2, move1.getAgent(), move2.getAgent(), move1.getTimeNow(),
                    move1.getCurrentLocation(), move2.getCurrentLocation(), move1.getMoveTo(), false);
        }
        if (move1.getCurrentLocation().equals(move2.getMoveTo()) && move2.getCurrentLocation().equals(move1.getMoveTo())) {
            return new EdgeConflict(plan1, plan2, move1.getAgent(), move2.getAgent(), move1.getTimeNow(), move1.getCurrentLocation(), move2.getCurrentLocation());
        }
        if (move1.getMoveTo().equals(move2.getCurrentLocation())) {
            return new FollowConflict(plan1, plan2, move1.getAgent(), move2.getAgent(), move1.getTimeNow(), move1.getCurrentLocation(), move2.getCurrentLocation());
        }
        if (move2.getMoveTo().equals(move1.getCurrentLocation())) {
            return new FollowConflict(plan2, plan1, move2.getAgent(), move1.getAgent(), move2.getTimeNow(), move2.getCurrentLocation(), move1.getCurrentLocation());
        }

        return null;
    }

    /**
     * @return an array of constraints, each of which could prevent this conflict
     */
    public abstract Constraint[] getPreventingConstraints();

    public Agent getAgent1() {
        return agent1;
    }

    public Agent getAgent2() {
        return agent2;
    }
    
    public MetaAgentPlan getPlan1() {
        return plan1;
    }

    public MetaAgentPlan getPlan2() {
        return plan2;
    }

    public int getTimeNow() {
        return timeNow;
    }

    public Location getLocationOfObj1() {
        return locationOfObj1;
    }

    public Location getLocationOfObj2() {
        return locationOfObj2;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plan1, plan2, agent1, agent2, timeNow, targetLocation, locationOfObj1, locationOfObj2);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AbstractConflict that = (AbstractConflict) obj;
        return timeNow == that.timeNow
                && Objects.equals(plan1, that.plan1)
                && Objects.equals(plan2, that.plan2)
                && Objects.equals(agent1, that.agent1)
                && Objects.equals(agent2, that.agent2)
                && Objects.equals(targetLocation, that.targetLocation)
                && Objects.equals(locationOfObj1, that.locationOfObj1)
                && Objects.equals(locationOfObj2, that.locationOfObj2);
    }
}
