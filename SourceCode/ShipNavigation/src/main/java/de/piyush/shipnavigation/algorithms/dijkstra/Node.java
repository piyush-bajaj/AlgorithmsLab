package de.piyush.shipnavigation.algorithms.dijkstra;

import java.math.BigDecimal;

public class Node {

    final private int id;
    final private double lat;
    final private double lng;


    public Node(int id, double lat, double lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }
    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lng);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        
        BigDecimal blat = new BigDecimal(lat).setScale(7, BigDecimal.ROUND_HALF_UP);
        BigDecimal bolat = new BigDecimal(other.lat).setScale(7, BigDecimal.ROUND_HALF_UP);
        
        BigDecimal blng = new BigDecimal(lng).setScale(7, BigDecimal.ROUND_HALF_UP);
        BigDecimal bolng = new BigDecimal(other.lng).setScale(7, BigDecimal.ROUND_HALF_UP);
        
        if (!blat.equals(bolat) )
            return false;
        if (!blng.equals(bolng))
            return false;
        
        return true;
    }

    @Override
    public String toString() {
        return id + "," + lat + "," + lng;
    }

}