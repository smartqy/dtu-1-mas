package searchclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class GraphSearch {

    public static Action[][] search(State initialState, Frontier<State> frontier) {
        boolean outputFixedSolution = false;

        if (outputFixedSolution) {
            //Part 1:
            //The agents will perform the sequence of actions returned by this method.
            //Try to solve a few levels by hand, enter the found solutions below, and run them:

            //hardcode for MAPF00.lvl
            return new Action[][]{{Action.MoveS}, {Action.MoveS}, {Action.MoveE}, {Action.MoveE}, {Action.MoveE}, {Action.MoveE}, {Action.MoveE}, {Action.MoveE}, {Action.MoveE}, {Action.MoveE}, {Action.MoveE}, {Action.MoveE}, {Action.MoveS}, {Action.MoveS},};
        } else {
            //Part 2:
            //Now try to implement the Graph-Search algorithm from R&N figure 3.7
            //In the case of "failure to find a solution" you should return null.
            //Some useful methods on the state class which you will need to use are:
            //state.isGoalState() - Returns true if the state is a goal state.
            //state.extractPlan() - Returns the Array of actions used to reach this state.
            //state.getExpandedStates() - Returns an ArrayList<State> containing the states reachable from the current state.
            //You should also take a look at Frontier.java to see which methods the Frontier interface exposes
            //
            //printSearchStatus(expanded, frontier): As you can see below, the code will print out status
            //(#expanded states, size of the frontier, #generated states, total time used) for every 10000th node generated.
            //You should also make sure to print out these stats when a solution has been found, so you can keep
            //track of the exact total number of states generated!!


            int iterations = 0;

            frontier.add(initialState);
            HashSet<State> expanded = new HashSet<>();

            while (true) {

                //Print a status message every 10000 iteration
                if (++iterations % 10000 == 0) {
                    printSearchStatus(expanded, frontier);
                }

                //Your code here... Don't forget to print out the stats when a solution has been found (see above)
                if (frontier.size() == 0) {
                    //failure
                    return null;
                }

                State current = frontier.pop();
                if (current.isGoalState()) {
                    System.out.println("#Finish-total-info");
                    printSearchStatus(expanded, frontier);

                    return current.extractPlan();
                } else {
                    expanded.add(current);
                }

                //now, to get child from current
                for (State child : current.getExpandedStates()) {
                    if (!frontier.contains(child) && !expanded.contains(child)) {
                        frontier.add(child);
                    }
                }
            }
        }
    }

    private static long startTime = System.nanoTime();

    private static void printSearchStatus(HashSet<State> expanded, Frontier frontier) {
        long current = System.nanoTime();
        double elapsedTime = (current - startTime) / 1_000_000_000d;
//        System.out.printf("#now nano:%,d ; second string:%s; second float:%3.3f s\n", current - startTime, elapsedTime, elapsedTime);
        String statusTemplate = "#Expanded: %,8d, #Frontier: %,8d, #Generated: %,8d, Time: %3.3f s\n%s\n";
        System.err.format(statusTemplate, expanded.size(), frontier.size(), expanded.size() + frontier.size(), elapsedTime, Memory.stringRep());
    }
}
