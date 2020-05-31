package com.yurev.marketprice.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yurev.marketprice.database.MarketContract.CurrencyTable;
import com.yurev.marketprice.database.MarketContract.FavoriteTable;
import com.yurev.marketprice.database.MarketContract.FuturesTable;
import com.yurev.marketprice.database.MarketContract.StockTable;
import com.yurev.marketprice.database.MarketContract.TimeTable;

public class DbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "quotation.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + StockTable.NAME_TABLE + "(" +
                StockTable._ID +" INTEGER PRIMARY KEY," +
                StockTable.COLUMN_SEC_ID + ", " +
                StockTable.COLUMN_SHORT_NAME + ", " +
                StockTable.COLUMN_SEC_NAME + ", " +
                StockTable.COLUMN_OPEN + "," +
                StockTable.COLUMN_HIGH  + "," +
                StockTable.COLUMN_LOW  + "," +
                StockTable.COLUMN_LAST  + "," +
                StockTable.COLUMN_LAST_TO_PREV_PRICE  + "," +
                StockTable.COLUMN_CHANGE  + "," +
                StockTable.COLUMN_VAL_TO_DAY  + ")"
        );

        db.execSQL("create table " + FavoriteTable.NAME_TABLE + "(" +
                StockTable._ID +" INTEGER PRIMARY KEY," +
                StockTable.COLUMN_SEC_ID + ", " +
                StockTable.COLUMN_SHORT_NAME + ", " +
                StockTable.COLUMN_SEC_NAME + ", " +
                StockTable.COLUMN_OPEN + "," +
                StockTable.COLUMN_HIGH  + "," +
                StockTable.COLUMN_LOW  + "," +
                StockTable.COLUMN_LAST  + "," +
                StockTable.COLUMN_LAST_TO_PREV_PRICE  + "," +
                StockTable.COLUMN_CHANGE  + "," +
                StockTable.COLUMN_VAL_TO_DAY  + ")"
        );

        db.execSQL("create table " + CurrencyTable.NAME_TABLE + "(" +
                StockTable._ID +" INTEGER PRIMARY KEY," +
                StockTable.COLUMN_SEC_ID + ", " +
                StockTable.COLUMN_SHORT_NAME + ", " +
                StockTable.COLUMN_SEC_NAME + ", " +
                StockTable.COLUMN_OPEN + "," +
                StockTable.COLUMN_HIGH  + "," +
                StockTable.COLUMN_LOW  + "," +
                StockTable.COLUMN_LAST  + "," +
                StockTable.COLUMN_LAST_TO_PREV_PRICE  + "," +
                StockTable.COLUMN_CHANGE  + "," +
                StockTable.COLUMN_VAL_TO_DAY  + ")"
        );

        db.execSQL("create table " + FuturesTable.NAME_TABLE + "(" +
                StockTable._ID +" INTEGER PRIMARY KEY," +
                StockTable.COLUMN_SEC_ID + ", " +
                StockTable.COLUMN_SHORT_NAME + ", " +
                StockTable.COLUMN_SEC_NAME + ", " +
                StockTable.COLUMN_OPEN + "," +
                StockTable.COLUMN_HIGH  + "," +
                StockTable.COLUMN_LOW  + "," +
                StockTable.COLUMN_LAST  + "," +
                StockTable.COLUMN_LAST_TO_PREV_PRICE  + "," +
                StockTable.COLUMN_CHANGE  + "," +
                StockTable.COLUMN_VAL_TO_DAY  + ")"
        );

        db.execSQL("create table " + TimeTable.NAME_TABLE + "(" +
                TimeTable._ID +" INTEGER PRIMARY KEY," +
                TimeTable.COLUMN_NUM_PAGE + ", " +
                TimeTable.COLUMN_TIME  + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StockTable.NAME_TABLE);    //delete
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteTable.NAME_TABLE);    //delete
        db.execSQL("DROP TABLE IF EXISTS " + CurrencyTable.NAME_TABLE);    //delete
        db.execSQL("DROP TABLE IF EXISTS " + FuturesTable.NAME_TABLE);    //delete
        db.execSQL("DROP TABLE IF EXISTS " + TimeTable.NAME_TABLE);    //delete
        onCreate(db);
    }

}
