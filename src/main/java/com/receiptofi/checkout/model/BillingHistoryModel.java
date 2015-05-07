package com.receiptofi.checkout.model;

import android.util.Log;

import com.receiptofi.checkout.model.types.AccountBillingType;
import com.receiptofi.checkout.model.types.BilledStatus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * User: hitender
 * Date: 4/19/15 6:19 PM
 */
public class BillingHistoryModel {
    private static final String TAG = BillingHistoryModel.class.getSimpleName();

    private static final DateFormat SDF_DD_MMM_YYYY = new SimpleDateFormat("DD-MMM-yyyy", Locale.US);
    private static final DateFormat DF_YYYY_MM = new SimpleDateFormat("yyyy-MM", Locale.US);
    private static final DateFormat DF_MMM_YYYY = new SimpleDateFormat("MMM yyyy", Locale.US);

    private String id;
    private String billedForMonth;
    private String billedStatus;
    private String accountBillingType;
    private String billedDate;

    public BillingHistoryModel(String id, String billedForMonth, String billedStatus, String accountBillingType, String billedDate) {
        this.id = id;
        this.billedForMonth = billedForMonth;
        this.billedStatus = billedStatus;
        this.accountBillingType = accountBillingType;
        this.billedDate = billedDate;
    }

    public String getId() {
        return id;
    }

    public String getBilledForMonth() {
        return billedForMonth;
    }

    public String getBilledStatus() {
        return billedStatus;
    }

    public String getAccountBillingType() {
        return accountBillingType;
    }

    public String getBilledDate() {
        return billedDate;
    }

    public String displayBilledMonth() {
        try {
            return DF_MMM_YYYY.format(DF_YYYY_MM.parse(billedForMonth));
        } catch (ParseException e) {
            Log.e("Date parsing date=" + billedForMonth, e.getLocalizedMessage(), e);
            return "Missing";
        }
    }

    public String displayBillingType() {
        return (AccountBillingType.valueOf(accountBillingType)).getDescription();
    }

    public String displayBilledInfo() {
        switch (BilledStatus.valueOf(billedStatus)) {
            case P:
                return "NA";
            case NB:
                return "Payment Due";
            case B:
                return SDF_DD_MMM_YYYY.format(billedDate);
            default:
                throw new UnsupportedOperationException("Reached unreachable condition " + billedStatus);

        }
    }
}
