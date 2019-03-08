package com.example.netef.battleships;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ScoreDB3.db";
    public static final String TABLE_NAME3 = "Easy";
    public static final String TABLE_NAME4 = "Normal";
    public static final String TABLE_NAME5 = "Hard";
    public static final String COL_1 = "Name";
    public static final String COL_2 = "Score";
    public static final String COL_3 = "City";
    public static final String COL_4 = "Lat";
    public static final String COL_5 = "Lon";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME3 + " (NAME TEXT,SCORE INTEGER,CITY TEXT,LAT REAL,LON REAL)");
        db.execSQL("create table " + TABLE_NAME4 + " (NAME TEXT,SCORE INTEGER,CITY TEXT,LAT REAL,LON REAL)");
        db.execSQL("create table " + TABLE_NAME5 + " (NAME TEXT,SCORE INTEGER,CITY TEXT,LAT REAL,LON REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME4);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME5);
        onCreate(db);
    }

    public boolean insertData(String difficulty, Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, player.getName());
        contentValues.put(COL_2, player.getScore());
        contentValues.put(COL_3, player.getCity());
        contentValues.put(COL_4, player.getLat());
        contentValues.put(COL_5, player.getLon());


        long result;
        if (difficulty.contentEquals("Easy")) {
            result = db.insert(TABLE_NAME3, null, contentValues);
            Log.i("333333333333", "33333333");
        } else if (difficulty.contentEquals("Normal")) {
            Log.i("444444", "444444");
            result = db.insert(TABLE_NAME4, null, contentValues);
        } else {
            Log.i("5555555555", "555555555555555555555");
            result = db.insert(TABLE_NAME5, null, contentValues);
        }
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(String difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = null;
        switch (difficulty) {
            case "Easy": {
                res = db.rawQuery("select * from Easy ORDER by Score limit 10 ", null);
                break;
            }
            case "Normal": {
                res = db.rawQuery("select * from Normal ORDER by Score limit 10 ", null);
                break;
            }
            case "Hard": {
                res = db.rawQuery("select * from Hard ORDER by Score limit 10 ", null);
                break;
            }
        }
        return res;
    }

    public void deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME4);
        db.execSQL("delete from " + TABLE_NAME3);
        db.execSQL("delete from " + TABLE_NAME5);

    }

}
