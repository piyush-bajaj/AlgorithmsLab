package de.piyush.shipnavigation.grid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.piyush.shipnavigation.Constants;
import de.piyush.shipnavigation.algorithms.dijkstra.Edge;
import de.piyush.shipnavigation.algorithms.dijkstra.Node;
import de.piyush.shipnavigation.algorithms.pip.Point;
import de.piyush.shipnavigation.algorithms.pip.PointInPolygon;
import de.piyush.shipnavigation.coastlines.Coastline;
import de.piyush.shipnavigation.osmartifacts.OSMDataSet;

public class Grid {

    private List<Coastline> coastlines;
    private OSMDataSet data;
    private List<Node> nodes;
    private List<Edge> edges;

    public Grid(List<Coastline> coastlines, OSMDataSet data) {
        this.coastlines = coastlines;
        this.data = data;
        nodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();
    }

    public List<Node> getNodes() {
        return this.nodes;
    }

    public List<Edge> getEdges() {
        return this.edges;
    }

    public void createSphericalGrid(int N) {
        int dNodeId = 0, dEdgeId = 0;
        List<Node> nodes = new ArrayList<Node>();
        List <Edge> edges = new ArrayList<Edge>();
        PointInPolygon pip = new PointInPolygon();

        double a = 4 * Math.PI * 6_371 * 6_371 / N;
        double d = Math.sqrt(a);
        long mPolar = Math.round(Math.PI * 6_371 / d);
        double dPolar = Math.PI * 6_371 / mPolar;
        double dAzimuth = a / dPolar;
        for (long m = 0; m < mPolar; m++) {

            double polar = Math.PI * (m + 0.5) / mPolar;
            long mAzimuth = Math.round(2 * Math.PI * 6_371 * Math.sin(polar) / dAzimuth);

            for (long n = 0; n < mAzimuth; n++) {

                double azimuth = 2 * Math.PI * n / mAzimuth;

                double x = 6_371 * Math.sin(polar) * Math.cos(azimuth);
                double y = 6_371 * Math.sin(polar) * Math.sin(azimuth);
                double z = 6_371 * Math.cos(polar);

                double lat = Constants.toOSMDegrees(Constants.toDegrees(Math.atan2(z, Math.sqrt(x * x + y * y))));
                double lng = Constants.toOSMDegrees(Constants.toDegrees(Math.atan2(y, x)));

                Node currNode = new Node(dNodeId, lat, lng);

                if (n < mAzimuth - 1) {
                    nodes.add(currNode);
                    dNodeId++;
                } else {
                    currNode = nodes.get(nodes.size() - (int) mAzimuth + 1);
                }

                //check if current node is in water
                if (pip.checkPoint(currNode.getLat(), currNode.getLng(), coastlines, data) == 0) {

                    //check if node to the left of the current node is in water, if yes make an edge from current node to left node
                    if (n != 0 && n != (mAzimuth - 1)) {
                        Node prevNode = nodes.get((int) (n - 1));

                        if (pip.checkPoint(prevNode.getLat(), prevNode.getLng(), coastlines, data) == 0) {
                            int dist = haversine_distance(currNode, prevNode);
                            Edge edge = new Edge(dEdgeId++, currNode, prevNode, dist);
                            edges.add(edge);
                            edge = new Edge(dEdgeId++, prevNode, currNode, dist);
                            edges.add(edge);
                        }
                    } else if (n == 0) {
                        double l_phi = 2 * Math.PI * (n - 1) / mAzimuth;
                        double l_x = 6_371 * Math.sin(polar) * Math.cos(l_phi);
                        double l_y = 6_371 * Math.sin(polar) * Math.sin(l_phi);

                        double l_lng = Constants.toOSMDegrees(Constants.toDegrees(Math.atan2(l_y, l_x)));

                        if (pip.checkPoint(lat, l_lng, coastlines, data) == 0) {
                            Node l_node = new Node((int) (mAzimuth - 1), lat, l_lng);
                            nodes.add(l_node);
                            dNodeId++;
                            int dist = haversine_distance(currNode, l_node);
                            Edge edge = new Edge(dEdgeId++, currNode, l_node, dist);
                            edges.add(edge);
                            edge = new Edge(dEdgeId++, l_node, currNode, dist);
                            edges.add(edge);
                        }
                    } else {
                        Node prevNode = nodes.get((int) (dNodeId - 1));

                        if (pip.checkPoint(prevNode.getLat(), prevNode.getLng(), coastlines, data) == 0) {
                            int dist = haversine_distance(currNode, prevNode);
                            Edge edge = new Edge(dEdgeId++, currNode, prevNode, dist);
                            edges.add(edge);
                            edge = new Edge(dEdgeId++, prevNode, currNode, dist);
                            edges.add(edge);
                        }
                    }
                }
            }
            System.out.println(nodes.size());
            System.out.println(edges.size());
        }
    }

