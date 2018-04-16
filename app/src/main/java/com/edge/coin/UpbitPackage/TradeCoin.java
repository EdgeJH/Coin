package com.edge.coin.UpbitPackage;

public class  TradeCoin {

    private String code;

    private String koreanName;

    private String englishName;

    private String pair;

    private String baseCurrencyCode;

    private String quoteCurrencyCode;

    private String exchange;

    private String tradeStatus;

    private String marketState;

    private String marketStateForIOS;

    private Boolean isTradingSuspended;

    private Integer baseCurrencyDecimalPlace;

    private Integer quoteCurrencyDecimalPlace;

    private long timestamp;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public void setKoreanName(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public String getQuoteCurrencyCode() {
        return quoteCurrencyCode;
    }

    public void setQuoteCurrencyCode(String quoteCurrencyCode) {
        this.quoteCurrencyCode = quoteCurrencyCode;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getMarketState() {
        return marketState;
    }

    public void setMarketState(String marketState) {
        this.marketState = marketState;
    }

    public String getMarketStateForIOS() {
        return marketStateForIOS;
    }

    public void setMarketStateForIOS(String marketStateForIOS) {
        this.marketStateForIOS = marketStateForIOS;
    }

    public Boolean getIsTradingSuspended() {
        return isTradingSuspended;
    }

    public void setIsTradingSuspended(Boolean isTradingSuspended) {
        this.isTradingSuspended = isTradingSuspended;
    }

    public Integer getBaseCurrencyDecimalPlace() {
        return baseCurrencyDecimalPlace;
    }

    public void setBaseCurrencyDecimalPlace(Integer baseCurrencyDecimalPlace) {
        this.baseCurrencyDecimalPlace = baseCurrencyDecimalPlace;
    }

    public Integer getQuoteCurrencyDecimalPlace() {
        return quoteCurrencyDecimalPlace;
    }

    public void setQuoteCurrencyDecimalPlace(Integer quoteCurrencyDecimalPlace) {
        this.quoteCurrencyDecimalPlace = quoteCurrencyDecimalPlace;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

}