package searchclient.cbs.model;

import java.util.*;

/**
 * Model for CBS
 * For high level of CT
 */
public class Solution {

    private final Map<String, MetaAgentPlan> metaPlans = new TreeMap<>();
    private boolean valid = true;
    private int maxMetaPath = 0;

    public MetaAgentPlan getPlanForAgent(String metaId) {
        return this.metaPlans.get(metaId);
    }

    public Solution deepCopy() {
        Solution solution = new Solution();
        for (Map.Entry<String, MetaAgentPlan> entry : this.metaPlans.entrySet()) {
            String metaId = entry.getKey();
            MetaAgentPlan metaAgentPlan = entry.getValue().deepCopy();
            solution.addMetaAgentPlan(metaId, metaAgentPlan);
        }
        solution.setValid(this.valid);
        return solution;
    }


    public Map<String, MetaAgentPlan> getMetaPlans() {
        return metaPlans;
    }

    public int getMaxMetaPath() {
        return maxMetaPath;
    }

    public void addMetaAgentPlan(String metaId, MetaAgentPlan metaAgentPlan) {
        if (metaAgentPlan.getMaxMoveSize() > this.maxMetaPath) {
            this.maxMetaPath = metaAgentPlan.getMaxMoveSize();
        }
        this.metaPlans.put(metaId, metaAgentPlan);
    }

    public void updateMaxSinglePath() {
        int newMaxSinglePath = 0;
        for (MetaAgentPlan metaAgentPlan : this.metaPlans.values()) {
            if (metaAgentPlan.getMaxMoveSize() > newMaxSinglePath) {
                newMaxSinglePath = metaAgentPlan.getMaxMoveSize();
            }
        }
        this.maxMetaPath = newMaxSinglePath;
    }

    public List<MetaAgentPlan> getMetaPlansInOrder() {
        return new ArrayList<>(this.metaPlans.values());
    }

    @Override
    public String toString() {
        return "Solution{" + "agentPlans=" + metaPlans + ", valid=" + valid + ", maxSinglePath=" + maxMetaPath + '}';
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(metaPlans);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Solution solution = (Solution) obj;
        return Objects.deepEquals(metaPlans, solution.metaPlans);
    }
}
