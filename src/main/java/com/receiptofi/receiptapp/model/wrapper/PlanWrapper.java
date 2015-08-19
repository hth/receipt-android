package com.receiptofi.receiptapp.model.wrapper;

import com.google.common.collect.Ordering;
import com.receiptofi.receiptapp.model.PlanModel;

import junit.framework.Assert;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static org.joda.time.Hours.hoursBetween;

/**
 * User: hitender
 * Date: 7/5/15 9:50 AM
 */
public class PlanWrapper {
    private static final String TAG = PlanWrapper.class.getSimpleName();
    public static final int CACHE_PLAN_HOURS = 24;

    private static List<PlanModel> planModels = new ArrayList<>();
    private static DateTime lastUpdated;

    private static final Ordering<PlanModel> byPriceOrdering = new Ordering<PlanModel>() {
        public int compare(PlanModel left, PlanModel right) {
            return Double.compare(left.getPrice(), right.getPrice());
        }
    };

    private PlanWrapper() {
    }

    public static List<PlanModel> getPlanModels() {
        return planModels;
    }

    public static void setPlanModels(List<PlanModel> planModels) {
        Assert.assertNotNull("Plan Model list should not be null", planModels);
        PlanWrapper.planModels = byPriceOrdering.sortedCopy(planModels);
        PlanWrapper.lastUpdated = DateTime.now();
    }

    public static boolean refresh() {
        return null == lastUpdated || hoursBetween(lastUpdated, DateTime.now()).getHours() > CACHE_PLAN_HOURS;
    }
}
