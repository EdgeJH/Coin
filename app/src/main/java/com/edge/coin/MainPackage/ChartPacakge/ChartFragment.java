package com.edge.coin.MainPackage.ChartPacakge;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edge.coin.MainPackage.MainActivity;
import com.edge.coin.R;
import com.edge.coin.UpbitPackage.TradeCoin;
import com.edge.coin.Utils.Candle;
import com.edge.coin.Utils.ChartValueFormatter;
import com.edge.coin.Utils.CoupleChartGestureListener;
import com.edge.coin.Utils.LoadingProgress;
import com.edge.coin.Utils.SharedPreference;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 2018-03-22.
 */

public class ChartFragment extends android.support.v4.app.Fragment implements ChartTask.View, View.OnClickListener {
    boolean isCreate = false;
    boolean isEnvel = false;
    boolean isKrw=true;
    RelativeLayout krwMarket,btcMarket;
    ArrayList<CandleEntry> candleEntries = new ArrayList<>();
    ArrayList<Entry> line5Entries = new ArrayList<>();
    ArrayList<Entry> line10Entries = new ArrayList<>();
    ArrayList<Entry> line20Entries = new ArrayList<>();
    ArrayList<Entry> line60Entries = new ArrayList<>();
    ArrayList<Entry> envelopePlusEntries = new ArrayList<>();
    ArrayList<Entry> envelopeMinusEntries = new ArrayList<>();
    ArrayList<Entry> rsiEndtries = new ArrayList<>();
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<Entry> obvEntries = new ArrayList<>();
    List<Integer> barColor = new ArrayList<>();
    CandleDataSet dataSet;
    CombinedData combinedData = new CombinedData();
    CombinedChart combinedChart;
    CandleData candleData;

    CombinedChart obvChart;
    CombinedData obvData = new CombinedData();
    LineDataSet obvLineSet;
    LineData obvLineData;

    BarDataSet barDataSet;
    BarData barData;
    LineDataSet line60DataSet;
    LineDataSet line10DataSet;
    LineDataSet line20DataSet;
    LineData lineData;
    LineDataSet line5DataSet;
    LineDataSet envelopePlusDataSet;
    LineDataSet envelopeMinusDataSet;
    LineData rsiData = new LineData();
    LineDataSet rsiDataSet;
    TextView currentPrice;
    LineChart rsiChart;

    String code;


    ArrayList<TradeCoin> coinList;
    ArrayList<TradeCoin> krwCoinList = new ArrayList<>();
    ArrayList<TradeCoin> btcCoinList = new ArrayList<>();
    ArrayList<String> coinName = new ArrayList<>();
    NiceSpinner spinner;
    boolean isChanged = false;
    Handler handler = new Handler();
    TabLayout tabLayout;
    int time = 1;
    double upAvg = 0;
    double downAvg = 0;
    ChartPresenter presenter;
    ChartTask.PresenterBridge presenterBridge;
    YAxis rsiLeftAxis, rsiRightAxis, leftAxis, rightAxis, obvLeftAxis, obvRightAxis;
    Legend combineLegend, rsiLegend, obvLegend;
    XAxis xAxis, rsiXAxis, obvXAxis;
    boolean isDarkTheme = false;
    SharedPreference sharedPreference = new SharedPreference();

    DecimalFormat df = new DecimalFormat("#.#########");
    MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        presenter = new ChartPresenter(this);
        setCoinList();
        code = krwCoinList.get(0).getCode();

        isDarkTheme = sharedPreference.getValue(activity, "theme", false);
        View view = inflater.inflate(R.layout.frag_chart, container, false);
        initView(view);
        setCombinedChart();
        setSpinner();
        setRsiChart();
        setObvChart();
        setValueFormat();

