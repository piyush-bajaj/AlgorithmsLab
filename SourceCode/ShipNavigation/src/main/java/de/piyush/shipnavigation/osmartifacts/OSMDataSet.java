package de.piyush.shipnavigation.osmartifacts;

import com.slimjars.dist.gnu.trove.map.TLongObjectMap;
import com.slimjars.dist.gnu.trove.map.hash.TLongObjectHashMap;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;
import de.topobyte.osm4j.core.resolve.OsmEntityProvider;

public class OSMDataSet implements OsmEntityProvider {

    private TLongObjectMap<OsmNode> nodes;
    private TLongObjectMap<OsmWay> ways;
    private TLongObjectMap<OsmRelation> relations;
    private TLongObjectMap<OsmBounds> bounds;

    public OSMDataSet() {
        nodes = new TLongObjectHashMap<>();
        ways = new TLongObjectHashMap<>();
        relations = new TLongObjectHashMap<>();
        bounds = new TLongObjectHashMap<>();
    }

    public TLongObjectMap<OsmNode> getNodes() {
        return nodes;
    }

    public void setNodes(TLongObjectMap<OsmNode> nodes) {
        this.nodes = nodes;
    }

    public TLongObjectMap<OsmWay> getWays() {
        return ways;
    }

    public void setWays(TLongObjectMap<OsmWay> ways) {
        this.ways = ways;
    }

    public TLongObjectMap<OsmRelation> getRelations() {
        return relations;
    }

    public void setRelations(TLongObjectMap<OsmRelation> relations) {
        this.relations = relations;
    }

    public TLongObjectMap<OsmBounds> getBounds() {
        return bounds;
    }

    public void setBounds(TLongObjectMap<OsmBounds> bounds) {
        this.bounds = bounds;
    }


    /**
     * Get the node with the given id.
     *
     * @param id the node's id.
     * @return the node.
     * @throws EntityNotFoundException if the implementation cannot return this entity.
     */
    @Override
    public OsmNode getNode(long id) throws EntityNotFoundException {
        OsmNode node = nodes.get(id);
        if (node == null) {
            throw new EntityNotFoundException(
                    "unable to find node with id: " + id);
        }
        return node;
    }

    /**
     * Get the way with the given id.
     *
     * @param id the way's id
     * @return the way.
     * @throws EntityNotFoundException if the implementation cannot return this entity.
     */
    @Override
    public OsmWay getWay(long id) throws EntityNotFoundException {
        OsmWay way = ways.get(id);
        if (way == null) {
            throw new EntityNotFoundException(
                    "unable to find way with id: " + id);
        }
        return way;
    }

    /**
     * Get the relation with the given id.
     *
     * @param id the relation's id.
     * @return the relation.
     * @throws EntityNotFoundException if the implementation cannot return this entity.
     */
    @Override
    public OsmRelation getRelation(long id) throws EntityNotFoundException {
        OsmRelation relation = relations.get(id);
        if (relation == null) {
            throw new EntityNotFoundException(
                    "unable to find relation with id: " + id);
        }
        return relation;
    }
}
