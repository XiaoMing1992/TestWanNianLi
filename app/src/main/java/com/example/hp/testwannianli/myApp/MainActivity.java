package com.example.hp.testwannianli.myApp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.adapter.CalendarGridViewAdapter;
import com.example.hp.testwannianli.dao.DataBaseUtil;
import com.example.hp.testwannianli.dao.MyDataBase;
import com.example.hp.testwannianli.dao.MyDataBaseHelper;
import com.example.hp.testwannianli.dayManager.AddDayManagerActivity;
import com.example.hp.testwannianli.dayManager.ManageAllDaysActivity;
import com.example.hp.testwannianli.dayManager.MenuActivity;
import com.example.hp.testwannianli.service.DayManageService;
import com.example.hp.testwannianli.util.CalendarUtil;
import com.example.hp.testwannianli.util.ManageActvity;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "StartService";

    private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
    private CalendarGridViewAdapter gAdapter;
    private int iMonthViewCurrentMonth = 0; // 当前视图月
    private int iMonthViewCurrentYear = 0; // 当前视图年
    private int currentMonth = 0; // 当前视图月
    private int currentYear = 0; // 当前视图年
    private int BASE_YEAR = 1901;//基年

    /**
     * 标注日期
     */
    private List<Date> markDates = null;

    Spinner select_year, select_month;
    ImageButton pre_month, next_month;
    Button go_back_today;
    GridView gridView;

    TextView TV_YI; //宜
    TextView TV_JI; //忌
    TextView ganZhi;//干支

    MyDataBaseHelper myDataBaseHelper;

    // 屏幕宽度和高度
    private int screenWidth;
    private int screenHeight;
    int select_day = 0;
    private Calendar calToday = Calendar.getInstance(); // 今日
    /**
     * 记录和判断是否已经开启过
     * */
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x01:
                    showToast("下载完成!");
                    handler.removeCallbacks(run); //移除
                    break;
                default:
                    break;
            }
        }
    };

    //开启新线程，把黄历数据库写进/data/data/com.example.hp.testwannianli/databases/中
    Runnable run = new Runnable() {
        @Override
        public void run() {
            //把数据库里面的数据写进真机
            boolean copy_state = copyDbToSdCard();
            //
            Message message = new Message();
            if (copy_state){
                message.what = 0x01;
            }
            handler.sendMessage(message);
        }
    };

    /**
     * created by claptrap
     *
     * @return true or false
     */
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i(TAG,service.service.getClassName());
            if ("com.example.hp.testwannianli.service.DayManageService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        myDataBaseHelper = new MyDataBaseHelper(this, "myHuangLi.db", 1);

        //开启新线程，下载数据库
        //handler.post(run);
        copyDbToSdCard();

        initView();
        initDatas();
        //初始化Spinner控件
        initSpinner();

        initAllControlButton();//悬浮菜单按钮
        //===============================================================
        // 底部显示日历
        UpdateStartDateForMonth();
        select_day = calToday.get(Calendar.DAY_OF_MONTH);
        init();
        currentMonth = iMonthViewCurrentMonth;
        currentYear = iMonthViewCurrentYear;
        select_month.setSelection(currentMonth);
        select_year.setSelection(currentYear - 1901);

        //=========================开启服务=============================
        final Intent service_intent = new Intent(this, DayManageService.class);
        if(!isServiceRunning())
            startService(service_intent);
        //==============================================================

        // 监听选择年
        select_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectYear(BASE_YEAR + position);
                init();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "没有改变的处理",
                        Toast.LENGTH_LONG).show();
            }
        });
        // 监听选择月
        select_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectMonth(position);
                init();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "没有改变的处理",
                        Toast.LENGTH_LONG).show();
            }
        });

        //前一个月监听
        pre_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPrevViewItem();
                init();
            }
        });
        //后一个月监听
        next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNextViewItem();
                init();
            }
        });

        //“返回今天”按钮监听
        go_back_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentViewItem();
                init();
            }
        });

        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();*/
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, MenuActivity.class);
//                startActivity(intent);
//
//            }
//        });
    }

    public void initView() {
        select_year = (Spinner) findViewById(R.id.select_year);
        select_month = (Spinner) findViewById(R.id.select_month);
        pre_month = (ImageButton) findViewById(R.id.pre_month);
        next_month = (ImageButton) findViewById(R.id.next_month);
        gridView = (GridView) findViewById(R.id.gridView);
        go_back_today = (Button) findViewById(R.id.goBackToday);

        TV_YI = (TextView) findViewById(R.id.textView01);
        TV_JI = (TextView) findViewById(R.id.textView02);
        ganZhi = (TextView) findViewById(R.id.gangzhi); //干支
    }

    public void initDatas() {
        // 得到屏幕的宽度
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
    }

    public void initSpinner() {
        String[] years;
        years = new String[150];
        for (int j = 0, i = 1901; i <= 2050; j++, i++) {
            years[j] = i + "年";
        }
        String[] months = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, years);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, months);
        select_year.setAdapter(yearAdapter);
        select_month.setAdapter(monthAdapter);
    }

    //当前
    private void setCurrentViewItem() {
        iMonthViewCurrentMonth = currentMonth;
        iMonthViewCurrentYear = currentYear;

        //=====设置下拉框当前显示数字
        select_month.setSelection(iMonthViewCurrentMonth);
        select_year.setSelection(iMonthViewCurrentYear - 1901);
        //=====

        calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); // 设置月
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear); // 设置年

        //设置回今天
        select_day = calToday.get(Calendar.DAY_OF_MONTH);
    }

    // 上一个月
    private void setPrevViewItem() {
        iMonthViewCurrentMonth--;// 当前选择月--
        // 如果当前月为负数的话显示上一年
        if (iMonthViewCurrentMonth == -1) {
            iMonthViewCurrentMonth = 11;
            iMonthViewCurrentYear--;
        }

        //====设置下拉框当前显示数字
        select_month.setSelection(iMonthViewCurrentMonth);
        select_year.setSelection(iMonthViewCurrentYear - 1901);
        //====

        calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth); // 设置月
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear); // 设置年
    }

    // 下一个月
    private void setNextViewItem() {
        iMonthViewCurrentMonth++;
        if (iMonthViewCurrentMonth == 12) {
            iMonthViewCurrentMonth = 0;
            iMonthViewCurrentYear++;
        }

        //====设置下拉框当前显示数字
        select_month.setSelection(iMonthViewCurrentMonth);
        select_year.setSelection(iMonthViewCurrentYear - 1901);
        //====

        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
    }

    // 响应选择年份的日期重置
    private void setSelectYear(int year) {
        iMonthViewCurrentYear = year;
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
    }

    // 响应选择月份的日期重置
    private void setSelectMonth(int month) {
        iMonthViewCurrentMonth = month;
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
        calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
    }

    // 根据改变的日期更新日历
    // 填充日历控件用
    private void UpdateStartDateForMonth() {
        calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
        iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月
        iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);// 得到当前日历显示的年

        // 星期一是2 星期天是1 填充剩余天数
        int iDay = 0;
        int iFirstDayOfWeek = Calendar.MONDAY;
        int iStartDay = iFirstDayOfWeek;
        if (iStartDay == Calendar.MONDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            if (iDay < 0)
                iDay = 6;
        }
        if (iStartDay == Calendar.SUNDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
            if (iDay < 0)
                iDay = 6;
        }
        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
    }

    public void init() {
        markDates = new ArrayList<Date>();
        markDates.add(new Date());
        setMarkDates(markDates);

        Calendar tempSelected = Calendar.getInstance();
        tempSelected.setTime(calStartDate.getTime());

        int finalDay = tempSelected.getActualMaximum(Calendar.DAY_OF_MONTH); //获取当月的最后一天
        if (finalDay < select_day)
            select_day = finalDay;

        //设置宜和忌
        initYiAndJi(String.valueOf(tempSelected.get(Calendar.YEAR)),
                String.valueOf(tempSelected.get(Calendar.MONTH) + 1),
                String.valueOf(select_day));

        // 显示公历
        CalendarUtil calendarUtil = new CalendarUtil(tempSelected);
        ganZhi.setText(calendarUtil.cyclical() + "年" + "\n【" + calendarUtil.animalsYear() + "年】");

        gAdapter = new CalendarGridViewAdapter(MainActivity.this, tempSelected,
                markDates, iMonthViewCurrentMonth, select_day);
        gridView.setAdapter(gAdapter);// 设置菜单Adapter

        int i = screenWidth / 7;
        int j = screenWidth - (i * 7);
        int x = j / 2;
        gridView.setPadding(x, 0, 0, 0);// 居中

        //gridView.setFocusable(true);

        //添加监听
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Calendar calCalendar = Calendar.getInstance();
                TextView xinli = (TextView) view.findViewById(R.id.xinli);
                select_day = Integer.valueOf(xinli.getText().toString()); //用户选择的某天
                gAdapter.setSeclection(position);
                gAdapter.notifyDataSetChanged();

                calCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(xinli.getText().toString())); //设置天
                //calCalendar.set(Calendar.MONTH, Integer.valueOf(month.getText().toString())); //设置月
                //calCalendar.set(Calendar.YEAR, Integer.valueOf(year.getText().toString()) - 1901);  //设置年
                calCalendar.set(Calendar.MONTH, iMonthViewCurrentMonth); //设置月
                calCalendar.set(Calendar.YEAR, iMonthViewCurrentYear);  //设置年

                // 显示公历
                CalendarUtil calendarUtil = new CalendarUtil(calCalendar);
                ganZhi.setText(calendarUtil.cyclical() + "年" + "\n【" + calendarUtil.animalsYear() + "年】");

                //设置宜和忌
                initYiAndJi(String.valueOf(calCalendar.get(Calendar.YEAR)),
                        String.valueOf(calCalendar.get(Calendar.MONTH) + 1),
                        String.valueOf(calCalendar.get(Calendar.DAY_OF_MONTH)));
            }
        });

        //选择监听
        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (view != null) {
                    Calendar calCalendar = Calendar.getInstance();
                    TextView xinli = (TextView) view.findViewById(R.id.xinli);
                    select_day = Integer.valueOf(xinli.getText().toString()); //用户选择的某天
                    gAdapter.setSeclection(position);
                    gAdapter.notifyDataSetChanged();

                    calCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(xinli.getText().toString())); //设置天
                    //calCalendar.set(Calendar.MONTH, Integer.valueOf(month.getText().toString())); //设置月
                    //calCalendar.set(Calendar.YEAR, Integer.valueOf(year.getText().toString()) - 1901);  //设置年
                    calCalendar.set(Calendar.MONTH, iMonthViewCurrentMonth); //设置月
                    calCalendar.set(Calendar.YEAR, iMonthViewCurrentYear);  //设置年

                    // 显示公历
                    CalendarUtil calendarUtil = new CalendarUtil(calCalendar);
                    ganZhi.setText(calendarUtil.cyclical() + "年" + "\n【" + calendarUtil.animalsYear() + "年】");

                    //设置宜和忌
                    initYiAndJi(String.valueOf(calCalendar.get(Calendar.YEAR)),
                            String.valueOf(calCalendar.get(Calendar.MONTH) + 1),
                            String.valueOf(calCalendar.get(Calendar.DAY_OF_MONTH)));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(),"日期没有选择",Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void setMarkDates(List<Date> markDates) {
        this.markDates.clear();
        this.markDates.addAll(markDates);
    }

    public void initYiAndJi(String year, String month, String day) {
        MyDataBase myDataBase = new MyDataBase(MainActivity.this);
        TV_YI.setText(myDataBase.getYi(year, month, day));
        TV_JI.setText(myDataBase.getJi(year, month, day));
    }

    private void showToast(String content) {
        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
    }
    private boolean copyDataBaseToPhone() {
        DataBaseUtil util = new DataBaseUtil(this);
        // 判断数据库是否存在
        boolean dbExist = util.checkDataBase();
        if (dbExist) {
            Log.i("tag", "The database is exist.");
        } else {// 不存在就把raw里的数据库写入手机
            try {
                util.copyDataBase();
                Log.i("tag", "复制结束");
                return true;
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
        return false;
    }
    //把数据库Huangli.db的数据复制到真机上
    private boolean copyDbToSdCard() {
        //boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        //if (hasSDCard) {
            return copyDataBaseToPhone();
        //} else {

        //    showToast("未检测到SDCard");
        //}
        //return false;
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
                && event.getRepeatCount() == 0) {
            //Toast.makeText(getApplicationContext(), "BYE-MainActivity", Toast.LENGTH_SHORT).show();
            ManageActvity.getInstance().closeActivity();//关掉activity
            MainActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
*/

    /*
 * initallControlButton  初始化浮动菜单栏以及点击事件
 * */
    public void initAllControlButton(){
        final ImageView allIcon = new ImageView(MainActivity.this);
        allIcon.setImageDrawable(getResources().getDrawable(R.drawable.button_action_dark));;

        final FloatingActionButton allControlButton = new FloatingActionButton.Builder(MainActivity.this).setContentView(allIcon).build();
        allControlButton.setFocusable(true);

        SubActionButton.Builder subBuilder = new SubActionButton.Builder(MainActivity.this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);
        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.button_action_dark_selector));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.all_calendar));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.button_action_selector));
        rlIcon4.setImageDrawable(getResources().getDrawable(R.drawable.button_action_dark_touch));
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(MainActivity.this)
                .addSubActionView(subBuilder.setContentView(rlIcon1).build())
                .addSubActionView(subBuilder.setContentView(rlIcon2).build())
                .addSubActionView(subBuilder.setContentView(rlIcon3).build())
                .addSubActionView(subBuilder.setContentView(rlIcon4).build())
                .attachTo(allControlButton).build();
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.button_action_dark));
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener(){
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                // 增加按钮中的+号图标顺时针旋转45度
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                // 增加按钮中的+号图标逆时针旋转45度
                // Rotate the icon of rightLowerButton 45 degrees
                // counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("test","第一个按钮");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AddDayManagerActivity.class);
                startActivity(intent);
            }
        });
        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,ManageAllDaysActivity.class);
                startActivity(intent);
            }
        });
        rlIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        rlIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * 键盘事件
     * */
    public boolean onKeyDown(int keyCode,KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_0:
                DisplayToast("你按数字键0");
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                DisplayToast("你按中间键");
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                DisplayToast("你按下方向键");
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                DisplayToast("你按左方向键");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                DisplayToast("你按右方向键");
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                DisplayToast("你按上方向键");
                break;
            case KeyEvent.KEYCODE_ALT_LEFT:
                DisplayToast("你按组合键alt+←");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * Toast展示
     * */
    public void DisplayToast(String content){
        Toast.makeText(getApplicationContext(),content,Toast.LENGTH_SHORT).show();
    }

    /**
     * 模拟鼠标事件
     * */
    /*
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch(event.getKeyCode()){
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                DisplayToast("模拟鼠标点击");
                break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }*/
}
