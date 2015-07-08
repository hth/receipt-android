package com.receiptofi.checkout.utils;

/**
 * Contains JSON field constants.
 * <p/>
 * User: hitender
 * Date: 5/2/15 3:59 PM
 */
public class ConstantsJson {
    /** JSON representation from Server. */
    public static final String PROFILE = "profile";
    public static final String ITEMS = "items";
    public static final String RECEIPTS = "receipts";
    public static final String EXPENSE_TAGS = "expenseTags";
    public static final String UNPROCESSED_DOCUMENTS = "unprocessedDocuments";
    public static final String NOTIFICATIONS = "notifications";
    public static final String BILLING = "billing";
    public static final String UNPROCESSED_COUNT = "unprocessedCount";
    public static final String UPLOADED_DOCUMENT_NAME = "uploadedDocumentName";

    /** JSON data for Receipt Action. */
    public static final String EXPENSE_TAG_ID = "expenseTagId";
    public static final String NOTES = "notes";
    public static final String RECHECK = "recheck";
    public static final String RECEIPT_ID = "receiptId";

    /** JSON data for Subscription Payment. */
    public static final String PLAN_ID = "planId";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String POSTAL = "postal";
    public static final String COMPANY = "company";
    public static final String PAYMENT_NONCE = "payment-method-nonce";
}
