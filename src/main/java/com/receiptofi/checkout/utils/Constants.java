package com.receiptofi.checkout.utils;

/**
 * Created by PT on 3/28/15.
 */
public class Constants {

    public static final String INTENT_EXTRA_FILTER_TYPE = "filter_type";
    public static final String INTENT_EXTRA_BIZ_NAME = "biz_name";

    public enum ReceiptFilter {
        FIlter_BY_BIZ_AND_MONTH("filter_by_biz_and_month"),
        FIlter_BY_KEYWORD("filter_by_keyword"),
        FIlter_BY_KEYWORD_AND_DATE("filter_by_keyword_and_date");

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
}
