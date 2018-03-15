package com.edge.coin.ServicePackage;

import com.edge.coin.Utils.Candle;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 2018-03-15.
 */

public interface ServiceDataCallback {
    void getFirstLineData(int guideDay, ArrayList<Entry> lineEntries);
    void  getFirstCandleData(List<Candle> candles);
}
