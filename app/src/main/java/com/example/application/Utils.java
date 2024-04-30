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
        final double EARTH_RADIUS_KM = 6371;
        double radlon1 = Math.toRadians(position.getLongitude());
        double radlat1 = Math.toRadians(position.getLatitude());
        double radlon2 = Math.toRadians(incidentPoint.getLongitude());
        double radlat2 = Math.toRadians(incidentPoint.getLatitude());
        double dLon = radlon2 - radlon1;
        double dLat = radlat2 - radlat1;
        double a = Math.sin(dLat / 2)  *  Math.sin(dLat / 2) +
                Math.cos(radlat1)  *  Math.cos(radlat2)  *
                        Math.sin(dLon / 2)  *  Math.sin(dLon / 2);
        double c = 2  *  Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        BigDecimal bd = new BigDecimal(EARTH_RADIUS_KM * c);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
