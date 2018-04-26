package com.example.hp.testwannianli.dayManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.dao.DataBaseHelper;
import com.example.hp.testwannianli.recorder.PlayRecordActivity;
import com.example.hp.testwannianli.recorder.RecordingActivity;
import com.example.hp.testwannianli.userdefinedwidget.DatePickerActivity;
import com.example.hp.testwannianli.util.ManageActvity;

import java.io.File;
import java.util.Calendar;

public class ManageAllDaysItemActivity extends AppCompatActivity {

    private static final String TAG = "ManageAllDaysItemActivity";

    //控件
    private TextView day_user_name;
    private Button day_item_edit;     //编辑
    private Button day_item_delete;   //删除

    private Button day_set_date; //日期
    private Button day_set_content; //设置内容

    //从主界面传递进来的列表的item的id
    private int item_id;
    //item的信息
    private String username = ""; //用户名
    private String pre_date = "";     //编辑前的日期
    private String pre_time = "";     //编辑前的时间

    private String previous_content = "";  //编辑之前内容
    private String temp_content = "";  //过渡期内容

    private int arrive_year;
    private int arrive_month;
    private int arrive_day;
    private int arrive_hour;
    private int arrive_min;

    private DataBaseHelper myDataBaseHelper;
    private final String DATABASE_NAME = "mySchedule";
    private final int VERSION = 1;

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    private String date_str = ""; //日期
    private String username_str = ""; //对象

