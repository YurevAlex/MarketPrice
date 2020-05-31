package com.yurev.marketprice.database;

import android.provider.BaseColumns;

public class MarketContract {
    private MarketContract() {};
    //BaseColumns add _ID columns
    public static final class StockTable implements BaseColumns {
        public static final String NAME_TABLE = "stock";

        public static final String COLUMN_SEC_ID = "secId";
        public static final String COLUMN_SHORT_NAME = "shortName";
        public static final String COLUMN_SEC_NAME = "secName";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_LAST = "last";
        public static final String COLUMN_LAST_TO_PREV_PRICE = "lastToPrevPrice";
        public static final String COLUMN_CHANGE = "change";
        public static final String COLUMN_VAL_TO_DAY = "valToDay";
    }

    public static final class FavoriteTable implements BaseColumns {
        public static final String NAME_TABLE = "favorite";

        public static final String COLUMN_SEC_ID = "secId";
        public static final String COLUMN_SHORT_NAME = "shortName";
        public static final String COLUMN_SEC_NAME = "secName";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_LAST = "last";
        public static final String COLUMN_LAST_TO_PREV_PRICE = "lastToPrevPrice";
        public static final String COLUMN_CHANGE = "change";
        public static final String COLUMN_VAL_TO_DAY = "valToDay";
    }

    public static final class CurrencyTable implements BaseColumns {
        public static final String NAME_TABLE = "currency";

        public static final String COLUMN_SEC_ID = "secId";
        public static final String COLUMN_SHORT_NAME = "shortName";
        public static final String COLUMN_SEC_NAME = "secName";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_LAST = "last";
        public static final String COLUMN_LAST_TO_PREV_PRICE = "lastToPrevPrice";
        public static final String COLUMN_CHANGE = "change";
        public static final String COLUMN_VAL_TO_DAY = "valToDay";
    }

    public static final class FuturesTable implements BaseColumns {
        public static final String NAME_TABLE = "futures";

        public static final String COLUMN_SEC_ID = "secId";
        public static final String COLUMN_SHORT_NAME = "shortName";
        public static final String COLUMN_SEC_NAME = "secName";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_LAST = "last";
        public static final String COLUMN_LAST_TO_PREV_PRICE = "lastToPrevPrice";
        public static final String COLUMN_CHANGE = "change";
        public static final String COLUMN_VAL_TO_DAY = "valToDay";
    }

    public static final class TimeTable implements BaseColumns {
        public static final String NAME_TABLE = "times";

        public static final String COLUMN_NUM_PAGE = "numPage";
        public static final String COLUMN_TIME = "time";
    }
}

