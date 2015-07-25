package com.receiptofi.receipts.service;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.receiptofi.receipts.model.ChartModel;
import com.receiptofi.receipts.model.ReceiptModel;
import com.receiptofi.receipts.utils.db.ReceiptUtils;

import java.util.ArrayList;

/**
 * User: hitender
 * Date: 1/9/15 4:27 PM
 */
public class ChartService {
    private static final String TAG = ChartService.class.getSimpleName();

    private ChartService() {
    }

    public static PieData getPieData() {
        ChartModel chartModel = ReceiptUtils.getReceiptsByBizName();

        ArrayList<Entry> yVals1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        int i = 0;
        for (ReceiptModel receiptModel : chartModel.getReceiptModels()) {
            yVals1.add(new Entry((float) (receiptModel.getTotal() / chartModel.getTotal()), i));
            xVals.add(receiptModel.getBizName());

            i++;
        }

        PieDataSet pieDataSet = new PieDataSet(yVals1, "Exp/Business");
        pieDataSet.setSliceSpace(1f);
        setColors(pieDataSet);

        return new PieData(xVals, pieDataSet);
    }

    private static void setColors(PieDataSet pieDataSet) {
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }

        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }

        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);
    }
}
