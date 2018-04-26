package com.example.hp.testwannianli.soundRecord;

/**
 *
 * Created by HP on 2016-9-18.
 */

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;

public class MusicPlayer {
    private static MediaPlayer mMediaPlayer;
    private Context mContext;

    public MusicPlayer(Context context){
        mContext = context;
    }

    public void playMicFile(File file){
        if (file!=null && file.exists()) {
            Uri uri = Uri.fromFile(file);
            mMediaPlayer = MediaPlayer.create(mContext, uri);
            mMediaPlayer.start();
//            mMediaPlayer.setLooping(true);
        }
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    public void stopPlayer(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    public MediaPlayer getPlayer(){
        return mMediaPlayer;
    }

}
