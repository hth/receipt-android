package com.receiptofi.checkout.db;

public class DatabaseTable {

    public static String DB_NAME = "receiptofi";

    public static class Receipt {
        public static final String TABLE_NAME = "RECEIPT";
        public static final String BIZ_NAME = "bizName";
        public static final String BIZ_STORE_ADDRESS = "address";
        public static final String BIZ_STORE_PHONE = "phone";
        public static final String DATE = "date";
        public static final String EXPENSE_REPORT = "expenseReport";
        public static final String FILES_BLOB = "blobIds";
        //TODO remove
        public static final String FILES_ORIENTATION = "filesOrientation";
        public static final String ID = "documentId";
        public static final String NOTES = "notes";
        public static final String P_TAX = "ptax";
        public static final String R_ID = "rid";
        //TODO remove
        public static final String SEQUENCE = "sequence";
        public static final String TOTAL = "total";
    }

    public static class ImageIndex {
        public static final String TABLE_NAME = "IMAGE_INDEX";
        public static final String BLOB_ID = "blobId";
        public static final String IMAGE_PATH = "imagePath";

    }

    public static class UploadQueue {
        public static final String TABLE_NAME = "UPLOAD_QUEUE";
        public static final String IMAGE_DATE = "imageDate";
        public static final String IMAGE_PATH = "imagePath";
        public static final String STATUS = "status";
    }

    public static class KeyValue {
        public static final String TABLE_NAME = "KEY_VALUE";
        public static final String KEY = "key";
        public static final String VALUE = "value";
    }

    public static class MonthlyReport {
        public static final String TABLE_NAME = "MONTHLY_REPORT";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
        public static final String TOTAL_AMT = "totalAmt";
        public static final String RECEIPT_COUNT = "receiptCount";
    }
}
