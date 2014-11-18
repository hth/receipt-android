package com.receiptofi.android.models;

public class ReceiptDB {

    public static String DB_NAME = "receiptofi";

    public static class Receipt {

        public static final String TABLE_NAME = "Receipts";
        public static final String BIZ_NAME = "bizName";
        public static final String BIZ_STORE_ADDRESS = "bizStoreAddress";
        public static final String BIZ_STORE_PHONE = "bizStorePhone";
        public static final String DATE_R = "dateR";
        public static final String EXPENSE_REPORT = "expenseReport";
        public static final String FILES_BLOB = "filesBlob";
        public static final String FILES_ORIENTATION = "filesOrientation";
        public static final String ID = "iD";
        public static final String NOTES = "notes";
        public static final String P_TAX = "ptax";
        public static final String R_ID = "rid";
        public static final String SEQUENCE = "sequence";
        public static final String TOTAL = "total";

    }

    public static class ImageIndex {
        public static final String TABLE_NAME = "ImageIndex";
        public static final String BLOB_ID = "blobId";
        public static final String IMAGE_PATH = "imagePath";

    }

    public static class UploadQueue {
        public static final String TABLE_NAME = "UploadQueue";
        public static final String IMAGE_DATE = "imageDate";
        public static final String IMAGE_PATH = "imagePath";
        public static final String STATUS = "status";
    }

    public static class KeyVal {
        public static final String TABLE_NAME = "keyvalues";
        public static final String KEY = "key";
        public static final String VALUE = "value";
    }


}