package com.example.hp.testwannianli.recorder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.dao.DataBaseHelper;
import com.example.hp.testwannianli.dayManager.AddDayManagerActivity;
import com.example.hp.testwannianli.dayManager.ManageAllDaysItemActivity;
import com.example.hp.testwannianli.util.ManageActvity;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayRecordActivity extends AppCompatActivity {

    private static final String LOG_TAG = "PlayRecordTest";

    private int[] records = new int[]{
            R.drawable.record00, R.drawable.record01, R.drawable.record02,
            R.drawable.record03, R.drawable.record04, R.drawable.record05,
            R.drawable.record06
    };

    private int current = 0;
    private File play_file;

    private Button play_again, play_over;
    private ImageView play_imag;
    private TextView play_tip_tv;

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    //语音文件保存路径
    private String FileName = null;
    private boolean flag_play = false;

    DataBaseHelper myDataBaseHelper;
    private final String DATABASE_NAME = "mySchedule";
    private final int VERSION = 1;

    private int id = 0; //item的id
    private Bundle bundle;
    private String from; //表示来自哪个activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_record);
        ManageActvity.getInstance().addActivity(PlayRecordActivity.this);//添加Activity
        myDataBaseHelper = new DataBaseHelper(PlayRecordActivity.this, DATABASE_NAME, VERSION); //初始化
        initView();
        //
        Intent intent = getIntent();
        bundle = intent.getBundleExtra("item_info");
        from =  bundle.getString("from","AddDayManagerActivity"); //求出来自哪个activity
        id = bundle.getInt("item_id", 0);

        if (id == 0) {
            FileName = bundle.getString("item_path");
            System.out.println("PlayRecord" + FileName);
        } else {
            FileName = getFileName(id);
            System.out.println("PlayRecord" + id);
        }
        playRecord(FileName);
        stopPlayRecord();
        playAgain();
    }

    public void initView() {
        play_again = (Button) findViewById(R.id.play_again);
        play_over = (Button) findViewById(R.id.play_over);
        play_imag = (ImageView) findViewById(R.id.play_img);
        play_tip_tv = (TextView) findViewById(R.id.play_tip_tv);
    }

    public void playAnimation(boolean flagPlay) {
        flag_play = flagPlay;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123 && flag_play) {
                    play_imag.setImageResource(records[current++]);
                    if (current > 6) {
                        current = 0;
                    }
                }
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0x123;
                handler.sendMessage(message);
            }
        }, 0, 500);
    }

    public String getFileName(int id) {
        //数据库操作部分，传递路径进来
        String name = "";
        SQLiteDatabase db = myDataBaseHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select schedule from schedule_management where _id = ?;", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex("schedule"));
        }
        return name;
    }

    public void playRecord(String fileName) {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        else {
            mPlayer.reset();
        }

        playAnimation(true);
        play_again.setEnabled(false);
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    play_again.setEnabled(true);
                    flag_play = false;
                    play_tip_tv.setText("播放结束");
                    //完成后要释放资源
                    //mPlayer.release();
                    //mPlayer = null;
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "播放失败");
        }
    }

    public void stopPlayRecord() {
        play_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id !=0){
                   PlayRecordActivity.this.finish();
                }else {
                    if (mPlayer.isPlaying()) {
                        mPlayer.stop();
                    }
                    mPlayer.release();
                    mPlayer = null;
                    flag_play = false; //停止播放动画

                    Intent intent = new Intent();
                    if (from.equals("AddDayManagerActivity")) {
                        intent.setClass(PlayRecordActivity.this, AddDayManagerActivity.class);
                    }
                    else if (from.equals("ManageAllDaysItemActivity")){
                        intent.putExtra("item_info",bundle);
                        intent.setClass(PlayRecordActivity.this, ManageAllDaysItemActivity.class);
                    }else {
                        intent.setClass(PlayRecordActivity.this, AddDayManagerActivity.class);
                    }
                    startActivity(intent);
                }
            }
        });
    }

    public void playAgain() {
        play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag_play = true;
                current = 0;
                playRecord(FileName);
                play_tip_tv.setText("正在播放...");
            }
        });
    }

}
