package com.example.hp.testwannianli.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HP on 2016-9-7.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper {

    private final String create_table = "create table newhuangli(ID integer primary key autoincrement," +
            "year integer, month integer, day integer, yi vchar(200), ji vchar(200))";
    private final String delete_table = "drop table if exists newhuangli";

    public MyDataBaseHelper(Context context, String name, int version){
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
