package com.example.hp.testwannianli.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.hp.testwannianli.service.DayManageService;

public class LaunchReceiver extends  BroadcastReceiver{
    public LaunchReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
            Intent tIntent = new Intent(context, DayManageService.class);
            context.startService(tIntent);
    }
}
