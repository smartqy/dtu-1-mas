package searchclient.cbs.model;

import searchclient.Color;
import searchclient.cbs.utils.AStarReachabilityChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Environment {

    private boolean isEPEA = false;
    private final int gridNumRows;
    private final int gridNumCol;
    private final Map<Color, LowLevelColorGroup> colorGroups;
    private final Map<Character, Map<Location, Boolean>> boxType2GoalMap;
    private final boolean[][] WALLS;
    private int agentNums;
    private List<Location> goalCells;
    private List<Location> freeCells;
    private Map<Location, Map<Location, AStarReachabilityChecker.ReachableResult>> costMap;

    public Map<Location, Map<Location, AStarReachabilityChecker.ReachableResult>> getCostMap() {
        return costMap;
    }

    public void setCostMap(Map<Location, Map<Location, AStarReachabilityChecker.ReachableResult>> costMap) {
        this.costMap = costMap;
    }

    public static Environment parseLevel(BufferedReader serverMessages) throws IOException {
        // We can assume that the level file is conforming to specification, since the server verifies this.
        // Read domain
        serverMessages.readLine(); // #domain
        serverMessages.readLine(); // hospital

        // Read Level name
        serverMessages.readLine(); // #levelname
        serverMessages.readLine(); // <name>

        // Read colors
        serverMessages.readLine(); // #colors
        String line = serverMessages.readLine();

        Map<Character, Agent> agents = new HashMap<>();
        Map<Color, LowLevelColorGroup> colorGroupMap = new HashMap<>();
        Map<Character, Map<Location, Boolean>> boxType2GoalMap = new HashMap<>();
        Map<Character, Color> boxLetter2Color = new HashMap<>();
        List<Location> goalCells = new ArrayList<>();
        List<Location> freeCells = new ArrayList<>();

        while (!line.startsWith("#")) {
            String[] split = line.split(":");
            Color color = Color.fromString(split[0].strip());
            LowLevelColorGroup colorGroup = new LowLevelColorGroup(color);
            String[] entities = split[1].split(",");
            for (String entity : entities) {
                char c = entity.strip().charAt(0);
                if ('0' <= c && c <= '9') {
                    Agent agent = new Agent(c, color);
                    colorGroup.addAgent(agent);
                    agents.put(c, agent);
                } else if ('A' <= c && c <= 'Z') {
                    boxLetter2Color.put(c, color);
                }
            }
            colorGroupMap.put(colorGroup.getColor(), colorGroup);
            line = serverMessages.readLine();
        }

        // Read initial state
        // line is currently "#initial"
        int numRows = 0;
        int numCols = 0;
        ArrayList<String> levelLines = new ArrayList<>(64);
        line = serverMessages.readLine();
        while (!line.startsWith("#")) {
            levelLines.add(line);
            numCols = Math.max(numCols, line.length());
            ++numRows;
            line = serverMessages.readLine();
        }
        boolean[][] walls = new boolean[numRows][numCols];
        for (int row = 0; row < numRows; ++row) {
            line = levelLines.get(row);
            for (int col = 0; col < line.length(); ++col) {
                char c = line.charAt(col);

                Location initLoc = new Location(row, col);
                if ('0' <= c && c <= '9') {
                    Agent agent = agents.get(c);
                    agent.setInitLocation(initLoc);
                    agent.setCurrentLocation(initLoc);
                } else if ('A' <= c && c <= 'Z') {
                    Color color = boxLetter2Color.get(c);
                    Box box = new Box(c, color, initLoc);
                    box.setCurrentLocation(initLoc);
                    colorGroupMap.get(color).addBox(box);
                } else if (c == '+') {
                    walls[row][col] = true;
                }
            }
        }

        // Read goal state
        // line is currently "#goal"
        line = serverMessages.readLine();
        int row = 0;
        while (!line.startsWith("#")) {
            for (int col = 0; col < line.length(); ++col) {
                char c = line.charAt(col);
                Location goalLoc = new Location(row, col);

                if (c == '+') {
                    continue;
                }

                if ('0' <= c && c <= '9') {
                    Agent agent = agents.get(c);
                    agent.setGoalLocation(goalLoc);
                    goalCells.add(goalLoc);
                } else if ('A' <= c && c <= 'Z') {
                    boxType2GoalMap.computeIfAbsent(c, k -> new HashMap<>()).put(goalLoc, Boolean.FALSE);
                    goalCells.add(goalLoc);
                }

                freeCells.add(goalLoc);
            }
            ++row;
            line = serverMessages.readLine();
        }

        //preprocess delete agents or boxes which don't have init loc
        Iterator<Map.Entry<Color, LowLevelColorGroup>> colorGroupIterator = colorGroupMap.entrySet().iterator();
        while (colorGroupIterator.hasNext()) {
            Map.Entry<Color, LowLevelColorGroup> group = colorGroupIterator.next();
            group.getValue().getAgents().removeIf(agent -> agent.getInitLocation() == null);
            group.getValue().getBoxes().removeIf(box -> box.getInitLocation() == null);

            if (group.getValue().getAgents().isEmpty() && group.getValue().getBoxes().isEmpty()) {
                //1. remove the whole group if there isn't anything
                colorGroupIterator.remove();
            } else if (group.getValue().getAgents().isEmpty()) {
                //2. set the box as the wall if there isn't any agent for them, then remove the group
                for (Box box : group.getValue().getBoxes()) {
                    //make the location of boxes which haven't agent as a wall
                    walls[box.getInitLocation().getRow()][box.getInitLocation().getCol()] = true;

                    Map<Location, Boolean> goalLocations = boxType2GoalMap.get(box.getBoxTypeLetter());
                    if (goalLocations == null) {
                        continue;
                    }
                    Boolean distributed = goalLocations.get(box.getInitLocation());
                    if (distributed != null) {
                        box.setGoalLocation(box.getInitLocation());
                        goalLocations.put(box.getInitLocation(), Boolean.TRUE);
                    }

                }
                colorGroupIterator.remove();
            }
        }

        Environment env = new Environment(colorGroupMap, walls, numRows, numCols, boxType2GoalMap, agents.size(), goalCells, freeCells);
        return env;
    }

    public void calculateCostMap() {
        long start = System.currentTimeMillis();
        this.costMap = new HashMap<>();
        for (Location freeCell : this.freeCells) {
            for (Location goalCell : this.goalCells) {
                AStarReachabilityChecker.ReachableResult result = AStarReachabilityChecker.reachable(freeCell, goalCell, this);
                this.costMap.computeIfAbsent(freeCell, k -> new HashMap<>()).put(goalCell, result);
            }
        }
        System.err.println("Time to calculate cost map: " + (System.currentTimeMillis() - start) + "ms");
    }

    public Environment(Map<Color, LowLevelColorGroup> colorGroups, boolean[][] walls, int gridNumRows, int gridNumCol, Map<Character, Map<Location, Boolean>> boxType2GoalMap, int agentNums
            , List<Location> goalCells, List<Location> freeCells) {
        WALLS = walls;
        this.colorGroups = colorGroups;
        this.gridNumRows = gridNumRows;
        this.gridNumCol = gridNumCol;
        this.boxType2GoalMap = boxType2GoalMap;
        this.agentNums = agentNums;
        this.goalCells = goalCells;
        this.freeCells = freeCells;
    }

    public Environment() {
        gridNumRows = 0;
        gridNumCol = 0;
        colorGroups = new HashMap<>();
        boxType2GoalMap = new HashMap<>();
        WALLS = new boolean[0][0];
    }


    /**
     * @param location
     * @return return true if the location is a wall
     */
    public boolean isWall(Location location) {
        return WALLS[location.getRow()][location.getCol()];
    }

    public Map<Color, LowLevelColorGroup> getColorGroups() {
        return colorGroups;
    }

    public int getGridNumRows() {
        return gridNumRows;
    }

    public int getGridNumCol() {
        return gridNumCol;
    }

    @Override
    public String toString() {
        return "Environment{" + "gridNumRows=" + gridNumRows + ", gridNumCol=" + gridNumCol + ", colorGroups=" + colorGroups + ", boxType2GoalMap=" + boxType2GoalMap + ", WALLS=" + (WALLS == null ? null : WALLS.length) + ", agentNums=" + agentNums + '}';
    }

    public Map<Character, Map<Location, Boolean>> getBoxType2GoalMap() {
        return boxType2GoalMap;
    }

    public int getAgentNums() {
        return agentNums;
    }

    public void setWallAt(Location location) {
        WALLS[location.getRow()][location.getCol()] = true;
    }

    public boolean isEPEA() {
        return isEPEA;
    }

    public void setEPEA(boolean EPEA) {
        isEPEA = EPEA;
    }
}
