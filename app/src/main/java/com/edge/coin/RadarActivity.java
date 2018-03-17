package com.edge.coin;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.edge.coin.ServicePackage.DataService;
import com.edge.coin.Utils.SharedPreference;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RadarActivity extends AppCompatActivity implements ServiceConnection, View.OnClickListener {
    SharedPreference sharedPreference = new SharedPreference();
    boolean isDarkTheme;
    boolean isBind;
    DataService dataService;
    RelativeLayout back,startRadar;
    NiceSpinner coinSpinner,timeSpinner;
    List<String> coinName,coinCode,candleTime = new ArrayList<>();
    String code;
    String defaultCode = "CRIX.UPBIT.";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDarkTheme = sharedPreference.getValue(this,"theme",false);
        if (isDarkTheme){
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.WhiteTheme);
        }
        setContentView(R.layout.activity_radar);
       initView();
        bindService(new Intent(this, DataService.class), this, BIND_AUTO_CREATE);
    }

    private void startRadar(String coinName,String code,int time){
        Intent intent =new Intent(this,DataService.class);
        intent.putExtra("code",code);
        intent.putExtra("time",time);
        intent.putExtra("coinName",coinName);
        if (isMyServiceRunning(DataService.class)){
            stopService(new Intent(this,DataService.class));
            startService(intent);
        } else {
            startService(intent);
        }
    }

    private void initView(){
        back =findViewById(R.id.back);
        startRadar = findViewById(R.id.radar);
        coinSpinner =findViewById(R.id.coin_spinner);
        timeSpinner =findViewById(R.id.time_spinner);
        setSpinner();
        startRadar.setOnClickListener(this);
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
    @Override //서비스가 실행될 때 호출
    public void onServiceConnected(ComponentName name, IBinder service) {
        DataService.DataBinder myBinder = (DataService.DataBinder) service;
        dataService = myBinder.getService();

        isBind = true;
        Log.d("LOG123123", "onServiceConnected() " );
    }

    @Override //서비스가 종료될 때 호출
    public void onServiceDisconnected(ComponentName name) {
        dataService = null;
        isBind = false;
        Log.d("LOG123123", "onServiceDisconnected()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }
    private void setSpinner() {
        coinName = Arrays.asList(getResources().getStringArray(R.array.coin_name));
        coinCode = Arrays.asList(getResources().getStringArray(R.array.coin_code));
        candleTime =Arrays.asList(getResources().getStringArray(R.array.candle_time));
        timeSpinner.attachDataSource(candleTime);
        coinSpinner.attachDataSource(coinName);
        coinSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                code = defaultCode + coinCode.get(position);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.radar:
                String name = coinName.get(coinSpinner.getSelectedIndex());
                int time= Integer.parseInt(candleTime.get(timeSpinner.getSelectedIndex()).replaceAll("분봉",""));
                startRadar(name,code,time);
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
