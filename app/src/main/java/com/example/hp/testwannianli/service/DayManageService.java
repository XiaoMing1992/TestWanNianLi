package com.example.hp.testwannianli.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.hp.testwannianli.dao.DataBaseHelper;
import com.example.hp.testwannianli.dayManager.AlarmActivity;
import com.example.hp.testwannianli.dayManager.SoundActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DayManageService extends Service {
    private static final String TAG = "DayManageService";
    public static final int SCHEDULE = 1;
    public static final int SOUND_PATH = 2;
    String sound_target = "";
    NotificationManager notificationManager;

    DataBaseHelper myDataBaseHelper;
    private final String DATABASE_NAME = "mySchedule";
    private final int VERSION = 1;

    SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm:00");
    SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SCHEDULE){
                Intent intent = new Intent(DayManageService.this,AlarmActivity.class);
                intent.putExtras(msg.getData());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else if (msg.what == SOUND_PATH){
                Intent intent = new Intent(DayManageService.this,SoundActivity.class);
                intent.putExtras(msg.getData());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            super.handleMessage(msg);
        }
    };

    public DayManageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Service is Created.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Service is Started.");
        notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        myDataBaseHelper = new DataBaseHelper(DayManageService.this, DATABASE_NAME, VERSION); //初始化
        final SQLiteDatabase db = myDataBaseHelper.getReadableDatabase(); //获取数据库

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i(TAG,"------service------");
                        Calendar now = Calendar.getInstance();
                        Date myDate = now.getTime();

                        String myTime = sdf_time.format(myDate); //格式化时间
                        //String[] time_str = myTime.split(":");
                        //myTime = time_str[0]+":"+time_str[1]+":00";
                        Log.i(TAG,myTime);

                        String myDate_str = sdf_date.format(myDate); //格式化日期
                        Cursor cursor =  db.rawQuery("select * from schedule_management where date = ? and time = ?;",new String[]{myDate_str,myTime});
                        Log.i(TAG,myDate_str);

                        String output = "";
                        String sound = "";
                        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
                            int scheduleColumn = cursor.getColumnIndex("schedule");
                            String schedule = cursor.getString(scheduleColumn);
                            int targetColumn = cursor.getColumnIndex("target");
                            String target = cursor.getString(targetColumn);
                            int flag = cursor.getInt(cursor.getColumnIndex("flag"));
                            if (flag == SCHEDULE) {
                                output = output + target + ":\n" + schedule + "\n";
                            }
                            /*
                             * 当字符串schedule表示音频文件位置时，即flag值为2时
                             * 同一时间只有一个音频文件
                             * 或若干个字符文件
                             */
                            else if(flag == SOUND_PATH){
                                sound = schedule;
                                sound_target = target;
                            }
                        }
                        if (!output.equals("")){
                            Message message = new Message();
                            message.what = SCHEDULE;
                            Bundle bundle = new Bundle();
                            bundle.putString("output",output);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                        if (!sound.equals("")){
                            Message message = new Message();
                            message.what = SOUND_PATH;
                            Bundle bundle = new Bundle();
                            bundle.putString("sound",sound);
                            bundle.putString("target",sound_target);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                },0,60*983); //半分钟执行一次run方法
            }
        });
        thread.start();
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service is Destroyed.");
    }
}
