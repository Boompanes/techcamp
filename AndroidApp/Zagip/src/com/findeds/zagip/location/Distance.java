package com.findeds.zagip.location;

import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by Emman on 3/15/14.
 */
public class Distance {
    public static String CalculationByDistance(double lat1, double lon1, String str_lat2, String str_lon2) {
        int Radius = 6371;//radius of earth in Km
//        LatLng StartP, LatLng EndP
//        double lat1 = StartP.latitude;
//        double lat2 = EndP.latitude;
//        double lon1 = StartP.longitude;
//        double lon2 = EndP.longitude;
        double lat2 = stringToDouble(str_lat2);
        double lon2 = stringToDouble(str_lon2);
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult * 1000 % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.e("Radius Value", "lat1: " + lat1 + ", lat2: " + lat2 + ", long1: " + lon1 + ", long2 " + lon2);
        Log.e("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);
        String distance_txt = "Distance: ";
        if (valueResult > 1) {
            distance_txt += String.format("%.2f", valueResult) + " km";
        } else {
            distance_txt += String.format("%.0f", meter) + " m";
        }
        return distance_txt;
//        return Radius * c;
    }
    public static double CalculationByDistance(double lat1, double lon1, double lat2, double lon2) {
        int Radius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }

    public static double stringToDouble(String str) {
        double n = 0;
        try {
            n = Double.parseDouble(str.replaceAll(" ", "."));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(null, e.getMessage());
        }
        return n;
    }
}
