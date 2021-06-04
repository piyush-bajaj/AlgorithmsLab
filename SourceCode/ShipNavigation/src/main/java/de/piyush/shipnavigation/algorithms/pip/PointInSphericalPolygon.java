package de.piyush.shipnavigation.algorithms.pip;

import de.piyush.shipnavigation.coastlines.Coastline;
import de.piyush.shipnavigation.osmartifacts.OSMDataSet;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;

import java.util.List;

public class PointInSphericalPolygon {


    //used in point in spherical polygon test
    private static double transformLongitude(double plat, double plon, double qlat, double qlon) {
        double tranlon;
        if(plat == (double)90) {
            tranlon = qlon;
        }
        else {
            double y = Math.sin((qlon-plon)*Math.PI/180) * Math.cos(qlat*Math.PI/180);
            double x = Math.sin(qlat*Math.PI/180)*Math.cos(plat*Math.PI/180) -
                    Math.sin(plat*Math.PI/180)*Math.cos(qlat*Math.PI/180)*Math.cos((qlon-plon)*Math.PI/180);
            tranlon = Math.atan2(y, x) * 180/Math.PI;
        }
        return tranlon;
    }

    //used in point in spherical polygon test
    private static int eastOrWest(double clon, double dlon) {
        int ibrng = 0;
        double del = dlon-clon;
        if(del > 180)
            del = del-360;
        if(del < -180)
            del = del + 360;
        if(del > 0 && del != 180)
            ibrng = -1;
        else if(del < 0 && del != -180)
            ibrng = 1;
        else
            ibrng = 0;

        return ibrng;
    }

    /**
     * checks if the point with given latitude and longitude is present inside one of the coastline polygons that were formed, if point is found in any one polygon, skip the others
     * @param latP latitude of the point to test
     * @param lngP longitude of the point to test
     * @param coastlines list of all the coastlines in which the point is to be checked
     * @param data dataset to fetch the lat and long of the nodes of the coastlines
     * @return 0 if point is in water, 1 point on land, 2 if the point lies on the coastline including nodes and edges
     * @throws EntityNotFoundException
     */
    //point in spherical polygon test
    //https://link.springer.com/content/pdf/10.1007/BF00894449.pdf
    //check if point p is the point x itself, then no calculations needed, the point is outside, in case of north pole outside assumption
    public int checkPoint(double latP, double lngP, List<Coastline> coastlines, OSMDataSet data) throws EntityNotFoundException {

        int location = 0;
        for(Coastline coastline: coastlines) {

            int count = 0;

            if(latP == (double)90) {
                System.out.println("Point is north pole, assumption outside");
                location = 0;
                return location;
            }

            //Consider Antarctica Points  on land
            if(latP < -85.1) {
                location = 1;
                return location;
            }

            double tlonP = transformLongitude(90.0, 0.0, latP, lngP);


            for(int i=0; i< coastline.getNumberOfNodes(); i++) {
                int noNodes = coastline.getNumberOfNodes();
                double powerCorrection = Math.pow(10.0, 7);
                //get arc AB coordinates
                double longA = Math.round(data.getNode(coastline.getNodeId(i%noNodes)).getLongitude() * powerCorrection) /powerCorrection;
                double latA = Math.round(data.getNode(coastline.getNodeId(i%noNodes)).getLatitude() * powerCorrection) / powerCorrection;
                double tlonA = longA;

                double longB = Math.round(data.getNode(coastline.getNodeId((i+1)%noNodes)).getLongitude() * powerCorrection) / powerCorrection;
                double latB = Math.round(data.getNode(coastline.getNodeId((i+1)%noNodes)).getLatitude() * powerCorrection) / powerCorrection;
                double tlonB = longB;

                //arc AB is along the same longitude, vertical edge to be considered as passing right of the ray
                if(longA == longB)
                    continue;

                boolean isStrike = false;

                if(tlonP == tlonA)
                    isStrike = true;
                else {
                    int ibrngAB = eastOrWest(tlonA,tlonB);
                    int ibrngAP = eastOrWest(tlonA,tlonP);
                    int ibrngPB = eastOrWest(tlonP,tlonB);

                    if(ibrngAP == ibrngAB && ibrngPB == ibrngAB)
                        isStrike = true;
                }

                if(isStrike ) {
                    if(latP == latA && lngP == longA) {
                        location = 2;
                        System.out.println("P lies on vertex of S");
                        return location;
                    }

                    double tlon_X = transformLongitude(latA, longA, 90.0, 0.0);
                    double tlon_B = transformLongitude(latA, longA, latB, longB);
                    double tlon_P = transformLongitude(latA, longA, latP, lngP);

                    if(tlon_P == tlonB) {
                        location = 2;
                        System.out.println("P lies on the side of S");
                        return location;
                    }
                    else {
                        int ibrng_BX = eastOrWest(tlon_B, tlon_X);
                        int ibrng_BP = eastOrWest(tlon_B, tlon_P);
                        if(ibrng_BX == -ibrng_BP)
                            count++;
                    }
                }
            }

            if(count%2 == 1) {
                location = 1;
                System.out.println("Point is inside");
                return location;
            }
        }
        System.out.println("Point is outside");
        return location;
    }

}
