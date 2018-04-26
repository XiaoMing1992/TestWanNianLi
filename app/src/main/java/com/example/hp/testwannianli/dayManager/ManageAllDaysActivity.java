package com.example.hp.testwannianli.dayManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.dao.DataBaseHelper;
import com.example.hp.testwannianli.util.ManageActvity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManageAllDaysActivity extends AppCompatActivity {
    /**
     * 初始显示的页数
     */
    private int pageNum = 1;
    /**
     * 每一次显示多少个
     */
    private static final int pageCount = 10;
    /**
     * 首页
     */
    private Button first_page;
    /**
     * 上一页
     */
    private Button up_page;
    /**
     * 下一页
     */
    private Button down_page;
    /**
     * 最后一页
     */
    private Button last_page;
    /**
     * 显示页码
     */
    private EditText page_num;
    /**
     * 总页数
     */
    private int total_page_num;

    private ListView day_item_lv;
    private ArrayList<Map<String, Object>> listItems;
    private DataBaseHelper myDataBaseHelper;
    private final String DATABASE_NAME = "mySchedule";
    private final int VERSION = 1;
    private ManageDayAdapter manageDayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_all_days);
        ManageActvity.getInstance().addActivity(ManageAllDaysActivity.this);//添加Activity
        initView();

        myDataBaseHelper = new DataBaseHelper(ManageAllDaysActivity.this, DATABASE_NAME, VERSION);
        //设置列表
        initList();
        total_page_num = listItems.size() % pageCount == 0 ? listItems.size() / pageCount : listItems.size() / pageCount + 1;
        up_page.setEnabled(false);
        if (total_page_num == 1)
        {
            down_page.setEnabled(false);
        }
        //设置列表
        setListView();
        if (listItems.size()!=0) {
            manageDayAdapter.setSeclection(0);
            manageDayAdapter.notifyDataSetChanged();
        }
        //监听item的点击事件
        itemClick();
        //监听item的选择事件
        itemSelected();
        //首页点击
        firstPageOnClick();
        //上一页点击
        upPageOnClick();
        //下一页点击
        downPageOnClick();
        //最后一页点击
        lastPageOnClick();
    }

    public void initView() {
        day_item_lv = (ListView) findViewById(R.id.day_item_listview);
        //分页用到
        first_page = (Button) findViewById(R.id.first_page);
        up_page = (Button) findViewById(R.id.up_page);
        down_page = (Button) findViewById(R.id.down_page);
        last_page = (Button) findViewById(R.id.last_page);
        //显示页码
        page_num = (EditText) findViewById(R.id.page_num);
    }

    public void firstPageOnClick() {
        /**首页点击*/
        first_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNum = 1;
                setListView();
                //manageDayAdapter.setPageNum(pageNum);
                //manageDayAdapter.notifyDataSetChanged();
                up_page.setEnabled(false);
                if (total_page_num>1)
                    down_page.setEnabled(true);
            }
        });
    }

    public void upPageOnClick() {
        /**上一页点击*/
        up_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pageNum > 1) {
                    pageNum--;
                    setListView();
                    //manageDayAdapter.setPageNum(pageNum);
                    //manageDayAdapter.notifyDataSetChanged();
                    down_page.setEnabled(true); //下一页按钮有效
                    if (pageNum<=1)
                        up_page.setEnabled(false);
                }else {

                    Toast.makeText(getApplicationContext(),"已经是第一页了",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void downPageOnClick() {
        /**下一页点击*/
        down_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listItems.size() - pageNum * pageCount > 0) {
                    pageNum++;
                    setListView();
                    //manageDayAdapter.setPageNum(pageNum);
                    //manageDayAdapter.notifyDataSetChanged();
                    up_page.setEnabled(true);//上一页按钮有效
                    if (pageNum>=total_page_num)
                        down_page.setEnabled(false);
                }else {
                    down_page.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"已经是最后一页了",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void lastPageOnClick() {
        /**最后一页点击*/
        last_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNum = total_page_num;
                setListView();
                //manageDayAdapter.setPageNum(pageNum);
                //manageDayAdapter.notifyDataSetChanged();
                down_page.setEnabled(false);
                if (total_page_num>1)
                    up_page.setEnabled(true);

            }
        });
    }

    public Cursor getAllDatasCursor() {
        SQLiteDatabase db = myDataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from schedule_management order by date desc,time desc;", null);
        return cursor;
    }

    public Cursor selectData(String date, String time) {
        SQLiteDatabase db = myDataBaseHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select * from schedule_management where date = ? and time = ?;", new String[]{date, time});
        return cursor;
    }

    public ArrayList<Map<String, Object>> initData() {
        Cursor cursor = getAllDatasCursor();
        ArrayList<Map<String, Object>> tempListItems = new ArrayList<Map<String, Object>>();
        while (cursor.moveToNext()) {
            //item_id
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            //item_time
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String item_time = date + "   " + time;

            int isValid = isOutOfDay(date, time); //判断该日程有没有过期
            //item_object
            String item_object = cursor.getString(cursor.getColumnIndex("target"));

            Map<String, Object> tempListItem = new HashMap<String, Object>();
            tempListItem.put("item_id", id);              //id
            tempListItem.put("item_time", item_time);     //提醒时间
            tempListItem.put("item_object", item_object); //对象
            tempListItem.put("item_valid", isValid); //判断该日程有没有过期
            tempListItems.add(tempListItem);
        }
        return tempListItems;
    }

    public ArrayList<Map<String, Object>> splitList() {
        ArrayList<Map<String, Object>> subList = new ArrayList<Map<String, Object>>();
        for (int i = (pageNum - 1) * pageCount; i < pageNum * pageCount && i < listItems.size(); i++) {
            subList.add(listItems.get(i));
        }
        return subList;
    }

    public void initList(){
        listItems = initData();
    }
    public void setListView() {
        if (listItems.size() == 0) {
            Toast.makeText(getApplicationContext(), "暂无日程", Toast.LENGTH_SHORT).show();
            day_item_lv.setVisibility(View.INVISIBLE);
        } else {
            ArrayList<Map<String, Object>> subList = splitList();
            //System.out.println(subList);
            //manageDayAdapter = new ManageDayAdapter(ManageAllDaysActivity.this, subList);
            manageDayAdapter = new ManageDayAdapter(ManageAllDaysActivity.this, listItems, subList, pageNum);

            /*
            SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.day_list_item,
                    new String[]{"item_time", "item_object"},
                    new int[]{R.id.list_item_time, R.id.list_item_object});
            day_item_lv.setAdapter(adapter);*/

            day_item_lv.setAdapter(manageDayAdapter);
            //int total_page_num = listItems.size() % pageCount == 0 ? listItems.size() / pageCount : listItems.size() / pageCount + 1;
            page_num.setText(pageNum + "/" + total_page_num);
            //day_item_lv.setDivider(getDrawable(R.drawable.));
        }
    }

    public void itemClick() {
        day_item_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Map<String, Object> myMap = listItems.get((pageNum - 1) * pageCount + position);
                //Toast.makeText(getApplicationContext(), "" + myMap, Toast.LENGTH_SHORT).show();

                int item_id = (int) myMap.get("item_id");
                //System.out.println(item_id);

                Bundle bundle = new Bundle();
                bundle.putInt("item_id", item_id);//点击的item的数据库的id

                Intent intent = new Intent();
                intent.putExtra("item_info", bundle);
                intent.setClass(ManageAllDaysActivity.this, ManageAllDaysItemActivity.class);
                startActivity(intent);

                //有点不确定
                //ManageAllDaysActivity.this.finish();
            }
        });
    }

    public void itemSelected() {
        day_item_lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (view != null) {
                    manageDayAdapter.setSeclection(position);
                    manageDayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /*
    * 返回 -1，表示过期；返回0，表示正好；返回1，表示还没有过期。
    */
    public int isOutOfDay(String date_str, String time_str) { //判断是否已经过期
        SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm:00");
        SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();

        String nowDate = sdf_date.format(now); //格式化日期
        String nowTime = sdf_time.format(now); //格式化时间
        /*
          比较现在时间的字符串（nowDate，nowTime）和提醒时间的字符串（date_str，time_str）
         */
        if (nowDate.compareTo(date_str) < 0) {
            return 1;
        } else if (nowDate.compareTo(date_str) == 0) {
            if (nowTime.compareTo(time_str) == 0) {
                return 0;
            } else if (nowTime.compareTo(time_str) < 0) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
                && event.getRepeatCount() == 0) {
            Toast.makeText(getApplicationContext(), "BYE-ManageAllDaysActivity", Toast.LENGTH_SHORT).show();
            ManageActvity.getInstance().closeActivity();//关掉activity
            //ManageAllDaysActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
