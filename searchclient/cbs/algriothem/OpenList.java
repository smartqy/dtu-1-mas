package searchclient.cbs.algriothem;

import searchclient.Frontier;
import searchclient.cbs.model.Node;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class OpenList implements Frontier<Node> {

    private final PriorityQueue<Node> queue;
    private final Set<Node> visited = new HashSet<Node>();

    public OpenList() {
        this.queue = new PriorityQueue<>(Node::compareTo);
    }

    @Override
    public void add(Node n) {
        this.queue.add(n);
        this.visited.add(n);
    }

    public Node pop() {
        return this.queue.remove();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public boolean contains(Node node) {
        return this.visited.contains(node);
    }

    @Override
    public boolean hasNotVisited(Node item) {
        return !this.visited.contains(item);
    }

    @Override
    public String getName() {
        return "OpenList for CBS Conflict Tree";
    }

}
