package de.piyush.shipnavigation;

public class Constants {
    private static final double DEGREE_TO_RADIAN = Math.PI / 180;

    private static final double RADIAN_TO_DEGREE = 180 / Math.PI;

    private static final double POWER_CORRECTION = Math.pow(10, 7);

    public static double toRadians(double degree) {
        return degree * DEGREE_TO_RADIAN;
    }

    public static double toDegrees(double radian) {
        return radian * RADIAN_TO_DEGREE;
    }

}
