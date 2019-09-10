package com.soft_sketch.hereim.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class dbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "police_db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_POLICE= "tbl_police";
    public static final String COL_TBL_POLICE_ID = "_id";
    public static final String COL_TBL_STATION_NAME = "station_name";
    public static final String COL_TBL_STATION_PHONE = "station_phone";
    public static final String COL_TBL_STATION_LAT = "station_lat";
    public static final String COL_TBL_STATION_LONG = "station_long";

    public static final String CREATE_TABLE_POLICE = "CREATE TABLE "+TABLE_POLICE+"("+
            COL_TBL_POLICE_ID+" INTEGER PRIMARY KEY, "+
            COL_TBL_STATION_NAME+" TEXT, "+
            COL_TBL_STATION_PHONE+" TEXT, "+
            COL_TBL_STATION_LAT+" TEXT, "+
            COL_TBL_STATION_LONG+" TEXT);";

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_POLICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
