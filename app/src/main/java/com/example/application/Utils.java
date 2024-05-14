package com.example.application;

import android.location.Location;
import com.google.android.gms.tasks.Task;
import com.yandex.mapkit.geometry.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {

    public static boolean isNumber(String str) {
        try {
            Long phone = Long.parseLong("8".concat(str));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double getLocation(Task<Location> task, Point point) {
        AtomicReference<Double> value = new AtomicReference<>(0.0);
        task.addOnCompleteListener(location -> {
            Point p = new Point(location.getResult().getLatitude(), location.getResult().getLongitude());
            value.set(Utils.calculateDistanceBetweenPoints(point, p));
        });
        return value.get();
    }


    public static String getLocalIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        assert sAddr != null;
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "";
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
