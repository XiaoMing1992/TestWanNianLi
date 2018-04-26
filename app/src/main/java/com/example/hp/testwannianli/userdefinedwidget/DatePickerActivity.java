package com.example.hp.testwannianli.userdefinedwidget;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.entity.Solar;
import com.example.hp.testwannianli.entity.Variables;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * created by claptrap
 *
 * 2016.9.30
 */
public class DatePickerActivity extends Activity {
    private static final String TAG = "UserDefinedWidget";

    NumberPicker yearPicker,monthPicker,dayPicker;
    NumberPicker hourPicker,minutePicker;
    Button ok,cancel;

    private int year = 1901;
    private int month = 1;
    private int day = 1;
    private int hour = 0;
    private int minute = 0;
//    private int second = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker_activity);

        yearPicker = (NumberPicker) findViewById(R.id.yearPicker);
        monthPicker = (NumberPicker) findViewById(R.id.monthPicker);
        dayPicker = (NumberPicker) findViewById(R.id.dayPicker);
        hourPicker = (NumberPicker) findViewById(R.id.hourPicker);
        minutePicker = (NumberPicker) findViewById(R.id.minutePicker);
//        secondPicker = (NumberPicker) findViewById(R.id.secondPicker);
        ok = (Button) findViewById(R.id.ok);
        cancel = (Button) findViewById(R.id.cancel);

        this.setToday();

        yearPicker.setMaxValue(Variables.YEAR_MAX_VALUE);
        yearPicker.setMinValue(Variables.YEAR_MIN_VALUE);
        yearPicker.setValue(year);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                year = newVal;
                setMaxValueOfDay(year,month);
            }
        });
        yearPicker.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if (getCurrentFocus() == view){
                    Log.i(TAG,"---yearPicker---");
//                    yearPicker.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    yearPicker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button1));
                }
                else{
                    Log.i(TAG,"---loseYearPicker---");
                    yearPicker.setBackgroundColor(Color.alpha(R.color.colorNone));
                }
            }
        });
        monthPicker.setMaxValue(Variables.MONTH_MAX_VALUE);
        monthPicker.setMinValue(Variables.MONTH_MIN_VALUE);
        monthPicker.setValue(month);
        monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                month = newVal;
                setMaxValueOfDay(year,month);
            }
        });
        monthPicker.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if (getCurrentFocus() == view){
                    Log.i(TAG,"---monthPicker---");
//                    monthPicker.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    monthPicker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button2));
                }
                else{
                    Log.i(TAG,"---loseMonthPicker---");
                    monthPicker.setBackgroundColor(Color.alpha(R.color.colorNone));
                }
            }
        });
//        dayPicker.setMaxValue(Variables.DAY_MAX_VALUE);
        dayPicker.setMinValue(Variables.DAY_MIN_VALUE);
        dayPicker.setValue(day);
        dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                day = newVal;
            }
        });
        dayPicker.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if (getCurrentFocus() == view){
                    Log.i(TAG,"---dayPicker---");
                    dayPicker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button3));
//                    dayPicker.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
                else{
                    Log.i(TAG,"---loseDayPicker---");
                    dayPicker.setBackgroundColor(Color.alpha(R.color.colorNone));
                }
            }
        });
        hourPicker.setMaxValue(Variables.HOUR_MAX_VALUE);
        hourPicker.setMinValue(Variables.HOUR_MIN_VALUE);
        hourPicker.setValue(hour);
        hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                hour = newVal;
            }
        });
        hourPicker.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if (getCurrentFocus() == view){
                    Log.i(TAG,"---hourPicker---");
//                    hourPicker.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    hourPicker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button4));
                }
                else{
                    Log.i(TAG,"---loseHourPicker---");
                    hourPicker.setBackgroundColor(Color.alpha(R.color.colorNone));
                }
            }
        });
        minutePicker.setMaxValue(Variables.MINUTE_MAX_VALUE);
        minutePicker.setMinValue(Variables.MINUTE_MIN_VALUE);
        minutePicker.setValue(minute);
        minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                minute = newVal;
            }
        });
        minutePicker.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if (getCurrentFocus() == view){
                    Log.i(TAG,"---minutePicker---");
//                    minutePicker.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    minutePicker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button5));
                }
                else{
                    Log.i(TAG,"---loseMinutePicker---");
                    minutePicker.setBackgroundColor(Color.alpha(R.color.colorNone));
                }
            }
        });
        /*secondPicker.setMaxValue(Variables.SECOND_MAX_VALUE);
        secondPicker.setMinValue(Variables.SECOND_MIN_VALUE);
        secondPicker.setValue(second);
        secondPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                second = newVal;
            }
        });
        secondPicker.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if (getCurrentFocus() == view){
                    Log.i(TAG,"---secondPicker---");
//                    secondPicker.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    secondPicker.setBackground(getResources().getDrawable(R.drawable.button6));
                }
                else{
                    Log.i(TAG,"---loseSecondPicker---");
                    secondPicker.setBackgroundColor(Color.alpha(R.color.colorNone));
                }
            }
        });*/

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                intent.putExtra("year",String.valueOf(year));
                intent.putExtra("month",String.valueOf(month));
                intent.putExtra("day",String.valueOf(day));
                intent.putExtra("hour",String.valueOf(hour));
                intent.putExtra("minute",String.valueOf(minute));
//                intent.putExtra("second",String.valueOf(second));
                DatePickerActivity.this.setResult(0,intent);
                DatePickerActivity.this.finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerActivity.this.finish();
            }
        });
    }
    private void setMaxValueOfDay(int year,int month){
        int flag = Solar.daysOfMonth(year, month);
        if (day > flag){
            dayPicker.setValue(flag);
            day = flag;
        }
        dayPicker.setMaxValue(flag);
    }
    private void setToday(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        year = Integer.parseInt((new SimpleDateFormat("yyyy", Locale.CHINA)).format(date));
        month = Integer.parseInt((new SimpleDateFormat("MM", Locale.CHINA)).format(date));
        day = Integer.parseInt((new SimpleDateFormat("dd", Locale.CHINA)).format(date));
        hour = Integer.parseInt((new SimpleDateFormat("kk", Locale.CHINA)).format(date));
        minute = Integer.parseInt((new SimpleDateFormat("mm", Locale.CHINA)).format(date));
//        second = Integer.parseInt((new SimpleDateFormat("ss", Locale.CHINA)).format(date));
        dayPicker.setMaxValue(Solar.daysOfMonth(year, month));
    }
}
