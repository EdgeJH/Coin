package com.edge.coin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edge.coin.MainPackage.MainActivity;
import com.edge.coin.UpbitPackage.CoinListCallback;
import com.edge.coin.UpbitPackage.TradeCoin;
import com.edge.coin.UpbitPackage.UpbitData;
import com.edge.coin.Utils.SharedPreference;
import com.edge.coin.Utils.VersionCallback;
import com.edge.coin.Utils.VersionChecker;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity implements  CoinListCallback, VersionCallback {

    Typeface typeface;
    TextView logoText;
    ImageView logo;
    SharedPreference sharedPreference = new SharedPreference();
    UpbitData upbitData;
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
        versionCheck();

    }
    public void  versionCheck(){
        VersionChecker versionChecker =new VersionChecker(getPackageManager(),this);
        versionChecker.execute(getPackageName());
    }


    @Override
    public void getCoinList(ArrayList<TradeCoin> coins) {
        sharedPreference.storeCoinList(this,"coinList",coins);

        Intent intent =new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void isUpdate(boolean update) {
        if (!update){
            upbitData = new UpbitData();
            upbitData.setCoinListCallback(this);
            upbitData.getCoinList();
        } else {
            showDialog();
        }
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);     // 여기서 this는 Activity의 this

// 여기서 부터는 알림창의 속성 설정
        builder .setMessage("새로운 업데이트가 있습니다.\n업데이트 하시겠습니까?")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        upbitData = new UpbitData();
                        upbitData.setCoinListCallback(SplashActivity.this);
                        upbitData.getCoinList();
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = builder.create();    // 알림창 객체 생성
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#555555"));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#64a2f6"));
            }
        });
        alertDialog.show();
    }
}
