package de.piyush.shipnavigation.coastlines;

import de.topobyte.osm4j.core.model.iface.OsmMetadata;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;

import java.util.ArrayList;
import java.util.List;

/**
 * A new data type that contains the list of node ids present in the merged ways with tag natural=coastline
 * one coastline is one way which has the same start and the end node
 */
public class Coastline implements OsmWay {

    private final List<Long> nodes = new ArrayList<>();
    private long id;
    private double[] boundingBox = new double[4];
    private static long coastLineNo = 0;

    /**
     * constructor that assigns an id to the coastline, indexed at 0
     */
    public Coastline() {
        this.id = coastLineNo++;
    }

    @Override
    public int getNumberOfNodes() {
        return nodes.size();
    }

    @Override
    public long getNodeId(int n) {
        return nodes.get(n);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int getNumberOfTags() {
        return 0;
    }

    @Override
    public OsmTag getTag(int n) {
        return null;
    }

    @Override
    public OsmMetadata getMetadata() {
        return null;
    }

    /**
     * method that combines OSM nodes of the provided way to the existing coastline
     * @param way first OsmWay
     */
    public void merge(OsmWay way) {
        for(int i=nodes.size() > 0 ? 1 : 0;i<way.getNumberOfNodes();i++) {
            nodes.add(way.getNodeId(i));
        }
    }

    public double[] getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(double[] boundingBox) {
        this.boundingBox = boundingBox;
    }
}
