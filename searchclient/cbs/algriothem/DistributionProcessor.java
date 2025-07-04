package searchclient.cbs.algriothem;

import searchclient.cbs.model.*;
import searchclient.cbs.utils.AStarReachabilityChecker;

import java.util.*;

public class DistributionProcessor {

    public static List<MetaAgentPlan> distributionAgent2Box2Goal(Environment environment) {
        List<MetaAgentPlan> agentPlanList = new ArrayList<>();
        Map<Agent, MetaAgentPlan> agentToMetaPlanMap = new HashMap<>();

        //box -> agent -> distance
        Map<Box, Map<Agent, Integer>> box2Agent = new HashMap<>();

        /**
         * 1. preprocessing
         * 1.1 每个agent初始化设置为一个plan
         * 1.2 检查agent->box的可达性.并更新环境的wall设置
         */
        doPreprocessing(environment, agentToMetaPlanMap, box2Agent, agentPlanList);

        //update cost map - 在重新设置wall之后进行
        environment.calculateCostMap();

        //2.分配box到goal
        assignedGoal2Box(environment);

        doCheckGoalReachability(environment);

        //3. 分配agent到box
        assignedBox2Agent(agentToMetaPlanMap, box2Agent, environment);

//        for (MetaAgentPlan metaAgentPlan : agentPlanList) {
//            for (Agent agent : metaAgentPlan.getAgents().values()) {
//                System.err.printf("Agent - %s, color: %s, init:[%s], goal:[%s]\n", agent.getAgentId(), agent.getColor(),
//                        agent.getInitLocation(), agent.getGoalLocation() != null ? agent.getGoalLocation() : "null");
//            }
//            for (Box box : metaAgentPlan.getBoxes().values()) {
//                System.err.printf("Box - %s, color: %s, init:[%s], goal:[%s]\n", box.getUniqueId(), box.getColor(),
//                        box.getInitLocation(), box.getGoalLocation() != null ? box.getGoalLocation() : "null");
//            }
//            System.err.println("---------------------");
//        }
        System.err.printf("End group, begin to find path, size = %d\n", agentPlanList.size());

        return agentPlanList;
    }

    private static void assignedBox2Agent(Map<Agent, MetaAgentPlan> agentToMetaPlanMap, Map<Box, Map<Agent, Integer>> box2Agent, Environment environment) {

        List<Box> unassignedBoxes = new ArrayList<>();
        Map<Agent, Integer> agentLoads = new HashMap<>();
        Map<Agent, Integer> agentBoxNum = new HashMap<>();
        for (Agent agent : agentToMetaPlanMap.keySet()) {
            agentBoxNum.put(agent, 0);
        }

        for (Map.Entry<Box, Map<Agent, Integer>> entry : box2Agent.entrySet()) {
            Box box = entry.getKey();
            if (box.getGoalLocation() == null) {
                unassignedBoxes.add(box);
                continue;
            }

            Agent bestAgent = findNearestAgentWithLoad(box, entry.getValue(), agentLoads);
            agentBoxNum.merge(bestAgent, 1, Integer::sum);

            MetaAgentPlan bestAgentPlan = agentToMetaPlanMap.get(bestAgent);
            bestAgentPlan.addBox(box);
        }

        for (Box box : unassignedBoxes) {
            Agent bestAgent = null;
            int minCount = Integer.MAX_VALUE;

            for (Map.Entry<Agent, Integer> entry : agentBoxNum.entrySet()) {
                if (entry.getKey().getColor().equals(box.getColor())) {
                    if (entry.getValue() < minCount) {
                        minCount = entry.getValue();
                        bestAgent = entry.getKey();
                    }
                }
            }

            if (bestAgent != null) {
                agentBoxNum.merge(bestAgent, 1, Integer::sum);
                MetaAgentPlan bestAgentPlan = agentToMetaPlanMap.get(bestAgent);
                bestAgentPlan.addBox(box);
            }
        }

    }

    private static Agent findNearestAgentWithLoad(Box box, Map<Agent, Integer> agent2Distance, Map<Agent, Integer> agentLoads) {
        int bestCost = Integer.MAX_VALUE;
        Agent bestAgent = null;

        for (Map.Entry<Agent, Integer> agentDistance : agent2Distance.entrySet()) {
            int agentLoad = agentLoads.getOrDefault(agentDistance.getKey(), 0);
            int totalCost = agentDistance.getValue() + agentLoad;
            if (totalCost < bestCost) {
                bestCost = totalCost;
                bestAgent = agentDistance.getKey();
            }
        }

        if (bestAgent == null) {
            throw new RuntimeException("Box " + box + " cannot be assigned — no agent can reach it.");
        }
        agentLoads.put(bestAgent, bestCost);
        return bestAgent;
    }

