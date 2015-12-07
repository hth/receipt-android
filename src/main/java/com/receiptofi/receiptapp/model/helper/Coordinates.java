package com.receiptofi.receiptapp.model.helper;

import com.google.common.base.Objects;

/**
 * User: hitender
 * Date: 12/5/15 7:28 PM
 */
public class Coordinates {
    private double lat;
    private double lng;

    public Coordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Objects.equal(lat, that.lat) &&
                Objects.equal(lng, that.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lat, lng);
    }
}
