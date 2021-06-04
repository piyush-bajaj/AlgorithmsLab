package de.piyush.shipnavigation.algorithms.dijkstra;

public class Edge {
    private int id;
    private Node from;
    private final Node to;
    private final int dist;

    public Edge(int id, Node from, Node to, int dist) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.dist = dist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public int getDist() {
        return dist;
    }

    @Override
    public String toString() {
        return id + "," + from.getId() + "," + from.getLat() + "," + from.getLng() + "," + to.getId() + "," + to.getLat() + "," + to.getLng() + "," + dist;
    }
}