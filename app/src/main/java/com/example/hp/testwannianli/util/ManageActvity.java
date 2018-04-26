package com.example.hp.testwannianli.util;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 2016-9-22.
 */
public class ManageActvity {
    private static ManageActvity instace = null;
    private List<Activity> myActivity = new ArrayList<Activity>();

    private ManageActvity() {}
    public static ManageActvity getInstance() {
        if (instace == null) {
            instace = new ManageActvity();
        }
        return instace;
    }
    //添加Activity
    public void addActivity(Activity activity) {
        myActivity.add(activity);
    }
    //关掉Activity
    public void closeActivity() {
        for (Activity activity : myActivity) {
            activity.finish();
        }
    }
    //清空myActivity，不关掉
    public void clearActivity(){
        myActivity.clear();
    }
}
