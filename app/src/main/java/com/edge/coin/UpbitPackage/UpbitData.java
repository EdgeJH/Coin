package com.edge.coin.UpbitPackage;

import android.util.Log;

import com.edge.coin.Utils.Candle;
import com.edge.coin.Utils.SetRetrofit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user1 on 2018-03-13.
 */

public class UpbitData {
    Call<List<Candle>> getCandleRealTime, getCandelFirst;
    Timer timer;
    TimerTask timerTask;
    UpbitCallback upbitCallback;
    public UpbitData(UpbitCallback upbitCallback) {
        this.upbitCallback = upbitCallback;
    }

    public void getFirstData(final int time, final String code) {

        getCandelFirst = SetRetrofit.setRefrofit().get1MinuteCandle(time,code, 200, System.currentTimeMillis());
        getCandelFirst.enqueue(new Callback<List<Candle>>() {
            @Override
            public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                if (response.isSuccessful()) {
                    upbitCallback.getFirstResult(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Candle>> call, Throwable t) {
                Log.d("test1234", t.getMessage());
            }
        });
    }

    public void getRealTimeData(final int time,final String code) {

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                getCandleRealTime = SetRetrofit.setRefrofit().get1MinuteCandle(time,code, 1, currentTime);
                getCandleRealTime.enqueue(new Callback<List<Candle>>() {
                    @Override
                    public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                        if (response.isSuccessful()) {
                            //Log.d("LOG123123", "onServiceDisconnected1111()");
                            upbitCallback.getRealTimeResult(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Candle>> call, Throwable t) {
                        Log.d("test1234", t.getMessage());
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 2000);
    }
    public void stopData(){
        if (timer!=null){
            timer.cancel();
        }
    }
}