    private String myflag = "0";
    private int flag = 0;          //0表示没有录音，1表示内容是字符串，2表示内容是录音文件
    private boolean can_look = false; //当内容是文字时，需要用该变量来表示是否可以查看内容

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_all_days_item);
        ManageActvity.getInstance().addActivity(ManageAllDaysItemActivity.this); //添加Activity
        initView();

        myDataBaseHelper = new DataBaseHelper(ManageAllDaysItemActivity.this, DATABASE_NAME, VERSION);
        sharedPreferences = getSharedPreferences("dayInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //初始化该界面的数据
        initData();
        //监听编辑按钮事件
        editOnClick();
        //监听删除按钮事件
        deleteOnClick();
        //监听内容按钮事件
        contentOnClick();
        setDateOnClick(); //监听日期设置按钮
    }

    public void initView() {
        day_user_name = (TextView) findViewById(R.id.day_user_name);
        day_item_delete = (Button) findViewById(R.id.day_item_delete);
        day_item_edit = (Button) findViewById(R.id.day_item_edit);

        day_set_date = (Button) findViewById(R.id.day_item_set_date);
        day_set_content = (Button) findViewById(R.id.content_bt);
    }

    public void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("item_info");
        Log.d("item_info",String.valueOf(bundle));

        item_id = bundle.getInt("item_id", 0);
        Log.d("item_id", String.valueOf(item_id));
        getItemInfoById(item_id); //根据传递进来的item，取出该item的相关信息

        day_user_name.setText(username); //显示对象
        day_set_date.setText(pre_date+" "+pre_time);   //显示提醒日期

        //如果flag是0，则表示没有内容；如果是1，表示是文字内容；如果是2，表示是录音文件
        if (flag == 1) {
            day_set_content.setText(previous_content);
            myflag = "1";
        } else if (flag == 2) {
            myflag = "2";
            day_set_content.setText("播放录音");
            day_set_content.setTextColor(Color.YELLOW);
            day_set_content.setEnabled(true);
        } else {
            day_set_content.setText("Nothing");
        }
    }

    public void getItemInfoById(int id) {
        SQLiteDatabase db = myDataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from schedule_management where _id = ?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            username = cursor.getString(cursor.getColumnIndex("target"));
            pre_date = cursor.getString(cursor.getColumnIndex("date")); //日期
            pre_time = cursor.getString(cursor.getColumnIndex("time")); //时间
            previous_content = cursor.getString(cursor.getColumnIndex("schedule"));
            flag = cursor.getInt(cursor.getColumnIndex("flag"));
        }
    }

    public void delete_item(int id) {
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        db.execSQL("delete from schedule_management where _id = ?", new String[]{String.valueOf(id)});
    }

    //监听编辑按钮
    public void editOnClick() {
        day_item_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("flag", String.valueOf(flag));
                Log.d("edit", day_item_edit.getText().toString());

                if (day_item_edit.getText().toString().equals("编辑")) {
                    day_set_date.setEnabled(true);

                    //当内容是录音文件时，显示录音按钮
                    if (flag == 1) {
                        day_set_content.setEnabled(true);
                    } else if (flag == 2) {
                        day_set_content.setText("重新录音");
                        day_set_content.setTextColor(Color.RED);
                        day_set_content.setEnabled(true);
                        myflag = "4"; //重新录音
                    }
                    day_item_edit.setText("点击选择");
                } else if (day_item_edit.getText().toString().equals("点击选择")) {

                    createSelectDialog(ManageAllDaysItemActivity.this);
                }
            }
        });
    }

    //监听删除按钮
    public void deleteOnClick() {
        day_item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_item(item_id); //删除该日程
                Intent intent = new Intent();
                intent.setClass(ManageAllDaysItemActivity.this, ManageAllDaysActivity.class);
                startActivity(intent);
                ManageAllDaysItemActivity.this.finish();
            }
        });
    }

    private void contentOnClick() {
        /*
        * myflag为1，表示是文字按钮；为2，表示是播放之前音乐的按钮；为3，表示是播放编辑之后的音乐的按钮；否则，是录音
        * */
        day_set_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("myFlag", String.valueOf(myflag));

                if (myflag.equals("1"))//文字按钮
                {
                    wenziOnClick(); //监听文字按钮点击
                }
                else if (myflag.equals("2"))//播放旧录音文件
                {
                    editor.putBoolean("action", true);  //存储录音文件的路径
                    Bundle bundle = new Bundle();
                    bundle.putString("item_path", previous_content);//传递编辑之前的录音文件的路径去 播放
                    bundle.putString("from","ManageAllDaysItemActivity");

                    Intent intent = new Intent();
                    intent.putExtra("item_info", bundle);
                    intent.setClass(ManageAllDaysItemActivity.this, PlayRecordActivity.class);
                    startActivity(intent);

                } else if (myflag.equals("3")) { //重新录音后，去播放该新录音文件
                    editor.putBoolean("action", true);  //除非取消编辑，否则一直播放重新录音文件
                    Bundle bundle = new Bundle();
                    bundle.putString("item_path", temp_content);//传递编辑之后的录音文件的路径去 播放
                    bundle.putString("from","ManageAllDaysItemActivity");

                    Log.d("temp_content",temp_content);

                    Intent intent = new Intent();
                    intent.putExtra("item_info", bundle);

                    intent.setClass(ManageAllDaysItemActivity.this, PlayRecordActivity.class);
                    startActivity(intent);
                } else {   //进入录音界面
                    editor.putBoolean("action", false);
                    Bundle bundle = new Bundle();

                    bundle.putInt("item_id", item_id);//因为还没有保存到数据库，所以没有item_id
                    bundle.putString("from","ManageAllDaysItemActivity");

                    Intent intent = new Intent();
                    intent.putExtra("from_bundle", bundle);

                    intent.setClass(ManageAllDaysItemActivity.this, RecordingActivity.class);
                    startActivity(intent);
                }
                //存储编辑之前录音文件的路径
                editor.putString("previous_content", previous_content);
                //存储编辑之后录音文件的路径
                editor.putString("temp_content", temp_content);

                //保存编辑前的日期和时间
                editor.putString("pre_date", pre_date); //存储日期
                editor.putString("pre_time", pre_time); //存储时间
                //保存编辑后的日期和时间
                editor.putString("date", day_set_date.getText().toString()); //存储日期
                //editor.putString("time", day_set_time.getText().toString()); //存储时间

                //保存不变的信息
                editor.putString("name", day_user_name.getText().toString().trim()); //存储对象
                editor.putInt("item_id", item_id);//保存item_id
                editor.putInt("flag", flag);//保存flag
                editor.putString("myflag", myflag);//保存myflag
                editor.commit(); //注意要提交
            }
        });
    }

    private void setDateOnClick() {
        day_set_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //存储编辑之前录音文件的路径
                editor.putString("previous_content", previous_content);
                //存储编辑之后录音文件的路径
                editor.putString("temp_content", temp_content);

                //保存编辑前的日期和时间
                editor.putString("pre_date", pre_date); //存储日期
                editor.putString("pre_time", pre_time); //存储时间
                //保存编辑后的日期和时间
                editor.putString("date", day_set_date.getText().toString()); //存储日期
                //editor.putString("time", day_set_time.getText().toString()); //存储时间

                //保存不变的信息
                editor.putString("name", day_user_name.getText().toString().trim()); //存储对象
                editor.putInt("item_id", item_id);//保存item_id
                editor.putInt("flag", flag);//保存flag
                editor.putString("myflag", myflag);//保存myflag

                editor.putBoolean("setDate", true);
                editor.commit(); //注意要提交

                startActivityForResult(new Intent(ManageAllDaysItemActivity.this, DatePickerActivity.class), 0);
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

            String arrive_month_str = arrive_month >= 10 ? "" + arrive_month : "0" + arrive_month;
            String arrive_day_str = arrive_day >= 10 ? "" + arrive_day : "0" + arrive_day;
            String dateStr = "" + arrive_year + "-" + arrive_month_str + "-" + arrive_day_str;

            //editor.putString("date", dateStr); //存储日期
            String arrive_hour_str = arrive_hour >= 10 ? "" + arrive_hour : "0" + arrive_hour;
            String arrive_min_str = arrive_min >= 10 ? "" + arrive_min : "0" + arrive_min;
            String arrive_second_str = "00";
            String timeStr = "" + arrive_hour_str + ":" + arrive_min_str + ":" + arrive_second_str;
            day_set_date.setText(dateStr+" "+timeStr);
        }
    }

    @Override
    protected void onResume() {
        //取出编辑之后的日期和时间
        date_str = sharedPreferences.getString("date", pre_date+" "+pre_time);
        boolean isHasSet = sharedPreferences.getBoolean("setDate", false);
        if (!isHasSet) {
            day_set_date.setText(date_str);
        }else {
            editor.remove("setDate");
            editor.commit();
        }

        //取出编辑之前的日期和时间
        pre_date = sharedPreferences.getString("pre_date", pre_date);
        pre_time = sharedPreferences.getString("pre_time", pre_time);

        //取出编辑之后的日期和时间
        ///date_str = sharedPreferences.getString("date", pre_date);
        //time_str = sharedPreferences.getString("time", pre_time);

        //取出用户名和item_id
        username_str = sharedPreferences.getString("name", username);
        item_id = sharedPreferences.getInt("item_id", item_id);
        flag = sharedPreferences.getInt("flag", flag);
        myflag = sharedPreferences.getString("myflag",myflag);

        Log.d(" onResume item_id",String.valueOf(item_id));
        Log.d("flag",String.valueOf(flag));

        //取出保存的录音路径
        previous_content = sharedPreferences.getString("previous_content", previous_content);
        temp_content = sharedPreferences.getString("temp_content", temp_content);

        boolean action01 = sharedPreferences.getBoolean("action", false);

        //day_set_date.setText(date_str);   //显示保存的日期
        //day_set_time.setText(time_str);   //显示保存的时间

        day_user_name.setText(username_str);//显示保存的对象

        Log.d(" onResume myflag",String.valueOf(myflag));

        if (action01) { //播放录音过来
            //根据myflag来播放旧录音，还是重新播放的录音
            if (myflag.equals("2")) {
                day_set_content.setText("播放录音");
                day_set_content.setTextColor(Color.YELLOW);
                day_set_content.setEnabled(true);
                myflag = "2"; //播放旧录音

            }else if (myflag.equals("3")){
                day_set_content.setText("播放录音");
                day_set_content.setTextColor(Color.RED);
                day_set_content.setEnabled(true);
                myflag = "3"; //播放新录音

                day_set_date.setEnabled(true);
                //day_set_time.setEnabled(true);
                day_item_edit.setText("点击选择");
            }
        } else { //不播放录音
            Intent intent = getIntent();
            char cancel_over_str = intent.getCharExtra("cancel_or_over",'N');
            //String cancel_over_str = cancel_over.getString("cancel_or_over","cancel");
            if (cancel_over_str=='O'){
                //boolean action02 = intent.getBooleanExtra("cancel_or_over", false);
                //if (action02) { //刚刚录完音,在录音界面，点击“说完了”按钮
                String record_file_path = intent.getStringExtra("record_file_path");
                temp_content = record_file_path;
                day_set_content.setText("播放录音");
                day_set_content.setTextColor(Color.RED);
                day_set_content.setEnabled(true);
                myflag = "3"; //播放 重新录 的录音

                day_set_date.setEnabled(true);
                //day_set_time.setEnabled(true);
                day_item_edit.setText("点击选择");

                Log.d("play new luyin.",record_file_path);
            } else if (cancel_over_str=='C'){  //刚刚录完音,在录音界面，点击“取消”按钮
                day_set_content.setTextColor(Color.RED);
                day_set_content.setText("重新录音");
                day_set_content.setEnabled(true);
                myflag = "4"; //重新录音

                day_set_date.setEnabled(true);
                //day_set_time.setEnabled(true);
                day_item_edit.setText("点击选择");
                Log.d(" onResume ","cancel");
                //System.out.println("Nothing to do.");
            }else {
                Log.d(" onResume ","Nothing to do");
            }
        }
        super.onResume();
    }

    private void cancelOnClick() {
        day_user_name.setText(username_str); //显示对象

        day_set_date.setEnabled(false);
        day_set_date.setText(pre_date+" "+pre_time);

        if (flag == 1) {
            day_set_content.setText(previous_content);
            day_set_content.setEnabled(false);
        } else if (flag == 2) {
            deleteMyFile(temp_content);
            day_set_content.setText("播放录音");
            day_set_content.setTextColor(Color.YELLOW);
            day_set_content.setEnabled(true);
            myflag = "2"; //播放旧录音
        }
        day_item_edit.setText("编辑");

        editor.clear();         //清空存储的文件内容
        editor.commit();
    }

    private void saveOnClick() {
        String my_date_str = day_set_date.getText().toString();
        Log.d("set-date",my_date_str);

        String[] my_date = my_date_str.split(" ");
        String my_time_str =  my_date[1];
        my_date_str = my_date[0];

        String my_schedule_str="";
        if (flag==1) {
            my_schedule_str = day_set_content.getText().toString();
        }
        else if (flag==2){
            my_schedule_str = temp_content;
            if (my_schedule_str.equals(""))
                my_schedule_str = previous_content;
        }
        Log.d("saveOnClick", my_schedule_str);

        //写进数据库
        //把路径写入到数据库
        //..........
        SQLiteDatabase db = myDataBaseHelper.getWritableDatabase();
        db.execSQL("update schedule_management set date = ?,time = ?,schedule = ? where _id = ?", new String[]{my_date_str, my_time_str, my_schedule_str, String.valueOf(item_id)});
        editor.clear();         //清空存储的文件内容
        editor.commit();

        Intent intent = new Intent();
        intent.setClass(ManageAllDaysItemActivity.this, ManageAllDaysActivity.class);
        startActivity(intent);
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

    private void createSelectDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("对编辑选择操作")
                .setItems(R.array.arrcontent,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String[] PK = getResources().getStringArray(R.array.arrcontent);
                                Toast.makeText(context, PK[which], Toast.LENGTH_LONG).show();
                                if (PK[which].equals("保存编辑")) {
                                    saveOnClick();
                                } else if (PK[which].equals("取消编辑")) {
                                    cancelOnClick();
                                }
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
    }

    private void wenziOnClick() {
        temp_content = day_set_content.getText().toString().trim();//获取按钮显示的内容

        final AlertDialog.Builder builder02 = new AlertDialog.Builder(ManageAllDaysItemActivity.this);
        builder02.setTitle("您的文字内容");
        builder02.setMessage(temp_content); //可以自定义view
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
                wenzi_content.setText(temp_content);

                final AlertDialog.Builder builder = new AlertDialog.Builder(ManageAllDaysItemActivity.this);
                builder.setView(wenziView);
                builder.setTitle("您要编辑的文字内容");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //将字符串后面的多余空格去掉
                        temp_content = wenzi_content.getText().toString().trim();
                        day_set_content.setText(temp_content);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                    }
                });
                builder.show();
            }
        });
        builder02.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
                && event.getRepeatCount() == 0) {
            cancelOnClick();
            Toast.makeText(getApplicationContext(), "BYE-ManageAllDaysItemActivity", Toast.LENGTH_SHORT).show();
            ManageActvity.getInstance().closeActivity();//关掉activity
            //ManageAllDaysItemActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