    /**
     * create a simple grid based on lat and long lines having m rows and n columns.
     * This method also performs the point in polygon test on each node of the grid and creates a list of nodes present in water and a list edges that connect the neighbouring nodes
     * @param m number of rows in the grid to create
     * @param n number of columns to create in the grid
     */
    public void createSimpleGrid(int m, int n) {
        PointInPolygon pip = new PointInPolygon();
        int topIdx[] = new int[n];
        Arrays.fill(topIdx, -1);
        boolean leftIdx = false;
        
        double lat = Constants.toOSMDegrees(90.0);
        double d_phi = new BigDecimal(Constants.toOSMDegrees(180.0) / m).setScale(7, RoundingMode.HALF_UP).doubleValue();
        double d_lambda = new BigDecimal(Constants.toOSMDegrees(360.0) / n).setScale(7, RoundingMode.HALF_UP).doubleValue();
        int dNodeId = 0, dEdgeId = 0;
        while (lat > -90.0) {
            double lng = Constants.toOSMDegrees(-180.0);
            int j=0;
            
            while (lng < 180.0) {
                
                if(pip.checkPoint(lat, lng, coastlines, data) == 0) {
                    Node currNode = new Node(dNodeId++, lat, lng);
                    Point left = new Point(lat, lng - d_lambda);
                    Point top = new Point(lat + d_phi, lng);

                    if(lng != -180.0) {
                        //check if left node is in water, if yes, get the node and create edges
                        // if (pip.checkPoint(left.getLat(), left.getLng(), coastlines, data) == 0) {
                        if(leftIdx == true) {
                            Node leftNode = nodes.get(nodes.size() - 1);
                            int distance = haversine_distance(leftNode, currNode);
                            edges.add(new Edge(dEdgeId++, currNode, leftNode, distance));
                            edges.add(new Edge(dEdgeId++, leftNode, currNode, distance));
                        }
                    }
                    else if( new BigDecimal(lng + d_lambda).setScale(7, RoundingMode.HALF_UP).doubleValue() >= 180.0) {
                    	//check the right node for the last node on latitude
                    	Point right = new Point(lat, lng - d_lambda);
//                    	if (pip.checkPoint(right.getLat(), right.getLng(), coastlines, data) == 0) {
                    	//alternate check thus saving some calculation
                    	if(topIdx[0] != -1) {
                            Node rightNode = nodes.get(topIdx[0]);
                            int distance = haversine_distance(rightNode, currNode);
                            edges.add(new Edge(dEdgeId++, currNode, rightNode, distance));
                            edges.add(new Edge(dEdgeId++, rightNode, currNode, distance));
                        }
                    }

                    if(lat != 90.0) {
                        //check if top node is in water, if yes, get the node and create edges
                        // if (pip.checkPoint(top.getLat(), top.getLng(), coastlines, data) == 0) {
                        if (topIdx[j] != -1) {
                            Node topNode = nodes.get(topIdx[j]);
                            int distance = haversine_distance(topNode, currNode);
                            edges.add(new Edge(dEdgeId++, currNode, topNode, distance));
                            edges.add(new Edge(dEdgeId++, topNode, currNode, distance));
                        }
                    }
                    nodes.add(currNode);
                    topIdx[j] = dNodeId - 1;
                    leftIdx = true;
                }
                else {
                    topIdx[j] = -1;
                    leftIdx = false;
                }
                j++;                
                lng = new BigDecimal(lng + d_lambda).setScale(7, RoundingMode.HALF_UP).doubleValue();
            }
            lat = new BigDecimal(lat - d_phi).setScale(7, RoundingMode.HALF_UP).doubleValue();
        }
    }

    //haversine distance
    private static int haversine_distance(de.piyush.shipnavigation.algorithms.dijkstra.Node source, de.piyush.shipnavigation.algorithms.dijkstra.Node target) {
        final double EARTH_RADIUS = 6_371_007.2;
        double phi1 = Constants.toRadians(source.getLat());
        double phi2 = Constants.toRadians(target.getLat());
        double delta_phi = Constants.toRadians(target.getLat() - source.getLat());
        double delta_lambda = Constants.toRadians(target.getLng() - source.getLng());
        double a = Math.pow(Math.sin(delta_phi / 2.0),2) + Math.cos(phi1) * Math.cos(phi2) * Math.pow(Math.sin(delta_lambda / 2.0), 2);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return (int) (EARTH_RADIUS * c);
    }
}
