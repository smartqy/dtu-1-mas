package searchclient.cbs.model;

import java.util.*;

/**
 * Model for Single Agent Plan => MetaAgentPlan
 * Represents the plan of multiple agent, including the env, agent, boxes and a effective move list for this plan.
 * 代表一个单独的代理的计划，包括环境、代理、箱子和使得该计划可解的一个有效移动列表。
 */
public class MetaAgentPlan {

    private Map<Character, Agent> agents = new HashMap<>();
    private Map<String, Box> boxes = new HashMap<>();
    private Map<Character, Map<Integer, Move>> agentMoves = new HashMap<>();

    private Map<Character, Location> agentsFinalLocations = new HashMap<>();
    private Map<String, Location> boxesFinalLocation = new HashMap<>();

    /**
     * Update the plan to the final state
     * 1. get the moves from the final state
     * 2. set the agent's final location by the final state - used to check the conflict
     * 3. update the boxes' final location by the final state - used to check the conflict
     *
     * @param goalState
     */
    public void update2Final(LowLevelState goalState) {
        for (Agent agent : goalState.getAgents().values()) {
            this.agentMoves.put(agent.getAgentId(), goalState.extractMovesForOneAgent(agent.getAgentId()));
            this.agentsFinalLocations.put(agent.getAgentId(), agent.getCurrentLocation().deepCopy());
        }

        for (String uniqueId : this.boxes.keySet()) {
            Box goalBox = goalState.getBoxes().get(uniqueId);
            if (goalBox != null) {
                this.boxesFinalLocation.put(uniqueId, goalBox.getCurrentLocation().deepCopy());
            } else {
                throw new IllegalArgumentException("Box " + uniqueId + " not found in goal state");
            }
        }
    }

    public MetaAgentPlan(Map<Character, Agent> agents) {
        this.agents = agents;
    }

    public MetaAgentPlan deepCopy() {
        Map<Character, Agent> agents = new HashMap<>();
        for (Map.Entry<Character, Agent> entry : this.agents.entrySet()) {
            agents.put(entry.getKey(), entry.getValue().deepCopy());
        }

        MetaAgentPlan plan = new MetaAgentPlan(agents);
        for (Map.Entry<String, Box> entry : this.boxes.entrySet()) {
            plan.addBox(entry.getValue().deepCopy());
        }

        for (Map.Entry<Character, Map<Integer, Move>> entry : this.agentMoves.entrySet()) {
            Map<Integer, Move> moves = new HashMap<>();
            for (Map.Entry<Integer, Move> moveEntry : entry.getValue().entrySet()) {
                moves.put(moveEntry.getKey(), moveEntry.getValue().deepCopy());
            }
            plan.agentMoves.put(entry.getKey(), moves);
        }

        for (Map.Entry<Character, Location> entry : this.agentsFinalLocations.entrySet()) {
            plan.agentsFinalLocations.put(entry.getKey(), entry.getValue().deepCopy());
        }

        for (Map.Entry<String, Location> entry : this.boxesFinalLocation.entrySet()) {
            plan.boxesFinalLocation.put(entry.getKey(), entry.getValue().deepCopy());
        }

        return plan;
    }

    /**
     * 返回当前MetaAgent的最长路径长度
     *
     * @return
     */
    public int getMaxMoveSize() {
        int max = 0;
        for (Map<Integer, Move> item : this.agentMoves.values()) {
            if (item.size() > max) {
                max = item.size();
            }
        }
        return max;
    }

    /**
     * The cost of the plan is the max size of the move List.
     *
     * @return
     */
    public int getCost() {
        if (this.agentMoves.isEmpty()) return 0;
        return getMaxMoveSize();
    }

    public Map<Integer, Move> getMoves(Character agentId) {
        return this.agentMoves.get(agentId);
    }

