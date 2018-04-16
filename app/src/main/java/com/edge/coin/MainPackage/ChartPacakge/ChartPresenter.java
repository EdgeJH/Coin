package com.edge.coin.MainPackage.ChartPacakge;

import com.edge.coin.UpbitPackage.UpbitData;
import com.edge.coin.Utils.Candle;
import com.edge.coin.UpbitPackage.UpbitCallback;

import java.util.List;

/**
 * Created by user1 on 2018-03-13.
 */

public class ChartPresenter implements ChartTask.PresenterBridge,UpbitCallback {
    private ChartTask.View view;
    private UpbitData upbitData;
    public ChartPresenter(ChartTask.View view) {
        this.view = view;
        view.setPresenterBridge(this);
        upbitData = new UpbitData();
        upbitData.setUpbitCallback(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void startFirstData(int time, String code) {
        upbitData.getFirstData(time,code);
    }

    @Override
    public void startRealTimeData(int time, String code) {
        upbitData.getRealTimeData(time,code);
    }

    @Override
    public void stopRealData() {
        upbitData.stopData();
    }

    @Override
    public void getFirstResult(List<Candle> candleEntries) {
        view.getFirstResult(candleEntries);
    }

    @Override
    public void getRealTimeResult(List<Candle> candleEntries) {
        view.getRealTimeResult(candleEntries);
    }
}
