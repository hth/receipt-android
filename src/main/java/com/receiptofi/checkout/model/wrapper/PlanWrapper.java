package com.receiptofi.checkout.model.wrapper;

import android.util.Log;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Ordering;
import com.receiptofi.checkout.model.PlanModel;

import junit.framework.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: hitender
 * Date: 7/5/15 9:50 AM
 */
public class PlanWrapper {
    private static final String TAG = PlanWrapper.class.getSimpleName();

    private static final int SIZE_1 = 1;
    private static final int planCacheMinutes = 3;
    public static final String PLANS = "PLANS";

    private static Cache<String, List<PlanModel>> planCache = CacheBuilder.newBuilder()
            .maximumSize(SIZE_1)
            .expireAfterWrite(planCacheMinutes, TimeUnit.MINUTES)
            .build();

    private static final Ordering<PlanModel> byPriceOrdering = new Ordering<PlanModel>() {
        public int compare(PlanModel left, PlanModel right) {
            return Double.compare(left.getPrice(), right.getPrice());
        }
    };

    private PlanWrapper() {
    }

    public static List<PlanModel> getPlanModels() {
        List<PlanModel> planModels = planCache.getIfPresent(PLANS);
        if (null == planModels) {
            planModels = new LinkedList<>();
            planCache.put(PLANS, planModels);
            return planModels;
        } else {
            return planModels;
        }
    }

    public static void setPlanCache(List<PlanModel> planModels) {
        Assert.assertNotNull("Plan Model list should not be null", planModels);
        planCache.put(PLANS, byPriceOrdering.sortedCopy(planModels));
    }

    public static int findPosition(String id) {
        Log.d(TAG, "Finding position in plan list for planId=" + id);
        List<PlanModel> planModels = planCache.getIfPresent(PLANS);
        int position = -1;
        for (PlanModel planModel : planModels) {
            position ++;
            if (planModel.getId().equals(id)) {
                break;
            }
        }
        return position;
    }
}
