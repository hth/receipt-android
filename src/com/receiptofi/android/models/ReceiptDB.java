package com.receiptofi.android.models;

public class ReceiptDB {
	
	public static String DB_NAME="receiptofi";

	public static class Receipt {

		public static String TABLE_NAME = "Receipts";
		public static String BIZ_NAME = "bizName";
		public static String BIZ_STORE_ADDRESS = "bizStoreAddress";
		public static String BIZ_STORE_PHONE = "bizStorePhone";
		public static String DATE_R = "dateR";
		public static String EXPENSE_REPORT = "expenseReport";
		public static String FILES_BLOB = "filesBlob";
		public static String FILES_ORIENTATION = "filesOrientation";
		public static String ID = "iD";
		public static String NOTES = "notes";
		public static String P_TAX = "ptax";
		public static String R_ID = "rid";
		public static String SEQUENCE = "sequence";
		public static String TOTAL = "total";

	}

	public static class ImageIndex {
		public static String TABLE_NAME = "ImageIndex";
		public static String BLOB_ID = "blobId";
		public static String IMAGE_NAME = "image_name";

	}

	public static class UploadQueue {
		public static String TABLE_NAME = "UploadQueue";
		public static String IMAGE_DATE = "imageData";
		public static String IMAGE_NAME = "imageName";
		public static String STATUS = "status";
	}
	public static class KeyVal {
		public static String TABLE_NAME = "keyvalues";
		public static String KEY = "key";
		public static String VALUE = "value";
	}

}