    private static void assignedGoal2Box(Environment environment) {
        for (LowLevelColorGroup colorGroup : environment.getColorGroups().values()) {
            Map<Box, Map<Location, Integer>> boxToReachableGoals = new HashMap<>();

            //  1.检查每个box到goal的可达，并记录box-goal-距离表
            for (Box box : colorGroup.getBoxes()) {
                char type = box.getBoxTypeLetter();
                Map<Location, Boolean> goalList = environment.getBoxType2GoalMap().get(type);
                if (goalList == null || goalList.isEmpty()) {
                    continue;
                }
                Location boxLoc = box.getInitLocation();
                Map<Location, AStarReachabilityChecker.ReachableResult> boxCanReachGoals = environment.getCostMap().get(boxLoc);
                for (Map.Entry<Location, Boolean> goalInfo : goalList.entrySet()) {
                    if (!goalInfo.getValue()) {
                        // 只处理没有分配掉的goal即可
                        Location goalLoc = goalInfo.getKey();
                        AStarReachabilityChecker.ReachableResult result = boxCanReachGoals.get(goalLoc);
                        if (result != null && result.isReachable()) {
                            boxToReachableGoals.computeIfAbsent(box, k -> new HashMap<>()).put(goalLoc, result.getSteps());
                        }
                    }
                }
            }

            //在检查之后，发现不需要进行box到goal的分配。则进入下一组处理
            if (boxToReachableGoals.isEmpty()) {
                continue;
            }

            //3.为box分配最短路径的goal
            Set<Location> assignedGoals = new HashSet<>();

            List<Box> sortedBoxes = boxToReachableGoals.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().size())).map(Map.Entry::getKey).toList();

            for (Box box : sortedBoxes) {
                Map<Location, Integer> reachableGoals = boxToReachableGoals.get(box);
                Location bestGoal = null;
                int minSteps = Integer.MAX_VALUE;

                for (Map.Entry<Location, Integer> goalEntry : reachableGoals.entrySet()) {
                    Location goal = goalEntry.getKey();
                    int steps = goalEntry.getValue();

                    if (steps < minSteps && !assignedGoals.contains(goal)) {
                        minSteps = steps;
                        bestGoal = goal;
                    }
                }

                //如果找到了bestGoal, 且goal没有被分配
                Boolean assigned = environment.getBoxType2GoalMap().get(box.getBoxTypeLetter()).get(bestGoal);
                if (bestGoal != null && !assigned) {
                    box.setGoalLocation(bestGoal);
                    environment.getBoxType2GoalMap().get(box.getBoxTypeLetter()).put(bestGoal, Boolean.TRUE);

                    assignedGoals.add(bestGoal);
                }
//                else {
//                    System.err.printf("There are unreachable goals[%s] for box: %s, or goal been assigned: %s\n", bestGoal != null ? "Y" : "N", box, assigned);
//                }
            }
        }
    }

    private static void doPreprocessing(Environment environment, Map<Agent, MetaAgentPlan> agentToMetaPlanMap, Map<Box, Map<Agent, Integer>> box2Agent, List<MetaAgentPlan> agentPlanList) {
        boolean doItAgain;
        do {
            doItAgain = false;
            agentPlanList.clear();
            agentToMetaPlanMap.clear();
            box2Agent.clear();

            for (LowLevelColorGroup colorGroup : environment.getColorGroups().values()) {
                //1. 每个agent初始化设置为一个plan
                for (Agent agent : colorGroup.getAgents()) {
                    Map<Character, Agent> agents = new HashMap<>();
                    agents.put(agent.getAgentId(), agent);
                    MetaAgentPlan metaAgentPlan = new MetaAgentPlan(agents);
                    agentToMetaPlanMap.put(agent, metaAgentPlan);
                    agentPlanList.add(metaAgentPlan);
                }

                //2. If this group don't have box, skip
                if (colorGroup.getBoxes().isEmpty()) {
                    continue;
                }

                //2. 检查agent->box的可达性.并更新环境的wall设置
                if (!checkAgent2Box(colorGroup, box2Agent, environment)) {
                    doItAgain = true;
                    break;
                }
            }
        } while (doItAgain);
    }

    /**
     * return true if all boxes are reachable by at least one agent
     * return false if there are unreachable boxes
     *
     * @param colorGroup
     * @param environment
     * @return
     */
    private static boolean checkAgent2Box(LowLevelColorGroup colorGroup, Map<Box, Map<Agent, Integer>> box2Agent, Environment environment) {
        List<Agent> agents = colorGroup.getAgents();
        List<Box> boxes = colorGroup.getBoxes();

        for (Box box : boxes) {
            Location boxLoc = box.getInitLocation();
            for (Agent agent : agents) {
                Location agentLoc = agent.getInitLocation();
                AStarReachabilityChecker.ReachableResult result = AStarReachabilityChecker.reachable(agentLoc, boxLoc, environment);
                if (result.isReachable()) {
                    box2Agent.computeIfAbsent(box, k -> new HashMap<>()).put(agent, result.getSteps());
                }
            }
        }

        Set<Box> reachableBoxes = box2Agent.keySet();
        List<Box> unreachableBoxes = new ArrayList<>(boxes);
        unreachableBoxes.removeIf(reachableBoxes::contains);

        if (unreachableBoxes.isEmpty()) {
            return true;
        }

        for (Box box : unreachableBoxes) {
            Location loc = box.getInitLocation();
            //设置为wall.并且不要在colorGroup中了。否被会被分配
            environment.setWallAt(loc);
            colorGroup.getBoxes().remove(box);

            Map<Location, Boolean> goalLocations = environment.getBoxType2GoalMap().get(box.getBoxTypeLetter());
            if (goalLocations == null) {
                continue;
            }
            Boolean distributed = goalLocations.get(loc);
            if (distributed != null) {
                box.setGoalLocation(loc);
                goalLocations.put(loc, Boolean.TRUE);
            }
        }
        return false;
    }

    private static void doCheckGoalReachability(Environment environment) {
        for (Map<Location, Boolean> singleGroup : environment.getBoxType2GoalMap().values()) {
            for (Boolean assigned : singleGroup.values()) {
                if (!assigned) {
                    throw new RuntimeException("There are unreachable goals");
                }
            }
        }
    }

}
