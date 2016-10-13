
package com.brian.testandroid;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.brian.testandroid.common.Env;

public class App extends Application {

    public static final String PROCESS_NAME_MAIN = "com.brian.testandroid";

    @Override
    protected void attachBaseContext(Context base) {
        Env.setContext(this);
        // app start here
        Env.setAppStartTime();
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setStrictModeEnable(false);
    }

    private void setStrictModeEnable(boolean enable) {
        if (!enable) {
            return;
        }
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }
}
