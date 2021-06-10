package de.piyush.shipnavigation;

import de.piyush.shipnavigation.algorithms.dijkstra.Edge;
import de.piyush.shipnavigation.algorithms.dijkstra.Node;
import de.piyush.shipnavigation.coastlines.Coastline;
import de.piyush.shipnavigation.coastlines.CoastlinesHelper;
import de.piyush.shipnavigation.grid.Grid;
import de.piyush.shipnavigation.osmartifacts.DataLoader;
import de.piyush.shipnavigation.osmartifacts.OSMDataSet;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.pbf.seq.PbfReader;
import de.piyush.shipnavigation.geojson.GeoJson;

import java.io.*;
import java.util.List;

public class ShipNavigation {

    public static void main(String args[]) {
    	System.out.println(new java.util.Date());
        try {
        	
            String fileLocation = args[0];
            
            //read the file
            InputStream inputStream = new FileInputStream(fileLocation);

            //pbf reader
            OsmReader osmReader = new PbfReader(inputStream, false);
            System.out.println("Creating data store from OSM file: " + new java.util.Date());
            //read OSM file and store data
            OSMDataSet selectedData = new DataLoader().read(osmReader);

            System.out.println("Creating coastlines: " + new java.util.Date());
            List<Coastline> coastlines = new CoastlinesHelper().createCoastlines(selectedData);

            new GeoJson().createGeoJson(coastlines, selectedData, "output");

            Grid grid = new Grid(coastlines, selectedData);
            System.out.println("Creating Grid: " + new java.util.Date());
            grid.createSimpleGrid(Integer.parseInt(args[1]), Integer.parseInt(args[2]));

            System.out.println("Starting storage to file: " + new java.util.Date());
            saveNodesAndEdgesToFile(grid, args[1], args[2]);
            System.out.println("Storage done: " + new java.util.Date());

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(new java.util.Date());

    }

    private static void saveNodesAndEdgesToFile(Grid grid, String m, String n) {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter("NodesEdges.txt"));
            output.write(Integer.toString(grid.getNodes().size()));
            for(Node node : grid.getNodes()) {
                output.newLine();
                output.write(node.toString());
            }
            output.newLine();
            output.write(Integer.toString(grid.getEdges().size()));
            for(Edge edge : grid.getEdges()) {
                output.newLine();
                output.write(edge.toString());
            }
            output.close();

            output = new BufferedWriter(new FileWriter("GridDimensions.txt"));
            output.write(m);
            output.newLine();
            output.write(n);
            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
