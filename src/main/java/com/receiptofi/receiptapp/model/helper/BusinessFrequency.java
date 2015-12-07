package com.receiptofi.receiptapp.model.helper;

import com.google.common.base.Objects;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hitender
 * Date: 12/5/15 11:43 AM
 */
public class BusinessFrequency {
    /** To compute visits in last # of weeks. */
    public static final int WEEKS = 9;
    private String bizName;

    /**
     * How long is the shopping history. Or since when did user started visiting this place.
     */
    private int weeksOfShoppingHistory;

    /**
     * How often this business was visited in last BusinessFrequency.WEEKS.
     */
    private int frequency;

    private List<DateTime> visits = new ArrayList<>();
    private Set<Coordinates> coordinates = new HashSet<>();

    public BusinessFrequency(String bizName) {
        this.bizName = bizName;
    }

    public String getBizName() {
        return bizName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public double multiplier() {
        return WEEKS / frequency;
    }

    public int getWeeksOfShoppingHistory() {
        return weeksOfShoppingHistory;
    }

    public void setWeeksOfShoppingHistory(int weeksOfShoppingHistory) {
        this.weeksOfShoppingHistory = weeksOfShoppingHistory;
    }

    public List<DateTime> getVisits() {
        return visits;
    }

    public void addVisit(DateTime visit) {
        this.visits.add(visit);
    }

    public int totalVisits() {
        return visits.size();
    }

    public Set<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void addCoordinates(Coordinates coordinate) {
        this.coordinates.add(coordinate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessFrequency that = (BusinessFrequency) o;
        return Objects.equal(bizName, that.bizName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bizName);
    }
}
