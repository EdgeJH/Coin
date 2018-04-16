package com.edge.coin.Utils;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;

/**
 * Created by user1 on 2018-03-21.
 */

public class ChartValueFormatter implements IAxisValueFormatter {

    int time;
    String minType = "hh:mm";
    String hourType = "dd|hh";
    SimpleDateFormat dateFormat;
    long current= System.currentTimeMillis();

    public ChartValueFormatter(int time) {
        this.time = time;
        setDateFormat(time);
    }

    @SuppressLint("SimpleDateFormat")
    private void setDateFormat(int time) {
        switch (time) {
            case 60:
                dateFormat = new SimpleDateFormat(hourType);
                break;
            case 240:
                dateFormat = new SimpleDateFormat(hourType);
                break;
            default:
                dateFormat = new SimpleDateFormat(minType);
                break;
        }
    }



    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long result =current-(time*60*1000*(200-(int)value));
        return dateFormat.format(result);
    }
}
