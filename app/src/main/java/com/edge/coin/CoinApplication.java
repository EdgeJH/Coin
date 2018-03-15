package com.edge.coin;

import android.app.Application;
import android.os.Build;

import com.edge.coin.Utils.NotificationManager;

/**
 * Created by user1 on 2018-03-12.
 */

public class CoinApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager.createChannel(this);
        }

    }
}
