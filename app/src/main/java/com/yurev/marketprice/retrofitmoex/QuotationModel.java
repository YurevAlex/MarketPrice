package com.yurev.marketprice.retrofitmoex;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yurev.marketprice.quotation.Quotation;

import java.util.ArrayList;
import java.util.List;

public class QuotationModel {

    @SerializedName("securities")
    @Expose
    private List<SecurityMoex> securities = null;
    @SerializedName("marketdata")
    @Expose
    private List<MarketdataMoex> marketdata = null;

    public List<SecurityMoex> getSecurities() {
        return securities;
    }

    public void setSecurities(List<SecurityMoex> securities) {
        this.securities = securities;
    }

    public List<MarketdataMoex> getMarketdata() {
        return marketdata;
    }

    public void setMarketdata(List<MarketdataMoex> marketdata) {
        this.marketdata = marketdata;
    }

    public List<Quotation> convertToQuotation (){
        List<Quotation> qList = new ArrayList<Quotation>(securities.size());

        for(int i = 0; i< securities.size(); i++){
            Quotation q = new Quotation(securities.get(i), marketdata.get(i));
            if (q.getLast() == 0){}
            else{    qList.add(q); }
        }
        return qList;
    }
}