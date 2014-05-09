package com.findeds.zagip.entity;

import com.turbomanage.storm.api.Entity;

/**
 * POJO used to create SQLite Account table.
 * <p/>
 * Uses storm-gen, a Lightweight DAO generator for Android SQLite.
 * Please be aware that this requires storm-apt for annotation.
 * See https://code.google.com/p/storm-gen/ for more info.
 *
 * @author Emmanuel Delos Santos
 * @since 3/16/14.
 */
@Entity(name = "Travel")
public class TravelTB {
    private long id;
    private double lat1;
    private double lon1;
    private double lat2;
    private double lon2;
    private String name1;
    private String name2;
    private String distance;
    private String caption;
    private long time;
    private long schedule;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat1() {
        return lat1;
    }

    public void setLat1(double lat1) {
        this.lat1 = lat1;
    }

    public double getLon1() {
        return lon1;
    }

    public void setLon1(double lon1) {
        this.lon1 = lon1;
    }

    public double getLat2() {
        return lat2;
    }

    public void setLat2(double lat2) {
        this.lat2 = lat2;
    }

    public double getLon2() {
        return lon2;
    }

    public void setLon2(double lon2) {
        this.lon2 = lon2;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSchedule() {
        return schedule;
    }

    public void setSchedule(long schedule) {
        this.schedule = schedule;
    }
}
