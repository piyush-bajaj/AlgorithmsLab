package de.piyush.shipnavigation.algorithms.dijkstra;

import java.util.*;

public class Dijkstra {

    private Graph graph;
    private int distance[];
    private HashSet<Node> visited;
    private HashSet<Node> queue;
    private Map<Node, Node> predecessors;

    public Dijkstra(Graph graph) {
        super();
        this.graph = graph;

    }

    public int dist(Node from, Node to) {

        visited = new HashSet<Node>();
        queue = new HashSet<Node>();
        this.distance = new int[graph.getNodes().size()];
        Arrays.fill(distance, Integer.MAX_VALUE);
        predecessors = new HashMap<Node, Node>();

        queue.add(from);
        distance[from.getId()] = 0;

        while (queue.size() > 0) {
            Node node = minimumVertex(queue);
            queue.remove(node);
            visited.add(node);
            findMinimalDistances(node);
        }
        LinkedList<Node> path = getPath(to);
        for (Node node : path) {
            System.out.println(node);
        }

        return 0;
    }

    private void findMinimalDistances(Node node) {
        List<Node> adjacentNodes = getNeighbors(node);
        for (Node target : adjacentNodes) {
            int alternate_distance = getShortestDistance(node) + getDistance(node, target);
            if (getShortestDistance(target) > alternate_distance) {
                distance[target.getId()] = alternate_distance;
                predecessors.put(target, node);
                queue.add(target);
            }
        }

    }

    private Node minimumVertex(HashSet<Node> queue) {
        Node minimum = null;
        for (Node node : queue) {
            if (minimum == null) {
                minimum = node;
            } else {
                if (getShortestDistance(node) < getShortestDistance(minimum)) {
                    minimum = node;
                }
            }
        }
        return minimum;
    }

    private int getShortestDistance(Node destination) {
        int d = distance[destination.getId()];
        return d;
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<Node>();
        for (Edge edge : graph.getOutgoingEdges(node)) {
            if (edge.getFrom().equals(node) && !isVisited(edge.getTo())) {
                neighbors.add(edge.getTo());
            }
        }
        return neighbors;
    }

    private boolean isVisited(Node node) {
        return visited.contains(node);
    }

    private int getDistance(Node node, Node target) {
        for (Edge edge : graph.getOutgoingEdges(node)) {
            if (edge.getFrom().equals(node)
                    && edge.getTo().equals(target)) {
                return edge.getDist();
            }
        }
        throw new RuntimeException("No edge present");
    }


    private LinkedList<Node> getPath(Node target) {
        LinkedList<Node> path = new LinkedList<Node>();
        Node step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }
}