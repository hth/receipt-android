package com.receiptofi.checkout.model.wrapper;

import com.receiptofi.checkout.model.PlanModel;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 7/5/15 9:50 AM
 */
public class PlanWrapper {
    private List<PlanModel> planModels = new LinkedList<>();

    public List<PlanModel> getPlanModels() {
        return planModels;
    }

    public void setPlanModels(List<PlanModel> planModels) {
        this.planModels = planModels;
    }
}
