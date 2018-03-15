package com.edge.coin.MainPackage;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.edge.coin.R;
import com.edge.coin.ServicePackage.DataService;
import com.edge.coin.Utils.Candle;
import com.edge.coin.Utils.CoupleChartGestureListener;
import com.edge.coin.Utils.LoadingProgress;
import com.edge.coin.Utils.NotificationManager;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainTask.View {
    ArrayList<CandleEntry> candleEntries = new ArrayList<>();
    ArrayList<Entry> line5Entries = new ArrayList<>();
    ArrayList<Entry> line10Entries = new ArrayList<>();
    ArrayList<Entry> line20Entries = new ArrayList<>();
    ArrayList<Entry> line60Entries = new ArrayList<>();
    ArrayList<Entry> envelopePlusEntries = new ArrayList<>();
    ArrayList<Entry> envelopeMinusEntries = new ArrayList<>();
    ArrayList<Entry> rsiEndtries = new ArrayList<>();
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    List<Integer> barColor = new ArrayList<>();
    CandleDataSet dataSet;
    CombinedData combinedData = new CombinedData();
    CombinedChart combinedChart;
    CandleData candleData;

    BarDataSet barDataSet;
    BarData barData;
    LineDataSet line60DataSet;
    LineDataSet line10DataSet;
    LineDataSet line20DataSet;
    LineData lineData;
    LineDataSet line5DataSet;
    LineDataSet envelopePlusDataSet;
    LineDataSet envelopeMinusDataSet;
    LineData rsiData;
    LineDataSet rsiDataSet;
    TextView currentPrice;
    LineChart rsiChart;

    String code;
    String defaultCode = "CRIX.UPBIT.";

    List<String> coinName;
    List<String> coinCode;
    NiceSpinner spinner;
    boolean isChanged = false;
    Handler handler =new Handler();
    TabLayout tabLayout;
    int time=1;
    DataService dataService;
    boolean isBind;
    double upAvg=0;
    double downAvg=0;
    MainPresenter presenter;
    MainTask.PresenterBridge presenterBridge;
    YAxis leftAxis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService(new Intent(this,DataService.class),sconn,BIND_AUTO_CREATE);
        presenter = new MainPresenter(this);
        initView();
        setCombinedChart();
        setSpinner();
        setRsiChart();
        code = defaultCode + coinCode.get(0);
        setTabLayout();
    }
    ServiceConnection sconn = new ServiceConnection() {
        @Override //서비스가 실행될 때 호출
        public void onServiceConnected(ComponentName name, IBinder service) {
            DataService.DataBinder myBinder = (DataService.DataBinder) service;
            dataService = myBinder.getService();

            isBind = true;
            Log.d("LOG123123", "onServiceConnected() "+dataService.getA());
        }

        @Override //서비스가 종료될 때 호출
        public void onServiceDisconnected(ComponentName name) {
            dataService = null;
            isBind = false;
            Log.d("LOG123123", "onServiceDisconnected()");
        }
    };
    private void initView() {
        currentPrice = findViewById(R.id.price);
        combinedChart = findViewById(R.id.candle);
        spinner = findViewById(R.id.spinner);
        rsiChart = findViewById(R.id.rsi);
        tabLayout = findViewById(R.id.tab_layout);

    }

    private void setTabLayout(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LoadingProgress.showDialog(MainActivity.this, true);
                switch (tab.getPosition()){
                    case 0:
                        time =1;
                        break;
                    case 1:
                        time =5;
                        break;
                    case 2:
                        time =15;
                        break;
                    case 3:
                        time =30;
                        break;
                    case 4:
                        time = 60;
                        break;
                    case 5:
                        time=240;

                }
                getData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setSpinner() {
        coinName = Arrays.asList(getResources().getStringArray(R.array.coin_name));
        coinCode = Arrays.asList(getResources().getStringArray(R.array.coin_code));
        spinner.attachDataSource(coinName);
        spinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoadingProgress.showDialog(MainActivity.this, true);
                code = defaultCode + coinCode.get(position);
                getData();
                isChanged = true;
            }
        });
    }

    private void getData(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presenterBridge.startFirstData(time,code);
            }
        },1000);
    }



    private void setRsiChart() {
        rsiChart.getDescription().setEnabled(false);
        Legend legend = rsiChart.getLegend();
        legend.setEnabled(true);
        legend.setDrawInside(true);
        legend.setTextColor(Color.LTGRAY);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        XAxis rsiXAxis = rsiChart.getXAxis();
        rsiXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        rsiXAxis.setDrawGridLines(true);
        rsiXAxis.setAxisMinimum(0f);
        rsiXAxis.setTextColor(Color.LTGRAY);
        rsiXAxis.setAxisLineColor(Color.LTGRAY);
        rsiXAxis.setSpaceMax(50f);

        YAxis rsiLeftAxis = rsiChart.getAxisLeft();
        rsiLeftAxis.setEnabled(false);
        YAxis rsiRightAxis = rsiChart.getAxisRight();
        rsiRightAxis.setEnabled(true);
        rsiRightAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        rsiRightAxis.setDrawGridLines(true);
        rsiRightAxis.setTextColor(Color.LTGRAY);
        rsiRightAxis.setAxisLineColor(Color.LTGRAY);
        rsiRightAxis.setLabelCount(5);
        rsiRightAxis.setMaxWidth(60f);
        rsiRightAxis.setMinWidth(60f);
        rsiRightAxis.setAxisMaximum(100f);
        rsiRightAxis.setAxisMinimum(0f);
        rsiRightAxis.setLabelCount(5,true);
        rsiRightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);


        rsiRightAxis.addLimitLine(new LimitLine(30, ""));
        rsiRightAxis.addLimitLine(new LimitLine(70, ""));
    }

    private void setCombinedChart() {
        combinedChart.getDescription().setEnabled(false);
        Legend legend = combinedChart.getLegend();
        LegendEntry legendEntry = new LegendEntry("5일선", Legend.LegendForm.LINE,10f,2f,null,ContextCompat.getColor(this, R.color.line5));
        LegendEntry legendEntry2 = new LegendEntry("10일선", Legend.LegendForm.LINE,10f,2f,null,ContextCompat.getColor(this, R.color.line10));
        LegendEntry legendEntry3 = new LegendEntry("20일선", Legend.LegendForm.LINE,10f,2f,null,ContextCompat.getColor(this, R.color.line20));
        LegendEntry legendEntry4 = new LegendEntry("60일선", Legend.LegendForm.LINE,10f,2f,null,ContextCompat.getColor(this, R.color.line60));
        LegendEntry[] entries = {legendEntry,legendEntry2,legendEntry3,legendEntry4};
        legend.setCustom(entries);
        legend.setEnabled(true);
        legend.setDrawInside(true);
        legend.setTextColor(Color.LTGRAY);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BAR});
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setAxisLineColor(Color.LTGRAY);
        xAxis.setSpaceMax(50f);


        leftAxis = combinedChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawLimitLinesBehindData(true);
       leftAxis.setSpaceBottom(0f);
       leftAxis.setInverted(false);
        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setDrawGridLines(true);
        rightAxis.setTextColor(Color.LTGRAY);
        rightAxis.setAxisLineColor(Color.LTGRAY);
        rightAxis.setLabelCount(5);
        rightAxis.setMaxWidth(60f);
        rightAxis.setMinWidth(60f);
        rightAxis.setSpaceBottom(0f);
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        combinedChart.setOnChartGestureListener(new CoupleChartGestureListener(combinedChart, new Chart[]{rsiChart}));
        rsiChart.setOnChartGestureListener(new CoupleChartGestureListener(rsiChart, new Chart[]{combinedChart}));


    }

    private void setCombinedChartData() {
        dataSet = new CandleDataSet(candleEntries, "캔들");
        dataSet.setDrawIcons(false);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setBarSpace(0.2f);
        dataSet.setShadowWidth(0.7f);
        dataSet.setShadowColorSameAsCandle(true);;
        dataSet.setDecreasingColor(ContextCompat.getColor(this, R.color.decrease));
        dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        dataSet.setIncreasingColor(ContextCompat.getColor(this, R.color.increase));
        dataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        dataSet.setNeutralColor(Color.DKGRAY);
        dataSet.setDrawValues(false);

        barDataSet = new BarDataSet(barEntries,"거래량");
        barDataSet.setColors(barColor);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        line5DataSet = new LineDataSet(line5Entries, "5 일선");
        setLineSet(line5DataSet, ContextCompat.getColor(this, R.color.line5));
        line10DataSet = new LineDataSet(line10Entries, "10 일선");
        setLineSet(line10DataSet, ContextCompat.getColor(this, R.color.line10));
        line20DataSet = new LineDataSet(line20Entries, "20 일선");
        setLineSet(line20DataSet, ContextCompat.getColor(this, R.color.line20));
        line60DataSet = new LineDataSet(line60Entries, "60 일선");
        setLineSet(line60DataSet, ContextCompat.getColor(this, R.color.line60));
        envelopePlusDataSet = new LineDataSet(envelopePlusEntries, "엔벨로프 20");
        setLineSet(envelopePlusDataSet, ContextCompat.getColor(this, R.color.envelope));
        envelopeMinusDataSet = new LineDataSet(envelopeMinusEntries, "엔벨로프 -20");
        setLineSet(envelopeMinusDataSet, ContextCompat.getColor(this, R.color.envelope));


        lineData = new LineData(line5DataSet, line10DataSet, line20DataSet, line60DataSet);
        candleData = new CandleData(dataSet);
        barData = new BarData(barDataSet);
        combinedData.setData(barData);
        combinedData.setData(candleData);
        combinedData.setData(lineData);
        combinedChart.setData(combinedData);
        combinedChart.zoom(2f, 1f, 160, candleEntries.get(199).getClose(), YAxis.AxisDependency.RIGHT);
        combinedChart.invalidate();


    }

    private void setLineSet(LineDataSet dataSet, int color) {
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setColor(color);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(1f);
    }

    private void setRsiChartData() {

        rsiDataSet = new LineDataSet(rsiEndtries, "RSI");
        setLineSet(rsiDataSet, Color.MAGENTA);
        rsiData = new LineData(rsiDataSet);
        rsiChart.setData(rsiData);
        rsiChart.invalidate();
    }


    private void setRsiData(ArrayList<CandleEntry> array, float guideDay) {
        double upPrice = 0;
        double downPrice = 0;

        for (int i = 0; i < array.size(); i++) {
            if (i >= (int) guideDay) {
                CandleEntry data;
                if (i==guideDay){
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
                    rsiEndtries.add(new Entry(guideDay-1  + rsiEndtries.size(), rsi));
                } else {
                    data =array.get(i);
                    if (data.getOpen()>data.getClose()){
                        upAvg =(upAvg*13)/14;
                        downAvg =((downAvg*13)+data.getBodyRange())/14;
                    } else {
                        upAvg =((upAvg*13)+data.getBodyRange())/14;
                        downAvg =(downAvg*13)/14;
                    }
                //    Log.d("test1223","up avg = "+upAvg);
                    double rs = upAvg / downAvg;
                    float rsi = (float) (100 - (100 / (1 + rs)));
                    rsiEndtries.add(new Entry(guideDay-1  + rsiEndtries.size(), rsi));
                }
            }
        }
        setRsiChartData();
    }

    private void setRsiRealTimeData(CandleEntry data, float guideDay, boolean isNow) {

        if (!isNow) {
            if (data.getOpen()>data.getClose()){
                upAvg =(upAvg*13)/14;
                downAvg =((downAvg*13)+data.getBodyRange())/14;
            } else {
                upAvg =((upAvg*13)+data.getBodyRange())/14;
                downAvg =(downAvg*13)/14;
            }
            //    Log.d("test1223","up avg = "+upAvg);
            double rs = upAvg / downAvg;
            float rsi = (float) (100 - (100 / (1 + rs)));

            rsiEndtries.add(new Entry(guideDay + rsiEndtries.size(), rsi));
        } else {
            double up;
            double down;
            if (data.getOpen()>data.getClose()){
                up =(upAvg*13)/14;
                down =((downAvg*13)+data.getBodyRange())/14;
            } else {
                up =((upAvg*13)+data.getBodyRange())/14;
                down =(downAvg*13)/14;
            }
            //    Log.d("test1223","up avg = "+upAvg);
            double rs = up / down;
            float rsi = (float) (100 - (100 / (1 + rs)));

            //  Log.d("test1234",rsi+",,,"+upAvg+",,,,,"+downAvg+",,,,,,"+rs);
            rsiEndtries.get(rsiEndtries.size() - 1).setY(rsi);
        }
    }
    private void setBarData(List<Candle> array){
        float maxvol =0;
        for (Candle candle :array){
            double volume =candle.getCandleAccTradeVolume();
            float volumeFloat = (float) volume;
            barEntries.add(new BarEntry(barEntries.size(),volumeFloat));
            if (maxvol ==0f){
                maxvol = volumeFloat;
            } else {
                if (volumeFloat>maxvol){
                    maxvol = volumeFloat;
                }
            }
            if (candle.getOpeningPrice()<candle.getTradePrice()){
                barColor.add(ContextCompat.getColor(this,R.color.vol_up));
            } else {
                barColor.add(ContextCompat.getColor(this,R.color.vol_down));
            }
        }
        leftAxis.setAxisMaximum(maxvol*3.2f);
    }

    private void setRealTimeBar(Candle candle,boolean isNow){
        double volume =candle.getCandleAccTradeVolume();
        float volumeFloat = (float) volume;
      if (!isNow){
          barEntries.add(new BarEntry(barEntries.size(),volumeFloat));
          if (candle.getOpeningPrice()<candle.getTradePrice()){
              barColor.add(ContextCompat.getColor(this,R.color.vol_up));
          } else {
              barColor.add(ContextCompat.getColor(this,R.color.vol_down));
          }
      } else {
          barEntries.get(barEntries.size()-1).setY(volumeFloat);
          if (candle.getOpeningPrice()<candle.getTradePrice()){
              barColor.set(barEntries.size()-1, ContextCompat.getColor(this,R.color.vol_up));
          } else {
              barColor.set(barEntries.size()-1, ContextCompat.getColor(this,R.color.vol_down));
          }

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

                    line5Entries.add(new Entry(guideDay + line5Entries.size(), tradePrice / guideDay));
                    break;
                case 10:
                    line10Entries.add(new Entry(guideDay + line10Entries.size(), tradePrice / guideDay));
                    break;
                case 20:
                    line20Entries.add(new Entry(guideDay + line20Entries.size(), tradePrice / guideDay));
                    envelopePlusEntries.add(new Entry(guideDay + line20Entries.size(), ((long) tradePrice / guideDay) * 1.2f));
                    envelopeMinusEntries.add(new Entry(guideDay + line20Entries.size(), ((long) tradePrice / guideDay) * 0.8f));
                    break;
                case 60:
                    line60Entries.add(new Entry(guideDay + line60Entries.size(), tradePrice / guideDay));
                    break;
            }
            if (line5Entries.get(line5Entries.size() - 1).getY() > line10Entries.get(line10Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() < line10Entries.get(line10Entries.size() - 2).getY()) {
                NotificationManager.sendNotification(MainActivity.this, 1, NotificationManager.Channel.MESSAGE, coinName.get(spinner.getSelectedIndex())+"매수신호", "5일 10일선 골크 / 현재가격 : "+(long)(array.get(array.size()-1).getClose()));
            } else if (line5Entries.get(line5Entries.size() - 1).getY() > line20Entries.get(line20Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() < line20Entries.get(line20Entries.size() - 2).getY()) {
                NotificationManager.sendNotification(MainActivity.this, 1, NotificationManager.Channel.MESSAGE, coinName.get(spinner.getSelectedIndex())+"매수신호", "5일 20일선 골크 / 현재가격 : "+(long)(array.get(array.size()-1).getClose()));
            } else if (line5Entries.get(line5Entries.size() - 1).getY() > line60Entries.get(line60Entries.size() - 1).getY() && line5Entries.get(line5Entries.size() - 2).getY() < line60Entries.get(line60Entries.size() - 2).getY()) {
                NotificationManager.sendNotification(MainActivity.this, 1, NotificationManager.Channel.MESSAGE, coinName.get(spinner.getSelectedIndex())+"매수신호", "5일 60일선 골크 / 현재가격 : "+(long)(array.get(array.size()-1).getClose()));
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
        if (isChanged) {
            setNotify();
        } else {
            setCombinedChartData();
        }
        LoadingProgress.dismissDialog();
        presenterBridge.startRealTimeData(1,code);
    }

    private void drawRealTime(List<Candle> candles) {
        for (Candle candle : candles) {
            float openingPrice = (float) candle.getOpeningPrice();
            float tradePrice = (float) candle.getTradePrice();
            float lowPrice = (float) candle.getLowPrice();
            float highPrice = (float) candle.getHighPrice();
            currentPrice.setText(String.valueOf((long) tradePrice) + " 원");
            if (candleEntries.get(candleEntries.size() - 1).getOpen() != openingPrice) {
                candleEntries.add(new CandleEntry(candleEntries.size(), highPrice, lowPrice, openingPrice, tradePrice));
                setRealTimeLineData(candleEntries, 5, false);
                setRealTimeLineData(candleEntries, 10, false);
                setRealTimeLineData(candleEntries, 20, false);
                setRealTimeLineData(candleEntries, 60, false);
                setRealTimeBar(candle,false);
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
                setRealTimeBar(candle,true);
                setRsiRealTimeData(candleEntries.get(lastIndex), 14, true);
            }
            setNotify();
        }
    }

    private void setNotify() {
        //------------combine------------
        candleData.notifyDataChanged();
        dataSet.notifyDataSetChanged();
        line5DataSet.notifyDataSetChanged();
        line10DataSet.notifyDataSetChanged();
        line20DataSet.notifyDataSetChanged();
        line60DataSet.notifyDataSetChanged();
        envelopeMinusDataSet.notifyDataSetChanged();
        envelopePlusDataSet.notifyDataSetChanged();
        barDataSet.notifyDataSetChanged();
        lineData.notifyDataChanged();
        combinedData.notifyDataChanged();
        combinedChart.notifyDataSetChanged();
        combinedChart.invalidate();
        //-----------rsi-------------
        rsiDataSet.notifyDataSetChanged();
        rsiData.notifyDataChanged();
        rsiChart.notifyDataSetChanged();
        rsiChart.invalidate();
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

    @Override
    protected void onResume() {
        super.onResume();
        presenterBridge.startFirstData(time,code);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sconn);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenterBridge.stopRealData();
    }


    @Override
    public void setPresenterBridge(MainTask.PresenterBridge presenterBridge) {
        this.presenterBridge = presenterBridge;
    }

    @Override
    public void getFirstResult(List<Candle> candleEntries) {
        allDataClear();
        drawFirstData(candleEntries);
    }

    @Override
    public void getRealTimeResult(List<Candle> candleEntries) {
        drawRealTime(candleEntries);
    }
}