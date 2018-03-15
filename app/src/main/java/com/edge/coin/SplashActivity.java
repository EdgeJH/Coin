package com.edge.coin;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edge.coin.MainPackage.MainActivity;
import com.edge.coin.ServicePackage.DataService;

public class SplashActivity extends AppCompatActivity implements Runnable {

    Typeface typeface;
    TextView logoText;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        typeface = Typeface.createFromAsset(getAssets(),"Comfortaa-Bold.ttf");
        logoText=findViewById(R.id.logo_text);
        logoText.setTypeface(typeface);
        logo = findViewById(R.id.logo);
        Glide.with(this).load(R.drawable.logo)
                .into(logo);
        if (isMyServiceRunning(DataService.class)){
            Log.d("LOG123123","service is already running");
        } else {
            startService(new Intent(this,DataService.class));
        }
        Handler handler =new Handler();
        handler.postDelayed(this,2000);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        Intent intent =new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
