package com.receiptofi.receiptapp.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.receiptofi.receiptapp.db.DatabaseTable.Profile;
import static com.receiptofi.receiptapp.db.DatabaseTable.Receipt;
import static com.receiptofi.receiptapp.db.DatabaseTable.ReceiptSplit;
import static com.receiptofi.receiptapp.db.DatabaseTable.ImageIndex;
import static com.receiptofi.receiptapp.db.DatabaseTable.UploadQueue;
import static com.receiptofi.receiptapp.db.DatabaseTable.KeyValue;
import static com.receiptofi.receiptapp.db.DatabaseTable.MonthlyReport;
import static com.receiptofi.receiptapp.db.DatabaseTable.Item;
import static com.receiptofi.receiptapp.db.DatabaseTable.ExpenseTag;
import static com.receiptofi.receiptapp.db.DatabaseTable.Notification;
import static com.receiptofi.receiptapp.db.DatabaseTable.BillingAccount;
import static com.receiptofi.receiptapp.db.DatabaseTable.BillingHistory;
import static com.receiptofi.receiptapp.db.DatabaseTable.ItemReceipt;
import static com.receiptofi.receiptapp.db.DatabaseTable.ShoppingItem;

/**
 * User: hitender
 * Date: 1/17/15 7:57 PM
 */
public class CreateTable {
    private static final String TAG = CreateTable.class.getSimpleName();

    private CreateTable() {
    }

    public static void createTableProfile(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableReceipts");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Profile.TABLE_NAME + "("
                + Profile.FIRST_NAME + " TEXT ,"
                + Profile.LAST_NAME + " TEXT ,"
                + Profile.MAIL + " TEXT ,"
                + Profile.NAME + " TEXT ,"
                + Profile.RID + " TEXT " +

                ");");
    }

    public static void createTableReceipts(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableReceipts");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Receipt.TABLE_NAME + "("
                + Receipt.BIZ_NAME + " TEXT ,"
                + Receipt.BIZ_STORE_ADDRESS + " TEXT ,"
                + Receipt.BIZ_STORE_PHONE + " TEXT ,"
                + Receipt.LAT + " DOUBLE ,"
                + Receipt.LNG + " DOUBLE ,"
                + Receipt.RECEIPT_DATE + " TEXT ,"
                + Receipt.EXPENSE_REPORT + " TEXT ,"
                + Receipt.BLOB_IDS + " TEXT ,"
                + Receipt.ID + " TEXT UNIQUE ,"
                + Receipt.NOTES + " TEXT ,"
                + Receipt.PTAX + " DOUBLE ,"
                + Receipt.RID + " TEXT ,"
                + Receipt.TAX + " DOUBLE ,"
                + Receipt.TOTAL + " DOUBLE , "
                + Receipt.EXPENSE_TAG_ID + " TEXT, "
                + Receipt.REFER_RECEIPT_ID + " TEXT, "
                + Receipt.SPLIT_COUNT + " INT, "
                + Receipt.SPLIT_TOTAL + " DOUBLE, "
                + Receipt.SPLIT_TAX + " DOUBLE, "
                + Receipt.COUNTRY_SHORT + " TEXT ,"
                + Receipt.PAYMENT_CARD_ID + " TEXT ,"
                + Receipt.ACTIVE + " INT, "
                + Receipt.DELETED + " INT " +

                ");");
    }

    public static void createTableReceiptSplit(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableReceiptSplit");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ReceiptSplit.TABLE_NAME + "("
                + ReceiptSplit.ID + " TEXT ,"
                + ReceiptSplit.RID + " TEXT ,"
                + ReceiptSplit.NAME + " TEXT ,"
                + ReceiptSplit.NAME_INITIALS + " TEXT " +

                ");");
    }

    public static void createTableImageIndex(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableImageIndex");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ImageIndex.TABLE_NAME + "("
                + ImageIndex.BLOB_ID + " TEXT ,"
                + ImageIndex.IMAGE_PATH + " TEXT " +

                ");");
    }

