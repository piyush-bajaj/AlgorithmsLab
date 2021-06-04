package de.piyush.shipnavigation.algorithms.dijkstra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Graph {

    private final List<Node> nodes;
    private final List<Edge> edges;
    private int offset[];

    public Graph(List<Node> nodes, List<Edge> edges) {
        sort(nodes, edges);

        this.offset = new int[nodes.size()+1];

        for(int i=0; i < edges.size(); i++) {
            edges.get(i).setId(i);
            this.offset[edges.get(i).getFrom().getId() + 1] = this.offset[edges.get(i).getFrom().getId() + 1] + 1;
        }

        for(int i=1;i<this.offset.length; i++) {
            this.offset[i] = this.offset[i] + this.offset[i-1];
        }

        this.edges = edges;
        this.nodes = nodes;

    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    void sort(List<Node> nodes, List<Edge> edges) {
        nodes.sort(new Comparator<Node>() {

            @Override
            public int compare(Node n1, Node n2) {
                return n1.getId() - n2.getId();
            }
        });

        edges.sort(new Comparator<Edge>() {

            @Override
            public int compare(Edge e1, Edge e2) {
                return e1.getFrom().getId() - e2.getFrom().getId();
            }
        });
    }

    public int node_count() {
        return this.nodes.size();
    }

    public Node node(int node_id) {
        return this.nodes.get(node_id);
    }

    public Edge edge(int edge_id) {
        return this.edges.get(edge_id);
    }

    public List<Edge> getOutgoingEdges(Node node) {
        List<Edge> edges = new ArrayList<Edge>();
        for(int i=this.offset[node.getId()];i<this.offset[node.getId()+1];i++) {
            Edge edge = this.edges.get(i);
            edges.add(edge);
        }
        return edges;
    }

}