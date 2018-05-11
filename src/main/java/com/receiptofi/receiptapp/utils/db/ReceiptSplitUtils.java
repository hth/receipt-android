package com.receiptofi.receiptapp.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.model.ReceiptSplitModel;

import java.util.LinkedList;
import java.util.List;

import static com.receiptofi.receiptapp.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 10/18/15 7:44 PM
 */
public class ReceiptSplitUtils {

    private static final String TAG = ReceiptSplitUtils.class.getSimpleName();
    private ReceiptSplitUtils() {
    }

    /**
     * Insert receipts in table.
     *
     * @param receiptSplitModels
     */
    public static void insert(List<ReceiptSplitModel> receiptSplitModels) {
        for (ReceiptSplitModel receiptSplitModel : receiptSplitModels) {
            insert(receiptSplitModel);
        }
    }

    public static boolean delete(String id) {
        return RDH.getWritableDatabase().delete(
                DatabaseTable.ReceiptSplit.TABLE_NAME,
                DatabaseTable.ReceiptSplit.ID + " = '" + id + "'",
                null
        ) > 0;
    }

    /**
     * Insert receiptSplitModel in table.
     *
     * @param receiptSplitModel
     */
    private static void insert(ReceiptSplitModel receiptSplitModel) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.ReceiptSplit.ID, receiptSplitModel.getId());
        values.put(DatabaseTable.ReceiptSplit.RID, receiptSplitModel.getRid());
        values.put(DatabaseTable.ReceiptSplit.NAME, receiptSplitModel.getName());
        values.put(DatabaseTable.ReceiptSplit.NAME_INITIALS, receiptSplitModel.getInitials());

        ReceiptofiApplication.RDH.getWritableDatabase().delete(
                DatabaseTable.ReceiptSplit.TABLE_NAME,
                "id = ?",
                new String[]{receiptSplitModel.getId()}
        );

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.ReceiptSplit.TABLE_NAME,
                null,
                values
        );
    }

    public static List<ReceiptSplitModel> getReceiptSplit(String rID)
    {
        List<ReceiptSplitModel> list = new LinkedList<>();
        Cursor cursor = null;
        try {
           /* cursor = RDH.getReadableDatabase().query(DatabaseTable.ReceiptSplit.TABLE_NAME,null,DatabaseTable.ReceiptSplit.RID + "=?",new String[]{rID},null,null,null);*/
            cursor = RDH.getReadableDatabase().rawQuery("Select * from "+ DatabaseTable.ReceiptSplit.TABLE_NAME + " where rid = ?",new String[]{rID});
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ReceiptSplitModel item = new ReceiptSplitModel(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
                    list.add(item);
                }
                }

            }catch (Exception e) {
            Log.e(TAG, "Error getting expense tag " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return list;
    }
}
