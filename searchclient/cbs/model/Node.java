package searchclient.cbs.model;

import java.util.Comparator;
import java.util.Objects;

/**
 * Model for CBS
 * Node in the conflict tree
 */
public class Node implements Comparable<Node> {

    /**
     * Every node is created for solving a selectedConflict with a specific constraint except the root one
     * As the paper analysis in 4.2.4
     */
    private AbstractConflict selectedConflict;
    /**
     * The constraint that was added in this node as one way to solve the selectedConflict{@link #selectedConflict}
     * (without constraints from {@link #parent}).
     */
    private Constraint addedConstraint;
    /**
     * A Set of k paths, one path for each agent.
     * The path for agent a_i is consistent with the constraints of a_i
     */
    private Solution solution;

    private final Node parent;

    public Node(Node parent) {
        this.parent = parent;
    }

    public Node(Node parent, AbstractConflict selectedConflict, Constraint addedConstraint) {
        this.parent = parent;
        this.selectedConflict = selectedConflict;
        this.addedConstraint = addedConstraint;
    }

    /**
     * the cost of Solution is the sum cost of every agent
     *
     * @return
     */
    public int getSolutionCost() {
        int cost = 0;
        for (MetaAgentPlan metaAgentPlan : solution.getMetaPlansInOrder()) {
            cost += metaAgentPlan.getCost();
        }
        return cost;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public Solution getSolution() {
        return solution;
    }

    public Node getParent() {
        return parent;
    }

    public Constraint getAddedConstraint() {
        return addedConstraint;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return Objects.equals(selectedConflict, node.selectedConflict) &&
                Objects.equals(addedConstraint, node.addedConstraint) &&
                Objects.equals(solution, node.solution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedConflict, addedConstraint, solution);
    }


    @Override
    public int compareTo(Node o) {
        return Objects.compare(this, o, Comparator.comparing(Node::getSolutionCost));
    }
}
