package de.piyush.shipnavigation.coastlines;

import de.piyush.shipnavigation.Constants;
import de.piyush.shipnavigation.osmartifacts.OSMDataSet;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoastlinesHelper {

    /**
     * creates coastlines with the data provided by joining the adjacent OsmWay
     * @param data data using which the coastline is to be created
     */
    public List<Coastline> createCoastlines(OSMDataSet data) {

        List<Coastline> coastlines = new ArrayList<Coastline>();
        Map<Long, Integer> start_nodes = new HashMap<Long, Integer>();
        OsmWay lines[] = new OsmWay[data.getWays().size()];
        lines = data.getWays().values(lines);
        //creating a map of start node of each line, with key as nodeid and value as the index in the lines array
        for(int i = 0; i<lines.length;i++) {
            start_nodes.put(lines[i].getNodeId(0), i);
        }
        //variable to identify the last node id of the selected line to search for the first node of the next line in merged line
        long last_node_id;
        //initially setting all lines as unused
        boolean used_lines[] = new boolean[data.getWays().size()];

        //going through all the ways[lines], check if the way[line] is already used, if yes then check the next way[line]
        for(int i=0; i< data.getWays().size();i++) {
            if(used_lines[i]) {
                continue;
            }
            //create coastline with 0 nodes
            Coastline coastline = new Coastline();
            //add the current way[line] to the coastline
            coastline.merge(lines[i]);
            //last node id to search for the first node id of other wasy[lines]
            last_node_id = lines[i].getNodeId(lines[i].getNumberOfNodes() - 1);
            //start node contains a way[line] with start node as the starting node
            while(start_nodes.get(last_node_id) != null) {
                //if the line itself is selected of the line is already used, a merged coastline is complete, add to the coastlines array
                if(start_nodes.get(last_node_id) == i || used_lines[start_nodes.get(last_node_id)]) {
                    createBoundingBox(coastline, data);
//                    createGreatCircleBoundingBox(coastline, data);
                    coastlines.add(coastline);
                    break;
                }
                //get the connecting way[line]
                OsmWay other_line = lines[start_nodes.get(last_node_id)];
                //add it to the existing coastline
                coastline.merge(other_line);
                //mark it as used
                used_lines[start_nodes.get(last_node_id)] = true;
                //last node id changed to the new last node id in the merged coastline way [line]
                last_node_id = coastline.getNodeId(coastline.getNumberOfNodes() - 1);

            }
        }
        return coastlines;
    }

    private void createBoundingBox(Coastline coastline, OSMDataSet data) {
        try {
            double minLat = Constants.toOSMDegrees(100.0), maxLat = Constants.toOSMDegrees(-100.0);
            double minLng = Constants.toOSMDegrees(200.0), maxLng = Constants.toOSMDegrees(-200.0);
            for (int i = 0; i < coastline.getNumberOfNodes(); i++) {
                long nodeId = coastline.getNodeId(i);
                OsmNode node = data.getNode(nodeId);
                double lat = Constants.toOSMDegrees(node.getLatitude());
                double lng = Constants.toOSMDegrees(node.getLongitude());
                if (lat < minLat) {
                    minLat = lat;
                }
                if(lat > maxLat) {
                    maxLat = lat;
                }
                if(lng < minLng) {
                    minLng = lng;
                }
                if(lng > maxLng) {
                    maxLng = lng;
                }
            }
            double bb[] = {minLat, minLng, maxLat, maxLng};
            coastline.setBoundingBox(bb);
        }
        catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createGreatCircleBoundingBox(Coastline coastline, OSMDataSet data) {
        try {
            double minLat = Constants.toOSMDegrees(100.0), maxLat = Constants.toOSMDegrees(-100.0);
            double minLng = Constants.toOSMDegrees(200.0), maxLng = Constants.toOSMDegrees(-200.0);
            int numNodes = coastline.getNumberOfNodes();
            int location = checkPolygon(coastline, data);

            for (int i = 0; i < numNodes; i++) {
                long nodeId = coastline.getNodeId(i);
                OsmNode node1 = data.getNode(nodeId);

                if (Constants.toOSMDegrees(node1.getLongitude()) < minLng) {
                    minLng = Constants.toOSMDegrees(node1.getLongitude());
                }
                if (Constants.toOSMDegrees(node1.getLongitude()) > maxLng) {
                    maxLng = Constants.toOSMDegrees(node1.getLongitude());
                }

                if (location == 1) {
                    if (Constants.toOSMDegrees(node1.getLatitude()) < minLat) {
                        minLat = Constants.toOSMDegrees(node1.getLatitude());
                    }
                    nodeId = coastline.getNodeId((i + 1) % numNodes);
                    OsmNode node2 = data.getNode(nodeId);

                    double theta;
                    if (Constants.toOSMDegrees(node1.getLongitude()) != Constants.toOSMDegrees(node2.getLongitude())) {
                        theta = Math.atan2(
                                (
                                        Math.sin(Constants.toRadians(Constants.toOSMDegrees(node2.getLongitude()) - Constants.toOSMDegrees(node1.getLongitude())))
                                                *
                                                Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLatitude())))
                                )
                                ,
                                (
                                        Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude()))) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLatitude())))
                                                -
                                                Math.sin(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude()))) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLatitude()))) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLongitude()) - Constants.toOSMDegrees(node1.getLongitude())))
                                )
                        );
                        double lat = Math.max(Constants.toDegrees(Math.acos(Math.abs(Math.sin(theta) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude())))))) , Math.max(Constants.toOSMDegrees(node1.getLatitude()), Constants.toOSMDegrees(node2.getLatitude())));
                        if (lat > maxLat) {
                            maxLat = lat;
                        }
                    } else if (Constants.toOSMDegrees(node1.getLongitude()) == Constants.toOSMDegrees(node2.getLongitude()) && Constants.toOSMDegrees(node1.getLatitude()) < Constants.toOSMDegrees(node2.getLatitude())) {
                        theta = 0;
                        double lat = Math.max(Constants.toDegrees(Math.acos(Math.abs(Math.sin(theta) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude())))))) , Math.max(Constants.toOSMDegrees(node1.getLatitude()), Constants.toOSMDegrees(node2.getLatitude())));
                        if (lat > maxLat) {
                            maxLat = lat;
                        }
                    } else if (Constants.toOSMDegrees(node1.getLongitude()) == Constants.toOSMDegrees(node2.getLongitude()) && Constants.toOSMDegrees(node1.getLatitude()) > Constants.toOSMDegrees(node2.getLatitude())) {
                        theta = Math.PI;
                        double lat = Math.max(Constants.toDegrees(Math.acos(Math.abs(Math.sin(theta) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude())))))) , Math.max(Constants.toOSMDegrees(node1.getLatitude()), Constants.toOSMDegrees(node2.getLatitude())));
                        if (lat > maxLat) {
                            maxLat = lat;
                        }
                    } else if (Constants.toOSMDegrees(node1.getLongitude()) == Constants.toOSMDegrees(node2.getLongitude()) && Constants.toOSMDegrees(node1.getLatitude()) == Constants.toOSMDegrees(node2.getLatitude())) {
                        continue;
                    }

                } else if (location == 2) {
                    if (Constants.toOSMDegrees(node1.getLatitude()) > maxLat) {
                        maxLat = Constants.toOSMDegrees(node1.getLatitude());
                    }
                    nodeId = coastline.getNodeId((i + 1) % numNodes);
                    OsmNode node2 = data.getNode(nodeId);

                    double theta;
                    if (Constants.toOSMDegrees(node1.getLongitude()) != Constants.toOSMDegrees(node2.getLongitude())) {
                        theta = Math.atan2(
                                (
                                        Math.sin(Constants.toRadians(Constants.toOSMDegrees(node2.getLongitude()) - Constants.toOSMDegrees(node1.getLongitude())))
                                                *
                                                Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLatitude())))
                                )
                                ,
                                (
                                        Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude()))) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLatitude())))
                                                -
                                                Math.sin(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude()))) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLatitude()))) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLongitude()) - Constants.toOSMDegrees(node1.getLongitude())))
                                )
                        );
                        double lat = Math.min(Constants.toDegrees(Math.acos(Math.abs(Math.sin(theta) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude())))))) , Math.min(Constants.toOSMDegrees(node1.getLatitude()), Constants.toOSMDegrees(node2.getLatitude())));
                        if (lat > minLat) {
                            minLat = lat;
                        }
                    } else if (Constants.toOSMDegrees(node1.getLongitude()) == Constants.toOSMDegrees(node2.getLongitude()) && Constants.toOSMDegrees(node1.getLatitude()) < Constants.toOSMDegrees(node2.getLatitude())) {
                        theta = 0;
                        double lat = Math.min(Constants.toDegrees(Math.acos(Math.abs(Math.sin(theta) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude())))))) , Math.min(Constants.toOSMDegrees(node1.getLatitude()), Constants.toOSMDegrees(node2.getLatitude())));
                        if (lat < minLat) {
                            minLat = lat;
                        }
                    } else if (Constants.toOSMDegrees(node1.getLongitude()) == Constants.toOSMDegrees(node2.getLongitude()) && Constants.toOSMDegrees(node1.getLatitude()) > Constants.toOSMDegrees(node2.getLatitude())) {
                        theta = Math.PI;
                        double lat = Math.min(Constants.toDegrees(Math.acos(Math.abs(Math.sin(theta) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude())))))) , Math.min(Constants.toOSMDegrees(node1.getLatitude()), Constants.toOSMDegrees(node2.getLatitude())));
                        if (lat < minLat) {
                            minLat = lat;
                        }
                    } else if (Constants.toOSMDegrees(node1.getLongitude()) == Constants.toOSMDegrees(node2.getLongitude()) && Constants.toOSMDegrees(node1.getLatitude()) == Constants.toOSMDegrees(node2.getLatitude())) {
                        continue;
                    }

                } else {
                    nodeId = coastline.getNodeId((i + 1) % numNodes);
                    OsmNode node2 = data.getNode(nodeId);

                    double theta = Double.MAX_VALUE;
                    if (Constants.toOSMDegrees(node1.getLongitude()) != Constants.toOSMDegrees(node2.getLongitude())) {
                        theta = Math.atan2(
                                (
                                        Math.sin(Constants.toRadians(Constants.toOSMDegrees(node2.getLongitude()) - Constants.toOSMDegrees(node1.getLongitude())))
                                                *
                                                Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLatitude())))
                                )
                                ,
                                (
                                        Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude()))) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLatitude())))
                                                -
                                                Math.sin(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude()))) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLatitude()))) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node2.getLongitude()) - Constants.toOSMDegrees(node1.getLongitude())))
                                )
                        );

                    } else if (Constants.toOSMDegrees(node1.getLongitude()) == Constants.toOSMDegrees(node2.getLongitude()) && Constants.toOSMDegrees(node1.getLatitude()) < Constants.toOSMDegrees(node2.getLatitude())) {
                        theta = 0;

                    } else if (Constants.toOSMDegrees(node1.getLongitude()) == Constants.toOSMDegrees(node2.getLongitude()) && Constants.toOSMDegrees(node1.getLatitude()) > Constants.toOSMDegrees(node2.getLatitude())) {
                        theta = Math.PI;

                    } else if (Constants.toOSMDegrees(node1.getLongitude()) == Constants.toOSMDegrees(node2.getLongitude()) && Constants.toOSMDegrees(node1.getLatitude()) == Constants.toOSMDegrees(node2.getLatitude())) {
                        continue;
                    }
                    if(Constants.toOSMDegrees(node1.getLatitude()) > 0 && Constants.toOSMDegrees(node2.getLatitude()) > 0) {
                        if(Constants.toOSMDegrees(node1.getLatitude()) < minLat) {
                            minLat = Constants.toOSMDegrees(node1.getLatitude());
                        }
                        double lat = Math.max(Constants.toDegrees(Math.acos(Math.abs(Math.sin(theta) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude())))))) , Math.max(Constants.toOSMDegrees(node1.getLatitude()), Constants.toOSMDegrees(node2.getLatitude())));
                        if(lat > maxLat) {
                            maxLat = lat;
                        }
                    }
                    else if(Constants.toOSMDegrees(node1.getLatitude()) < 0 && Constants.toOSMDegrees(node2.getLatitude()) < 0) {
                        if(Constants.toOSMDegrees(node1.getLatitude()) > maxLat) {
                            maxLat = Constants.toOSMDegrees(node1.getLatitude());
                        }
                        double lat = Math.min(Constants.toDegrees(Math.acos(Math.abs(Math.sin(theta) * Math.cos(Constants.toRadians(Constants.toOSMDegrees(node1.getLatitude())))))) , Math.min(Constants.toOSMDegrees(node1.getLatitude()), Constants.toOSMDegrees(node2.getLatitude())));
                        if(lat < minLat) {
                            minLat = lat;
                        }
                    }
                    else {
                        if(Constants.toOSMDegrees(node1.getLatitude()) < minLat) {
                            minLat = Constants.toOSMDegrees(node1.getLatitude());
                        }
                        if(Constants.toOSMDegrees(node1.getLatitude()) > maxLat) {
                            maxLat = Constants.toOSMDegrees(node1.getLatitude());
                        }
                    }
                }
            }
            double bb[] = {minLat, minLng, maxLat, maxLng};
            coastline.setBoundingBox(bb);
        }
        catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int checkPolygon (Coastline coastline, OSMDataSet data) throws EntityNotFoundException {
        int south = 0, north = 0;
        for(int i=0;i<coastline.getNumberOfNodes();i++) {
            if(data.getNode(coastline.getNodeId(i)).getLatitude() < 0) {
                south++;
            }
            else {
                north++;
            }
        }
        if(north == 0) {
            return 2;
        }
        if(south == 0) {
            return 1;
        }
        return 0;
    }
}
