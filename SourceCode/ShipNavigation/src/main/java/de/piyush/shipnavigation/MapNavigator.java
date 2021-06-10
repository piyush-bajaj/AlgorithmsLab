package de.piyush.shipnavigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.piyush.shipnavigation.algorithms.dijkstra.DijkstraPriority;
import de.piyush.shipnavigation.algorithms.dijkstra.Edge;
import de.piyush.shipnavigation.algorithms.dijkstra.Graph;
import de.piyush.shipnavigation.algorithms.dijkstra.Node;

public class MapNavigator {
	

	static List<Node> nodes = new ArrayList<Node>();
	static List<Edge> edges = new ArrayList<Edge>();
	

	public static void main(String[] args) throws FileNotFoundException {
		
		DijkstraPriority dijkstra = setupDijkstra();
		
		//-16.559999999999892,-0.3599999999982224
		//89.0,-124.0
		double sourceLat = -83.46, sourceLng =90.0; //lat,lng of starting point
		double destLat = -86.25, destLng = 18.57;//lat,lng of ending point
		
		Node srcNode = setupSource(sourceLat, sourceLng);
		Node destNode = setupDestination(destLat, destLng); 
        
        if(srcNode != null && destNode != null) {
        	int dist = dijkstra.dist(srcNode, destNode);
        	dijkstra.printPath(destNode);
        }
        else {
        	System.out.println("Path does not exits select correct nodes");
        }
        
	}

	private static Node setupSource(double lat, double lng) {

		double d_phi = 180.0 / 200; //change in lat
        double d_lambda = 360.0 / 500; //change in lng
        
		int mSource = (int) Math.round(Math.abs(lat-90.0) / d_phi); //getting closest lat in grid
        int nSource = (int) Math.round(Math.abs(lng-180.0) / d_lambda);//getting closest lng in grid
		
        lat = new BigDecimal(90 - (mSource * d_phi)).setScale(7, RoundingMode.HALF_UP).doubleValue(); //source lat to search in graph in the range -90 to 90
        lng = new BigDecimal(180 - (nSource * d_lambda)).setScale(7, RoundingMode.HALF_UP).doubleValue(); //source lng to search in graph in range -180 to 180 
       
        int idx = nodes.indexOf(new Node(0, lat, lng));
        
        return idx != -1 ? nodes.get(idx) : null;
        
	}
	
	private static Node setupDestination(double lat, double lng) {

		double d_phi = 180.0 / 200; //change in lat
        double d_lambda = 360.0 / 500; //change in lng
        
        int mDest = (int) Math.round(Math.abs(lat-90.0) / d_phi); //getting closest lat in grid
        int nDest = (int) Math.round(Math.abs(lng-180.0) / d_lambda);//getting closest lng in grid
		
        lat = 90 - (mDest * d_phi); //source lat to search in graph in the range -90 to 90
        lng = 180 - (nDest * d_lambda); //source lng to search in graph in range -180 to 180 
        
        int idx = nodes.indexOf(new Node(0, lat, lng));
        
        return idx != -1 ? nodes.get(idx) : null;
	}

	private static DijkstraPriority setupDijkstra() {
		DijkstraPriority dijkstraPriority = null;
		
		try {
			Scanner s = new Scanner(new File("NodesEdges.txt"));
			int numNodes = Integer.parseInt(s.nextLine());
			String line[];
			Node n1, n2;
			for(int i=0;i<numNodes;i++) {
				line = s.next().split(",");
				n1 = new Node(Integer.parseInt(line[0]), Double.parseDouble(line[1]), Double.parseDouble(line[2]));
				nodes.add(n1);
			}
			int numEdges = Integer.parseInt(s.next());
			Edge edge;
			for(int i=0;i<numEdges;i++) {
				line = s.next().split(",");
				n1 = new Node(Integer.parseInt(line[1]), Double.parseDouble(line[2]), Double.parseDouble(line[3]));
				n2 = new Node(Integer.parseInt(line[4]), Double.parseDouble(line[5]), Double.parseDouble(line[6]));
				
				edge = new Edge(Integer.parseInt(line[0]), n1, n2, Integer.parseInt(line[7]));
				edges.add(edge);
			}
			
			Graph g = new Graph(nodes, edges);
	
			dijkstraPriority = new DijkstraPriority(g);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return dijkstraPriority;
	}

}
