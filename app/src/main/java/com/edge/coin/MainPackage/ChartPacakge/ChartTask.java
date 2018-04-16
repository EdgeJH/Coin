package com.edge.coin.MainPackage.ChartPacakge;

import com.edge.coin.BasePresenter;
import com.edge.coin.BaseView;
import com.edge.coin.Utils.Candle;

import java.util.List;

/**
 * Created by user1 on 2018-03-13.
 */

public interface ChartTask {
    interface PresenterBridge extends BasePresenter {
        void startFirstData(int time ,String code);
        void startRealTimeData(int time ,String code);
        void stopRealData();
    }

    interface View extends BaseView<PresenterBridge> {
        void getFirstResult(List<Candle> candleEntries);
        void getRealTimeResult(List<Candle> candleEntries);
    }
}
