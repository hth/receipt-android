package com.receiptofi.checkout.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by PT on 3/28/15.
 */
public class Constants {

    public static final boolean SET_RECEIPT_REMINDER = false;

    public static final String INTENT_EXTRA_FILTER_TYPE = "filter_type";
    public static final String INTENT_EXTRA_BIZ_NAME = "biz_name";

    public static final int DEFAULT_REMINDER_TIME = 15;

    public static final DateFormat ISO_DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
    public static final DateFormat MMM_DD_DF = new SimpleDateFormat("MMM dd',' yyyy HH:mm a", Locale.US);

    public enum ReceiptFilter {
        FILTER_BY_BIZ_AND_MONTH("filter_by_biz_and_month"),
        FILTER_BY_KEYWORD("filter_by_keyword"),
        FILTER_BY_KEYWORD_AND_DATE("filter_by_keyword_and_date");

        private final String filterType;

        /**
         * @param filterType
         */
        private ReceiptFilter(final String filterType) {
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

    public enum FilterActionBarType {
        MENU_MAIN,
        MENU_FILTER
    }

    public enum DialogMode {
        MODE_CREATE,
        MODE_UPDATE
    }
}
