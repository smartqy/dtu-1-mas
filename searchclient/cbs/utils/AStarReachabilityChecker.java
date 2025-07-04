package searchclient.cbs.utils;

import searchclient.cbs.model.Environment;
import searchclient.cbs.model.Location;

import java.util.*;

public class AStarReachabilityChecker {

    private static class Node {
        Location loc;
        int g;

        Node(Location loc, int g) {
            this.loc = loc;
            this.g = g;
        }
    }

    public static class ReachableResult {
        boolean isReachable;
        int steps;

        public ReachableResult(boolean isReachable, int steps) {
            this.isReachable = isReachable;
            this.steps = steps;
        }

        public boolean isReachable() {
            return isReachable;
        }

        public int getSteps() {
            return steps;
        }

        @Override
        public String toString() {
            return "ReachableResult{" +
                    "isReachable=" + isReachable +
                    ", step=" + steps +
                    "}";
        }
    }

    /**
     * A* search to determine whether two points are reachable.
     *
     * @param start starting location (agent start)
     * @param goal  target location (box location)
     * @param env   environment with wall info
     * @return ReachableResult include whether reachable and the whole cost
     */
    public static ReachableResult reachable(Location start, Location goal, Environment env) {
        if (start.equals(goal)) {
            return new ReachableResult(true, 0);
        }

        PriorityQueue<Node> frontier = new PriorityQueue<>(
                Comparator.comparingInt(n -> n.g + (Math.abs(goal.getRow() - n.loc.getRow()) + Math.abs(goal.getCol() - n.loc.getCol()))));
        Set<Location> visited = new HashSet<>();

        frontier.add(new Node(start, 0));
        visited.add(start);

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();
            if (current.loc.equals(goal)) {
                return new ReachableResult(true, current.g);
            }
            if (current.g > 20000) {
                //the step limitation to avoid a dead loop
                return new ReachableResult(false, -1);
            }

            for (Location neighbor : getNeighbors(current.loc, env)) {
                if (!visited.contains(neighbor)) {
                    frontier.add(new Node(neighbor, current.g + 1));
                    visited.add(neighbor); // Add here to avoid duplicates
                }
            }
        }

        return new ReachableResult(false, -1);
    }

    private static List<Location> getNeighbors(Location loc, Environment env) {
        List<Location> neighbors = new ArrayList<>();
        int[] dr = {-1, 1, 0, 0}; // up, down
        int[] dc = {0, 0, -1, 1}; // left, right

        for (int i = 0; i < 4; i++) {
            int nr = loc.getRow() + dr[i];
            int nc = loc.getCol() + dc[i];

            if (nr < 0 || nr >= env.getGridNumRows() || nc < 0 || nc >= env.getGridNumCol()) {
                continue;
            }

            Location next = new Location(nr, nc);
            if (!env.isWall(next)) {
                neighbors.add(next);
            }
        }
        return neighbors;
    }
}
