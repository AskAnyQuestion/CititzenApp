package com.example.application;

import com.yandex.mapkit.geometry.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {

    public static boolean isNumber(String str) {
        try {
            Long phone = Long.parseLong("8".concat(str));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double calculateDistanceBetweenPoints(Point position, Point incidentPoint) {
        final double EARTH_RADIUS = 6371000;
        double radiansLat1 = Math.toRadians(position.getLatitude());
        double radiansLon1 = Math.toRadians(position.getLongitude());
        double radiansLat2 = Math.toRadians(incidentPoint.getLatitude());
        double radiansLon2 = Math.toRadians(incidentPoint.getLongitude());
        double deltaLat = radiansLat2 - radiansLat1;
        double deltaLon = radiansLon2 - radiansLon1;
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(radiansLat1) * Math.cos(radiansLat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        BigDecimal bd = new BigDecimal(EARTH_RADIUS * c);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
