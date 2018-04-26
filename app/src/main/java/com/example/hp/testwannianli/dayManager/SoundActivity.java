package com.example.hp.testwannianli.dayManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.testwannianli.R;
import com.example.hp.testwannianli.soundRecord.SoundRecorder;

public class SoundActivity extends Activity {
    private static final String TAG = "SoundActivity";
    SoundRecorder soundRecorderActivity = new SoundRecorder();
    private String sound_path;
    private int flag = 0;
    Button play,exit;
    TextView title,content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_dialog);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        sound_path = bundle.getString("sound");
        String target = bundle.getString("target");

        play = (Button) findViewById(R.id.ok);
        exit = (Button) findViewById(R.id.cancel);
        title = (TextView) findViewById(R.id.titles);
        content = (TextView) findViewById(R.id.content);
        title.setText(target);
        content.setText(R.string.soundHintContent);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (++flag == 1 && !soundRecorderActivity.isPlaying()){
//                    soundRecorderActivity.playRecorder(getApplicationContext(),getApplicationContext().getFilesDir()+"/alarm.mp3");
                    soundRecorderActivity.playRecorder(SoundActivity.this,getApplicationContext(),sound_path);
                    content.setText(R.string.soundContent);
                }
                else{
                    if (flag < 5)
                        Toast.makeText(getApplicationContext(),"正在为您播放",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(),"有意思吗？",Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "---------MusicStart---------"+flag);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (soundRecorderActivity.isPlaying())
                    soundRecorderActivity.stopPlayer();
                SoundActivity.this.finish();
            }
        });
    }

    public void changeText(){
        Log.i(TAG,"changeText");
        content = (TextView) findViewById(R.id.content);
        content.setText(R.string.soundHintContent);
        flag = 0;
    }
}
