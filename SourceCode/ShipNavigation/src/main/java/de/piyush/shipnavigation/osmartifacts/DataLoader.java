package de.piyush.shipnavigation.osmartifacts;

import com.slimjars.dist.gnu.trove.map.TLongObjectMap;
import de.topobyte.osm4j.core.access.DefaultOsmHandler;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;

import java.io.IOException;
import java.util.Map;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DataLoader {

    public OSMDataSet read(OsmReader reader) {

        final OSMDataSet dataSet = new OSMDataSet();
        final TLongObjectMap<OsmNode> nodes = dataSet.getNodes();
        final TLongObjectMap<OsmWay> ways = dataSet.getWays();
        final TLongObjectMap<OsmRelation> relations = dataSet.getRelations();
        final TLongObjectMap<OsmBounds> bounds = dataSet.getBounds();

        reader.setHandler(new DefaultOsmHandler() {
            @Override
            public void handle(OsmBounds bounds) throws IOException {
                super.handle(bounds);
            }

            @Override
            public void handle(OsmNode node) throws IOException {
                super.handle(node);
                node = new Node(node.getId(), new BigDecimal(node.getLongitude()).setScale(7, RoundingMode.HALF_UP).doubleValue(), new BigDecimal(node.getLatitude()).setScale(7, RoundingMode.HALF_UP).doubleValue());
                nodes.put(node.getId(), node);
            }

            @Override
            public void handle(OsmWay way) throws IOException {
                //get all tags of the way
                Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
                //filter tags with key=natural and value=coastline
                String filteredTag = tags.get("natural");
                if(filteredTag != null && filteredTag.equalsIgnoreCase("coastline")) {
                    ways.put(way.getId(), way);
                }
            }

            @Override
            public void handle(OsmRelation relation) throws IOException {
                super.handle(relation);
            }

            @Override
            public void complete() throws IOException {
                super.complete();
            }
        });

        try {
            reader.read();
        } catch (OsmInputException e) {
            e.printStackTrace();
        }

        return dataSet;
    }

}
