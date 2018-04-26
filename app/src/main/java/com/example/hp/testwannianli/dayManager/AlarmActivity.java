package com.example.hp.testwannianli.dayManager;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hp.testwannianli.R;

public class AlarmActivity extends Activity {
    MediaPlayer alarmMusic;
    TextView textView;
    Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_dialog);

        alarmMusic = MediaPlayer.create(this,R.raw.alarm);
        alarmMusic.setLooping(true);
        alarmMusic.start();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String output = bundle.getString("output");

        textView = (TextView) findViewById(R.id.content);
        textView.setText(output);
        textView.setMovementMethod(new ScrollingMovementMethod());
        exit  = (Button) findViewById(R.id.cancel);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmMusic.stop();
                alarmMusic.release();
                alarmMusic = null;
                AlarmActivity.this.finish();
            }
        });
    }
}

