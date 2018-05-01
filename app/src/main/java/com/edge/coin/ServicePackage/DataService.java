package com.edge.coin.ServicePackage;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.edge.coin.UpbitPackage.UpbitCallback;
import com.edge.coin.UpbitPackage.UpbitData;
import com.edge.coin.Utils.Candle;
import com.edge.coin.Utils.LoadingProgress;
import com.edge.coin.Utils.NotificationManager;
import com.edge.coin.Utils.SharedPreference;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 2018-03-14.
 */

public class DataService extends Service implements UpbitCallback{
    ArrayList<CandleEntry> candleEntries = new ArrayList<>();
    ArrayList<Entry> line5Entries = new ArrayList<>();
    ArrayList<Entry> line10Entries = new ArrayList<>();
    ArrayList<Entry> line20Entries = new ArrayList<>();
    ArrayList<Entry> line60Entries = new ArrayList<>();
    ArrayList<Entry> envelopePlusEntries = new ArrayList<>();
    ArrayList<Entry> envelopeMinusEntries = new ArrayList<>();
    ArrayList<Entry> rsiEndtries = new ArrayList<>();
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    SharedPreference sharedPreference = new SharedPreference();

    double upAvg,downAvg;
    String code;
    String coinName;
    UpbitData upbitData;
    boolean isVol,isGold,isDead,isRsi,isEnvel;
    int volPer,volCandle,rsiBt,rsiT;
    private IBinder dataBinder = new DataBinder();
    int time;
    public class DataBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }
    }

    public void startFirst(int time ,String code){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return dataBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent!=null){
            coinName= intent.getStringExtra("coinName");
            code = intent.getStringExtra("code");
            time = intent.getIntExtra("time",0);
            isVol = intent.getBooleanExtra("isVol",false);
            isDead = intent.getBooleanExtra("isDead",false);
            isGold = intent.getBooleanExtra("isGold",false);
            isEnvel = intent.getBooleanExtra("isEnvel",false);

            if (isVol){
                volPer = sharedPreference.getValue(this,"volPer",0);
                volCandle = sharedPreference.getValue(this,"volCandle",0);
            }

            if (isRsi){
                rsiBt = sharedPreference.getValue(this,"rsiBt",0);
                rsiT = sharedPreference.getValue(this,"rsiTop",0);
            }
            NotificationManager.startForgroundNoti(this,1010,NotificationManager.Channel.NOTICE,coinName+" 모니터링 중..",coinName+"의 시그널을 모니터링 하고 있습니다...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    upbitData.getServiceFirstData(time,code);
                }
            },1000);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        upbitData =new UpbitData();
        upbitData.setUpbitCallback(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void getFirstResult(List<Candle> candleEntries) {
        allDataClear();
        drawFirstData(candleEntries);
        upbitData.getServiceRealTimeData(time,code);
    }

    @Override
    public void getRealTimeResult(List<Candle> candleEntries) {
        drawRealTime(candleEntries);

    }
    private void setRsiData(ArrayList<CandleEntry> array, float guideDay) {
        double upPrice = 0;
        double downPrice = 0;

        for (int i = 0; i < array.size(); i++) {
            if (i >= (int) guideDay) {
                CandleEntry data;
                if (i == guideDay) {
                    for (int j = 0; j < guideDay; j++) {
                        if ((i - j) > 0) {
                            data = array.get(i - j);
                            if (data.getOpen() > data.getClose()) {
                                downPrice += data.getBodyRange();
                            } else {
                                upPrice += data.getBodyRange();
                            }
                        }
                    }
                    upAvg = upPrice / guideDay;
                    downAvg = downPrice / guideDay;
                    double rs = upAvg / downAvg;
                    float rsi = (float) (100 - (100 / (1 + rs)));
                    rsiEndtries.add(new Entry(guideDay - 1 + rsiEndtries.size(), rsi));
                } else {
                    data = array.get(i);
                    if (data.getOpen() > data.getClose()) {
                        upAvg = (upAvg * 13) / 14;
                        downAvg = ((downAvg * 13) + data.getBodyRange()) / 14;
                    } else {
                        upAvg = ((upAvg * 13) + data.getBodyRange()) / 14;
                        downAvg = (downAvg * 13) / 14;
                    }
                    double rs = upAvg / downAvg;
                    float rsi = (float) (100 - (100 / (1 + rs)));
                    rsiEndtries.add(new Entry(guideDay - 1 + rsiEndtries.size(), rsi));
                }
            }
        }
    }

    private void setRsiRealTimeData(CandleEntry data, float guideDay, boolean isNow) {

        if (!isNow) {
            if (data.getOpen() > data.getClose()) {
                upAvg = (upAvg * 13) / 14;
                downAvg = ((downAvg * 13) + data.getBodyRange()) / 14;
            } else {
                upAvg = ((upAvg * 13) + data.getBodyRange()) / 14;
                downAvg = (downAvg * 13) / 14;
            }
            double rs = upAvg / downAvg;
            float rsi = (float) (100 - (100 / (1 + rs)));

            rsiEndtries.add(new Entry(guideDay + rsiEndtries.size(), rsi));
        } else {
            double up;
            double down;
            if (data.getOpen() > data.getClose()) {
                up = (upAvg * 13) / 14;
                down = ((downAvg * 13) + data.getBodyRange()) / 14;
            } else {
                up = ((upAvg * 13) + data.getBodyRange()) / 14;
                down = (downAvg * 13) / 14;
            }
            double rs = up / down;
            float rsi = (float) (100 - (100 / (1 + rs)));

            rsiEndtries.get(rsiEndtries.size() - 1).setY(rsi);
        }
    }
    private void setBarData(List<Candle> array) {

        for (int i =0; i<array.size(); i++) {
            Candle candle = array.get(array.size() - 1 - i);
            double volume = candle.getCandleAccTradeVolume();
            float volumeFloat = (float) volume;
            barEntries.add(new BarEntry(barEntries.size(), volumeFloat));
        }
    }

    private void setRealTimeBar(Candle candle, boolean isNow) {
        double volume = candle.getCandleAccTradeVolume();
        float volumeFloat = (float) volume;
        if (!isNow) {
            if (barEntries.size()==500){
                barEntries.remove(0);
            }
            barEntries.add(new BarEntry(barEntries.size(), volumeFloat));
            if (isVol){
                volCheck(volCandle);
            }
        } else {
            barEntries.get(barEntries.size() - 1).setY(volumeFloat);
        }
    }

    private void setLineData(ArrayList<CandleEntry> array, float guideDay) {
        for (int i = 0; i < array.size(); i++) {
            if (i >= (int) guideDay - 1) {
                int tradePrice = 0;
                for (int j = 0; j < guideDay; j++) {
                    tradePrice +=  array.get(i - j).getClose();
                }
                int day = (int) guideDay;
                switch (day) {
                    case 5:
                        line5Entries.add(new Entry(guideDay - 1 + line5Entries.size(),  tradePrice / guideDay));
                        break;
                    case 10:
                        line10Entries.add(new Entry(guideDay - 1 + line10Entries.size(), tradePrice / guideDay));
                        break;
                    case 20:
                        line20Entries.add(new Entry(guideDay - 1 + line20Entries.size(),  tradePrice / guideDay));
                        envelopePlusEntries.add(new Entry(guideDay - 1 + line20Entries.size(), ( tradePrice / guideDay) * 1.2f));
                        envelopeMinusEntries.add(new Entry(guideDay - 1 + line20Entries.size(), (tradePrice / guideDay) * 0.8f));
                        break;
                    case 60:
                        line60Entries.add(new Entry(guideDay - 1 + line60Entries.size(),  tradePrice / guideDay));
                        break;
                }
            }
        }
    }

    private void setRealTimeLineData(List<CandleEntry> array, int guideDay, boolean isNow) {
        float tradePrice = 0f;
        for (int i = 0; i < guideDay; i++) {
            if ((array.size() - 1 - i) >= 0) {
                CandleEntry data = array.get(array.size() - i - 1);
                tradePrice += data.getClose();
            }
        }
        if (!isNow) {
            switch (guideDay) {
                case 5:
                    removeLineEntry(line5Entries);
                    line5Entries.add(new Entry(guideDay + line5Entries.size(), tradePrice / guideDay));
                    break;
                case 10:
                    removeLineEntry(line10Entries);
                    line10Entries.add(new Entry(guideDay + line10Entries.size(), tradePrice / guideDay));
                    break;
                case 20:
                    removeLineEntry(line20Entries);
                    line20Entries.add(new Entry(guideDay + line20Entries.size(), tradePrice / guideDay));
                    removeLineEntry(envelopePlusEntries);
                    removeLineEntry(envelopeMinusEntries);
                    envelopePlusEntries.add(new Entry(guideDay + line20Entries.size(), ( tradePrice / guideDay) * 1.2f));
                    envelopeMinusEntries.add(new Entry(guideDay + line20Entries.size(), (tradePrice / guideDay) * 0.8f));
                    break;
                case 60:
                    removeLineEntry(line60Entries);
                    line60Entries.add(new Entry(guideDay + line60Entries.size(), tradePrice / guideDay));
                    break;
            }
            if (isGold){
                goldCrossNoti(candleEntries);
            }
            if (isDead){
                deadCross(candleEntries);
            }
            if (isEnvel){
                envelopNoti(candleEntries);
            }
        } else {

            switch (guideDay) {
                case 5:
                    line5Entries.get(line5Entries.size() - 1).setY(tradePrice / guideDay);
                    break;
                case 10:

                    line10Entries.get(line10Entries.size() - 1).setY(tradePrice / guideDay);
                    break;
                case 20:

                    line20Entries.get(line20Entries.size() - 1).setY(tradePrice / guideDay);
                    envelopePlusEntries.get(envelopePlusEntries.size() - 1).setY(tradePrice / guideDay * 1.2f);
                    envelopeMinusEntries.get(envelopeMinusEntries.size() - 1).setY( tradePrice / guideDay * 0.8f);
                    break;
                case 60:
                    line60Entries.get(line60Entries.size() - 1).setY(tradePrice / guideDay);
                    break;
            }
        }
    }

    private void removeLineEntry(ArrayList<Entry> lineEntries){
        if (lineEntries.size()==500){
            lineEntries.remove(0);
        }
    }

    private void envelopNoti(List<CandleEntry> array){
        if (envelopeMinusEntries.get(envelopeMinusEntries.size()-1).getY()>=array.get(array.size()-1).getClose()){
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "시그널", "엔벨로프 하단 터치(과매도)  / 현재가격 : " +  (array.get(array.size() - 1).getClose()));
        }
        if (envelopePlusEntries.get(envelopePlusEntries.size()-1).getY()>=array.get(array.size()-1).getClose()){
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "시그널", "엔벨로프 상단 터치 (과매수)  / 현재가격 :" +  (array.get(array.size() - 1).getClose()));
        }
    }

    private void goldCrossNoti(List<CandleEntry> array){
        if (line5Entries.get(line5Entries.size() - 1).getY() > line10Entries.get(line10Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() < line10Entries.get(line10Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "시그널", "5일 10일선 골드크로스 / 현재가격 : " +  (array.get(array.size() - 1).getClose()));
        } else if (line5Entries.get(line5Entries.size() - 1).getY() > line20Entries.get(line20Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() < line20Entries.get(line20Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "시그널", "5일 20일선 골드크로스 / 현재가격 : " +  (array.get(array.size() - 1).getClose()));
        } else if (line5Entries.get(line5Entries.size() - 1).getY() > line60Entries.get(line60Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() < line60Entries.get(line60Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName+ "시그널", "5일 60일선 골드크로스 / 현재가격 : " +  (array.get(array.size() - 1).getClose()));
        } else if (line10Entries.get(line10Entries.size() - 1).getY() > line20Entries.get(line20Entries.size() - 1).getY() && line10Entries.get(line10Entries.size() - 2).getY() < line20Entries.get(line20Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "시그널", "10일 20일선 골드크로스 / 현재가격 : " +  (array.get(array.size() - 1).getClose()));
        } else if (line10Entries.get(line10Entries.size() - 1).getY() > line60Entries.get(line60Entries.size() - 1).getY() && line10Entries.get(line10Entries.size() - 2).getY() < line60Entries.get(line60Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName+ "시그널", "10일 60일선 골드크로스 / 현재가격 : " +  (array.get(array.size() - 1).getClose()));
        }else if (line20Entries.get(line20Entries.size() - 1).getY() > line60Entries.get(line60Entries.size() - 1).getY() && line20Entries.get(line20Entries.size() - 2).getY() < line60Entries.get(line60Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName+ "시그널", "20일 60일선 골드크로스 / 현재가격 : " +  (array.get(array.size() - 1).getClose()));
        }
    }


    private void deadCross(List<CandleEntry> array){
        if (line5Entries.get(line5Entries.size() - 1).getY() < line10Entries.get(line10Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() > line10Entries.get(line10Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "시그널", "5일 10일선 데드크로스 / 현재가격 : " + (long) (array.get(array.size() - 1).getClose()));
        } else if (line5Entries.get(line5Entries.size() - 1).getY() < line20Entries.get(line20Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() > line20Entries.get(line20Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "시그널", "5일 20일선 데드크로스 / 현재가격 : " + (long) (array.get(array.size() - 1).getClose()));
        } else if (line5Entries.get(line5Entries.size() - 1).getY() < line60Entries.get(line60Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() >line60Entries.get(line60Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName+ "시그널", "5일 60일선 데드크로스 / 현재가격 : " + (long) (array.get(array.size() - 1).getClose()));
        }else if (line10Entries.get(line10Entries.size() - 1).getY() < line20Entries.get(line20Entries.size() - 1).getY() && line10Entries.get(line10Entries.size() - 2).getY() > line20Entries.get(line20Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "시그널", "10일 20일선 데드크로스 / 현재가격 : " + (long) (array.get(array.size() - 1).getClose()));
        } else if (line10Entries.get(line10Entries.size() - 1).getY() < line60Entries.get(line60Entries.size() - 1).getY() && line10Entries.get(line10Entries.size() - 2).getY() >line60Entries.get(line60Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName+ "시그널", "10일 60일선 데드크로스 / 현재가격 : " + (long) (array.get(array.size() - 1).getClose()));
        } else if (line20Entries.get(line20Entries.size() - 1).getY() < line60Entries.get(line60Entries.size() - 1).getY() && line20Entries.get(line20Entries.size() - 2).getY() >line60Entries.get(line60Entries.size() - 2).getY()) {
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName+ "시그널", "20일 60일선 데드크로스 / 현재가격 : " + (long) (array.get(array.size() - 1).getClose()));
        }
    }

    private void volCheck(int volCandle){
        boolean isVolUp=false;

        for (int i=0;i<volCandle; i++){
            float preVolume =barEntries.get(barEntries.size()-1-volCandle).getY()*(1+(volPer/100f));
            if (barEntries.get(barEntries.size()-1).getY()>preVolume){
                isVolUp =true;
                break;
            }
        }
        if (isVolUp){
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "거래량 시그널", "이전 "+volCandle+" 캔들 내 거래량 보다 "+volPer+" 증가 중");
        }
    }

    private void rsiCheck(float rsi){

        if (rsi<=30){
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "RSI 시그널", "현재 RSI 가 "+(int)rsi +" 이므로 과매도 구간에 진입 하였습니다");
        }
        if (rsi>=65){
            NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.NOTICE, coinName + "RSI 시그널", "현재 RSI 가 "+(int)rsi +" 이므로 과매수 구간에 진입 하였습니다");
        }
    }
    private void drawFirstData(List<Candle> candles) {
        for (int i = 0; i < candles.size(); i++) {
            Candle data = candles.get(candles.size() - 1 - i);
            float openingPrice = data.getOpeningPrice();
            float tradePrice = data.getTradePrice();
            float lowPrice = data.getLowPrice();
            float highPrice = data.getHighPrice();
            candleEntries.add(new CandleEntry(candleEntries.size(), highPrice, lowPrice, openingPrice, tradePrice));
        }
        setBarData(candles);
        setLineData(candleEntries, 5);
        setLineData(candleEntries, 10);
        setLineData(candleEntries, 20);
        setLineData(candleEntries, 60);
        setRsiData(candleEntries, 14);

        LoadingProgress.dismissDialog();

    }

    private void drawRealTime(List<Candle> candles) {
        for (Candle candle : candles) {
            float openingPrice = (float) candle.getOpeningPrice();
            float tradePrice = (float) candle.getTradePrice();
            float lowPrice = (float) candle.getLowPrice();
            float highPrice = (float) candle.getHighPrice();
//            currentPrice.setText(String.valueOf((long) tradePrice) + " 원");
            if (candleEntries.get(candleEntries.size() - 1).getOpen() != openingPrice) {
                if (candleEntries.size()==500){
                    candleEntries.remove(0);
                }
                candleEntries.add(new CandleEntry(candleEntries.size(), highPrice, lowPrice, openingPrice, tradePrice));
                setRealTimeLineData(candleEntries, 5, false);
                setRealTimeLineData(candleEntries, 10, false);
                setRealTimeLineData(candleEntries, 20, false);
                setRealTimeLineData(candleEntries, 60, false);
                setRealTimeBar(candle, false);
                setRsiRealTimeData(new CandleEntry(candleEntries.size(), highPrice, lowPrice, openingPrice, tradePrice), 14, false);
            } else {
                int lastIndex = candleEntries.size() - 1;
                candleEntries.get(lastIndex).setHigh(highPrice);
                candleEntries.get(lastIndex).setClose(tradePrice);
                candleEntries.get(lastIndex).setLow(lowPrice);
                candleEntries.get(lastIndex).setOpen(openingPrice);
                setRealTimeLineData(candleEntries, 5, true);
                setRealTimeLineData(candleEntries, 10, true);
                setRealTimeLineData(candleEntries, 20, true);
                setRealTimeLineData(candleEntries, 60, true);
                setRealTimeBar(candle, true);
                setRsiRealTimeData(candleEntries.get(lastIndex), 14, true);
            }

        }
    }
    private void allDataClear() {
        candleEntries.clear();
        barEntries.clear();
        line5Entries.clear();
        line10Entries.clear();
        line20Entries.clear();
        line60Entries.clear();
        envelopeMinusEntries.clear();
        envelopePlusEntries.clear();
        rsiEndtries.clear();
    }
}
