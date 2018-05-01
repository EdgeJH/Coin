package com.edge.coin;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;

import com.edge.coin.ServicePackage.DataService;
import com.edge.coin.UpbitPackage.TradeCoin;
import com.edge.coin.Utils.SharedPreference;
import com.edge.edge_centerseekbar.CenterSeekBar;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RadarActivity extends AppCompatActivity implements ServiceConnection, View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
    SharedPreference sharedPreference = new SharedPreference();
    boolean isDarkTheme;
    boolean isBind;
    boolean isGold, isDead, isVol, isRsi,isEnvel,isAlarm, isGoldSet, isDeadSet, isVolSet, isRsiSet,isEnvelSet;
    DataService dataService;
    RelativeLayout back, startRadar;
    NiceSpinner coinSpinner, timeSpinner, volCandleSpinner;
    List<String> coinName = new ArrayList<>();
    List<String>candleTime = new ArrayList<>();
    List<String>volCandleList = new ArrayList<>();
    String code, myRadarCoin;
    int myRadarTime, volPer, volCandle, rsiBt, rsiT;
    Switch goldCross, deadCross, volCheck, rsiCheck,envelCheck,alarmSwitch;
    LinearLayout volDetail, rsiDetail;
    CenterSeekBar volSeek, rsiBtSeek, rsiTopSeek;
    EditText volPercent, rsiBottom, rsiTop;
    ArrayList<TradeCoin> coinList;
    ArrayList<TradeCoin> krwCoinList = new ArrayList<>();
    ArrayList<TradeCoin> btcCoinList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCoinList();
        setCoinName();
        getData();
        initView();
        //bindService(new Intent(this, DataService.class), this, BIND_AUTO_CREATE);
    }

    private void startRadar(String coinName, String code, int time) {
        Intent intent = new Intent(this, DataService.class);
        intent.putExtra("code", code);
        intent.putExtra("time", time);
        intent.putExtra("coinName", coinName);
        intent.putExtra("isVol", isVol);
        intent.putExtra("isGold", isGold);
        intent.putExtra("isDead", isDead);
        intent.putExtra("isRsi", isRsi);
        intent.putExtra("isEnvel",isEnvel);
        saveCheckState("rsiCheck", isRsi);
        saveCheckState("goldCross", isGold);
        saveCheckState("deadCross", isDead);
        saveCheckState("volCheck", isVol);
        saveCheckState("envelCheck",isEnvel);
        saveCheckState("isAlarm",isAlarm);
        sharedPreference.put(this, "time", time);
        sharedPreference.put(this, "coinName", coinName);

        if (isRsi) {
            sharedPreference.put(this, "rsiTop", rsiT);
            sharedPreference.put(this, "rsiBt", rsiBt);
        }
        if (isVol) {
            sharedPreference.put(this, "volPer", volPer);
            sharedPreference.put(this, "volCandle", volCandle);
        }
        if (isAlarm){
            if (isMyServiceRunning(DataService.class)) {
                stopService(new Intent(this, DataService.class));
                startService(intent);
            } else {
                startService(intent);
            }
        } else {
            stopService(new Intent(this, DataService.class));
        }
    }

    private void initView() {
        if (isDarkTheme) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.WhiteTheme);
        }
        setContentView(R.layout.activity_radar);
        back = findViewById(R.id.back);
        startRadar = findViewById(R.id.radar);
        coinSpinner = findViewById(R.id.coin_spinner);
        timeSpinner = findViewById(R.id.time_spinner);
        goldCross = findViewById(R.id.gold_cross);
        deadCross = findViewById(R.id.dead_cross);
        volCheck = findViewById(R.id.vol_check);
        volDetail = findViewById(R.id.vol_detail);
        volCandleSpinner = findViewById(R.id.vol_candle);
        volPercent = findViewById(R.id.percent);
        volSeek = findViewById(R.id.vol_seek);
        volSeek.setOnSeekBarChangeListener(this);
        volPercent.setText(volPer + "%");
        volSeek.setProgress(volPer + 100);
        setVolDetail();

        envelCheck = findViewById(R.id.envelope);
        envelCheck.setOnCheckedChangeListener(this);

        alarmSwitch = findViewById(R.id.alarm_switch);

        alarmSwitch.setOnCheckedChangeListener(this);
        rsiDetail = findViewById(R.id.rsi_detail);
        rsiBottom = findViewById(R.id.rsi_bottom);
        rsiTop = findViewById(R.id.rsi_top);
        rsiCheck = findViewById(R.id.rsi_check);
        rsiBtSeek = findViewById(R.id.rsi_bt_seek);
        rsiTopSeek = findViewById(R.id.rsi_t_seek);

        rsiBtSeek.setOnSeekBarChangeListener(this);
        rsiTopSeek.setOnSeekBarChangeListener(this);

        rsiBottom.setText(String.valueOf(rsiBt));
        rsiTop.setText(String.valueOf(rsiT));
        rsiBtSeek.setProgress(rsiBt);
        rsiTopSeek.setProgress(rsiT - 50);
        setRsiDetail();
        alarmSwitch.setChecked(isAlarm);
        if (isAlarm){
            envelCheck.setChecked(isEnvel);
            rsiCheck.setChecked(isRsi);
            volCheck.setChecked(isVol);
            goldCross.setChecked(isGold);
            deadCross.setChecked(isDead);
        }
        setSpinner();
        startRadar.setOnClickListener(this);
        back.setOnClickListener(this);
        rsiCheck.setOnCheckedChangeListener(this);
        deadCross.setOnCheckedChangeListener(this);
        volCheck.setOnCheckedChangeListener(this);
        goldCross.setOnCheckedChangeListener(this);


    }


    private void setVolDetail() {

        if (isAlarm){
            if (isVol) {
                volDetail.setVisibility(View.VISIBLE);
            } else {
                volDetail.setVisibility(View.GONE);
            }
        }
    }

    private void setRsiDetail() {
        if (isAlarm){
            if (isRsi) {
                rsiDetail.setVisibility(View.VISIBLE);
            } else {
                rsiDetail.setVisibility(View.GONE);
            }
        }
    }

    private void getData() {
        isDarkTheme = sharedPreference.getValue(this, "theme", false);
        isGold = sharedPreference.getValue(this, "goldCross", false);
        isDead = sharedPreference.getValue(this, "deadCross", false);
        isVol = sharedPreference.getValue(this, "volCheck", false);
        isRsi = sharedPreference.getValue(this, "rsiCheck", false);
        myRadarCoin = sharedPreference.getValue(this, "coinName", "");
        myRadarTime = sharedPreference.getValue(this, "time", 0);
        volPer = sharedPreference.getValue(this, "volPer", 0);
        volCandle = sharedPreference.getValue(this, "volCandle", 0);
        rsiT = sharedPreference.getValue(this, "rsiTop", 70);
        rsiBt = sharedPreference.getValue(this, "rsiBt", 30);
        isEnvel= sharedPreference.getValue(this,"isEnvel",false);
        isAlarm  = sharedPreference.getValue(this,"isAlarm",false);

        isGoldSet =isGold;
        isDeadSet = isDead;
        isVolSet= isVol;
        isRsiSet = isRsi;
        isEnvelSet=isEnvel;
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
    }

    @Override //서비스가 종료될 때 호출
    public void onServiceDisconnected(ComponentName name) {
        dataService = null;
        isBind = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
  //      unbindService(this);
    }

    private void setSpinner() {

        volCandleList.clear();
        for (int i = 0; i < 100; i++) {
            volCandleList.add(String.valueOf(i + 1));
        }

        candleTime = Arrays.asList(getResources().getStringArray(R.array.candle_time));
        timeSpinner.attachDataSource(candleTime);
        coinSpinner.attachDataSource(coinName);
        volCandleSpinner.attachDataSource(volCandleList);
        if (!myRadarCoin.equals("") && myRadarTime != 0) {
            int coinIndex = coinName.indexOf(myRadarCoin);
            int timeIndex = candleTime.indexOf(String.valueOf(myRadarTime) + "분봉");
            timeSpinner.setSelectedIndex(timeIndex);
            coinSpinner.setSelectedIndex(coinIndex);
        }
        if (isVol && volCandle != 0) {
            volCandleSpinner.setSelectedIndex(volCandleList.indexOf(String.valueOf(volCandle)));
        }


    }

    private void setCoinList() {
        coinList = sharedPreference.getCoinList(this, "coinList");
        for (TradeCoin tradeCoin : coinList) {
            switch (tradeCoin.getQuoteCurrencyCode()) {
                case "KRW":
                    if (tradeCoin.getMarketState().equals("ACTIVE") && tradeCoin.getExchange().equals("UPBIT")) {
                        krwCoinList.add(tradeCoin);
                    }
                    break;
                case "BTC":
                    if (tradeCoin.getMarketState().equals("ACTIVE") && tradeCoin.getExchange().equals("UPBIT")) {
                        btcCoinList.add(tradeCoin);
                    }
                    break;
                case "ETH":
                    break;
                case "USDT":
                    break;
            }
        }
    }
    private void setCoinName() {
        for (int i = 0; i < krwCoinList.size(); i++) {
            coinName.add(krwCoinList.get(i).getKoreanName());
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radar:
                String name = coinName.get(coinSpinner.getSelectedIndex());
                code = krwCoinList.get(coinSpinner.getSelectedIndex()).getCode();
                int time = Integer.parseInt(candleTime.get(timeSpinner.getSelectedIndex()).replaceAll("분봉", ""));
                volPer = Integer.parseInt(volPercent.getText().toString().replace("%", ""));
                volCandle = Integer.parseInt(volCandleList.get(volCandleSpinner.getSelectedIndex()));
                rsiBt = Integer.parseInt(rsiBottom.getText().toString());
                rsiT = Integer.parseInt(rsiTop.getText().toString());
                showDialog(name, time);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.gold_cross:
                isGold = b;
                break;
            case R.id.dead_cross:
                isDead = b;
                break;
            case R.id.vol_check:
                isVol = b;
                setVolDetail();
                break;
            case R.id.rsi_check:
                isRsi = b;
                setRsiDetail();
                break;
            case R.id.envelope:
                isEnvel = b;
                break;
            case R.id.alarm_switch:
                isAlarm =b;
                if(!isAlarm){
                    rsiCheck.setChecked(b);
                    envelCheck.setChecked(b);
                    volCheck.setChecked(b);
                    goldCross.setChecked(b);
                    deadCross.setChecked(b);
                    rsiDetail.setVisibility(View.GONE);
                    volDetail.setVisibility(View.GONE);
                } else {
                    rsiCheck.setChecked(isRsiSet);
                    envelCheck.setChecked(isEnvelSet);
                    volCheck.setChecked(isVolSet);
                    goldCross.setChecked(isGoldSet);
                    deadCross.setChecked(isDeadSet);

                }
                break;

        }
    }

    private void saveCheckState(String key, boolean state) {
        sharedPreference.put(this, key, state);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int progress = seekBar.getProgress();
        switch (seekBar.getId()) {
            case R.id.vol_seek:
                volPercent.setText(String.valueOf(progress - 100) + "%");
                break;
            case R.id.rsi_bt_seek:
                rsiBottom.setText(String.valueOf(progress));
                break;
            case R.id.rsi_t_seek:
                rsiTop.setText(String.valueOf(progress + 50));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void showDialog(final String name, final int time) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RadarActivity.this);

        if (isAlarm){
            alertDialogBuilder.setTitle(name+" 모니터링 시작");
            alertDialogBuilder  .setMessage("\n"+"상단 알림을 통해  " + name + "  모니터링을 시작합니다. 바로 시작하시겠습니까?");
            alertDialogBuilder .setPositiveButton("시작하기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startRadar(name, code, time);
                    finish();
                    dialogInterface.dismiss();
                }
            });
        } else {
            alertDialogBuilder.setTitle(" 모니터링 중단");
            alertDialogBuilder  .setMessage("\n"+"모니터링을 중지 하시겠습니까?");
            alertDialogBuilder .setPositiveButton("중단하기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startRadar(name, code, time);
                    finish();
                    dialogInterface.dismiss();
                }
            });
            // AlertDialog 셋팅
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher)
                    .setCancelable(false)
                    .setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    // 다이얼로그를 취소한다
                                    dialog.dismiss();

                                }
                            });
        }
        // 다이얼로그 생성
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ff454f"));
            }
        });
        // 다이얼로그 보여주기
        alertDialog.show();
    }
}
