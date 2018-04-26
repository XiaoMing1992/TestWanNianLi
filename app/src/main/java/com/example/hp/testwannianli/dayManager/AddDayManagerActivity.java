package com.example.hp.testwannianli.dayManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.dao.DataBaseHelper;
import com.example.hp.testwannianli.myApp.MainActivity;
import com.example.hp.testwannianli.recorder.PlayRecordActivity;
import com.example.hp.testwannianli.recorder.RecordingActivity;
import com.example.hp.testwannianli.userdefinedwidget.DatePickerActivity;
import com.example.hp.testwannianli.util.ManageActvity;

import java.io.File;
import java.util.Calendar;

public class AddDayManagerActivity extends AppCompatActivity {

    private static final String TAG = "AddDayManagerActivity";

    private Button set_date; //日期
    private Button wenzi;   //文字输入
    private Button luyin;   //录音输入
    private Button save;    //保存
    private EditText username;  //对象输入

    private int arrive_year;
    private int arrive_month;
    private int arrive_day;
    private int arrive_hour;
    private int arrive_min;

    private String content_str = null;
    private String flag = "0";
    private String schedule_str = null;
    private boolean can_look = false;

    private DataBaseHelper myDataBaseHelper;
    private final String DATABASE_NAME = "mySchedule";
    private final int VERSION = 1;
    //数据库要存储的内容

