package com.edge.coin.MainPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.edge.coin.MainPackage.AnalyticsPackage.AnalyFragment;
import com.edge.coin.MainPackage.ChartPacakge.ChartFragment;
import com.edge.coin.R;
import com.edge.coin.RadarActivity;
import com.edge.coin.Utils.FragAdapter;
import com.edge.coin.Utils.SharedPreference;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    boolean isDarkTheme = false;
    ImageView themeIcon;
    RelativeLayout changeTheme;
    SharedPreference sharedPreference = new SharedPreference();
    RelativeLayout radar;
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    MenuItem menuItem;
    OnBackListener onBackListener;
    OnScrollTopListener onScrollTopListener;
    LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDarkTheme = sharedPreference.getValue(this, "theme", false);
        setTheme();
        initView();
        setViewPager();
        changeThemeIcon();
    }
    private void initView(){
        setContentView(R.layout.activity_main);
        changeTheme = findViewById(R.id.change_theme);
        radar = findViewById(R.id.radar);
        viewPager = findViewById(R.id.viewpager);
        themeIcon = findViewById(R.id.theme_icon);
        lottieAnimationView = findViewById(R.id.lottie);
        lottieAnimationView.setSpeed(1.5f);
        bottomNavigationView = findViewById(R.id.bt_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        changeTheme.setOnClickListener(this);
        radar.setOnClickListener(this);
    }

    private void setViewPager(){
        FragAdapter fragAdapter = new FragAdapter(getSupportFragmentManager());
        fragAdapter.addFragment(new ChartFragment());
        fragAdapter.addFragment(new AnalyFragment());
        viewPager.setAdapter(fragAdapter);
        viewPager.addOnPageChangeListener(this);
    }
    private void changeThemeIcon() {
        if (isDarkTheme) {
            themeIcon.setImageResource(R.drawable.sun);
        } else {
            themeIcon.setImageResource(R.drawable.moon);
        }
    }
    private void setTheme() {
        if (!isDarkTheme) {
            setTheme(R.style.WhiteTheme);
        } else {
            setTheme(R.style.DarkTheme);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_theme:
                if (isDarkTheme) {
                    isDarkTheme = false;
                } else {
                    isDarkTheme = true;
                }
                setTheme();
                sharedPreference.put(this, "theme", isDarkTheme);
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.radar:
                startActivity(new Intent(this, RadarActivity.class));
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (menuItem != null) {
            menuItem.setChecked(false);
        }
        menuItem = bottomNavigationView.getMenu().getItem(position);
        menuItem.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_chart:
                viewPager.setCurrentItem(0);
                break;
            case R.id.nav_analysis:
                viewPager.setCurrentItem(1);
                if (onScrollTopListener!=null){
                    onScrollTopListener.scrollTop();
                }
                break;
        }
        return false;
    }

    public void setOnBackListener(OnBackListener onBackListener){
        this.onBackListener = onBackListener;
    }

    public void setOnScrollTopListener(OnScrollTopListener onScrollTopListener) {
        this.onScrollTopListener = onScrollTopListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackListener!=null){
            onBackListener.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
