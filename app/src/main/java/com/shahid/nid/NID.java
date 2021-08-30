package com.shahid.nid;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.shahid.nid.Utils.DbHelper;

import java.util.ArrayList;

/**
 * Created by shahid on 10/8/2017.
 */

public class NID extends Application {

    private int runningActivities = 0;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void incrementActivities(){
        runningActivities++;
    }

    public void decrementActivities(){
        runningActivities--;
        if (runningActivities <= 0){
            DbHelper.getInstance(this).destroy();
        }
    }

}