    private String date_str=""; //日期
    private String time_str=""; //时间
    private String username_str; //对象

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_manager);
        ManageActvity.getInstance().addActivity(AddDayManagerActivity.this);//添加Activity
        initView();

        myDataBaseHelper = new DataBaseHelper(AddDayManagerActivity.this, DATABASE_NAME, VERSION); //初始化
        sharedPreferences = getSharedPreferences("dayInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setDateOnClick(); //监听日期设置按钮
        wenziOnClick();   //监听文字输入按钮
        luyinOnClick();   //监听录音输入按钮
        saveOnClick();    //监听保存按钮
    }

    private void initView() {
        set_date = (Button) findViewById(R.id.set_date);
        wenzi = (Button) findViewById(R.id.wenzi);
        luyin = (Button) findViewById(R.id.luyin);
        save = (Button) findViewById(R.id.save);
        username = (EditText) findViewById(R.id.toUser);
    }

    public void initDatas() {

        //sharedPreferences = getSharedPreferences("dayInfo",MODE_PRIVATE);
        //editor = sharedPreferences.edit();
        date_str = sharedPreferences.getString("date", "");
        //time_str = sharedPreferences.getString("time", "");
        username_str = sharedPreferences.getString("name", "");

        //从数据库取出数据

        //wenzi_flag为1，表示是文字输入；为2表示是录音输入
        int wenzi_flag = 0;
        if (wenzi_flag == 1) {
            //设置文字输入按钮
            wenzi.setText("查看文字输入内容");
            wenzi.setTextColor(Color.RED);
            //设置录音输入按钮不能用
            luyin.setEnabled(false);
        } else if (wenzi_flag == 2) {
            //设置录音输入按钮
            luyin.setText("播放录音");
            luyin.setTextColor(Color.BLUE);
            //设置文字输入按钮不能用
            wenzi.setEnabled(false);
        }
    }

    private void setDateOnClick() {
        set_date.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                editor.putString("date", set_date.getText().toString()); //存储日期
                editor.putString("name", username.getText().toString().trim()); //存储对象

                editor.putBoolean("setDate", true);
                editor.commit();
                startActivityForResult(new Intent(AddDayManagerActivity.this, DatePickerActivity.class), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 0) {
            if (data == null)
                return;
            Bundle bundle = data.getExtras();

            arrive_month = Integer.valueOf(bundle.getString("month"));
            arrive_day = Integer.valueOf(bundle.getString("day"));
            arrive_year = Integer.valueOf(bundle.getString("year"));
            arrive_hour = Integer.valueOf(bundle.getString("hour"));
            arrive_min = Integer.valueOf(bundle.getString("minute"));

            String arrive_month_str = (arrive_month) >= 10 ? "" + (arrive_month) : "0" + (arrive_month);
            String arrive_day_str = arrive_day >= 10 ? "" + arrive_day : "0" + arrive_day;
            String dateStr = "" + arrive_year + "-" + arrive_month_str + "-" + arrive_day_str;

            //editor.putString("date", dateStr); //存储日期
            //onCreate(null);
            //set_date.setText(dateStr);

            String arrive_hour_str = arrive_hour >= 10 ? "" + arrive_hour : "0" + arrive_hour;
            String arrive_min_str = arrive_min >= 10 ? "" + arrive_min : "0" + arrive_min;
            String arrive_second_str = "00";
            String timeStr = "" + arrive_hour_str + ":" + arrive_min_str + ":" + arrive_second_str;
            set_date.setText(dateStr+" "+timeStr);
        }
    }

    private void wenziOnClick() {
        wenzi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = "1"; //文字输入

                if (!can_look) {
                    View wenziView = View.inflate(getApplicationContext(), R.layout.wenzi_dialog, null);
                    final EditText wenzi_content = (EditText) wenziView.findViewById(R.id.wenzi_content);
                    wenzi_content.setText(content_str);
                    //flag = "1"; //文字输入

                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddDayManagerActivity.this);
                    builder.setView(wenziView);
                    if (content_str == null)
                        builder.setTitle(R.string.wenzi_content);
                    else
                        builder.setTitle("您的文字内容");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //将字符串后面的多余空格去掉
                            content_str = wenzi_content.getText().toString().trim();
                            //设置文字输入按钮
                            wenzi.setText("查看文字输入内容");
                            wenzi.setTextColor(Color.RED);
                            //设置录音输入按钮不能用
                            luyin.setEnabled(false);

                            can_look = true;  //再次点击，则进入查看文字内容状态
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                        }
                    });
                    builder.show();
                } else {
                    final AlertDialog.Builder builder02 = new AlertDialog.Builder(AddDayManagerActivity.this);
                    builder02.setTitle("您的文字内容");
                    builder02.setMessage(content_str); //可以自定义view
                    builder02.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder02.setNegativeButton("编辑", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {

                            View wenziView = View.inflate(getApplicationContext(), R.layout.wenzi_dialog, null);
                            final EditText wenzi_content = (EditText) wenziView.findViewById(R.id.wenzi_content);
                            wenzi_content.setText(content_str);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddDayManagerActivity.this);
                            builder.setView(wenziView);
                            builder.setTitle("您要编辑的文字内容");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //将字符串后面的多余空格去掉
                                    content_str = wenzi_content.getText().toString().trim();
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                }
                            });
                            builder.show();
                            can_look = true;
                        }
                    });
                    builder02.show();
                }
            }
        });
    }

    private void luyinOnClick() {
        luyin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag.equals("2"))//进入播放录音界面
                {
                    editor.putString("item_path", content_str);  //存储录音文件的路径
                    editor.putBoolean("action", true);  //存储录音文件的路径

                    Bundle bundle = new Bundle();
                    //bundle.putInt("item_id", item_id);//因为还没有保存到数据库，所以没有item_id
                    bundle.putString("item_path", content_str);//传递录音文件的路径

                    Intent intent = new Intent();
                    intent.putExtra("item_info", bundle);
                    intent.putExtra("from", "AddDayManagerActivity");
                    intent.setClass(AddDayManagerActivity.this, PlayRecordActivity.class);
                    startActivity(intent);
                } else {   //进入录音界面
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "AddDayManagerActivity");
                    Intent intent = new Intent();
                    intent.putExtra("from_bundle", bundle);

                    intent.setClass(AddDayManagerActivity.this, RecordingActivity.class);
                    startActivity(intent);
                }
                editor.putString("date", set_date.getText().toString()); //存储日期
                editor.putString("name", username.getText().toString().trim()); //存储对象
                editor.commit(); //注意要提交

            }
        });
    }

    private void saveOnClick() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String username_str = username.getText().toString();
                //String date_str = set_date.getText().toString();
                //String time_str = set_time.getText().toString();

                username_str = username.getText().toString().trim();

                //time_str = set_time.getText().toString();

                if (flag.equals("0") || username_str.equals("") //注意将字符串后面多余的空格去掉
                        || set_date.getText().toString().equals("设置日期") /*|| time_str.equals("设置时间")*/) {
                    final AlertDialog.Builder save_builder = new AlertDialog.Builder(AddDayManagerActivity.this);
                    save_builder.setTitle("请不要留空");
                    save_builder.setPositiveButton("明白", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //System.out.println("");
                        }
                    });
                    save_builder.show();
                } else {
                    if (flag.equals("1")) //文字输入
                        schedule_str = content_str.trim(); //文字内容
                    else if (flag.equals("2")) //录音输入
                        schedule_str = content_str.trim(); //录音路径

                    date_str = set_date.getText().toString();
                    Log.d("set-date",date_str);
                    String[] my_date = date_str.split(" ");
                    time_str =  my_date[1];
                    date_str = my_date[0];

                    //写进数据库
                    //把路径写入到数据库
                    //..........
                    SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
                    db.execSQL("insert into schedule_management(date,time,schedule,flag,target) values(?,?,?,?,?)", new String[]{date_str, time_str, schedule_str, flag, username_str});

                    //editor.remove("date");
                    //editor.remove("time");
                    //editor.remove("name");
                    editor.clear();         //清空存储的文件内容
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(AddDayManagerActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        date_str = sharedPreferences.getString("date", "设置日期");
        username_str = sharedPreferences.getString("name", "");

        boolean isHasSet = sharedPreferences.getBoolean("setDate", false);
        if (!isHasSet) {
            set_date.setText(date_str);
        }else {
            editor.remove("setDate");
            editor.commit();
        }
        username.setText(username_str);

        boolean action01 = sharedPreferences.getBoolean("action", false);
        if (action01) {
            content_str = sharedPreferences.getString("item_path", "录音输入");
            luyin.setText("播放录音");
            luyin.setTextColor(Color.BLUE);
            wenzi.setEnabled(false); //文字输入不能操作
            flag = "2"; //录音输入
        } else {
            Intent intent = getIntent();
            char cancel_over_str = intent.getCharExtra("cancel_or_over", 'N');
            if (cancel_over_str == 'O') {
                String record_file_path = intent.getStringExtra("record_file_path");
                content_str = record_file_path;
                luyin.setText("播放录音");
                luyin.setTextColor(Color.BLUE);
                wenzi.setEnabled(false); //文字输入不能操作
                flag = "2"; //录音输入
                System.out.println(record_file_path);
            } else if (cancel_over_str == 'C') {  //刚刚录完音,在录音界面，点击“取消”按钮
                Log.d(" onResume ", "cancel");
            } else {
                Log.d(" onResume ", "Nothing to do");
            }
        }
        super.onResume();
    }

    private void deleteMyFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
            Toast.makeText(getApplicationContext(), "删除完成", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "对不起，所要删除的录音文件不存在", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
                && event.getRepeatCount() == 0) {
            editor.clear();
            editor.commit();
            if (flag.equals("2"))
                deleteMyFile(content_str); //如果录音了，但是没有保存就离开了当前增加日程页面，
            //则同时删除该录音文件
            Toast.makeText(getApplicationContext(), "BYE-DayManager", Toast.LENGTH_SHORT).show();
            ManageActvity.getInstance().closeActivity();//关掉activity
            AddDayManagerActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
