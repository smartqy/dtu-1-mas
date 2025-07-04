package searchclient.cbs.algriothem;

import searchclient.TimeoutException;
import searchclient.cbs.model.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AStarRunner {

    private final long startTime;
    private final long timeoutLimit;
    private boolean abortedForTimeout = false;

    public AStarRunner(long startTime, long timeout) {
        this.startTime = startTime;
        this.timeoutLimit = timeout;
    }

    /**
     * Low-level
     * find path for single agent
     *
     * @param currentNode
     * @param metaAgentPlan
     * @return
     */
    public boolean findPath(Node currentNode, MetaAgentPlan metaAgentPlan, boolean allInOne) {

        boolean findPath = false;
        LowLevelState initState = LowLevelState.initRootStateForPlan(metaAgentPlan);
        initState.setAllInOne(allInOne);
        AStarFrontier frontier = new AStarFrontier();
        frontier.add(initState);

        while (!frontier.isEmpty() && !checkTimeout()) {
            LowLevelState currentState = frontier.pop();
            if (currentState.isGoal()) {
                metaAgentPlan.update2Final(currentState);
//                System.err.printf("#Finish-Lower-level, MetaId=%s , size=%d, VisitedSize=%,8d\n", metaAgentPlan.getMetaId(), metaAgentPlan.getCost(),
//                        frontier.getVisitedSize());
                findPath = true;
                break;
            }

//            printDetail(frontier, metaAgentPlan, currentState);

            Environment environment = AppContext.getEnv();
            if (environment.isEPEA()) {
                doEPEA(currentState, frontier, currentNode);
            } else {
                for (LowLevelState child : currentState.expand(currentNode)) {
                    if (frontier.hasNotVisited(child)) {
                        frontier.add(child);
                    }
                }
            }
        }
        return findPath;
    }

    private void printDetail(AStarFrontier frontier, MetaAgentPlan metaAgentPlan, LowLevelState currentState) {
        if (frontier.getVisitedSize() % 10000 == 0) {
            double elapsedTime = (System.currentTimeMillis() - this.startTime) / 1_000d;
            System.err.printf("#Current-Lower-level, MetaId=%s , VisitedSize=%,4d, FrontierReminderSize=%,4d, time cost=%3.3f s, steps=%d\n",
                    metaAgentPlan.getMetaId(), frontier.getVisitedSize(), frontier.size(), elapsedTime, currentState.timeNow);

//            if (currentState.timeNow > 50) {
//                LowLevelState current = currentState;
//                while (current != null) {
//                    System.err.printf("Current State, timeNow=%d\n", current.timeNow);
//                    for (Map.Entry<Character, Move> item : current.getAgentMove().entrySet()) {
//                        System.err.printf("Agent:[%s],Action: [%s] at [%s],Box:[%s]-[%s]\n", item.getValue().getAgent().getAgentId(), item.getValue().getAction().name
//                                , item.getValue().getAgent().getCurrentLocation().toString()
//                                , item.getValue().getBox() == null ? "" : item.getValue().getBox().getUniqueId()
//                                , item.getValue().getBox() == null ? "" : item.getValue().getBox().getCurrentLocation().toString());
//                    }
//                    current = current.getParent();
//                }
//            }
        }
    }

    private void doEPEA(LowLevelState currentState, AStarFrontier frontier, Node currentNode) {
        List<LowLevelState> children = currentState.expand(currentNode);
        if (!children.isEmpty()) {
            //1. 子节点按照 getPrioirity 的返回值排序
            children.sort(Comparator.comparingDouble(LowLevelState::getPriority));
            //2. 只插入getPrioirity 最小的几个节点
            double minPriority = children.get(0).getPriority();
            if (!currentState.isDefaultFValue()) {
                minPriority = currentState.getPriority();
            }
            for (LowLevelState child : children) {
                double childPriority = child.getPriority();
                if (childPriority < minPriority) {
                    //已经入过队了
                    continue;
                } else if (childPriority == minPriority) {
                    if (frontier.hasNotVisited(child)) {
                        frontier.add(child);
                    }
                } else {
                    currentState.setfValue(childPriority);
                    if (frontier.hasNotVisited(currentState)) {
                        frontier.add(currentState);
                    }
                    break;
                }
            }
        }
    }

    private boolean checkTimeout() {
        if (System.currentTimeMillis() - this.startTime > this.timeoutLimit) {
            this.abortedForTimeout = true;
            throw new TimeoutException("Low-level Multibody A* Timeout");
        }
        return false;
    }

    public boolean isAbortedForTimeout() {
        return abortedForTimeout;
    }
}
