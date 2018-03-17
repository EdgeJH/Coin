package com.edge.coin.ServicePackage;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.edge.coin.UpbitPackage.UpbitCallback;
import com.edge.coin.UpbitPackage.UpbitData;
import com.edge.coin.Utils.Candle;
import com.edge.coin.Utils.LoadingProgress;
import com.edge.coin.Utils.NotificationManager;
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


    double upAvg,downAvg;
    String code;
    List<String> coinName;
    UpbitData upbitData;

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
        Log.d("LOG123123","service bind");
        return dataBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LOG123123","service startcommand ");
        if (intent!=null){
            String coinName= intent.getStringExtra("coinName");
            code = intent.getStringExtra("code");
            time = intent.getIntExtra("time",0);
            NotificationManager.startForgroundNoti(this,1010,NotificationManager.Channel.NOTICE,coinName+" 모니터링 중..",coinName+"의 시그널을 모니터링 하고 있습니다...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    upbitData.getServiceFirstData(time,code);
                }
            },1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.d("LOG123123","service start create");
        upbitData =new UpbitData(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LOG123123","service destroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("LOG123123","service unbind");
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
                    //    Log.d("test1223","up avg = "+upAvg);
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
            //    Log.d("test1223","up avg = "+upAvg);
            double rs = upAvg / downAvg;
            float rsi = (float) (100 - (100 / (1 + rs)));
            if (rsiEndtries.size()==2000){
                rsiEndtries.remove(0);
            }
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
            //    Log.d("test1223","up avg = "+upAvg);
            double rs = up / down;
            float rsi = (float) (100 - (100 / (1 + rs)));

            //  Log.d("test1234",rsi+",,,"+upAvg+",,,,,"+downAvg+",,,,,,"+rs);
            rsiEndtries.get(rsiEndtries.size() - 1).setY(rsi);
        }
    }

    private void setBarData(List<Candle> array) {
        float maxvol = 0;
        for (Candle candle : array) {
            double volume = candle.getCandleAccTradeVolume();
            float volumeFloat = (float) volume;
            barEntries.add(new BarEntry(barEntries.size(), volumeFloat));
            if (maxvol == 0f) {
                maxvol = volumeFloat;
            } else {
                if (volumeFloat > maxvol) {
                    maxvol = volumeFloat;
                }
            }

        }

    }

    private void setRealTimeBar(Candle candle, boolean isNow) {
        double volume = candle.getCandleAccTradeVolume();
        float volumeFloat = (float) volume;
        if (!isNow) {
            if (barEntries.size()==2000){
                barEntries.remove(0);
            }
            barEntries.add(new BarEntry(barEntries.size(), volumeFloat));
        } else {
            barEntries.get(barEntries.size() - 1).setY(volumeFloat);
        }
    }

    private void setLineData(ArrayList<CandleEntry> array, float guideDay) {
        for (int i = 0; i < array.size(); i++) {
            if (i >= (int) guideDay - 1) {
                int tradePrice = 0;
                for (int j = 0; j < guideDay; j++) {
                    tradePrice += (int) array.get(i - j).getClose();
                }
                int day = (int) guideDay;
                switch (day) {
                    case 5:
                        line5Entries.add(new Entry(guideDay - 1 + line5Entries.size(), (long) tradePrice / guideDay));
                        break;
                    case 10:
                        line10Entries.add(new Entry(guideDay - 1 + line10Entries.size(), (long) tradePrice / guideDay));
                        break;
                    case 20:
                        line20Entries.add(new Entry(guideDay - 1 + line20Entries.size(), (long) tradePrice / guideDay));
                        envelopePlusEntries.add(new Entry(guideDay - 1 + line20Entries.size(), ((long) tradePrice / guideDay) * 1.2f));
                        envelopeMinusEntries.add(new Entry(guideDay - 1 + line20Entries.size(), ((long) tradePrice / guideDay) * 0.8f));
                        break;
                    case 60:
                        line60Entries.add(new Entry(guideDay - 1 + line60Entries.size(), (long) tradePrice / guideDay));
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
                    envelopePlusEntries.add(new Entry(guideDay + line20Entries.size(), ((long) tradePrice / guideDay) * 1.2f));
                    envelopeMinusEntries.add(new Entry(guideDay + line20Entries.size(), ((long) tradePrice / guideDay) * 0.8f));
                    break;
                case 60:
                    removeLineEntry(line60Entries);
                    line60Entries.add(new Entry(guideDay + line60Entries.size(), tradePrice / guideDay));
                    break;
            }
            if (line5Entries.get(line5Entries.size() - 1).getY() > line10Entries.get(line10Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() < line10Entries.get(line10Entries.size() - 2).getY()) {
                NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.MESSAGE, coinName + "매수신호", "5일 10일선 골크 / 현재가격 : " + (long) (array.get(array.size() - 1).getClose()));
            } else if (line5Entries.get(line5Entries.size() - 1).getY() > line20Entries.get(line20Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() < line20Entries.get(line20Entries.size() - 2).getY()) {
                NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.MESSAGE, coinName + "매수신호", "5일 20일선 골크 / 현재가격 : " + (long) (array.get(array.size() - 1).getClose()));
            } else if (line5Entries.get(line5Entries.size() - 1).getY() > line60Entries.get(line60Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() < line60Entries.get(line60Entries.size() - 2).getY()) {
                NotificationManager.startForgroundNoti(this, 1, NotificationManager.Channel.MESSAGE, coinName+ "매수신호", "5일 60일선 골크 / 현재가격 : " + (long) (array.get(array.size() - 1).getClose()));
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
                    envelopePlusEntries.get(envelopePlusEntries.size() - 1).setY((long) tradePrice / guideDay * 1.2f);
                    envelopeMinusEntries.get(envelopeMinusEntries.size() - 1).setY((long) tradePrice / guideDay * 0.8f);
                    break;
                case 60:

                    line60Entries.get(line60Entries.size() - 1).setY(tradePrice / guideDay);
                    break;
            }
        }
    }

    private void removeLineEntry(ArrayList<Entry> lineEntries){
        if (lineEntries.size()==2000){
            lineEntries.remove(0);
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
                if (candleEntries.size()==2000){
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
