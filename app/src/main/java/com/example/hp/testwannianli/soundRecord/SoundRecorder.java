package com.example.hp.testwannianli.soundRecord;

/**
 *
 * Created by HP on 2016-9-18.
 */

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.hp.testwannianli.dayManager.SoundActivity;

import java.io.File;

public class SoundRecorder {
    private MusicPlayer mPlayer;
    private static final String TAG = "SoundRecorder";

    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    public void playRecorder(final SoundActivity soundDialogActivity, Context context, String path){
        mPlayer = new MusicPlayer(context);
        mPlayer.playMicFile(new File(path));
        mPlayer.getPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                Log.i(TAG,"setOnCompletionListener");
                soundDialogActivity.changeText();
            }
        });
    }

    public void stopPlayer(){
        mPlayer.stopPlayer();
    }
}