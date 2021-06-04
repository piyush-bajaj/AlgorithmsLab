package de.piyush.navigation.datatypes;

public class Path {
	
	private final int distance;
	private final double points[][];
	

	public Path(int distance, double points[][]) {
		super();
		this.distance = distance;
		this.points = points;
	}



	public int getDistance() {
		return distance;
	}	
	
	public double[][] getPoints() {
		return points;
	}

}
