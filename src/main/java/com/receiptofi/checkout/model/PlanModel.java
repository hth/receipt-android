package com.receiptofi.checkout.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * User: hitender
 * Date: 6/28/15 9:46 AM
 */
public class PlanModel implements Parcelable {

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

    @Override
    public String toString() {
        return "PlanModel{" +
                "id='" + id + '\'' +
                ", price=" + price +
                ", billingFrequency='" + billingFrequency + '\'' +
                ", description='" + description + '\'' +
                ", billingDayOfMonth='" + billingDayOfMonth + '\'' +
                ", name='" + name + '\'' +
                ", paymentGateway='" + paymentGateway + '\'' +
                ", billingPlan='" + billingPlan + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeValue(this.price);
        dest.writeString(this.billingFrequency);
        dest.writeString(this.description);
        dest.writeString(this.billingDayOfMonth);
        dest.writeString(this.name);
        dest.writeString(this.paymentGateway);
        dest.writeString(this.billingPlan);
    }

    private PlanModel(Parcel in) {
        this.id = in.readString();
        this.price = (Double) in.readValue(Double.class.getClassLoader());
        this.billingFrequency = in.readString();
        this.description = in.readString();
        this.billingDayOfMonth = in.readString();
        this.name = in.readString();
        this.paymentGateway = in.readString();
        this.billingPlan = in.readString();
    }

    public static final Parcelable.Creator<PlanModel> CREATOR = new Parcelable.Creator<PlanModel>() {
        public PlanModel createFromParcel(Parcel source) {
            return new PlanModel(source);
        }

        public PlanModel[] newArray(int size) {
            return new PlanModel[size];
        }
    };
}
