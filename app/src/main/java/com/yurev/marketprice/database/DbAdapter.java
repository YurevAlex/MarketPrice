package com.yurev.marketprice.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yurev.marketprice.database.MarketContract.StockTable;
import com.yurev.marketprice.database.MarketContract.TimeTable;
import com.yurev.marketprice.quotation.Quotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.yurev.marketprice.database.MarketContract.TimeTable.COLUMN_NUM_PAGE;

public class DbAdapter {

    private static DbAdapter sDbAdapter;  //префикс s говорит что переменная static
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DbAdapter get(Context context) {
        if (sDbAdapter == null) {
            sDbAdapter = new DbAdapter(context);
        }
        return sDbAdapter;
    }

    private DbAdapter(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DbHelper(mContext).getWritableDatabase();
    }

    public List<Quotation> getQuotations(String tableName) {
        List<Quotation> q = new ArrayList<>();
        MarketCursorWrapper cursor = queryQuotations(tableName,null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                q.add(cursor.getQuotation());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return q;
    }

    public HashMap<Integer,String> getTimes() {
        HashMap<Integer,String> times = new HashMap<>();
        MarketCursorWrapper cursor = queryQuotations("times",null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                times.putAll(cursor.getTime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return times;
    }

    public Quotation getQuotation(String tableName, int position) {
        return getQuotations(tableName).get(position);
    }

    public String getTime(int numPage) {
        return getTimes().get(numPage);
    }

    public Quotation getQuotationBySecId(String tableName, String secId) {
        MarketCursorWrapper cursor = queryQuotations(tableName,StockTable.COLUMN_SEC_ID + " = ?", new String[] {secId});

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getQuotation();
        } finally {
            cursor.close();
        }
    }

    public void addQuotation(String tableName, Quotation q) {
        ContentValues values = getContentValues(q);
        long c = mDatabase.insert(tableName, null, values);
    }

    public void deleteQuotation(String tableName, Quotation q) {
        //ContentValues values = getContentValues(c);
        mDatabase.delete(tableName, StockTable.COLUMN_SEC_ID + " = ?", new String[]{q.getSecId()});

    }

    public void deleteQuotation(String tableName, int position) {
        //ContentValues values = getContentValues(c);
        Quotation q = getQuotation(tableName, position);
        mDatabase.delete(tableName, StockTable.COLUMN_SEC_ID + " = ?", new String[]{q.getSecId()});

    }

    private static ContentValues getContentValues(Quotation q) {
        ContentValues values = new ContentValues();
        values.put(StockTable.COLUMN_SEC_ID, q.getSecId());
        values.put(StockTable.COLUMN_SHORT_NAME, q.getShortName());
        values.put(StockTable.COLUMN_SEC_NAME, q.getSecName());
        values.put(StockTable.COLUMN_OPEN, q.getOpen());
        values.put(StockTable.COLUMN_HIGH, q.getHigh());
        values.put(StockTable.COLUMN_LOW, q.getLow());
        values.put(StockTable.COLUMN_LAST, q.getLast());
        values.put(StockTable.COLUMN_LAST_TO_PREV_PRICE, q.getLastToPrevPrice());
        values.put(StockTable.COLUMN_CHANGE, q.getChange());
        values.put(StockTable.COLUMN_VAL_TO_DAY, q.getValToDay());
        return values;
    }

    private static ContentValues getContentValues(int numPage, String time) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUM_PAGE, numPage);
        values.put(TimeTable.COLUMN_TIME, time);
        return values;
    }

    public void updateQuotation(String tableName, Quotation q) {
        ContentValues values = getContentValues(q);
        int count = mDatabase.update(tableName, values, "StockTable.COLUMN_SEC_ID;" + " = ?", new String[] { q.getSecId() });
    }

    public void updateDb(String tableName, List<Quotation> quotations) {
        List<ContentValues> valuesList = new ArrayList<>();

        Iterator<Quotation> iter = quotations.iterator();
        ContentValues values;
        mDatabase.delete(tableName,null,null);
        while(iter.hasNext()){
            values = getContentValues(iter.next());
            mDatabase.insert(tableName, null, values);
        }
    }

    public void updateDb(String tableName, Integer numPage, String time) {
        ContentValues values;

       // String [] whereArgs = new String[] {numPage.toString()};
       // mDatabase.delete(tableName,COLUMN_NUM_PAGE, whereArgs);

        values = getContentValues(numPage,time);
        mDatabase.insert(tableName, null, values);

    }

    private MarketCursorWrapper queryQuotations(String tableName, String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                tableName,
                null, // columns - с null выбираются все столбцы
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new MarketCursorWrapper(cursor);
    }
}
