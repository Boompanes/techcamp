package com.findeds.zagip.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The Geographical information
 *
 * @author Axel S. Trajano
 * @since May 4, 2012
 */
public class Geo implements Cloneable {

    private final static String DATE_FORMAT = "MMM dd, yyyy HH:mm:ss";
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private double latitude;
    private double longitude;
    private String timestamp;

    public Geo() {
        this.address = "";
        this.latitude = 0;
        this.longitude = 0;
        this.timestamp = getCurrentDate(DATE_FORMAT);
    }

    public static String convertToString(Date date, String format) {
        Date tmpDate = date;

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

        return sdf.format(tmpDate);
    }

    public static String getCurrentDate(String format) {

        Date tmpDate = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(tmpDate);

    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTime(long time) {
        Date date = new Date(time);
        this.timestamp = convertToString(date, DATE_FORMAT);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String name) {
        this.address = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean setAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() <= 0)
                return false;
            Address obj = addresses.get(0);

            StringBuilder address = new StringBuilder();
            if (obj.getFeatureName() != null) {
                address.append(obj.getFeatureName());
                setName(obj.getFeatureName());
            }

            if (obj.getSubLocality() != null) {
                if (address.length() > 1)
                    address.append(", ");
                address.append(obj.getSubLocality());
            }

            if (obj.getLocality() != null) {
                if (address.length() > 1)
                    address.append(", ");
                address.append(obj.getLocality());
            }

            if (obj.getAdminArea() != null) {
                if (address.length() > 1)
                    address.append(", ");
                address.append(obj.getAdminArea());
            }
            if (obj.getCountryName() != null) {
                if (address.length() > 1)
                    address.append(", ");
                address.append(obj.getCountryName());
            }

            this.address = address.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getSummary() {

        StringBuilder summary = new StringBuilder();
        summary.append("Address: ");
        summary.append(address);
        summary.append("\nLatitude: ");
        summary.append(latitude);
        summary.append("\nLongitude: ");
        summary.append(longitude);
        summary.append("\nDate: ");
        summary.append(timestamp);

        return summary.toString();

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