    public static void createTableUploadQueue(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableUploadQueue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + UploadQueue.TABLE_NAME + "("
                + UploadQueue.IMAGE_DATE + " TEXT ,"
                + UploadQueue.IMAGE_PATH + " TEXT UNIQUE ,"
                + UploadQueue.STATUS + " TEXT " +

                ");");
    }

    public static void createTableKeyValue(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableKeyValue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + KeyValue.TABLE_NAME + "("
                + KeyValue.KEY + " TEXT ,"
                + KeyValue.VALUE + " TEXT " +

                ");");
    }

    public static void createTableMonthlyReport(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableMonthlyReports");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MonthlyReport.TABLE_NAME + "("
                + MonthlyReport.MONTH + " TEXT ,"
                + MonthlyReport.YEAR + " TEXT ,"
                + MonthlyReport.TOTAL + " DOUBLE ,"
                + MonthlyReport.COUNT + " INT " +

                ");");
        Log.d(TAG, "Finished executing createTableMonthlyReports");
    }

    public static void createTableItem(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableItem");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Item.TABLE_NAME + "("
                + Item.ID + " TEXT ,"
                + Item.NAME + " TEXT ,"
                + Item.PRICE + " DOUBLE ,"
                + Item.QUANTITY + " TEXT ,"
                + Item.RECEIPTID + " TEXT ,"
                + Item.SEQUENCE + " TEXT ,"
                + Item.TAX + " TEXT ,"
                + Item.EXPENSE_TAG_ID + " TEXT " +

                ");");
        Log.d(TAG, "Finished executing createTableItem");
    }

    public static void createTableExpenseTag(SQLiteDatabase db) {
        Log.d(TAG, "executing createExpenseTag");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ExpenseTag.TABLE_NAME + "("
                + ExpenseTag.ID + " TEXT ,"
                + ExpenseTag.TAG + " TEXT ,"
                + ExpenseTag.COLOR + " TEXT ,"
                + ExpenseTag.ICON + " TEXT ,"
                + ExpenseTag.DELETED + " BOOLEAN " +

                ");");
        Log.d(TAG, "Finished executing createTableItem");
    }

    public static void createTableNotification(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableNotification");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Notification.TABLE_NAME + "("
                + Notification.ID + " TEXT ,"
                + Notification.MESSAGE + " TEXT ,"
                + Notification.VISIBLE + " BOOLEAN ,"
                + Notification.NOTIFICATION_TYPE + " TEXT ,"
                + Notification.REFERENCE_ID + " TEXT ,"
                + Notification.CREATED + " TEXT ,"
                + Notification.UPDATED + " TEXT ,"
                + Notification.ACTIVE + " BOOLEAN " +

                ");");
        Log.d(TAG, "Finished executing createTableNotification");
    }

    public static void createTableBillingAccount(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableNotification");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + BillingAccount.TABLE_NAME + "("
                + BillingAccount.ACCOUNT_BILLING_TYPE + " TEXT " +

                ");");
        Log.d(TAG, "Finished executing createTableBillingAccount");
    }

    public static void createTableBillingHistory(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableNotification");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + BillingHistory.TABLE_NAME + "("
                + BillingHistory.ID + " TEXT ,"
                + BillingHistory.BILLED_MONTH + " TEXT ,"
                + BillingHistory.BILLED_STATUS + " TEXT ,"
                + BillingHistory.ACCOUNT_BILLING_TYPE + " TEXT ,"
                + BillingHistory.BILLED_DATE + " TEXT " +

                ");");
        Log.d(TAG, "Finished executing createTableBillingHistory");
    }

    public static void createTableItemReceipt(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableItemReceipt");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ItemReceipt.TABLE_NAME + "("
                + ItemReceipt.RECEIPT_ID + " TEXT ,"
                + ItemReceipt.BIZ_NAME + " TEXT ,"
                + ItemReceipt.BIZ_STORE_ADDRESS + " TEXT ,"
                + ItemReceipt.LAT + " DOUBLE ,"
                + ItemReceipt.LNG + " DOUBLE ,"
                + ItemReceipt.RECEIPT_DATE + " TEXT ,"
                + ItemReceipt.EXPENSE_TAG_ID + " TEXT ,"
                + ItemReceipt.ITEM_ID + " TEXT ,"
                + ItemReceipt.NAME + " TEXT ,"
                + ItemReceipt.PRICE + " DOUBLE ,"
                + ItemReceipt.QUANTITY + " TEXT ,"
                + ItemReceipt.TAX + " DOUBLE ,"
                + ItemReceipt.ACTIVE + " INT ,"
                + ItemReceipt.DELETED + " INT " +

                ");");
    }

    public static void createTableShoppingItem(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableShoppingItem");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ShoppingItem.TABLE_NAME + "("
                + ShoppingItem.NAME + " TEXT ,"
                + ShoppingItem.CUSTOM_NAME + " TEXT ,"
                + ShoppingItem.BIZ_NAME + " TEXT ,"
                + ShoppingItem.COUNT + " INT ,"
                + ShoppingItem.SMOOTH_COUNT + " DOUBLE, "
                + ShoppingItem.CHECKED + " INT " +

                ");");
    }
}
