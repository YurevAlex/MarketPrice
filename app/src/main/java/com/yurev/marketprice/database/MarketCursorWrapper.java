package com.yurev.marketprice.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.yurev.marketprice.database.MarketContract.StockTable;
import com.yurev.marketprice.database.MarketContract.TimeTable;
import com.yurev.marketprice.quotation.Quotation;

import java.util.HashMap;

public class MarketCursorWrapper extends CursorWrapper {

    public MarketCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Quotation getQuotation() {
        String secId = getString(getColumnIndex(StockTable.COLUMN_SEC_ID));
        String shortName = getString(getColumnIndex(StockTable.COLUMN_SHORT_NAME));
        String secName = getString(getColumnIndex(StockTable.COLUMN_SEC_NAME));
        Double open = getDouble(getColumnIndex(StockTable.COLUMN_OPEN));
        Double high = getDouble(getColumnIndex(StockTable.COLUMN_HIGH));
        Double low = getDouble(getColumnIndex(StockTable.COLUMN_LOW));
        Double last = getDouble(getColumnIndex(StockTable.COLUMN_LAST));
        Double lastToPrevPrice = getDouble(getColumnIndex(StockTable.COLUMN_LAST_TO_PREV_PRICE));
        Double change = getDouble(getColumnIndex(StockTable.COLUMN_CHANGE));
        Long valToDay = getLong(getColumnIndex(StockTable.COLUMN_VAL_TO_DAY));


        Quotation quotation = new Quotation();

        quotation.setSecId(secId);
        quotation.setShortName(shortName);
        quotation.setSecName(secName);
        quotation.setOpen(open);
        quotation.setHigh(high);
        quotation.setLow(low);
        quotation.setLast(last);
        quotation.setLastToPrevPrice(lastToPrevPrice);
        quotation.setChange(change);
        quotation.setValToDay(valToDay);

        return quotation;
    }

    public HashMap<Integer, String> getTime() {
        HashMap<Integer, String> time = new HashMap<>();
        Integer key = getInt(getColumnIndex(TimeTable.COLUMN_NUM_PAGE));
        String value = getString(getColumnIndex(TimeTable.COLUMN_TIME));
        time.put(key,value);
        return time;
    }


}
