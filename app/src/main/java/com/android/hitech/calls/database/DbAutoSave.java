package com.android.hitech.calls.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAutoSave extends SQLiteOpenHelper {
    boolean result = false;
    Cursor cursor, cursor1;

    public DbAutoSave(Context context) {
        super(context, "DbAutoSave", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table autosave (ID INTEGER PRIMARY KEY AUTOINCREMENT,DATE TEXT,ROWID TEXT)";
        String query1 = "create table meetings (ID INTEGER PRIMARY KEY AUTOINCREMENT,StartDate LONG,MeetingId TEXT,MeetingStartTime LONG,TravelDuration LONG,MeetingEndTime LONG)";
        db.execSQL(query);
        db.execSQL(query1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists autosave");
        db.execSQL("drop table if exists meetings");
        onCreate(db);
    }

    public void insertData(String dataDate, String rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DATE", dataDate);
        cv.put("ROWID", rowId);
        db.insert("autosave", null, cv);
    }

    public boolean getData(String queryData) {
        String[] selection = {queryData};
        SQLiteDatabase db = this.getReadableDatabase();
        cursor1 = db.rawQuery("select * from autosave", null);
        if (cursor1.getCount() > 20000) {
            onDelete();
        }
        cursor1.close();
        String query = "select * from autosave where DATE =?";
        cursor = db.rawQuery(query, selection);
        if (cursor.getCount() != 0) {
            if (cursor.moveToLast()) {
                result = true;
            }
        }
        cursor.close();
        return result;
    }

    private void onDelete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists autosave");
        onCreate(db);
    }
}
