package com.edge.coin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataModel {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("candleDateTime")
    @Expose
    private String candleDateTime;
    @SerializedName("candleDateTimeKst")
    @Expose
    private String candleDateTimeKst;
    @SerializedName("openingPrice")
    @Expose
    private double openingPrice;
    @SerializedName("highPrice")
    @Expose
    private double highPrice;
    @SerializedName("lowPrice")
    @Expose
    private double lowPrice;
    @SerializedName("tradePrice")
    @Expose
    private double tradePrice;
    @SerializedName("candleAccTradeVolume")
    @Expose
    private double candleAccTradeVolume;
    @SerializedName("candleAccTradePrice")
    @Expose
    private double candleAccTradePrice;
    @SerializedName("timestamp")
    @Expose
    private long timestamp;
    @SerializedName("unit")
    @Expose
    private Integer unit;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCandleDateTime() {
        return candleDateTime;
    }

    public void setCandleDateTime(String candleDateTime) {
        this.candleDateTime = candleDateTime;
    }

    public String getCandleDateTimeKst() {
        return candleDateTimeKst;
    }

    public void setCandleDateTimeKst(String candleDateTimeKst) {
        this.candleDateTimeKst = candleDateTimeKst;
    }

    public float getOpeningPrice() {
        return (float) openingPrice;
    }

    public void setOpeningPrice(double openingPrice) {
        this.openingPrice = openingPrice;
    }

    public float getHighPrice() {
        return (float) highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public float getLowPrice() {
        return (float) lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public float getTradePrice() {
        return (float) tradePrice;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public Double getCandleAccTradeVolume() {
        return candleAccTradeVolume;
    }

    public void setCandleAccTradeVolume(Double candleAccTradeVolume) {
        this.candleAccTradeVolume = candleAccTradeVolume;
    }

    public Double getCandleAccTradePrice() {
        return candleAccTradePrice;
    }

    public void setCandleAccTradePrice(Double candleAccTradePrice) {
        this.candleAccTradePrice = candleAccTradePrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

}
