package com.edge.coin.UpbitPackage;

import android.os.Handler;

import com.edge.coin.Utils.ApiService;
import com.edge.coin.Utils.Candle;
import com.edge.coin.Utils.RetrofitCall;

import java.util.ArrayList;
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
    CoinListCallback coinListCallback;


    public void getFirstData(final int time, final String code) {

        getCandelFirst = RetrofitCall.retrofit(ApiService.BASE_URL).get1MinuteCandle(time,code, 200, System.currentTimeMillis());
        getCandelFirst.enqueue(new Callback<List<Candle>>() {
            @Override
            public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                if (response.isSuccessful()) {
                    upbitCallback.getFirstResult(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Candle>> call, Throwable t) {

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
                getCandleRealTime = RetrofitCall.retrofit(ApiService.BASE_URL).get1MinuteCandle(time,code, 1, currentTime);
                getCandleRealTime.enqueue(new Callback<List<Candle>>() {
                    @Override
                    public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                        if (response.isSuccessful()) {

                            if (upbitCallback!=null){
                                upbitCallback.getRealTimeResult(response.body());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Candle>> call, Throwable t) {

                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 2000);
    }
    public void getServiceFirstData(final int time, final String code) {

        getCandelFirst =RetrofitCall.retrofit(ApiService.BASE_URL).get1MinuteCandle(time,code, 200, System.currentTimeMillis());
        getCandelFirst.enqueue(new Callback<List<Candle>>() {
            @Override
            public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                if (response.isSuccessful()) {
                    if (upbitCallback!=null){
                        upbitCallback.getFirstResult(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Candle>> call, Throwable t) {

            }
        });
    }

    public void getServiceRealTimeData(final int time,final String code) {

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                getCandleRealTime = RetrofitCall.retrofit(ApiService.BASE_URL).get1MinuteCandle(time,code, 1, currentTime);
                getCandleRealTime.enqueue(new Callback<List<Candle>>() {
                    @Override
                    public void onResponse(Call<List<Candle>> call, Response<List<Candle>> response) {
                        if (response.isSuccessful()) {

                            if (upbitCallback!=null){
                                upbitCallback.getRealTimeResult(response.body());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Candle>> call, Throwable t) {
                        Handler handler =new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getServiceRealTimeData(time,code);
                            }
                        },30000);
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 15000);
    }

    public void getCoinList(){
        Call<List<TradeCoin>> coin = RetrofitCall.retrofit(ApiService.COIN_URL).getCoinList(System.currentTimeMillis());
        coin.enqueue(new Callback<List<TradeCoin>>() {
            @Override
            public void onResponse(Call<List<TradeCoin>> call, Response<List<TradeCoin>> response) {

               if (response.isSuccessful()){
                   if (coinListCallback!=null){
                       coinListCallback.getCoinList((ArrayList<TradeCoin>) response.body());
                   }
               }
            }

            @Override
            public void onFailure(Call<List<TradeCoin>> call, Throwable t) {

            }
        });
    }
    public void stopData(){
        if (timer!=null){
            timer.cancel();
        }
    }


    public  void setUpbitCallback(UpbitCallback upbitCallback){
            this.upbitCallback = upbitCallback;
    }


    public void setCoinListCallback(CoinListCallback coinListCallback){
        this.coinListCallback =coinListCallback;
    }
}
