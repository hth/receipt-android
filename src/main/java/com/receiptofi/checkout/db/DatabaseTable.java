package com.receiptofi.checkout.db;

public class DatabaseTable {

    public static String DB_NAME = "receiptofi.db";

    public static class Profile {
        public static final String TABLE_NAME = "PROFILE";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String MAIL = "mail";
        public static final String NAME = "name";
        public static final String RID = "rid";

        private Profile() {
        }
    }

    public static class Receipt {
        public static final String TABLE_NAME = "RECEIPT";
        public static final String BIZ_NAME = "bizName";
        public static final String BIZ_STORE_ADDRESS = "address";
        public static final String BIZ_STORE_PHONE = "phone";
        public static final String RECEIPT_DATE = "receiptDate";
        public static final String EXPENSE_REPORT = "expenseReport";
        public static final String BLOB_IDS = "blobIds";
        public static final String ID = "id";
        public static final String NOTES = "notes";
        public static final String PTAX = "ptax";
        public static final String RID = "rid";
        public static final String TAX = "tax";
        public static final String TOTAL = "total";
        public static final String BILL_STATUS = "bs";
        public static final String EXPENSE_TAG_ID = "expenseTagId";
        public static final String ACTIVE = "active";
        public static final String DELETED = "deleted";

        private Receipt() {
        }
    }

    public static class ImageIndex {
        public static final String TABLE_NAME = "IMAGE_INDEX";
        public static final String BLOB_ID = "blobId";
        public static final String IMAGE_PATH = "imagePath";

        private ImageIndex() {
        }
    }

    public static class UploadQueue {
        public static final String TABLE_NAME = "UPLOAD_QUEUE";
        public static final String IMAGE_DATE = "imageDate";
        public static final String IMAGE_PATH = "imagePath";
        public static final String STATUS = "status";

        private UploadQueue() {
        }
    }

    public static class KeyValue {
        public static final String TABLE_NAME = "KEY_VALUE";
        public static final String KEY = "key";
        public static final String VALUE = "value";

        private KeyValue() {
        }
    }

    public static class MonthlyReport {
        public static final String TABLE_NAME = "MONTHLY_REPORT";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
        public static final String TOTAL = "total";
        public static final String COUNT = "count";

        private MonthlyReport() {
        }
    }

    public static class Item {
        public static final String TABLE_NAME = "ITEM";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        public static final String RECEIPTID = "receiptId";
        public static final String SEQUENCE = "sequence";
        public static final String TAX = "tax";
        public static final String EXPENSE_TAG_ID = "expenseTagId";

        private Item() {
        }
    }

    public static class ExpenseTag {
        public static final String TABLE_NAME = "EXPENSE_TAG";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String COLOR = "color";
        public static final String DELETED = "deleted";

        private ExpenseTag() {
        }
    }

    public static class Notification {
        public static final String TABLE_NAME = "NOTIFICATION";
        public static final String ID = "id";
        public static final String MESSAGE = "message";
        public static final String VISIBLE = "visible";
        public static final String NOTIFICATION_TYPE = "notificationType";
        public static final String REFERENCE_ID = "referenceId";
        public static final String CREATED = "created";
        public static final String UPDATED = "updated";
        public static final String ACTIVE = "active";

        private Notification() {
        }
    }

    public static class BillingAccount {
        public static final String TABLE_NAME = "BILLING_ACCOUNT";
        public static final String ACCOUNT_BILLING_TYPE = "accountBillingType";

        private BillingAccount() {
        }
    }

    public static class BillingHistory {
        public static final String TABLE_NAME = "BILLING_HISTORY";
        public static final String ID = "id";
        public static final String BILLED_MONTH = "billedForMonth";
        public static final String BILLED_STATUS = "billedStatus";
        public static final String ACCOUNT_BILLING_TYPE = "accountBillingType";
        public static final String BILLED_DATE = "billedDate";

        private BillingHistory() {
        }
    }
}
