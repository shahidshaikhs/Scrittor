package com.shahid.nid;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/** Authentication is process-local; an external Intent cannot unlock the app. */
public class NID extends Application implements Application.ActivityLifecycleCallbacks {
    private boolean authenticated;
    private int startedActivities;
    @Override public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }
    public boolean isLockEnabled() {
        return "enable".equals(getSharedPreferences(getString(R.string.MY_PREFS_PASSWORD), MODE_PRIVATE).getString("password_check", null))
                || "enable".equals(getSharedPreferences(getString(R.string.MY_PREFS_FINGERPRINT), MODE_PRIVATE).getString("fingerprint_check", null));
    }
    public boolean isAuthenticated() { return authenticated; }
    public void unlock() { authenticated = true; }
    @Override public void onActivityStarted(Activity activity) { startedActivities++; }
    @Override public void onActivityStopped(Activity activity) {
        startedActivities--;
        if (startedActivities == 0 && !activity.isChangingConfigurations()) authenticated = false;
    }
    @Override public void onActivityCreated(Activity activity, Bundle state) {}
    @Override public void onActivityResumed(Activity activity) {}
    @Override public void onActivityPaused(Activity activity) {}
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle state) {}
    @Override public void onActivityDestroyed(Activity activity) {}
}
