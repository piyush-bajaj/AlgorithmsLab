package de.piyush.shipnavigation.algorithms.dijkstra;

import de.piyush.shipnavigation.algorithms.dijkstra.datastructures.HeapElement;

import java.util.*;

public class DijkstraPriority {

    private Graph graph;
    private int distance[];
    private Node prev[];
    private PriorityQueue<HeapElement> heap;
    private List<Node> visited;

    public DijkstraPriority(Graph g) {
        this.graph = g;
        distance = new int[g.getNodes().size()];
        Arrays.fill(distance, Integer.MAX_VALUE);
        prev = new Node[g.getNodes().size()];
        heap = new PriorityQueue<HeapElement>(new Comparator<HeapElement>() {
            @Override
            public int compare(HeapElement element1, HeapElement element2) {
                return element1.getDistance() - element2.getDistance();
            }
        });
        visited = new ArrayList<Node>();
    }
    
    public void printPath(Node target) {
    	System.out.println("Path is as follows :");
    	LinkedList<Node> path = new LinkedList<Node>();
    	Node step = target;
    	
    	// check if a path exists
        if (prev[step.getId()] == null) {
            System.out.println("Does not exist");
        }
        
        path.add(step);
        
        while (prev[step.getId()] != null && prev[step.getId()] != step) {
        	step = prev[step.getId()];
            path.add(prev[step.getId()]);
        }
        // Put it into the correct order
        Collections.reverse(path);
        for(Node node : path) {
        	System.out.println("Point [" + node.getLat() + "," + node.getLng() + "]");
        }
    }
    
    public List<Node> getPath(Node target) {
    	LinkedList<Node> path = new LinkedList<Node>();
    	Node step = target;
    	
    	// check if a path exists
        if (prev[step.getId()] == null) {
            return path;
        }
        
        path.add(step);
        
        while (prev[step.getId()] != null && prev[step.getId()] != step) {
        	step = prev[step.getId()];
            path.add(prev[step.getId()]);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

    public int dist(Node from, Node to) {

        reset_state();

        heap.add(new HeapElement(0, from, from));

        HeapElement element = heap.poll();
        while(element != null) {
            //if the heap does not support a decrease key operation, we can include nodes multiple times and with the following condition ensure that each is only processes once.
            //This is also said to perform better than decrease key --> using the method peek for example
            if(element.getDistance() >= distance[element.getNode().getId()]) {
            	element = heap.poll();
                continue;
            }

            distance[element.getNode().getId()] = element.getDistance();
            prev[element.getNode().getId()] = element.getPrev_node();
            visited.add(element.getNode());

            for(Edge edge : graph.getOutgoingEdges(element.getNode())) {
                int alternate_distance = element.getDistance() + edge.getDist();
                if(alternate_distance < distance[edge.getTo().getId()]) {
                    heap.add(new HeapElement(alternate_distance, edge.getTo(), element.getNode()));
                }
            }

            //stopping when the target node is reached
            //after adding all edges in the heap, since we want to continue from the old heap data
            if(element.getNode() == to) {
                return element.getDistance();
            }

            element = heap.poll();
        }
        return distance[to.getId()];
    }

    /**
     * reset only the variables that were changed
     */
    private void reset_state() {
        for(Node node: visited) {
            this.distance[node.getId()] = Integer.MAX_VALUE;
            this.prev[node.getId()] = null;
        }
        this.heap.clear();
        this.visited.clear();
    }
}
