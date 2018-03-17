package com.edge.coin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edge.coin.MainPackage.MainActivity;
import com.edge.coin.Utils.SharedPreference;

public class SplashActivity extends AppCompatActivity implements Runnable {

    Typeface typeface;
    TextView logoText;
    ImageView logo;
    SharedPreference sharedPreference = new SharedPreference();
    boolean isDarkTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDarkTheme = sharedPreference.getValue(this, "theme", false);
        if (isDarkTheme) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.WhiteTheme);
        }
        setContentView(R.layout.activity_splash);
        typeface = Typeface.createFromAsset(getAssets(), "Comfortaa-Bold.ttf");
        logoText = findViewById(R.id.logo_text);
        logoText.setTypeface(typeface);
        logo = findViewById(R.id.logo);
        if (isDarkTheme) {
            Glide.with(this).load(R.drawable.logo)
                    .into(logo);
        } else {
            Glide.with(this).load(R.drawable.logo2)
                    .into(logo);
        }
        Handler handler =new Handler();
        handler.postDelayed(this,2000);
    }

    @Override
    public void run() {
        Intent intent =new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
