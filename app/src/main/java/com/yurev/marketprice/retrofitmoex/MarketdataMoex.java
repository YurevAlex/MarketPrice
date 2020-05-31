package com.yurev.marketprice.retrofitmoex;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MarketdataMoex {

    @SerializedName("SECID")
    @Expose
    private String secId;
    @SerializedName("OPEN")
    @Expose
    private Double open;
    @SerializedName("LOW")
    @Expose
    private Double low;
    @SerializedName("HIGH")
    @Expose
    private Double high;
    @SerializedName("LAST")
    @Expose
    private Double last;
    @SerializedName("LASTTOPREVPRICE")
    @Expose
    private Double lastToPrevPrice;
    @SerializedName("CHANGE")
    @Expose
    private Double change;
    @SerializedName("VALTODAY_RUR")
    @Expose
    private Long valToDayRur;
    @SerializedName("VALTODAY_USD")
    @Expose
    private Long valToDayUsd;
    /*@SerializedName("SYSTIME")
    @Expose
    private Date systime;*/

    public String getSecId() {
        return secId;
    }
    public void setSecId(String secId) {
        this.secId = secId;
    }

    public Double getOpen() {
        return open;
    }
    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getLow() {
        return low;
    }
    public void setLow(Double low) {
        this.low = low;
    }

    public Double getHigh() {
        return high;
    }
    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLast() {
        return last;
    }
    public void setLast(Double last) {
        this.last = last;
    }

    public Double getLastToPrevPrice() {
        return lastToPrevPrice;
    }
    public void setLastToPrevPrice(Double lastToPrevPrice) {
        this.lastToPrevPrice = lastToPrevPrice;
    }

    public Double getChange() {
        return change;
    }
    public void setChange(Double Change) {
        this.change = Change;
    }

    public Long getValToDayRur() {
        return valToDayRur;
    }
    public void setValToDayRur(Long ValToDayRur) {
        this.valToDayRur = ValToDayRur;
    }

    public Long getValToDayUsd() {
        return valToDayUsd;
    }
    public void setValToDayUsd(Long ValToDayUsd) {
        this.valToDayUsd = ValToDayUsd;
    }
/*
    public Date getSysTime() {
        return systime;
    }

    public void setSysTime(Date SysTime) {
        this.systime = SysTime;
    }*/

}