package de.piyush.shipnavigation.osmartifacts;

import de.piyush.shipnavigation.Constants;
import de.topobyte.osm4j.core.model.iface.OsmNode;

public class OSMEdge {

    private OsmNode from;
    private OsmNode to;


    public OSMEdge(OsmNode node1, OsmNode node2) {
        this.from = node1;
        this.to = node2;
    }

    public boolean isNorthSouth() {
        if(from.getLongitude() == to.getLongitude()) {
            return true;
        }
        return false;
    }

    public boolean isLngBetween(double lngP) {
        if(lngP > from.getLongitude() && lngP < to.getLongitude())
            return true;
        if(lngP < from.getLongitude() && lngP > to.getLongitude()) {
            return true;
        }
        return false;
    }

    public boolean isNorthernHemisphere() {
        if(from.getLatitude() > 0 && to.getLatitude() > 0)
            return true;
        return false;
    }

    public double minLat() {
        if(from.getLatitude() < to.getLatitude())
            return from.getLatitude();
        return to.getLatitude();
    }
}
