package com.example.hp.testwannianli.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * SQLite中数据的读取
 *
 * Created by HP on 2016-9-12.
 */
public class Database {
    private static final String TAG = "Database";
    private SQLiteDatabase sqLiteDatabase;

    public SQLiteDatabase openOrCreateSQLite(String file_path,String database_name){
        try {
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file_path+"/"+database_name,null);
        }
        catch (SQLiteException e){
            sqLiteDatabase.execSQL("CREATE TABLE [schedule_management]([_id] INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "[time] TIME,[date] DATE,[schedule] VARCHAR(100),[flag] INT DEFAULT 1,[target] VARCHAR(20) DEFAULT 单身狗的我);");
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(file_path+"/"+database_name,null);
        }
        return sqLiteDatabase;
    }
    public void insertData(String date,String time,String schedule){
        Log.i(TAG,"----insert-----");
        sqLiteDatabase.execSQL("insert into schedule_management(date,time,schedule) values(\'"+date+"\',\'"+time+"\',\'"+schedule+"\');");
    }

    /**
     * @param date
     * 日期
     * @param time
     * 时间
     * @param schedule
     * 日程
     * @param target
     * 日程目标
     */
    public void insertData(String date,String time,String schedule,String target){
        Log.i(TAG,"----insert-----");
        sqLiteDatabase.execSQL("insert into schedule_management(date,time,schedule,target) " +
                "values(\'"+date+"\',\'"+time+"\',\'"+schedule+"\',\'"+target+"\');");
    }
    public void insertSoundData(String date,String time,String schedule){
        Log.i(TAG,"----insertSound-----");
        sqLiteDatabase.execSQL("insert into schedule_management(date,time,schedule,flag)" +
                " values(\'"+date+"\',\'"+time+"\',\'"+schedule+"\',\'"+2+"\');");
    }
    public void insertSoundData(String date,String time,String schedule,String target){
        Log.i(TAG,"----insertSound-----");
        sqLiteDatabase.execSQL("insert into schedule_management(date,time,schedule,flag,target)" +
                " values(\'"+date+"\',\'"+time+"\',\'"+schedule+"\',\'"+2+"\',\'"+target+"\');");
    }
    public void deleteData(String date,String time){
        sqLiteDatabase.rawQuery("delete from schedule_management where date=? and time='?';",new String[]{date,time});
    }
    public void changeData(String date,String time,String schedule){
        sqLiteDatabase.rawQuery("UPDATE schedule_management SET schedule = ? WHERE date = ? and time = ?;",new String[]{schedule,date,time});
    }
    public Cursor selectData(String date,String time){
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("select * from schedule_management where date = ? and time = ?;",new String[]{date,time});
        return cursor;
    }
    public Cursor selectAllData(){
        Cursor cursor;
        cursor = sqLiteDatabase.rawQuery("select * from schedule_management;",null);
        return cursor;
    }
    public Cursor selectData(Calendar date){
        Cursor cursor;
        try {
            Date date1 = date.getTime();
            Log.i(TAG,"------selectData------");
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("kk:mm:ss");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String time2 = simpleDateFormat2.format(date1);
            Log.i(TAG,time2);
            String date_f = simpleDateFormat.format(date1);
            cursor = selectData(date_f,time2);
            return cursor;
        }
        catch (SQLiteException e){
            Log.i(TAG,"-------cursor is null-------");
            return null;
        }
    }
    public void onDestroy(){
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()){
            sqLiteDatabase.close();
        }
    }
}
