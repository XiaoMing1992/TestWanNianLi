package com.example.hp.testwannianli.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2016-9-12.
 */
public class MyDataBase {

    private Context context;
    private final String dbName = "Huangli.db";

    public MyDataBase(Context context) {
          this.context =context;
    }

    public String getYi(String year, String month, String day) {
        SQLiteDatabase db2 = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.hp.testwannianli/databases/Huangli.db", null);

        List<String> yi = new ArrayList<String>();
        Cursor cursor = null;
        try{
            cursor = db2.rawQuery("select yi from huangli where y = ? and m = ? and d = ?",
                    new String[]{year, month, day});
        }catch (Exception e){
            cursor = null;
            e.printStackTrace();
        }
        if (cursor == null)
            return "";

        String[] yi_str_arrs = null;
        while (cursor.moveToNext()) {
            String yi_str = cursor.getString(cursor.getColumnIndex("yi"));
            yi_str_arrs = yi_str.split("、");
        }
        if (yi_str_arrs.length > 5) {
            for (int i = 0; i < 5; i++)
                yi.add(yi_str_arrs[i]);
        } else {
            for (String yi_str_arr : yi_str_arrs) {
                yi.add(yi_str_arr);
            }
        }
        db2.close();
        String yi_str = "";
        for (int i = 0; i < yi.size(); i++)
            yi_str += yi.get(i) + " ";
        return yi_str;
    }

    public String getJi(String year, String month, String day) {
        SQLiteDatabase db3 = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.hp.testwannianli/databases/Huangli.db", null);

        List<String> ji = new ArrayList<String>();

        Cursor cursor = null;
        try{
            cursor = db3.rawQuery("select ji from huangli where y = ? and m = ? and d = ?",
                    new String[]{year, month, day});
        }catch (Exception e){
            cursor = null;
            e.printStackTrace();
        }
        if (cursor == null)
            return "";

        String[] ji_str_arrs = null;
        while (cursor.moveToNext()) {
            String ji_str = cursor.getString(cursor.getColumnIndex("ji"));
            ji_str_arrs = ji_str.split("、");
        }
        if (ji_str_arrs.length > 5) {
            for (int i = 0; i < 5; i++)
                ji.add(ji_str_arrs[i]);
        } else {
            for (String yi_str_arr : ji_str_arrs) {
                ji.add(yi_str_arr);
            }
        }
        db3.close();
        String ji_str="";
        for (int i = 0; i < ji.size(); i++)
            ji_str += ji.get(i) + " ";
        return ji_str;
    }

    private void insertData(SQLiteDatabase db) {
        //try {
            /*
            //File dir = Environment.getDataDirectory();   //获取data目录
            File outFile=new File("db.txt");

            FileInputStream file = new FileInputStream(outFile);

            //FileReader fileReader = new FileReader(outFile);
            //BufferedReader bufferedReader = new BufferedReader(fileReader);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file));
            String content = null;

            //db.beginTransaction(); //设置事务标志
            System.out.println("----------insertData---------");
            while ((content = bufferedReader.readLine())!=null){
                String[] sub_contents = content.split(",");
                String [] pre_5_sub_contents_1 = sub_contents[4].split("、");
                String [] pre_5_sub_contents_2 = sub_contents[5].split("、");
                System.out.println("----------"+sub_contents);
                db.execSQL("insert into huangli values(null,?,?,?,?,?)",new String[]{sub_contents[1],
                        sub_contents[2],sub_contents[3],pre_5_sub_contents_1[0],pre_5_sub_contents_2[0]});

            }
            // 设置事务标志为成功，当结束事务时就会提交事务
            //db.setTransactionSuccessful();
            */
        db.execSQL("insert into newhuangli values(null,?,?,?,?,?)", new String[]{"1901", "1", "1", "入宅", "嫁娶"});
        /*} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //结束事务
            //db.endTransaction();
            db.close();

        }*/
    }
}
