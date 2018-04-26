package com.example.hp.testwannianli.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.util.CalendarUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by HP on 2016-9-7.
 */

/**
 * 显示日期的adapter
 */
public class CalendarGridViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

    /**
     * 日历item中默认id从0xff0000开始
     */
    private final static int DEFAULT_ID = 0xff0000;
    private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历
    private Calendar calSelected = Calendar.getInstance();// 当前显示的日历

    /**
     * 标注的日期
     */
    private int select_day;
    private int clickTemp = -1;
    private int oldMonth; //保留传递过来的日期的月份，用来比较
    private View tempView = null; //保留没有点击的view

    private List<Date> markDates;
    private Context mContext;
    private Calendar calToday = Calendar.getInstance(); // 今日
    private ArrayList<Date> titles;

    private ArrayList<Date> getDates() {
        UpdateStartDateForMonth();
        ArrayList<Date> alArrayList = new ArrayList<Date>();
        for (int i = 1; i <= 42; i++) {
            alArrayList.add(calStartDate.getTime());
            calStartDate.add(Calendar.DAY_OF_MONTH, 1);   //天数加1
        }
        return alArrayList;
    }

    // construct
    public CalendarGridViewAdapter(Context context, Calendar cal, List<Date> dates, int month, int select_day) {

        calStartDate = cal;
        this.mContext = context;
        titles = getDates();
        this.markDates = dates;
        this.mInflater = LayoutInflater.from(context);

        this.oldMonth = month; //保留传递过来的日期的月份，用来比较
        this.select_day = select_day;//用户选择的某天
    }

    /**
     * 构造函数
     */
    public CalendarGridViewAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Date myDate = (Date) getItem(position);
        Calendar calCalendar = Calendar.getInstance();
        calCalendar.setTime(myDate);
        int day = myDate.getDate(); // 日期

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item, null);
            holder = new ViewHolder();
            /**得到各个控件的对象*/
            holder.nongli = (TextView) convertView.findViewById(R.id.nongli);
            holder.xinli = (TextView) convertView.findViewById(R.id.xinli);

            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
        /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
        holder.xinli.setText(String.valueOf(day));

        // 显示公历
        CalendarUtil calendarUtil = new CalendarUtil(calCalendar);
        Map<String, String> map;
        map = calendarUtil.getCalendarItem();
        String message = map.get("message");
        String isJieQi = map.get("jieqi");
        holder.nongli.setText(message);

        // 点击改变选中listItem的背景色
        if (clickTemp == position) {
            convertView.setBackgroundColor(Color.argb(0xff, 0x00, 0x99, 0xFF));
            //tempView.setBackgroundColor(Color.TRANSPARENT);
            //clickTemp = -1;
        } else {
            //System.out.println("-------------"+select_day);
            //System.out.println("+++++++++++++"+myDate.getDate());
            if (select_day == myDate.getDate())  //选中的天数和该item的天数相同，则显示蓝色
            {
                /*
                if (tempView != null) {
                    tempView.setBackgroundColor(Color.TRANSPARENT);

            }*/
                //convertView.setBackgroundColor(Color.argb(0xff, 0x00, 0x99, 0xFF));
                convertView.setBackgroundColor(Color.argb(0xff, 0xCC, 0xCC, 0x99));
                //tempView = convertView;  //因为是没有点击的默认选中的view，所以需要暂时保存，
                // 当点击其他view时，再把该view的背景色设置透明
            } else
                convertView.setBackgroundColor(Color.TRANSPARENT);
            //convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        //System.out.println("----------------"+calToday.getTime());
        //System.out.println("+++++++++++++++++"+myDate);

        // 如果是当前日期则显示不同颜色
        if (equalsDate(calToday.getTime(), myDate)) {
            convertView.setBackgroundColor(Color.argb(0xff, 0xff, 0xff, 0x66));
        }
        // 这里用于比对是不是比当前日期小，如果比当前日期小则显示浅灰色
        //if (!CalendarUtil.compare(myDate, calToday.getTime())) {

        //比较传递过来的日期的月份与所要展示的item的月份，不是该月份的，则显示为不同颜色
        if (myDate.getMonth() != this.oldMonth) {
            convertView.setBackgroundColor(Color.argb(0xff, 0xee, 0xee, 0xee));
            holder.xinli.setTextColor(Color.argb(0xff, 0xc0, 0xc0, 0xc0));
            holder.nongli.setTextColor(Color.argb(0xff, 0xc0, 0xc0, 0xc0));
        } else {
            if (isJieQi == "1") {
                holder.xinli.setTextColor(Color.argb(0xff, 0xff, 0x40, 0x81));
                holder.nongli.setTextColor(Color.argb(0xff, 0xff, 0x40, 0x81));
            } else {
                holder.nongli.setTextColor(Color.argb(0xff, 0x60, 0x3b, 0x07));
                holder.xinli.setTextColor(Color.argb(0xff, 0x60, 0x3b, 0x07));
            }
        }

        // 设置标注日期颜色
        if (markDates != null) {
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            for (Date date : markDates) {
                if (format.format(myDate).equals(format.format(date))) {
                    convertView.setBackgroundColor(Color.argb(0xff, 0xd3, 0x3a, 0x3a));
                    break;
                }
            }
        }
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @SuppressWarnings("deprecation")
    private Boolean equalsDate(Date date1, Date date2) {
        if (date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDate() == date2.getDate()) {
            return true;
        } else {
            return false;
        }
    }

    // 根据改变的日期更新日历
    // 填充日历控件用
    private void UpdateStartDateForMonth() {
        calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天

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
        calStartDate.add(Calendar.DAY_OF_MONTH, -1);// 周日第一位
    }

    public void setSelectedDate(Calendar cal) {
        calSelected = cal;
    }

    //标识选择的Item
    public void setSeclection(int position) {
        clickTemp = position;
    }

    /**
     * 存放控件
     */
    public class ViewHolder {
        public TextView xinli;
        public TextView nongli;
    }
}