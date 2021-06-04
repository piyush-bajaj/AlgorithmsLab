package de.piyush.shipnavigation.algorithms.pip;

import de.piyush.shipnavigation.Constants;
import de.piyush.shipnavigation.coastlines.Coastline;
import de.piyush.shipnavigation.osmartifacts.OSMDataSet;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.piyush.shipnavigation.osmartifacts.OSMEdge;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;

import java.util.List;

public class PointInPolygon {

    /**
     *
     * @param latP latitude of the point to test
     * @param lngP longitude of the point to test
     * @param coastlines list of all the coastlines in which the point is to be checked
     * @param data dataset to fetch the lat and long of the nodes of the coastlines
     * @return 0 if point is in water, 1 point is on land, 2 if the point lies on the coastline including nodes and edges
     */
    public int checkPoint(double latP, double lngP, List<Coastline> coastlines, OSMDataSet data) {
        try {
            for(Coastline coastline : coastlines) {
                int i = checkPointForCoastline(latP, lngP, coastline, data);
                if(i != 0)
                    return i;
            }
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int checkPointForCoastline(double latP, double lngP, Coastline coastline, OSMDataSet data) throws EntityNotFoundException {
        int location = 0, count = 0;
        int numNodes = coastline.getNumberOfNodes();
        latP = Constants.toOSMDegrees(latP);
        lngP = Constants.toOSMDegrees(lngP);

        //Consider Antarctica Points  on land
        if(latP < -85.1) {
            location = 1;
            return location;
        }

        double bb[] = coastline.getBoundingBox();
        //checking if point intersects the bounding box
        if(latP >= bb[0] && latP <= bb[2] && lngP >= bb[1] && lngP <= bb[3]) {
            //checking if point is one of the nodes/vertex of the polygon
            for(int i=0;i<numNodes;i++) {
                long nodeId = coastline.getNodeId(i);
                OsmNode node = data.getNode(nodeId);

                if(Constants.toOSMDegrees(node.getLatitude()) == latP && Constants.toOSMDegrees(node.getLongitude()) == lngP) {
                    location = 2;
//                    System.out.println("Point is on vertex");
                    return location;
                }
            }
            //checking for each edge
            for(int i=0;i<numNodes;i++) {
                long nodeId = coastline.getNodeId(i%numNodes);
                OsmNode node = data.getNode(nodeId);
                nodeId = coastline.getNodeId((i+1)%numNodes);
                OsmNode  node1 = data.getNode(nodeId);
                OSMEdge edge = new OSMEdge(node, node1);
                if(!edge.isNorthSouth()) {
                    if(edge.isLngBetween(lngP)){
                        if(edge.isNorthernHemisphere() && edge.minLat() > latP) {
                            count++;
                        }
                        else {
                            //calculation of latitude based on lat long lines as straight lines
//                            double lat = node1.getLatitude() + ((lngP - node.getLongitude())/(node1.getLongitude() - node.getLongitude())) * (node1.getLatitude() - node.getLatitude());
                            //calculation of latitude based on great circle arc lines as straight lines
                            double lat = Constants.toDegrees(
                                    Math.atan(
                                    ( Math.tan(Constants.toRadians(Constants.toOSMDegrees(node.getLatitude())))
                                            * ( Math.sin(Constants.toRadians(lngP - Constants.toOSMDegrees(node1.getLongitude())))
                                                /
                                                Math.sin(Constants.toRadians(Constants.toOSMDegrees(node.getLongitude()) - Constants.toOSMDegrees(node1.getLongitude())))
                                               )
                                    )
                                    -
                                    ( Math.tan(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude())))
                                            * ( Math.sin(Constants.toRadians(lngP - Constants.toOSMDegrees(node.getLongitude())))
                                                /
                                                Math.sin(Constants.toRadians(Constants.toOSMDegrees(node.getLongitude()) - Constants.toOSMDegrees(node1.getLongitude())))
                                               )
                                    )
                                )
                            );
                            if(lat > latP) {
//                                System.out.println("Point intersects the edge");
                                count++;
                            }
                            else if(lat == latP) {
//                                System.out.println("Point is on the edge");
                                location = 2;
                                return location;
                            }
                        }
                    }
                }
            }
            if(count%2 == 1) {
                location = 1;
//                System.out.println("Point is on the land");
                return location;
            }
            location = 0;
//            System.out.println("Point is in the water");
            return location;
        }
        else {
//            System.out.println("Point is in the water");
            return location;
        }
    }
}
