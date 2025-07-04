package searchclient.cbs.algriothem;

import searchclient.Action;
import searchclient.TimeoutException;
import searchclient.cbs.model.*;
import searchclient.cbs.utils.AStarReachabilityChecker;

import java.util.*;

public class CBSRunner {

    private final long DEFAULT_TIMEOUT = 3 * 60 * 1000;
    private boolean abortedForTimeout = false;
    private long startTime;

    //this class can be improved by different methods
    private final AStarRunner lowLevelRunner;
    private final MinTimeConflictDetection conflictDetection;

    public CBSRunner() {
        this.startTime = System.currentTimeMillis();
        this.lowLevelRunner = new AStarRunner(startTime, DEFAULT_TIMEOUT);
        this.conflictDetection = new MinTimeConflictDetection();
    }

    /**
     * High level of CBS
     *
     * @return
     */
    public Action[][] findSolution(int superB) {
        Environment initEnv = AppContext.getEnv();
        Node rootNode = initRoot(initEnv);
        if (!rootNode.getSolution().isValid()) {
            System.err.println("Root node is invalid");
            return null;
        }

        OpenList openList = new OpenList();
        openList.add(rootNode);

        int[][] cmMatrix = new int[initEnv.getAgentNums()][initEnv.getAgentNums()];

        while (!openList.isEmpty() && !checkTimeout()) {
            Node node = openList.pop();
            AbstractConflict firstConflict = conflictDetection.detect(node);
            if (firstConflict == null) {
                return convertPaths2Actions(node.getSolution());
            }
//            System.err.println("Conflict detected result - " + firstConflict);
            //update cmMatrix
            updateCMMatrix(cmMatrix, firstConflict);

            Constraint[] constraints = firstConflict.getPreventingConstraints();

            int conflictsCount = cmMatrix[firstConflict.getAgent1().getAgentIdNum()][firstConflict.getAgent2().getAgentIdNum()];

            if (superB > -1 && conflictsCount >= superB) {
                doMergeAndUpdate(node, firstConflict);
                if (node.getSolution().isValid()) {
                    openList.add(node);
                }
            } else {
                for (Constraint constraint : constraints) {
                    Node child = buildChild(node, firstConflict, constraint);
                    if (child.getSolution().isValid()) {
                        openList.add(child);
                    }
                }
            }
        }
        System.err.println("CBS openlist empty with a solution");
        return null;
    }

    private void doMergeAndUpdate(Node node, AbstractConflict firstConflict) {
        MetaAgentPlan plan1 = firstConflict.getPlan1();
        MetaAgentPlan plan2 = firstConflict.getPlan2();
        MetaAgentPlan metaAgentPlan = plan1.merge(plan2);

        node.getSolution().getMetaPlans().remove(plan1.getMetaId());
        node.getSolution().getMetaPlans().remove(plan2.getMetaId());
        node.getSolution().addMetaAgentPlan(metaAgentPlan.getMetaId(), metaAgentPlan);

        //在内部处理掉对于内部的冲突的过滤 - 见：searchclient.cbs.model.LowLevelState.expand
        boolean allInOne = node.getSolution().getMetaPlans().size() == 1;
        boolean findNewPath = lowLevelRunner.findPath(node, metaAgentPlan, allInOne);
        node.getSolution().setValid(findNewPath);
        if (findNewPath) {
            node.getSolution().updateMaxSinglePath();
        }
    }

    private void updateCMMatrix(int[][] cmMatrix, AbstractConflict conflict) {
        //冲突的meta计划里的每个元素都要相互加一
        for (Agent agent1 : conflict.getPlan1().getAgents().values()) {
            for (Agent agent2 : conflict.getPlan2().getAgents().values()) {
                int agent1Index = agent1.getAgentIdNum();
                int agent2Index = agent2.getAgentIdNum();
                cmMatrix[agent1Index][agent2Index]++;
                cmMatrix[agent2Index][agent1Index]++;
            }
        }
    }

    private Node buildChild(Node parentCTNode, AbstractConflict firstConflict, Constraint constraint) {
        Node childCTNode = new Node(parentCTNode, firstConflict, constraint);

        Solution childSolution = parentCTNode.getSolution().deepCopy();
        MetaAgentPlan currentAgentPlan = childSolution.getPlanForAgent(constraint.getBelongToMetaId());
        boolean allInOne = childSolution.getMetaPlans().size() == 1;
        boolean findNewPath = lowLevelRunner.findPath(childCTNode, currentAgentPlan, allInOne);

        childSolution.setValid(findNewPath);
        if (findNewPath) {
            childSolution.updateMaxSinglePath();
        }

        childCTNode.setSolution(childSolution);
        return childCTNode;
    }


    /**
     * Convert path of every agent into Action[][]
     *
     * @param solution
     * @return
     */
    private Action[][] convertPaths2Actions(Solution solution) {
        List<MetaAgentPlan> agentPathList = solution.getMetaPlansInOrder();
        //action 2-D array; 1st is the max steps of all agents; 2nd is the number of agents
        int rows = solution.getMaxMetaPath();
        int cols = 0;
        Map<Character, List<Move>> agent2MovesMap = new TreeMap<>();
        for (MetaAgentPlan agentPath : agentPathList) {
            cols += agentPath.getAgents().size();
            for (Map.Entry<Character, Agent> entry : agentPath.getAgents().entrySet()) {
                List<Move> moves = new ArrayList<>(agentPath.getMoves(entry.getKey()).values());
                agent2MovesMap.put(entry.getKey(), moves);
            }
        }

        List<List<Move>> agent2Moves = new ArrayList<>(agent2MovesMap.values());

        Action[][] actions = new Action[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                List<Move> moves = agent2Moves.get(j);
                //as i the max steps of all agents, so some agent may not have this step
                //todo 如果一个agent到达后，又需要出来，在内部处理掉。从而让moves list的总大小表达agent最终的cost
                actions[i][j] = (moves.size() > i ? moves.get(i).getAction() : Action.NoOp);
            }
        }
        return actions;
    }

    private Node initRoot(Environment environment) {
        List<MetaAgentPlan> metaAgentPlanList = DistributionProcessor.distributionAgent2Box2Goal(environment);

        Node rootNode = new Node(null);

        Solution solution = new Solution();
        boolean allInOne = metaAgentPlanList.size() == 1;
        for (MetaAgentPlan metaAgentPlan : metaAgentPlanList) {
            boolean findPath = lowLevelRunner.findPath(rootNode, metaAgentPlan, allInOne);
            if (!findPath) {
                //it will be invalid if anyone agent cannot find a path
                solution.setValid(false);
                break;
            }
            solution.addMetaAgentPlan(metaAgentPlan.getMetaId(), metaAgentPlan);
        }
        rootNode.setSolution(solution);

        return rootNode;
    }

    private boolean checkTimeout() {
        if (System.currentTimeMillis() - startTime > DEFAULT_TIMEOUT) {
            this.abortedForTimeout = true;
            throw new TimeoutException("High-level CBS Timeout");
        }
        return false;
    }

    public boolean isAbortedForTimeout() {
        return abortedForTimeout;
    }
}
