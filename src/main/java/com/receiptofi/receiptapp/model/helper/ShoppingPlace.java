package com.receiptofi.receiptapp.model.helper;

import com.google.common.collect.Ordering;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 12/8/15 5:24 AM
 */
public class ShoppingPlace {

    private String bizName;
    private List<Date> lastShopped = new LinkedList<>();
    private Set<Coordinate> coordinates = new HashSet<>();
    private List<Double> distance = new LinkedList<>();

    private static Ordering<Double> SORT_BY_DISTANCE = new Ordering<Double>() {
        public int compare(Double right, Double left) {
            return Double.compare(right, left);
        }
    };

    public ShoppingPlace(String bizName) {
        this.bizName = bizName;
    }

    public String getBizName() {
        return bizName;
    }

    public List<Date> getLastShopped() {
        return lastShopped;
    }

    public void addLastShopped(Date lastShopped) {
        this.lastShopped.add(lastShopped);
    }

    public Set<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void addCoordinates(Coordinate coordinate) {
        this.coordinates.add(coordinate);
    }

    public List<Double> getDistance() {
        return distance;
    }

    public void computeDistanceFromLocation(Coordinate location) {
        for (Coordinate coordinate : coordinates) {
            distance.add(distance(location.getLat(), location.getLng(), coordinate.getLat(), coordinate.getLng(), 'M'));
        }

        distance = SORT_BY_DISTANCE.reverse().sortedCopy(distance);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
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
