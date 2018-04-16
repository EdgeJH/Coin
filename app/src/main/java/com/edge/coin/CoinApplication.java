package com.edge.coin;

import android.app.Application;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.edge.coin.Utils.NotificationManager;
import com.edge.coin.Utils.SharedPreference;
import io.fabric.sdk.android.Fabric;

/**
 * Created by user1 on 2018-03-12.
 */

public class CoinApplication extends Application {
    SharedPreference sharedPreference= new SharedPreference();
    boolean isDarkTheme;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        isDarkTheme = sharedPreference.getValue(this,"theme",false);
        if (isDarkTheme){
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.WhiteTheme);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager.createChannel(this);
        }

    }


}
