package de.piyush.shipnavigation.algorithms.dijkstra.datastructures;

import de.piyush.shipnavigation.algorithms.dijkstra.Node;

public class HeapElement {

    private int distance;
    private Node node;
    private Node prev_node;

    public HeapElement(int distance, Node from, Node to) {
        this.distance = distance;
        this.node = from;
        this.prev_node = to;
    }

    public int getDistance() {
        return distance;
    }

    public Node getNode() {
        return node;
    }

    public Node getPrev_node() {
        return prev_node;
    }
}
