package com.example.hp.testwannianli.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HP on 2016-9-20.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private final String create_table = "create table schedule_management(_id integer primary key autoincrement," +
            "date date, time time, schedule vchar(200), flag integer default 0, target vchar(200) default 单身狗的我)";
    private final String delete_table = "drop table if exists schedule_management";

    public DataBaseHelper(Context context, String name, int version){
        super(context,name,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("start----------------------");
        db.execSQL(create_table);
        //insertData(db); //第一次创建时，插入数据
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(delete_table);
        onCreate(db);
    }
}