        setTabLayout();
        return view;
    }

    private void setKrwCoinName() {
        coinName.clear();
        for (int i = 0; i < krwCoinList.size(); i++) {
            coinName.add(krwCoinList.get(i).getKoreanName());
        }
    }
    private void setBtcCoinName() {
        coinName.clear();
        for (int i = 0; i < btcCoinList.size(); i++) {
            coinName.add(btcCoinList.get(i).getKoreanName());
        }
    }

    private void setCoinList() {
        coinList = sharedPreference.getCoinList(activity, "coinList");
        for (TradeCoin tradeCoin : coinList) {
            switch (tradeCoin.getQuoteCurrencyCode()) {
                case "KRW":
                    if (tradeCoin.getMarketState().equals("ACTIVE") && tradeCoin.getExchange().equals("UPBIT")) {
                        krwCoinList.add(tradeCoin);
                    }
                    break;
                case "BTC":
                    if (tradeCoin.getMarketState().equals("ACTIVE") && tradeCoin.getExchange().equals("UPBIT")) {
                        btcCoinList.add(tradeCoin);
                    }
                    break;
                case "ETH":
                    break;
                case "USDT":
                    break;
            }
        }
    }

    private void initView(View rootView) {
        currentPrice = rootView.findViewById(R.id.price);
        combinedChart = rootView.findViewById(R.id.candle);
        spinner = rootView.findViewById(R.id.spinner);
        rsiChart = rootView.findViewById(R.id.rsi);
        tabLayout = rootView.findViewById(R.id.tab_layout);
        obvChart = rootView.findViewById(R.id.obv);
        krwMarket = rootView.findViewById(R.id.krw_market);
        btcMarket= rootView.findViewById(R.id.btc_market);
        krwMarket.setSelected(true);
        krwMarket.setOnClickListener(this);
        btcMarket.setOnClickListener(this);
    }


    private void setTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LoadingProgress.showDialog(activity, true);
                switch (tab.getPosition()) {
                    case 0:
                        time = 1;
                        isEnvel = false;
                        break;
                    case 1:
                        time = 5;
                        isEnvel = false;
                        break;
                    case 2:
                        time = 15;
                        isEnvel = false;
                        break;
                    case 3:
                        time = 30;
                        isEnvel = false;
                        break;
                    case 4:
                        time = 60;
                        isEnvel = false;
                        break;
                    case 5:
                        time = 240;
                        isEnvel = true;
                        break;

                }
                setValueFormat();
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
        if (isKrw){
            setKrwCoinName();
        } else {
            setBtcCoinName();
        }

        spinner.attachDataSource(coinName);
        spinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LoadingProgress.showDialog(activity, true);
                if (isKrw){
                    code = krwCoinList.get(position).getCode();
                } else {
                    code = btcCoinList.get(position).getCode();
                }
                getData();
                isChanged = true;
            }
        });
    }

    private void getData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                presenterBridge.startFirstData(time, code);
            }
        }, 1000);
    }

    private void setObvChart() {
        obvChart.getDescription().setEnabled(false);
        obvLegend = obvChart.getLegend();
        setLegend(obvLegend);
        obvXAxis = obvChart.getXAxis();
        obvXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        obvXAxis.setDrawGridLines(false);
        obvXAxis.setSpaceMax(50f);
        obvXAxis.setAxisMinimum(0f);
        obvLeftAxis = obvChart.getAxisLeft();
        obvLeftAxis.setEnabled(false);
        obvRightAxis = obvChart.getAxisRight();
        setYAixs(obvRightAxis);

    }

    private void setRsiChart() {
        rsiChart.getDescription().setEnabled(false);
        rsiLegend = rsiChart.getLegend();
        setLegend(rsiLegend);
        rsiXAxis = rsiChart.getXAxis();
        rsiXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        rsiXAxis.setDrawGridLines(false);
        rsiXAxis.setSpaceMax(50f);
        rsiXAxis.setAxisMinimum(0f);
        rsiLeftAxis = rsiChart.getAxisLeft();
        rsiLeftAxis.setEnabled(false);
        rsiRightAxis = rsiChart.getAxisRight();
        setYAixs(rsiRightAxis);
        rsiRightAxis.addLimitLine(new LimitLine(70, ""));
    }

    private void setValueFormat() {
        xAxis.setValueFormatter(new ChartValueFormatter(time));
        rsiXAxis.setValueFormatter(new ChartValueFormatter(time));
        obvXAxis.setValueFormatter(new ChartValueFormatter(time));
    }

    private void setCombinedChart() {
        combinedChart.getDescription().setEnabled(false);
        combinedChart.setPinchZoom(true);
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BAR});
        xAxis = combinedChart.getXAxis();
        setXAxis(xAxis);
        setCombineLegend();
        leftAxis = combinedChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setSpaceBottom(0f);
        leftAxis.setInverted(false);
        rightAxis = combinedChart.getAxisRight();
        setYAixs(rightAxis);

        combinedChart.setOnChartGestureListener(new CoupleChartGestureListener(combinedChart, new Chart[]{rsiChart, obvChart}));
        rsiChart.setOnChartGestureListener(new CoupleChartGestureListener(rsiChart, new Chart[]{combinedChart, obvChart}));
        obvChart.setOnChartGestureListener(new CoupleChartGestureListener(obvChart, new Chart[]{combinedChart, rsiChart}));
    }

    private void setCombineLegend() {
        combineLegend = combinedChart.getLegend();
        LegendEntry legendEntry = new LegendEntry("5일선", Legend.LegendForm.LINE, 10f, 2f, null, ContextCompat.getColor(activity, R.color.line5));
        LegendEntry legendEntry2 = new LegendEntry("10일선", Legend.LegendForm.LINE, 10f, 2f, null, ContextCompat.getColor(activity, R.color.line10));
        LegendEntry legendEntry3 = new LegendEntry("20일선", Legend.LegendForm.LINE, 10f, 2f, null, ContextCompat.getColor(activity, R.color.line20));
        LegendEntry legendEntry4 = new LegendEntry("60일선", Legend.LegendForm.LINE, 10f, 2f, null, ContextCompat.getColor(activity, R.color.line60));
        LegendEntry legendEntry5 = new LegendEntry("엔벨+", Legend.LegendForm.LINE, 10f, 2f, null, ContextCompat.getColor(activity, R.color.envelope));
        LegendEntry legendEntry6 = new LegendEntry("엔벨-", Legend.LegendForm.LINE, 10f, 2f, null, ContextCompat.getColor(activity, R.color.envelope));
        LegendEntry[] entries;
        if (isEnvel) {
            entries = new LegendEntry[]{legendEntry, legendEntry2, legendEntry3, legendEntry4, legendEntry5, legendEntry6};
        } else {
            entries = new LegendEntry[]{legendEntry, legendEntry2, legendEntry3, legendEntry4};
        }
        combineLegend.setCustom(entries);
        setLegend(combineLegend);
    }

    private void setXAxis(XAxis xAxis) {
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setSpaceMax(50f);
    }

    private void setLegend(Legend legend) {
        legend.setEnabled(true);
        legend.setDrawInside(true);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
    }

    private void setYAixs(YAxis yAixs) {
        yAixs.setEnabled(true);
        yAixs.setLabelCount(5);
        yAixs.setDrawGridLines(true);
        yAixs.setMaxWidth(60f);
        yAixs.setMinWidth(60f);
        yAixs.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
    }

    private void setChartTheme() {
        if (isDarkTheme) {
            rightAxis.setTextColor(Color.LTGRAY);
            rightAxis.setAxisLineColor(Color.LTGRAY);
            rightAxis.setGridColor(Color.parseColor("#443446"));
            leftAxis.setGridColor(Color.parseColor("#443446"));
            rsiRightAxis.setTextColor(Color.LTGRAY);
            obvRightAxis.setTextColor(Color.LTGRAY);

            combineLegend.setTextColor(Color.LTGRAY);
            rsiLegend.setTextColor(Color.LTGRAY);
            rsiXAxis.setTextColor(Color.LTGRAY);
            rsiXAxis.setAxisLineColor(Color.TRANSPARENT);

            obvLegend.setTextColor(Color.LTGRAY);
            obvXAxis.setTextColor(Color.LTGRAY);
            obvXAxis.setAxisLineColor(Color.TRANSPARENT);

            xAxis.setTextColor(Color.LTGRAY);
            xAxis.setGridColor(Color.parseColor("#443446"));
            xAxis.setAxisLineColor(Color.TRANSPARENT);
            dataSet.setIncreasingColor(ContextCompat.getColor(activity, R.color.increase_dark));
            dataSet.setDecreasingColor(ContextCompat.getColor(activity, R.color.decrease_dark));
        } else {
            rightAxis.setTextColor(Color.GRAY);
            rightAxis.setAxisLineColor(Color.GRAY);
            rightAxis.setGridColor(Color.parseColor("#aaaaaa"));
            leftAxis.setGridColor(Color.parseColor("#aaaaaa"));
            rsiRightAxis.setTextColor(Color.GRAY);
            obvRightAxis.setTextColor(Color.GRAY);
            combineLegend.setTextColor(Color.GRAY);
            rsiLegend.setTextColor(Color.GRAY);
            rsiXAxis.setTextColor(Color.GRAY);
            rsiXAxis.setAxisLineColor(Color.WHITE);

            obvLegend.setTextColor(Color.GRAY);
            obvXAxis.setTextColor(Color.GRAY);
            obvXAxis.setAxisLineColor(Color.WHITE);
            xAxis.setTextColor(Color.GRAY);
            xAxis.setGridColor(Color.parseColor("#aaaaaa"));
            xAxis.setAxisLineColor(Color.WHITE);
            dataSet.setIncreasingColor(ContextCompat.getColor(activity, R.color.increase_light));
            dataSet.setDecreasingColor(ContextCompat.getColor(activity, R.color.decrease_light));
        }
    }

    private void setCombinedChartData() {
        if (combinedChart.getData() != null) {
            combinedChart.clearValues();
            combinedChart.removeAllViews();

        }
        dataSet = new CandleDataSet(candleEntries, "캔들");
        dataSet.setDrawIcons(false);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setBarSpace(0.2f);
        dataSet.setShadowWidth(0.7f);
        dataSet.setShadowColorSameAsCandle(true);
        dataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        dataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        dataSet.setNeutralColor(Color.DKGRAY);
        dataSet.setDrawValues(false);

        barDataSet = new BarDataSet(barEntries, "거래량");
        barDataSet.setDrawValues(false);
        barDataSet.setColors(barColor);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        line5DataSet = new LineDataSet(line5Entries, "5 일선");
        setLineSet(line5DataSet, ContextCompat.getColor(activity, R.color.line5));
        line10DataSet = new LineDataSet(line10Entries, "10 일선");
        setLineSet(line10DataSet, ContextCompat.getColor(activity, R.color.line10));
        line20DataSet = new LineDataSet(line20Entries, "20 일선");
        setLineSet(line20DataSet, ContextCompat.getColor(activity, R.color.line20));
        line60DataSet = new LineDataSet(line60Entries, "60 일선");
        setLineSet(line60DataSet, ContextCompat.getColor(activity, R.color.line60));
        envelopePlusDataSet = new LineDataSet(envelopePlusEntries, "엔벨로프 20");
        setLineSet(envelopePlusDataSet, ContextCompat.getColor(activity, R.color.envelope));
        envelopeMinusDataSet = new LineDataSet(envelopeMinusEntries, "엔벨로프 -20");
        setLineSet(envelopeMinusDataSet, ContextCompat.getColor(activity, R.color.envelope));

        if (lineData != null) {
            lineData.clearValues();
        }
        candleData = new CandleData(dataSet);
        barData = new BarData(barDataSet);
        combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(candleData);
        if (candleEntries.size() > 62) {
            combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BAR});

            if (!isCreate) {
                combinedChart.zoom(1.5f, 1f, candleEntries.size() - 20, candleEntries.get(candleEntries.size() - 1).getClose(), YAxis.AxisDependency.RIGHT);
                isCreate = true;
            }
            if (isEnvel) {
                lineData = new LineData(line5DataSet, line10DataSet, line20DataSet, line60DataSet, envelopePlusDataSet, envelopeMinusDataSet);
            } else {
                lineData = new LineData(line5DataSet, line10DataSet, line20DataSet, line60DataSet);
            }
            combinedData.setData(lineData);
        } else if (candleEntries.size() > 22 && candleEntries.size() <= 60) {
            combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BAR});
            lineData = new LineData(line5DataSet, line10DataSet, line20DataSet);
            combinedData.setData(lineData);
        } else if (candleEntries.size() > 12 && candleEntries.size() <= 20) {
            combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BAR});
            lineData = new LineData(line5DataSet, line10DataSet);
            combinedData.setData(lineData);
        } else if (candleEntries.size() > 7 && candleEntries.size() <= 10) {
            combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.BAR});
            lineData = new LineData(line5DataSet);
            combinedData.setData(lineData);
        } else {
            combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.BAR});
        }
        combinedChart.setData(combinedData);
        setChartTheme();
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

    private void setObvLineData() {
        obvLineSet = new LineDataSet(obvEntries, "OBV");
        setLineSet(obvLineSet, ContextCompat.getColor(activity, R.color.envelope));
        obvLineData = new LineData(obvLineSet);
        obvData.setData(obvLineData);
        obvChart.setData(obvData);
        obvChart.invalidate();
    }

    private void setObvData(List<Candle> candles) {

        for (int i = 0; i < candles.size(); i++) {
            if (i > 0) {
                double curVol = candles.get(i).getCandleAccTradeVolume();
                float result = candles.get(i).getTradePrice() - candles.get(i - 1).getTradePrice();
                if (result > 0) {
                    if (i == 1) {
                        obvEntries.add(new Entry(obvEntries.size() + 1, (float) curVol));
                    } else {

                        obvEntries.add(new Entry(obvEntries.size() + 1, obvEntries.get(i - 2).getY() + (float) curVol));
                    }
                } else {
                    if (i == 1) {
                        obvEntries.add(new Entry(obvEntries.size() + 1, -(float) curVol));
                    } else {

                        obvEntries.add(new Entry(obvEntries.size() + 1, obvEntries.get(i - 2).getY() - (float) curVol));
                    }
                }
            }
        }
        setObvLineData();
    }

    private void setRealTimeObv(Candle candle, boolean isNow) {
        double curVol = candle.getCandleAccTradeVolume();
        float result = candle.getTradePrice() - candleEntries.get(candleEntries.size() - 1).getClose();
        if (!isNow) {
            if (result > 0) {
                obvEntries.add(new Entry(obvEntries.size() + 1, obvEntries.get(obvEntries.size() - 2).getY() + (float) curVol));
            } else {
                obvEntries.add(new Entry(obvEntries.size() + 1, obvEntries.get(obvEntries.size() - 2).getY() - (float) curVol));
            }
        } else {
            if (result > 0) {
                obvEntries.get(obvEntries.size() - 1).setY(obvEntries.get(obvEntries.size() - 2).getY() + (float) curVol);
            } else {
                obvEntries.get(obvEntries.size() - 1).setY(obvEntries.get(obvEntries.size() - 2).getY() - (float) curVol);
            }
        }
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
        if (array.size() > guideDay) {
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
        setRsiChartData();
    }

    private void setRsiRealTimeData(CandleEntry data, float guideDay, boolean isNow) {

        if (rsiEndtries.size() > 0) {
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
                double rs = up / down;
                float rsi = (float) (100 - (100 / (1 + rs)));
                rsiEndtries.get(rsiEndtries.size() - 1).setY(rsi);
            }
        }
    }

    private void setBarData(List<Candle> array) {
        float maxvol = 0;
        for (int i = 0; i < array.size(); i++) {
            Candle candle = array.get(array.size() - 1 - i);
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
            if (candle.getOpeningPrice() <= candle.getTradePrice()) {
                barColor.add(ContextCompat.getColor(activity, R.color.vol_up));
            } else {
                barColor.add(ContextCompat.getColor(activity, R.color.vol_down));
            }
        }
        leftAxis.setAxisMaximum(maxvol * 3.2f);
    }

    private void setRealTimeBar(Candle candle, boolean isNow) {
        double volume = candle.getCandleAccTradeVolume();
        float volumeFloat = (float) volume;
        if (!isNow) {
            barEntries.add(new BarEntry(barEntries.size(), volumeFloat));
            if (candle.getOpeningPrice() <= candle.getTradePrice()) {
                barColor.add(ContextCompat.getColor(activity, R.color.vol_up));
            } else {
                barColor.add(ContextCompat.getColor(activity, R.color.vol_down));
            }
        } else {
            barEntries.get(barEntries.size() - 1).setY(volumeFloat);
            if (candle.getOpeningPrice() <= candle.getTradePrice()) {
                barColor.set(barEntries.size() - 1, ContextCompat.getColor(activity, R.color.vol_up));
            } else {
                barColor.set(barEntries.size() - 1, ContextCompat.getColor(activity, R.color.vol_down));
            }

        }
    }

    private void setLineData(ArrayList<CandleEntry> array, float guideDay) {
        if (array.size() > guideDay) {
            for (int i = 0; i < array.size(); i++) {
                if (i >= (int) guideDay - 1) {
                    float tradePrice = 0;
                    for (int j = 0; j < guideDay; j++) {
                        tradePrice +=  array.get(i - j).getClose();
                    }
                    int day = (int) guideDay;
                    switch (day) {
                        case 5:
                            line5Entries.add(new Entry(guideDay - 1 + line5Entries.size(),  tradePrice / guideDay));
                            break;
                        case 10:
                            line10Entries.add(new Entry(guideDay - 1 + line10Entries.size(),  tradePrice / guideDay));
                            break;
                        case 20:

                            line20Entries.add(new Entry(guideDay - 1 + line20Entries.size(),  tradePrice / guideDay));
                            envelopePlusEntries.add(new Entry(guideDay - 1 + line20Entries.size(), (tradePrice / guideDay) * 1.2f));
                            envelopeMinusEntries.add(new Entry(guideDay - 1 + line20Entries.size(), (tradePrice / guideDay) * 0.8f));
                            break;
                        case 60:

                            line60Entries.add(new Entry(guideDay - 1 + line60Entries.size(),  tradePrice / guideDay));
                            break;
                    }
                }
            }
        }
    }

    private void setRealTimeLineData(List<CandleEntry> array, float guideDay, boolean isNow) {
        float tradePrice = 0f;
        if (array.size() > guideDay) {

            for (int i = 0; i < guideDay; i++) {
                if ((array.size() - 1 - i) >= 0) {
                    CandleEntry data = array.get(array.size() - i - 1);
                    tradePrice += data.getClose();
                }
            }
            if (!isNow) {
                switch ((int) guideDay) {
                    case 5:
                        line5Entries.add(new Entry(guideDay + line5Entries.size(), tradePrice / guideDay));
                        break;
                    case 10:
                        line10Entries.add(new Entry(guideDay + line10Entries.size(), tradePrice / guideDay));
                        break;
                    case 20:
                        line20Entries.add(new Entry(guideDay + line20Entries.size(), tradePrice / guideDay));
                        envelopePlusEntries.add(new Entry(guideDay + line20Entries.size(),  (tradePrice / guideDay) * 1.2f));
                        envelopeMinusEntries.add(new Entry(guideDay + line20Entries.size(), ( tradePrice / guideDay) * 0.8f));
                        break;
                    case 60:
                        line60Entries.add(new Entry(guideDay + line60Entries.size(), tradePrice / guideDay));
                        break;
                }

            } else {

                switch ((int) guideDay) {
                    case 5:
                        line5Entries.get(line5Entries.size() - 1).setY(tradePrice / guideDay);
                        break;
                    case 10:
                        line10Entries.get(line10Entries.size() - 1).setY(tradePrice / guideDay);
                        break;
                    case 20:
                        line20Entries.get(line20Entries.size() - 1).setY(tradePrice / guideDay);
                        envelopePlusEntries.get(envelopePlusEntries.size() - 1).setY(tradePrice / guideDay * 1.2f);
                        envelopeMinusEntries.get(envelopeMinusEntries.size() - 1).setY( tradePrice / guideDay * 0.8f);
                        break;
                    case 60:
                        line60Entries.get(line60Entries.size() - 1).setY(tradePrice / guideDay);
                        break;
                }
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
        setObvData(candles);
        setLineData(candleEntries, 5);
        setLineData(candleEntries, 10);
        setLineData(candleEntries, 20);
        setLineData(candleEntries, 60);
        setRsiData(candleEntries, 14);

        setCombinedChartData();
        setCombineLegend();
        LoadingProgress.dismissDialog();
        presenterBridge.startRealTimeData(1, code);
    }

    private void drawRealTime(List<Candle> candles) {
        for (Candle candle : candles) {
            float openingPrice = (float) candle.getOpeningPrice();
            float tradePrice = (float) candle.getTradePrice();
            float lowPrice = (float) candle.getLowPrice();
            float highPrice = (float) candle.getHighPrice();
            if (isKrw){
                currentPrice.setText(String.valueOf((long) tradePrice) + " 원");
            } else {
                currentPrice.setText(String.valueOf(df.format((double)tradePrice)) + " BTC");
            }
            if (candleEntries.get(candleEntries.size() - 1).getOpen() != openingPrice) {
                candleEntries.add(new CandleEntry(candleEntries.size(), highPrice, lowPrice, openingPrice, tradePrice));
                setRealTimeLineData(candleEntries, 5, false);
                setRealTimeLineData(candleEntries, 10, false);
                setRealTimeLineData(candleEntries, 20, false);
                setRealTimeLineData(candleEntries, 60, false);
                setRealTimeObv(candle, false);
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
                setRealTimeObv(candle, true);
                setRsiRealTimeData(candleEntries.get(lastIndex), 14, true);
            }
            setNotify();
        }
    }

    private void setNotify() {
        //------------combine------------
        combinedData.notifyDataChanged();
        dataSet.notifyDataSetChanged();
        line5DataSet.notifyDataSetChanged();
        line10DataSet.notifyDataSetChanged();
        line20DataSet.notifyDataSetChanged();
        line60DataSet.notifyDataSetChanged();
        envelopeMinusDataSet.notifyDataSetChanged();
        envelopePlusDataSet.notifyDataSetChanged();
        barDataSet.notifyDataSetChanged();
        combinedChart.notifyDataSetChanged();
        combinedChart.invalidate();
        obvLineSet.notifyDataSetChanged();
        obvData.notifyDataChanged();
//        -----------rsi-------------
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
        obvEntries.clear();
        envelopeMinusEntries.clear();
        envelopePlusEntries.clear();
        rsiEndtries.clear();
        //  dataSetClear();
    }

    private void dataSetClear() {
        if (line60DataSet != null)
            line60DataSet.clear();
        if (line5DataSet != null)
            line5DataSet.clear();
        if (line10DataSet != null)
            line10DataSet.clear();
        if (line20DataSet != null)
            line20DataSet.clear();
        if (obvLineSet != null)
            obvLineSet.clear();
        if (rsiDataSet != null)
            rsiDataSet.clear();
        if (dataSet != null)
            dataSet.clear();
        if (barDataSet != null)
            barDataSet.clear();
        if (envelopeMinusDataSet != null)
            envelopeMinusDataSet.clear();
        if (envelopePlusDataSet != null)
            envelopePlusDataSet.clear();
        if (dataSet != null)
            dataSet.clear();

    }

    @Override
    public void onResume() {
        super.onResume();
        presenterBridge.startFirstData(time, code);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStop() {
        super.onStop();
        presenterBridge.stopRealData();
    }


    @Override
    public void setPresenterBridge(ChartTask.PresenterBridge presenterBridge) {
        this.presenterBridge = presenterBridge;
    }

    @Override
    public void getFirstResult(List<Candle> candleEntries) {
        allDataClear();
        if (candleEntries != null) {
            drawFirstData(candleEntries);
        }
    }

    @Override
    public void getRealTimeResult(List<Candle> candleEntries) {
        drawRealTime(candleEntries);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.krw_market:
                isKrw= true;
                btcMarket.setSelected(false);
                krwMarket.setSelected(true);
                setSpinner();
                changeMarket();
                break;
            case R.id.btc_market:
                isKrw= false;
                btcMarket.setSelected(true);
                krwMarket.setSelected(false);
                setSpinner();
                changeMarket();
                break;
        }
    }

    private void  changeMarket(){
        LoadingProgress.showDialog(activity, true);
        if (isKrw){
            code = krwCoinList.get(0).getCode();
        } else {
            code = btcCoinList.get(0).getCode();
        }
        getData();
        isChanged = true;
    }
}
