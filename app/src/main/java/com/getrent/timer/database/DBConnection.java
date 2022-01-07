package com.getrent.timer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.getrent.timer.entity.TimerEntity;
import com.getrent.timer.model.TimerList;

import java.util.ArrayList;

public class DBConnection extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABSE_NAME="timerDatabase.db";

    public DBConnection(@Nullable Context context) {
        super(context, DATABSE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+ TimerEntity.TABLE_NAME+"("+
                TimerEntity._ID+" INTEGER PRIMARY KEY,"+
                TimerEntity.START_TIME+" TEXT,"+
                TimerEntity.STOP_TIME+" TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TimerEntity.TABLE_UPGRADE);
        onCreate(db);
    }

    public String addData(String startTime, String stopTime){
        String message = null;
        SQLiteDatabase database = getWritableDatabase();

        ContentValues registrationDataSave = new ContentValues();
        registrationDataSave.put(TimerEntity.START_TIME,startTime);
        registrationDataSave.put(TimerEntity.STOP_TIME,stopTime);

        long resultOfRegistration = database.insert(TimerEntity.TABLE_NAME, null, registrationDataSave);
        if(resultOfRegistration>0){
            System.out.println("Data save successfully");
            message = "Data Added";
        }
        return message;
    }
    SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

    public ArrayList<TimerList> checkTimerList(){
        ArrayList<TimerList> data = new ArrayList<>();
        try{
            String sortOrder = TimerEntity._ID + " DESC";

            Cursor cursor = sqLiteDatabase.query(
                    TimerEntity.TABLE_NAME,   // The table to query
                    null,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            while (cursor.moveToNext()) {
                data.add(new TimerList(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            }

        }catch (Exception ex){
            Log.e("DBERROR",ex.getMessage());
        }
        return data;
    }



    public boolean deleteData(int id){
        SQLiteDatabase db = getWritableDatabase();

        String selection = TimerEntity._ID+"=?";
        String[] selectionArgs = {String.valueOf(id)};

        int deleteResult = db.delete(TimerEntity.TABLE_NAME, selection, selectionArgs);
        if(deleteResult>0)
            return Boolean.TRUE;
        return Boolean.FALSE;
    }
}
