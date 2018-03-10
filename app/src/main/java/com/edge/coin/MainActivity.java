package com.edge.coin;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ArrayList<CandleEntry> candleEntries = new ArrayList<>();
    ArrayList<Entry> line5Entries = new ArrayList<>();
    ArrayList<Entry> line10Entries = new ArrayList<>();
    ArrayList<Entry> line20Entries = new ArrayList<>();
    ArrayList<Entry> line60Entries = new ArrayList<>();
    CandleDataSet dataSet;
    CombinedData combinedData = new CombinedData();
    CombinedChart combinedChart;
    CandleData candleData;

    LineDataSet line60DataSet;
    LineDataSet line10DataSet;
    LineDataSet line20DataSet;
    LineData lineData;
    LineDataSet line5DataSet;
    Timer timer;
    TimerTask timerTask;
    String firstDate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    String code = "CRIX.UPBIT.KRW-BTC";
    String date;
    String firstMin, currentMin;
    Call<List<DataModel>> getCandleRealTime, getCandelFirst;
    boolean isNextMin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        combinedChart = findViewById(R.id.candle);
        combinedChart.getDescription().setEnabled(false);
        combinedChart.getLegend().setEnabled(false);
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE});
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setAxisLineColor(Color.LTGRAY);
        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setEnabled(false);
        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setDrawGridLines(true);
        rightAxis.setTextColor(Color.LTGRAY);
        rightAxis.setAxisLineColor(Color.LTGRAY);
        rightAxis.setLabelCount(5);
        timer = new Timer();
        combinedChart.setVisibleXRangeMaximum(120);

        combinedChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e.getData() != null) {
                    Log.d("test123", e.getData().getClass().toString());
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        getFirstData();

    }

    private void setCandleStickChart() {
        dataSet = new CandleDataSet(candleEntries, "");

        dataSet.setDrawIcons(false);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
//        set1.setColor(Color.rgb(80, 80, 80));
        dataSet.setBarSpace(0.3f);
        dataSet.setShadowWidth(0.7f);
        dataSet.setShadowColorSameAsCandle(true);
        dataSet.setDecreasingColor(Color.RED);
        dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        dataSet.setIncreasingColor(Color.rgb(122, 242, 84));
        dataSet.setIncreasingPaintStyle(Paint.Style.STROKE);
        dataSet.setNeutralColor(Color.WHITE);
        dataSet.setValueTextColor(Color.TRANSPARENT);
        dataSet.setHighlightEnabled(true);

        line5DataSet = new LineDataSet(line5Entries, "");
        line5DataSet.setDrawCircles(false);

        line5DataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        line5DataSet.setLineWidth(1f);


        line10DataSet = new LineDataSet(line10Entries, "");
        line10DataSet.setDrawCircles(false);
        line10DataSet.setColor(Color.MAGENTA);
        line10DataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        line10DataSet.setLineWidth(1f);


        line20DataSet = new LineDataSet(line20Entries, "");
        line20DataSet.setDrawCircles(false);
        line20DataSet.setColor(Color.YELLOW);
        line20DataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        line20DataSet.setLineWidth(1f);


        line60DataSet = new LineDataSet(line60Entries, "");
        line60DataSet.setDrawCircles(false);
        line60DataSet.setColor(Color.GREEN);
        line60DataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        line60DataSet.setLineWidth(1f);


        lineData = new LineData(line5DataSet, line10DataSet, line20DataSet, line60DataSet);
        candleData = new CandleData(dataSet);
        combinedData.setData(candleData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);
        combinedChart.invalidate();

    }


    private void setLineData(ArrayList<CandleEntry> array, float guideDay) {
        for (int i = 0; i < array.size(); i++) {
            if (i>= (int)guideDay-1) {
                int tradePrice = 0;
                for (int j = 0; j < guideDay; j++) {
                        CandleEntry data = array.get( i -j);
                        tradePrice += data.getClose();
                    Log.d("test1234",tradePrice+",,"+(long)tradePrice/guideDay +",,."+(long)data.getClose()+",,"+(i-j));
                }
                int day= (int) guideDay;
                switch (day) {
                    case 5:
                        Log.d("test1234",(long)tradePrice/guideDay +",,.");

                        line5Entries.add(new Entry(guideDay-1 + line5Entries.size(), (long)tradePrice / guideDay));
                        break;
                    case 10:
                        line10Entries.add(new Entry(guideDay  -1+ line10Entries.size(), (long)tradePrice / guideDay));
                        break;
                    case 20:
                        line20Entries.add(new Entry(guideDay-1  + line20Entries.size(), (long)tradePrice / guideDay));
                        break;
                    case 60:
                        line60Entries.add(new Entry(guideDay -1 + line60Entries.size(), (long)tradePrice / guideDay));
                        break;
                }
            }
        }
    }

    private void setRealTimeLineData(List<CandleEntry> array, int guideDay,boolean isNow) {
        float tradePrice = 0f;
        for (int i = 0; i < guideDay; i++) {
            if ((array.size() - 1 - i) >= 0) {
                CandleEntry data = array.get(array.size() - i - 1);
                tradePrice += data.getClose();
            }
        }
        if (!isNow){
          //  Log.d("test1234",tradePrice / guideDay+".");
            switch (guideDay) {
                case 5:

                    line5Entries.add(new Entry(guideDay-1  + line5Entries.size(), tradePrice / guideDay));
                    break;
                case 10:
                    line10Entries.add(new Entry(guideDay -1 + line10Entries.size(), tradePrice / guideDay));
                    break;
                case 20:
                    line20Entries.add(new Entry(guideDay -1 + line20Entries.size(), tradePrice / guideDay));
                    break;
                case 60:
                    line60Entries.add(new Entry(guideDay -1+ line60Entries.size(), tradePrice / guideDay));
                    break;
            }
        } else {

            switch (guideDay) {
                case 5:
                    //Log.d("test1235",tradePrice / guideDay+".");
                    line5Entries.get(line5Entries.size()-1).setY(tradePrice/guideDay);
                    break;
                case 10:
                    line10Entries.get(line10Entries.size()-1).setY(tradePrice/guideDay);
                    break;
                case 20:
                    line20Entries.get(line20Entries.size()-1).setY(tradePrice/guideDay);
                    break;
                case 60:
                    line60Entries.get(line60Entries.size()-1).setY(tradePrice/guideDay);
                    break;
            }
        }


    }

    private void getFirstData() {

        String date = dateFormat.format(new Date());
        String hour = date.split(" ")[1].split(":")[0];
        if (hour.equals("24")) {
            hour = "00";
            String time = hour + ":" + date.split(" ")[1].split(":")[0] + ":" + date.split(" ")[1].split(":")[2];
            date = date.split(" ")[0] + " " + time;
            Log.d("aaaa", date);
            Log.d("aaaa", time);
        }
        getCandelFirst = SetRetrofit.setRefrofit().get1MinuteCandle(code, 200, date);
        getCandelFirst.enqueue(new Callback<List<DataModel>>() {
            @Override
            public void onResponse(Call<List<DataModel>> call, Response<List<DataModel>> response) {
                if (response.isSuccessful()) {
                   drawFirstData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<DataModel>> call, Throwable t) {
                Log.d("test1234", t.getMessage());
            }
        });
    }

    private void drawFirstData(List<DataModel> dataModels){
        for (int i = 0; i < dataModels.size(); i++) {
            DataModel data = dataModels.get(dataModels.size() - 1 - i);
            float openingPrice = data.getOpeningPrice();
            float tradePrice = data.getTradePrice();
            float lowPrice = data.getLowPrice();
            float highPrice = data.getHighPrice();
            candleEntries.add(new CandleEntry(candleEntries.size(), highPrice, lowPrice, openingPrice, tradePrice));
            setCandleStickChart();
        }
        setLineData(candleEntries, 5);
        setLineData(candleEntries, 10);
        setLineData(candleEntries, 20);
       setLineData(candleEntries, 60);
        getData();
    }

    private void drawRealTime(List<DataModel> dataModels){
        for (DataModel dataModel : dataModels) {
            float openingPrice = (float) dataModel.getOpeningPrice();
            float tradePrice = (float) dataModel.getTradePrice();
            float lowPrice = (float) dataModel.getLowPrice();
            float highPrice = (float) dataModel.getHighPrice();
            if (candleEntries.get(candleEntries.size() - 1).getOpen() != openingPrice) {
                candleEntries.add(new CandleEntry(candleEntries.size(), highPrice, lowPrice, openingPrice, tradePrice));
                setRealTimeLineData(candleEntries, 5,false);
                setRealTimeLineData(candleEntries, 10,false);
                setRealTimeLineData(candleEntries, 20,false);
                setRealTimeLineData(candleEntries, 60,false);
            } else {
                int lastIndex = candleEntries.size() - 1;
                candleEntries.get(lastIndex).setHigh(highPrice);
                candleEntries.get(lastIndex).setClose(tradePrice);
                candleEntries.get(lastIndex).setLow(lowPrice);
                candleEntries.get(lastIndex).setOpen(openingPrice);
                setRealTimeLineData(candleEntries, 5,true);
                setRealTimeLineData(candleEntries, 10,true);
                setRealTimeLineData(candleEntries, 20,true);
                setRealTimeLineData(candleEntries, 60,true);
            }
            candleData.notifyDataChanged();
            dataSet.notifyDataSetChanged();
            line5DataSet.notifyDataSetChanged();
            line10DataSet.notifyDataSetChanged();
            line20DataSet.notifyDataSetChanged();
            line60DataSet.notifyDataSetChanged();
            lineData.notifyDataChanged();
            combinedData.notifyDataChanged();
            combinedChart.notifyDataSetChanged();
            combinedChart.invalidate();
        }
    }

    private void getData() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                date = dateFormat.format(new Date());
                String hour = date.split(" ")[1].split(":")[0];
                if (hour.equals("24")) {
                    hour = "00";
                    String time = hour + ":" + date.split(" ")[1].split(":")[0] + ":" + date.split(" ")[1].split(":")[2];
                    date = date.split(" ")[0] + " " + time;
                }
                if (firstDate == null) {
                    firstDate = date;
                } else {
                    firstMin = firstDate.split(" ")[1].split(":")[1];
                    currentMin = date.split(" ")[1].split(":")[1];
                    isNextMin = firstMin.equals(currentMin);
                    firstDate = date;
                }
                getCandleRealTime = SetRetrofit.setRefrofit().get1MinuteCandle(code, 1, date);
                getCandleRealTime.enqueue(new Callback<List<DataModel>>() {
                    @Override
                    public void onResponse(Call<List<DataModel>> call, Response<List<DataModel>> response) {
                        if (response.isSuccessful()) {
                            drawRealTime(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DataModel>> call, Throwable t) {
                        Log.d("test1234", t.getMessage());
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 500);

    }
}
