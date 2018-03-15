package com.edge.coin.UpbitPackage;

import com.edge.coin.Utils.Candle;

import java.util.List;

/**
 * Created by user1 on 2018-03-13.
 */

public interface UpbitCallback {
    void getFirstResult(List<Candle> candleEntries);
    void getRealTimeResult(List<Candle> candleEntries);
}
