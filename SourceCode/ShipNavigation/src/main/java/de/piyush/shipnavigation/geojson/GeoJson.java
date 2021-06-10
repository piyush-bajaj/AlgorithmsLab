package de.piyush.shipnavigation.geojson;

import com.slimjars.dist.gnu.trove.map.TLongObjectMap;
import de.piyush.shipnavigation.coastlines.Coastline;
import de.piyush.shipnavigation.osmartifacts.OSMDataSet;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;
import de.topobyte.osm4j.geometry.GeometryBuilder;
import de.topobyte.osm4j.geometry.RegionBuilder;
import de.topobyte.osm4j.geometry.RegionBuilderResult;
import org.locationtech.jts.geom.Geometry;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoJson {

    public void createGeoJson(TLongObjectMap<OsmWay> ways, OSMDataSet data, String outputFile) {
        try {
            //creating an instance to create the geometry for the data
            GeometryBuilder geometryBuilder = new GeometryBuilder();
            //region builder is used to build multipolygon from ways
            RegionBuilder rb = new RegionBuilder();
            //features list for geojson
            ArrayList<Feature> features = new ArrayList<Feature>();

            //result of region build
            RegionBuilderResult result;
            //geometry to draw --> multipolygon or linestring
            Geometry polygon ;

            //collection of ways from the given pbf file
            for(OsmWay way : ways.valueCollection()) {

                //get the multipolygon coordinates for the given way from the data set
                result = rb.build(way, data);
                polygon = result.getMultiPolygon();
                //check if the multipolygon fails, then it may be linestring
                if(!result.getLineStrings().isEmpty()) {
                    //get the linestring coordinates for the given way from the data set
                    polygon = geometryBuilder.build(way, data);
                }

                //fetch all the tags and put it in the properties of geojson
                Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
                Map<String, Object> properties = new HashMap<>();
				for (String key : tags.keySet()) {
					properties.put(key, tags.get(key));
				}

                //geojson writer utility
                GeoJSONWriter writer = new GeoJSONWriter();
                org.wololo.geojson.Geometry g = writer.write(polygon);

                //get the geomerty and properties for geojson
                features.add(new Feature(g, properties));

            }
            //feature collection for multiple features
            FeatureCollection featureCollection = new FeatureCollection(features.toArray(new Feature[features.size()]));
            String json = featureCollection.toString();
            //use helper to pretty the output, adding indentation and stuff
            json = GeoJsonHelper.prettyPrintFeatureCollection(json);


            //creating an output stream to write the output in geojson file
            OutputStream output = new FileOutputStream(new File(outputFile+".geojson"));
            output.write(json.getBytes(),0, json.length());
            output.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (EntityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createGeoJson(List<Coastline> ways, OSMDataSet data, String outputFile) {
        try {
            //creating an instance to create the geometry for the data
            GeometryBuilder geometryBuilder = new GeometryBuilder();
            //region builder is used to build multipolygon from ways
            RegionBuilder rb = new RegionBuilder();
            //features list for geojson
            ArrayList<Feature> features = new ArrayList<Feature>();

            //result of region build
            RegionBuilderResult result;
            //geometry to draw --> multipolygon or linestring
            Geometry polygon ;

            //collection of ways from the given pbf file
            for(OsmWay way : ways) {

                //get the multipolygon coordinates for the given way from the data set
                result = rb.build(way, data);
                polygon = result.getMultiPolygon();
                //check if the multipolygon fails, then it may be linestring
                if(!result.getLineStrings().isEmpty()) {
                    //get the linestring coordinates for the given way from the data set
                    polygon = geometryBuilder.build(way, data);
                }

                //fetch all the tags and put it in the properties of geojson
                Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
                Map<String, Object> properties = new HashMap<>();
                for (String key : tags.keySet()) {
                    properties.put(key, tags.get(key));
                }

                Coastline cway = (Coastline) way;
                properties.put("bounding-box", cway.getBoundingBox());

                //geojson writer utility
                GeoJSONWriter writer = new GeoJSONWriter();
                org.wololo.geojson.Geometry g = writer.write(polygon);

                //get the geomerty and properties for geojson
                features.add(new Feature(g, properties));

            }
            //feature collection for multiple features
            FeatureCollection featureCollection = new FeatureCollection(features.toArray(new Feature[features.size()]));
            String json = featureCollection.toString();
            //use helper to pretty the output, adding indentation and stuff
            json = GeoJsonHelper.prettyPrintFeatureCollection(json);


            //creating an output stream to write the output in geojson file
            OutputStream output = new FileOutputStream(new File(outputFile+".geojson"));
            output.write(json.getBytes(),0, json.length());
            output.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (EntityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
