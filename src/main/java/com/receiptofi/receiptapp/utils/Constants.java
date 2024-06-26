package com.receiptofi.receiptapp.utils;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * User: PT
 * Date: 3/28/15 4:05 AM
 */
public class Constants {

    public static final boolean SET_RECEIPT_REMINDER = false;
    public static final int DEFAULT_REMINDER_TIME = 15;

    /** Higher value mean faster is the effect. */
    public static final float RIPPLE_SPEED_EFFECT = 80.0F;

    public static final long EXPANSE_TAG_UPDATE_DELAY = 6000;

    public final static String ARG_INDEX = "index";
    public final static String ARG_POSITION = "position";
    public final static String ARG_TYPE_FILTER = "filter";
    public final static String ARG_IMAGE_URL = "image_url";

    public static final String INTENT_EXTRA_FILTER_TYPE = "filter_type";
    public static final String INTENT_EXTRA_BIZ_NAME = "biz_name";
    public static final String INTENT_EXTRA_PLAN_MODEL = "plan_model";
    public static final String INTENT_EXTRA_FIRST_NAME = "firstname";
    public static final String INTENT_EXTRA_LAST_NAME = "lastname";
    public static final String INTENT_EXTRA_POSTAL_CODE = "postalcode";
    public static final String INTENT_EXTRA_TRANSACTION_TYPE = "type";


    public static final DateFormat ISO_DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
    public static final DateTimeFormatter ISO_J_DF = ISODateTimeFormat.dateTime();
    public static final DateFormat MMM_DD_DF = new SimpleDateFormat("MMM dd',' yyyy hh:mm a", Locale.US);

    public enum ReceiptFilter {
        FILTER_BY_BIZ_AND_MONTH("filter_by_biz_and_month"),
        FILTER_BY_KEYWORD("filter_by_keyword"),
        FILTER_BY_KEYWORD_AND_DATE("filter_by_keyword_and_date");

        private final String filterType;

        /**
         * @param filterType
         */
        ReceiptFilter(final String filterType) {
            this.filterType = filterType;
        }

        public String getValue() {
            return this.filterType;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return filterType;
        }
    }

    public enum DialogMode {
        MODE_CREATE,
        MODE_UPDATE
    }
}
