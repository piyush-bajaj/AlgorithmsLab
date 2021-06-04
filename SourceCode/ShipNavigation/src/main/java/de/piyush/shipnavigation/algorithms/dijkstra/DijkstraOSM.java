package de.piyush.shipnavigation.algorithms.dijkstra;

import java.util.Arrays;
import java.util.HashSet;

public class DijkstraOSM {

    private Graph graph;

    public DijkstraOSM(Graph g) {
        this.graph = g;
    }

    public int distance(Node from, Node to) {
        int dist[] = new int[graph.getNodes().size()];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Node [] prev = new Node[graph.getNodes().size()];
        HashSet<Node> q = new HashSet<Node>();

        q.add(from);
        dist[from.getId()] = 0;

        Node node = minimumVertex(q, dist);
        while(node != null) {
            q.remove(node);

            for(Edge edge : this.graph.getOutgoingEdges(node)) {
                int alternate_distance = dist[node.getId()] + edge.getDist();
                if(alternate_distance < dist[edge.getTo().getId()]) {
                    dist[edge.getTo().getId()] = alternate_distance;
                    prev[edge.getTo().getId()] = node;
                    q.add(edge.getTo());
                }
            }
            node = minimumVertex(q, dist);
        }
        return dist[to.getId()];
    }

    private Node minimumVertex(HashSet<Node> q, int[] dist) {
        Node min_node = null;
        int min_distance = Integer.MAX_VALUE;

        for(Node node : q) {
            int v_dist = dist[node.getId()];
            if(v_dist < min_distance) {
                min_node = node;
                min_distance = v_dist;
            }
        }

        return min_node;
    }
}