    public String getMetaId() {
        StringBuilder sb = new StringBuilder();
        this.agents.keySet().forEach(id -> sb.append(id).append("-"));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public Map<Character, Agent> getAgents() {
        return this.agents;
    }

    public AbstractConflict firstConflict(MetaAgentPlan otherPlan) {
        //1. check every step, either vertex or edge conflict may happen
        Environment env = AppContext.getEnv();
        int plan1EndTime = this.getMaxMoveSize();
        int plan2EndTime = otherPlan.getMaxMoveSize();
        int minEndTime = Math.min(plan1EndTime, plan2EndTime);
        int maxEndTime = Math.max(plan1EndTime, plan2EndTime);
        Box[][] tmpBoxLocations1 = new Box[env.getGridNumRows()][env.getGridNumCol()];
        Box[][] tmpBoxLocations2 = new Box[env.getGridNumRows()][env.getGridNumCol()];

        //初始化箱子的位置
        this.getBoxes().values().forEach(box -> tmpBoxLocations1[box.getInitLocation().getRow()][box.getInitLocation().getCol()] = box.deepCopy());
        otherPlan.getBoxes().values().forEach(box -> tmpBoxLocations2[box.getInitLocation().getRow()][box.getInitLocation().getCol()] = box.deepCopy());

        //move 的 time是从1开始的。表示它是走到了 time 时刻的位置
        for (int i = 1; i <= minEndTime; i++) {
            //每个plan里面有多个agent。都要逐一检查
            List<Move> moveInThisPlan = new ArrayList<>();
            for (Map<Integer, Move> item : this.agentMoves.values()) {
                moveInThisPlan.add(item.get(i));
            }
            List<Move> moveInOtherPlan = new ArrayList<>();
            for (Map<Integer, Move> item : otherPlan.agentMoves.values()) {
                moveInOtherPlan.add(item.get(i));
            }

            //1. simple check move
            for (Move thisMove : moveInThisPlan) {
                for (Move otherMove : moveInOtherPlan) {
                    AbstractConflict conflict = AbstractConflict.conflictBetween(this, otherPlan, thisMove, otherMove);
                    if (conflict != null) return conflict;

                    //2. check env conflict same as Vertex Conflic 这里箱子的位置是i-1时刻的。因为箱子无法自己移动。如果移动了，那么冲突在上面可以检测。这里主要检测的就是不动的箱子对于agent的影响
                    //检查在plan2中箱子是否挡住move1了。所以用move1的目标位置去检查plan2的箱子
                    Box checkForMove1 = tmpBoxLocations2[thisMove.getMoveTo().getRow()][thisMove.getMoveTo().getCol()];
                    if (checkForMove1 != null) {
                        return new VertexConflict(this, otherPlan, thisMove.getAgent(), otherMove.getAgent(), i, thisMove.getCurrentLocation(), otherMove.getCurrentLocation(), thisMove.getMoveTo(), true);
                    }

                    //检查在plan1中箱子是否挡住move2了。所以用move1的目标位置去检查plan2的箱子
                    Box checkForMove2 = tmpBoxLocations1[otherMove.getMoveTo().getRow()][otherMove.getMoveTo().getCol()];
                    if (checkForMove2 != null) {
                        return new VertexConflict(otherPlan, this, otherMove.getAgent(), thisMove.getAgent(), i, otherMove.getCurrentLocation(), thisMove.getCurrentLocation(), otherMove.getMoveTo(), true);
                    }
                }
            }

            //更新走完这一步，box的全局新位置
            for (Move thisMove : moveInThisPlan) {
                if (thisMove.getBox() != null) {
                    Box tmp = tmpBoxLocations1[thisMove.getBox().getCurrentLocation().getRow()][thisMove.getBox().getCurrentLocation().getCol()];
                    if (!thisMove.getBox().originEqual(tmp)) {
                        System.err.printf("There must have the box %s, but is %s\n", thisMove.getBox(), tmp);
                        throw new IllegalArgumentException("There must have the box");
                    }
                    tmpBoxLocations1[tmp.getCurrentLocation().getRow()][tmp.getCurrentLocation().getCol()] = null;

                    tmp.setCurrentLocation(thisMove.getBoxTargetLocation());
                    tmpBoxLocations1[tmp.getCurrentLocation().getRow()][tmp.getCurrentLocation().getCol()] = tmp;
                }
            }

            for (Move otherMove : moveInOtherPlan) {
                if (otherMove.getBox() != null) {
                    Box tmp = tmpBoxLocations2[otherMove.getBox().getCurrentLocation().getRow()][otherMove.getBox().getCurrentLocation().getCol()];
                    if (!otherMove.getBox().originEqual(tmp)) {
                        System.err.printf("There must have the box %s, but is %s\n", otherMove.getBox(), tmp);
                        throw new IllegalArgumentException("There must have the box");
                    }

                    tmpBoxLocations2[tmp.getCurrentLocation().getRow()][tmp.getCurrentLocation().getCol()] = null;
                    tmp.setCurrentLocation(otherMove.getBoxTargetLocation());
                    tmpBoxLocations2[tmp.getCurrentLocation().getRow()][tmp.getCurrentLocation().getCol()] = tmp;
                }
            }
        }

        //2. check that whether one entity at the path of another when it is already at its goal
        if (plan1EndTime != plan2EndTime) {
            MetaAgentPlan earlyEndingPlan = (plan1EndTime > plan2EndTime ? otherPlan : this);
            MetaAgentPlan laterEndingPlan = (plan1EndTime > plan2EndTime ? this : otherPlan);

            List<Location> stayLocations = new ArrayList<>();
            stayLocations.addAll(earlyEndingPlan.getAgentFinalLocation().values());
            stayLocations.addAll(earlyEndingPlan.getBoxesFinalLocation().values());

            //如果plan2 先结束 - 则去检测plan1是否会经过2的goal - 只检测vertex Conflict即可
            //这里需要考虑所有的plan下所有的对象 agent+boxes - by stayLocations
            for (int time = minEndTime + 1; time <= maxEndTime; time++) {

                for (Map<Integer, Move> moves : laterEndingPlan.agentMoves.values()) {
                    Move move2 = moves.get(time);
                    Location moveTo = move2.getMoveTo();
                    if (stayLocations.contains(moveTo)) {
                        List<Agent> agents = earlyEndingPlan.getAgents().values().stream().toList();
                        int index = new Random().nextInt(agents.size());
                        Agent agentEarly = agents.get(index);
                        return new VertexConflict(laterEndingPlan, earlyEndingPlan, move2.getAgent(), agentEarly,
                                time, move2.getCurrentLocation(), moveTo, moveTo, minEndTime);
                    }

                }
            }
        }

        return null;
    }

    public Map<String, Box> getBoxes() {
        return this.boxes;
    }

    public void addBox(Box box) {
        this.boxes.put(box.getUniqueId(), box);
    }

    @Override
    public String toString() {
        return "MetaAgentPlan{" +
                "agent=" + agents +
                ", boxes=" + boxes +
                ", moves=" + agentMoves +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(agents, boxes, agentMoves);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MetaAgentPlan that = (MetaAgentPlan) obj;
        return Objects.equals(agents, that.agents) &&
                Objects.equals(boxes, that.boxes) &&
                Objects.equals(agentMoves, that.agentMoves);
    }

    public Map<Character, Location> getAgentFinalLocation() {
        return this.agentsFinalLocations;
    }

    public Map<String, Location> getBoxesFinalLocation() {
        return boxesFinalLocation;
    }

    public MetaAgentPlan merge(MetaAgentPlan plan2) {
        MetaAgentPlan merged = this.deepCopy();
        merged.agents.clear();
        merged.boxes.clear();
        merged.agentsFinalLocations.clear();
        merged.boxesFinalLocation.clear();

        merged.agents.putAll(this.agents);
        merged.agents.putAll(plan2.agents);
        merged.boxes.putAll(this.boxes);
        merged.boxes.putAll(plan2.boxes);

        return merged;
    }
}
