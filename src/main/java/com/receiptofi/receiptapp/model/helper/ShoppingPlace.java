package com.receiptofi.receiptapp.model.helper;

import com.google.common.collect.Ordering;
import com.receiptofi.receiptapp.model.types.DistanceUnit;

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

    public void computeDistanceFromLocation(double lat, double lng) {
        for (Coordinate coordinate : coordinates) {
            distance.add(coordinate.getDistance(lat, lng));
        }

        distance = SORT_BY_DISTANCE.reverse().sortedCopy(distance);
    }
}
