package de.piyush.navigation;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.*;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.piyush.navigation.datatypes.Path;
import de.piyush.shipnavigation.Constants;
import de.piyush.shipnavigation.algorithms.dijkstra.DijkstraPriority;
import de.piyush.shipnavigation.algorithms.dijkstra.Edge;
import de.piyush.shipnavigation.algorithms.dijkstra.Graph;
import de.piyush.shipnavigation.algorithms.dijkstra.Node;

@RestController
public class Navigation {

	static List<Node> nodes = new ArrayList<Node>();
	static List<Edge> edges = new ArrayList<Edge>();
	DijkstraPriority dijkstraPriority = null;

	@CrossOrigin
	@GetMapping("/init")
	public void init() {

		try {

			Scanner s = new Scanner(new File("NodesEdges.txt"));
			int numNodes = Integer.parseInt(s.nextLine());
			String line[];
			Node n1, n2;
			for (int i = 0; i < numNodes; i++) {
				line = s.next().split(",");
				n1 = new Node(Integer.parseInt(line[0]), Double.parseDouble(line[1]), Double.parseDouble(line[2]));
				nodes.add(n1);
			}
			int numEdges = Integer.parseInt(s.next());
			Edge edge;
			for (int i = 0; i < numEdges; i++) {
				line = s.next().split(",");
				n1 = new Node(Integer.parseInt(line[1]), Double.parseDouble(line[2]), Double.parseDouble(line[3]));
				n2 = new Node(Integer.parseInt(line[4]), Double.parseDouble(line[5]), Double.parseDouble(line[6]));

				edge = new Edge(Integer.parseInt(line[0]), n1, n2, Integer.parseInt(line[7]));
				edges.add(edge);
			}

			Graph g = new Graph(nodes, edges);

			dijkstraPriority = new DijkstraPriority(g);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@CrossOrigin
	@GetMapping("/distance")
	public Path distance(
			@RequestParam(value = "sourceLat") String sourceLat,
			@RequestParam(value = "sourceLng") String sourceLng, 
			@RequestParam(value = "destLat") String destLat,
			@RequestParam(value = "destLng") String destLng) {
		
		int dist = 0;
		double points[][] = null;
		// -16.559999999999892,-0.3599999999982224
		// 89.0,-124.0
		double sLat = new BigDecimal(Double.parseDouble(sourceLat)).setScale(7, RoundingMode.HALF_UP).doubleValue();
		double sLng = new BigDecimal(Double.parseDouble(sourceLng)).setScale(7, RoundingMode.HALF_UP).doubleValue();
		double dLat = new BigDecimal(Double.parseDouble(destLat)).setScale(7, RoundingMode.HALF_UP).doubleValue();
		double dLng = new BigDecimal(Double.parseDouble(destLng)).setScale(7, RoundingMode.HALF_UP).doubleValue();
		Node srcNode = setupSource(sLat, sLng);
		Node destNode = setupDestination(dLat, dLng);

		if (srcNode != null && destNode != null) {
			dist = dijkstraPriority.dist(srcNode, destNode);
			// dijkstraPriority.printPath(destNode);
			List<Node> way = dijkstraPriority.getPath(destNode);
			points = new double[way.size()][2];
			way.stream().map(node -> { 
				double coordinates[] = {node.getLat(), node.getLng()};
				return coordinates;
			}).collect(Collectors.toList()).toArray(points);

			way.stream().map(node -> { 
				double coordinates[] = {node.getLng(), node.getLat()};
				return coordinates;
			}).collect(Collectors.toList()).toArray(points);
		} else {
			dist = -1;
			System.out.println("Path does not exits select correct nodes");
		}

		return new Path(dist, points);
	}

	private static Node setupSource(double lat, double lng) {

		double d_phi = Constants.toOSMDegrees(180.0) / 200; // change in lat
		double d_lambda = Constants.toOSMDegrees(360.0) / 500; // change in lng

		int mSource = (int) Math.round(Math.abs(lat - 90.0) / d_phi); // getting closest lat in grid
		int nSource = (int) Math.round(Math.abs(lng - 180.0) / d_lambda);// getting closest lng in grid

		lat = new BigDecimal(90 - (mSource * d_phi)).setScale(7, RoundingMode.HALF_UP).doubleValue(); // source lat to
																										// search in
																										// graph in the
																										// range -90 to
																										// 90
		lng = new BigDecimal(180 - (nSource * d_lambda)).setScale(7, RoundingMode.HALF_UP).doubleValue(); // source lng
																											// to search
																											// in graph
																											// in range
																											// -180 to
																											// 180

		int idx = nodes.indexOf(new Node(0, lat, lng));

		return idx != -1 ? nodes.get(idx) : null;

	}

	private static Node setupDestination(double lat, double lng) {

		double d_phi = Constants.toOSMDegrees(180.0) / 200; // change in lat
		double d_lambda = Constants.toOSMDegrees(360.0) / 500; // change in lng

		int mDest = (int) Math.round(Math.abs(lat - 90.0) / d_phi); // getting closest lat in grid
		int nDest = (int) Math.round(Math.abs(lng - 180.0) / d_lambda);// getting closest lng in grid

		lat = 90 - (mDest * d_phi); // source lat to search in graph in the range -90 to 90
		lng = 180 - (nDest * d_lambda); // source lng to search in graph in range -180 to 180

		int idx = nodes.indexOf(new Node(0, lat, lng));

		return idx != -1 ? nodes.get(idx) : null;
	}

}
