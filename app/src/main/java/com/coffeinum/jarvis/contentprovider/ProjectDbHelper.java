package com.coffeinum.jarvis.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProjectDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Jarvis.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DeviceContract.DeviceEntry.TABLE_NAME + " (" +
                    DeviceContract.DeviceEntry._ID + " INTEGER PRIMARY KEY," +
                    DeviceContract.DeviceEntry.COLUMN_NAME_SERVICE_ID + " TEXT," +
                    DeviceContract.DeviceEntry.COLUMN_NAME + " TEXT," +
                    DeviceContract.DeviceEntry.COLUMN_UPDATE_DATE + " TEXT," +
                    DeviceContract.DeviceEntry.COLUMN_STATE + " INTEGER" +
                    " )";
    private Context context;


    public ProjectDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + DeviceContract.DeviceEntry.TABLE_NAME);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Do nothing
    }
}