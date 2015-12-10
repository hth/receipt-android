package com.receiptofi.receiptapp.model.helper;

import com.google.common.base.Objects;
import com.receiptofi.receiptapp.model.types.DistanceUnit;

/**
 * User: hitender
 * Date: 12/5/15 7:28 PM
 */
public class Coordinate {
    private double lat;
    private double lng;
    private String address;
    /** Compute distance from current location. */
    private double distance;

    public Coordinate(double lat, double lng, String address) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }

    public double getDistance(double lat, double lng) {
        distance = distance(lat, lng, DistanceUnit.M);
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Objects.equal(lat, that.lat) &&
                Objects.equal(lng, that.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lat, lng);
    }

    private double distance(double lat2, double lon2, DistanceUnit unit) {
        double theta = lng - lon2;
        double dist = Math.sin(deg2rad(lat)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == DistanceUnit.K) {
            dist = dist * 1.609344;
        } else if (unit == DistanceUnit.N) {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
