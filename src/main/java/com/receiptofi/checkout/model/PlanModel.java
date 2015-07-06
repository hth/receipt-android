package com.receiptofi.checkout.model;

import android.os.Bundle;

/**
 * User: hitender
 * Date: 6/28/15 9:46 AM
 */
public class PlanModel {

    private String id;
    private Double price;
    private String billingFrequency;
    private String description;
    private String billingDayOfMonth;
    private String name;
    private String paymentGateway;
    private String billingPlan;

    // needed for view
    public PlanModel() {

    }

    public PlanModel(
            String id,
            Double price,
            String billingFrequency,
            String description,
            String billingDayOfMonth,
            String name,
            String paymentGateway,
            String billingPlan
    ) {
        this.id = id;
        this.price = price;
        this.billingFrequency = billingFrequency;
        this.description = description;
        this.billingDayOfMonth = billingDayOfMonth;
        this.name = name;
        this.paymentGateway = paymentGateway;
        this.billingPlan = billingPlan;
    }

    public String getId() {
        return id;
    }

    public Double getPrice() {
        return price;
    }

    public String getBillingFrequency() {
        return billingFrequency;
    }

    public String getDescription() {
        return description;
    }

    public String getBillingDayOfMonth() {
        return billingDayOfMonth;
    }

    public String getName() {
        return name;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public String getBillingPlan() {
        return billingPlan;
    }

    public Bundle getAsBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putDouble("price", price);
        bundle.putString("billingFrequency", String.valueOf(billingFrequency));
        bundle.putString("description", description);
        bundle.putString("billingDayOfMonth", String.valueOf(billingDayOfMonth));
        bundle.putString("name", name);
        bundle.putString("paymentGateway", paymentGateway);
        bundle.putString("billingPlan", billingPlan);

        return bundle;
    }
}
