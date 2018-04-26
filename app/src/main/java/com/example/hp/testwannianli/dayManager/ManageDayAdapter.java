package com.example.hp.testwannianli.dayManager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hp.testwannianli.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by HP on 2016-9-20.
 */
public class ManageDayAdapter extends BaseAdapter {

    /**
     * 初始显示的页数
     */
    private int pageNum = 1;
    /**
     * 每一次显示多少个
     */
    private static final int pageCount = 10;

    private Context mContext;
    private ArrayList<Map<String, Object>> mListItems;
    private ArrayList<Map<String, Object>> mListItemsPage;
    private int clickTemp = -1;

    public ManageDayAdapter(Context context) {
        this.mContext = context;
    }

    public ManageDayAdapter(Context context, ArrayList<Map<String, Object>> listItems,ArrayList<Map<String, Object>> listItemsPage,int pageNum) {
        this.mContext = context;
        this.mListItems = listItems;
        this.mListItemsPage = listItemsPage;
        this.pageNum = pageNum;
    }

    @Override
    public int getCount() {
        // 返回该显示的数据个数
       // pageCount 一页显示多少个
      //pageNum 第几页
        //return pageCount*pageNum;
      //return mListItems.size();
        return mListItemsPage.size();
    }
    @Override
    public Object getItem(int position) {
        return mListItems != null ? mListItems.get((pageNum-1)*pageCount+position) : null;
    }
    @Override
    public long getItemId(int position) {
        return (pageNum-1)*pageCount+position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.day_list_item, null);
            holder = new ViewHolder();
            holder.item_time = (TextView) convertView.findViewById(R.id.list_item_time);
            holder.item_object = (TextView) convertView.findViewById(R.id.list_item_object);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
        /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
        String item_time_str = mListItems.get((pageNum-1)*pageCount+position).get("item_time").toString();
        String item_object_str = mListItems.get((pageNum-1)*pageCount+position).get("item_object").toString();
        int item_valid = Integer.parseInt(String.valueOf(mListItems.get((pageNum-1)*pageCount+position).get("item_valid")));

        holder.item_time.setText(item_time_str); //提醒时间
        if (item_object_str.length()>15){
            holder.item_object.setText(item_object_str.substring(0,14)+"..."); //对象
        }else {
            holder.item_object.setText(item_object_str); //对象
        }
        //设置颜色,返回 -1，表示过期；返回0，表示正好；返回1，表示还没有过期。
        if (item_valid == 1) {
            holder.item_time.setTextColor(Color.argb(0xff, 0xff, 0x40, 0x81));
            holder.item_object.setTextColor(Color.argb(0xff, 0xff, 0x40, 0x81));
        } else if (item_valid == 0) {
            holder.item_time.setTextColor(Color.argb(0xff, 0xff, 0xff, 0x66));
            holder.item_object.setTextColor(Color.argb(0xff, 0xff, 0xff, 0x66));
        } else {
            holder.item_time.setTextColor(Color.argb(0xff, 0xc0, 0xc0, 0xc0));
            holder.item_object.setTextColor(Color.argb(0xff, 0xc0, 0xc0, 0xc0));
        }
        // 点击改变选中listItem的背景色
        if (clickTemp == position) {
            convertView.setBackgroundColor(Color.argb(0xff, 0x00, 0x99, 0xFF));
            holder.item_time.setTextSize(20);
            holder.item_object.setTextSize(20);
            //clickTemp = -1;
        }else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            holder.item_time.setTextSize(15);
            holder.item_object.setTextSize(15);
        }
        return convertView;
    }
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
    //标识选择的Item
    public void setSeclection(int position) {
        clickTemp = position;
    }
    //设置pageNum
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    /**
     * 存放控件
     */
   public final class ViewHolder {
        public TextView item_time;
        public TextView item_object;
    }
}
