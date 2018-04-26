package com.example.hp.testwannianli.recorder;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.dayManager.AddDayManagerActivity;
import com.example.hp.testwannianli.dayManager.ManageAllDaysItemActivity;
import com.example.hp.testwannianli.util.ManageActvity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RecordingActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    //语音文件保存路径
    private String FileName = null;
    private boolean isSDCardExit;

    private Button cancel, over;
    private ImageView imageView;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;

    private int[] records = new int[]{
            R.drawable.record00, R.drawable.record01, R.drawable.record02,
            R.drawable.record03, R.drawable.record04, R.drawable.record05,
            R.drawable.record06
    };
    private int current = 0;
    private File record_file;
    private String from; //表示来自哪个activity
    Bundle from_bundle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_item);
        ManageActvity.getInstance().addActivity(RecordingActivity.this); //添加Activity
        Intent intent = getIntent();
        from_bundle= intent.getBundleExtra("from_bundle");
        from =  from_bundle.getString("from","AddDayManagerActivity"); //求出来自哪个activity

        initView();

        playAnimation();
        setStartRecord();

        //监听"说完了"按钮
        overOnClick();
        //监听"取消"按钮
        cancelOnClick();
    }

    public void initView() {
        cancel = (Button) findViewById(R.id.cancel);
        over = (Button) findViewById(R.id.over);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void playAnimation() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    imageView.setImageResource(records[current++]);
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
        }, 0, 400);
    }

    public void setStartRecord() {
        //判断是否有SD卡
        isSDCardExit = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        //设置sdcard的路径
        if (isSDCardExit) {
            FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            File path = new File(FileName);
            if (!path.exists())
                path.mkdir();
            //Date date = new Date();
            //FileName = "/" + sdf.format(date) + ".3gp";
            //System.out.println(FileName);
            //System.out.println("有sd卡");
        }else{
            String packageName = RecordingActivity.this.getPackageName();
        FileName = "/data/data/" + packageName + "/luyinfiles/";
        File path = new File(FileName);
        if (!path.exists())
            path.mkdir();
    }

        Date date = new Date();
        record_file = new File(FileName, sdf.format(date) + ".mp3");
        //System.out.println(record_file.getAbsolutePath());

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int i, int i1) {
                    System.out.println("Error here");
                }
            });
        } else {
            mRecorder.reset();
        }

        try {
        //开始
        //mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        //mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        //mRecorder.setOutputFile(FileName);
        //System.out.println(record_file.getAbsoluteFile().getName());

        mRecorder.setOutputFile(record_file.getAbsolutePath());

            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void setStopRecord() {
        if (mRecorder != null) {
            try {
                mRecorder.setOnErrorListener(null);
                mRecorder.setOnInfoListener(null);
                mRecorder.stop();
            } catch (IllegalStateException e) {
                // TODO: handle exception
                Log.i("Exception", Log.getStackTraceString(e));
            } catch (RuntimeException e) {
                // TODO: handle exception
                Log.i("Exception", Log.getStackTraceString(e));
            } catch (Exception e) {
                // TODO: handle exception
                Log.i("Exception", Log.getStackTraceString(e));
            }finally {
                mRecorder.release();
                mRecorder = null;
            }
        }
        //mRecorder.release();
        //mRecorder = null;
    }

    public void deleteFile() {
        //File file = new File(FileName);
        if (record_file.exists()) {
            record_file.delete();
            Toast.makeText(getApplicationContext(), "删除完成", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "对不起，所要删除的录音文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    public void overOnClick() {
        //说完了
        over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStopRecord();
                Intent intent = new Intent();
                intent.putExtra("cancel_or_over",'O');
                intent.putExtra("record_file_path", record_file.getAbsolutePath());

                if (from.equals("AddDayManagerActivity")) {
                    intent.setClass(RecordingActivity.this, AddDayManagerActivity.class);
                }
                else if (from.equals("ManageAllDaysItemActivity")){
                    intent.putExtra("item_info",from_bundle);
                    intent.setClass(RecordingActivity.this, ManageAllDaysItemActivity.class);
                }else {
                    intent.setClass(RecordingActivity.this, AddDayManagerActivity.class);
                }
                startActivity(intent);
                //RecordingActivity.this.finish();
            }
        });
    }

    public void cancelOnClick() {
        //取消
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStopRecord();
                deleteFile();

                Intent intent = new Intent();
                intent.putExtra("cancel_or_over",'C');
                if (from.equals("AddDayManagerActivity")) {
                    intent.setClass(RecordingActivity.this, AddDayManagerActivity.class);
                }
                else if (from.equals("ManageAllDaysItemActivity")){
                    intent.putExtra("item_info",from_bundle);
                    intent.setClass(RecordingActivity.this, ManageAllDaysItemActivity.class);
                }
                startActivity(intent);
                //RecordingActivity.this.finish();
            }
        });
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
                && event.getRepeatCount() == 0) {
            Toast.makeText(getApplicationContext(), "BYE-Recording", Toast.LENGTH_SHORT).show();
            setStopRecord();
            deleteFile();
            //ManageActvity.getInstance().closeActivity();//关掉activity
            //RecordingActivity.this.finish();
            return true;
        }
        return false;
        //return super.onKeyDown(keyCode, event);
    }
    */
}
