package com.yurev.marketprice.quotation;


import com.yurev.marketprice.retrofitmoex.MarketdataMoex;
import com.yurev.marketprice.retrofitmoex.SecurityMoex;

import java.util.Objects;

public class Quotation {
    private String secId;
    private String shortName;
    private String secName;
    private Double open;
    private Double high;
    private Double low;
    private Double last;
    private Double lastToPrevPrice;
    private Double change;
    private Long valToDay;
   // private Date sysTime;

    private static final String KEY_LAST = "lastPrice";
    private static final String KEY_LAST_TO_PREV_PRICE = "lastToPrevPrice";

    public Quotation(SecurityMoex s, MarketdataMoex m){
        secId = s.getSecId();
        shortName = s.getShortName();
        secName = s.getSecName();
        open = m.getOpen();
        high= m.getHigh();
        low = m.getLow();
        last = m.getLast();
        lastToPrevPrice = m.getLastToPrevPrice();
        change = m.getChange();


        if(m.getValToDayRur() == null) valToDay = m.getValToDayUsd();
        else valToDay = m.getValToDayRur();
       // sysTime = m.getSysTime();
    }



    public Quotation(){
         /*secId = "Error!";
         shortName = "";
         secName = "";
         open = 0.0;
         high = 0.0;
         low = 0.0;
         last = 0.0;
         lastToPrevPrice = 0.0;
         change = 0.0;
         valToDay = 0L;*/
    }


    public String getSecId() {
        if (secId == null) return "---";
        else return secId;
    }
    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getShortName() {
        if (shortName == null) return "---";
        else return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;

    }

    public String getSecName() {
        if (secName == null) return "---";
        else return secName;
    }
    public void setSecName(String secName) {
        this.secName = secName;
    }

    public Double getOpen() {
        if (open == null) return 0.0;
        else return open;
    }
    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        if (high == null) return 0.0;
        else return high;
    }
    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        if (low == null) return 0.0;
        else return low;
    }
    public void setLow(Double low) {
        this.low = low;
    }

    public Double getLast() {
        if (last == null) return 0.0;
        else              return last;
    }
    public void setLast(Double last) {
        this.last = last;
    }

    public Double getLastToPrevPrice() {
        if (lastToPrevPrice == null) return 0.0;
        else return lastToPrevPrice;
    }
    public void setLastToPrevPrice(Double lastToPrevPrice) {
        this.lastToPrevPrice = lastToPrevPrice;
    }

    public Double getChange() {
        if (change == null) return 0.0;
        else return change;

    }
    public void setChange(Double change) {
        this.change = change;
    }

    public Long getValToDay() {
        if (valToDay == null) return 0L;
        else return valToDay;
    }
    public void setValToDay(Long valToDay) {
        this.valToDay = valToDay;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quotation quotation = (Quotation) o;
        return getSecId().equals(quotation.getSecId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSecId());
    }
}


