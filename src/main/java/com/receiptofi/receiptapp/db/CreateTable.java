package com.receiptofi.receiptapp.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.Profile.TABLE_NAME + "("
                + DatabaseTable.Profile.FIRST_NAME + " TEXT ,"
                + DatabaseTable.Profile.LAST_NAME + " TEXT ,"
                + DatabaseTable.Profile.MAIL + " TEXT ,"
                + DatabaseTable.Profile.NAME + " TEXT ,"
                + DatabaseTable.Profile.RID + " TEXT " +

                ");");
    }

    public static void createTableReceipts(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableReceipts");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.Receipt.TABLE_NAME + "("
                + DatabaseTable.Receipt.BIZ_NAME + " TEXT ,"
                + DatabaseTable.Receipt.BIZ_STORE_ADDRESS + " TEXT ,"
                + DatabaseTable.Receipt.BIZ_STORE_PHONE + " TEXT ,"
                + DatabaseTable.Receipt.LAT + " DOUBLE ,"
                + DatabaseTable.Receipt.LNG + " DOUBLE ,"
                + DatabaseTable.Receipt.TYPE + " TEXT ,"
                + DatabaseTable.Receipt.RATING + " DOUBLE ,"
                + DatabaseTable.Receipt.RECEIPT_DATE + " TEXT ,"
                + DatabaseTable.Receipt.EXPENSE_REPORT + " TEXT ,"
                + DatabaseTable.Receipt.BLOB_IDS + " TEXT ,"
                + DatabaseTable.Receipt.ID + " TEXT UNIQUE ,"
                + DatabaseTable.Receipt.NOTES + " TEXT ,"
                + DatabaseTable.Receipt.PTAX + " DOUBLE ,"
                + DatabaseTable.Receipt.RID + " TEXT ,"
                + DatabaseTable.Receipt.TAX + " DOUBLE ,"
                + DatabaseTable.Receipt.TOTAL + " DOUBLE , "
                + DatabaseTable.Receipt.BILL_STATUS + " TEXT, "
                + DatabaseTable.Receipt.EXPENSE_TAG_ID + " TEXT, "
                + DatabaseTable.Receipt.REFER_RECEIPT_ID + " TEXT, "
                + DatabaseTable.Receipt.SPLIT_COUNT + " INT, "
                + DatabaseTable.Receipt.SPLIT_TOTAL + " DOUBLE, "
                + DatabaseTable.Receipt.SPLIT_TAX + " DOUBLE, "
                + DatabaseTable.Receipt.ACTIVE + " INT, "
                + DatabaseTable.Receipt.DELETED + " INT " +

                ");");
    }

    public static void createTableReceiptSplit(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableReceiptSplit");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.ReceiptSplit.TABLE_NAME + "("
                + DatabaseTable.ReceiptSplit.ID + " TEXT ,"
                + DatabaseTable.ReceiptSplit.RID + " TEXT ,"
                + DatabaseTable.ReceiptSplit.NAME + " TEXT ,"
                + DatabaseTable.ReceiptSplit.NAME_INITIALS + " TEXT " +

                ");");
    }

    public static void createTableImageIndex(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableImageIndex");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.ImageIndex.TABLE_NAME + "("
                + DatabaseTable.ImageIndex.BLOB_ID + " TEXT ,"
                + DatabaseTable.ImageIndex.IMAGE_PATH + " TEXT " +

                ");");
    }

    public static void createTableUploadQueue(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableUploadQueue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.UploadQueue.TABLE_NAME + "("
                + DatabaseTable.UploadQueue.IMAGE_DATE + " TEXT ,"
                + DatabaseTable.UploadQueue.IMAGE_PATH + " TEXT UNIQUE ,"
                + DatabaseTable.UploadQueue.STATUS + " TEXT " +

                ");");
    }

    public static void createTableKeyValue(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableKeyValue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.KeyValue.TABLE_NAME + "("
                + DatabaseTable.KeyValue.KEY + " TEXT ,"
                + DatabaseTable.KeyValue.VALUE + " TEXT " +

                ");");
    }

    public static void createTableMonthlyReport(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableMonthlyReports");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.MonthlyReport.TABLE_NAME + "("
                + DatabaseTable.MonthlyReport.MONTH + " TEXT ,"
                + DatabaseTable.MonthlyReport.YEAR + " TEXT ,"
                + DatabaseTable.MonthlyReport.TOTAL + " DOUBLE ,"
                + DatabaseTable.MonthlyReport.COUNT + " INT " +

                ");");
        Log.d(TAG, "Finished executing createTableMonthlyReports");
    }

    public static void createTableItem(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableItem");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.Item.TABLE_NAME + "("
                + DatabaseTable.Item.ID + " TEXT ,"
                + DatabaseTable.Item.NAME + " TEXT ,"
                + DatabaseTable.Item.PRICE + " DOUBLE ,"
                + DatabaseTable.Item.QUANTITY + " TEXT ,"
                + DatabaseTable.Item.RECEIPTID + " TEXT ,"
                + DatabaseTable.Item.SEQUENCE + " TEXT ,"
                + DatabaseTable.Item.TAX + " TEXT ,"
                + DatabaseTable.Item.EXPENSE_TAG_ID + " TEXT " +

                ");");
        Log.d(TAG, "Finished executing createTableItem");
    }

    public static void createTableExpenseTag(SQLiteDatabase db) {
        Log.d(TAG, "executing createExpenseTag");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.ExpenseTag.TABLE_NAME + "("
                + DatabaseTable.ExpenseTag.ID + " TEXT ,"
                + DatabaseTable.ExpenseTag.NAME + " TEXT ,"
                + DatabaseTable.ExpenseTag.COLOR + " TEXT ,"
                + DatabaseTable.ExpenseTag.DELETED + " BOOLEAN " +

                ");");
        Log.d(TAG, "Finished executing createTableItem");
    }

    public static void createTableNotification(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableNotification");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.Notification.TABLE_NAME + "("
                + DatabaseTable.Notification.ID + " TEXT ,"
                + DatabaseTable.Notification.MESSAGE + " TEXT ,"
                + DatabaseTable.Notification.VISIBLE + " BOOLEAN ,"
                + DatabaseTable.Notification.NOTIFICATION_TYPE + " TEXT ,"
                + DatabaseTable.Notification.REFERENCE_ID + " TEXT ,"
                + DatabaseTable.Notification.CREATED + " TEXT ,"
                + DatabaseTable.Notification.UPDATED + " TEXT ,"
                + DatabaseTable.Notification.ACTIVE + " BOOLEAN " +

                ");");
        Log.d(TAG, "Finished executing createTableNotification");
    }

    public static void createTableBillingAccount(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableNotification");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.BillingAccount.TABLE_NAME + "("
                + DatabaseTable.BillingAccount.ACCOUNT_BILLING_TYPE + " TEXT " +

                ");");
        Log.d(TAG, "Finished executing createTableBillingAccount");
    }

    public static void createTableBillingHistory(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableNotification");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.BillingHistory.TABLE_NAME + "("
                + DatabaseTable.BillingHistory.ID + " TEXT ,"
                + DatabaseTable.BillingHistory.BILLED_MONTH + " TEXT ,"
                + DatabaseTable.BillingHistory.BILLED_STATUS + " TEXT ,"
                + DatabaseTable.BillingHistory.ACCOUNT_BILLING_TYPE + " TEXT ,"
                + DatabaseTable.BillingHistory.BILLED_DATE + " TEXT " +

                ");");
        Log.d(TAG, "Finished executing createTableBillingHistory");
    }

    public static void createTableItemReceipt(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableItemReceipt");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.ItemReceipt.TABLE_NAME + "("
                + DatabaseTable.ItemReceipt.RECEIPT_ID + " TEXT ,"
                + DatabaseTable.ItemReceipt.BIZ_NAME + " TEXT ,"
                + DatabaseTable.ItemReceipt.BIZ_STORE_ADDRESS + " TEXT ,"
                + DatabaseTable.ItemReceipt.LAT + " DOUBLE ,"
                + DatabaseTable.ItemReceipt.LNG + " DOUBLE ,"
                + DatabaseTable.ItemReceipt.RECEIPT_DATE + " TEXT ,"
                + DatabaseTable.ItemReceipt.EXPENSE_TAG_ID + " TEXT ,"
                + DatabaseTable.ItemReceipt.ITEM_ID + " TEXT ,"
                + DatabaseTable.ItemReceipt.NAME + " TEXT ,"
                + DatabaseTable.ItemReceipt.PRICE + " DOUBLE ,"
                + DatabaseTable.ItemReceipt.QUANTITY + " TEXT ,"
                + DatabaseTable.ItemReceipt.TAX + " DOUBLE ,"
                + DatabaseTable.ItemReceipt.ACTIVE + " INT ,"
                + DatabaseTable.ItemReceipt.DELETED + " INT " +

                ");");
    }

    public static void createTableShoppingItem(SQLiteDatabase db) {
        Log.d(TAG, "executing createTableShoppingItem");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.ShoppingItem.TABLE_NAME + "("
                + DatabaseTable.ShoppingItem.NAME + " TEXT ,"
                + DatabaseTable.ShoppingItem.CUSTOM_NAME + " TEXT ,"
                + DatabaseTable.ShoppingItem.BIZ_NAME + " TEXT ,"
                + DatabaseTable.ShoppingItem.COUNT + " INT ,"
                + DatabaseTable.ShoppingItem.SMOOTH_COUNT + " DOUBLE " +

                ");");
    }
}
