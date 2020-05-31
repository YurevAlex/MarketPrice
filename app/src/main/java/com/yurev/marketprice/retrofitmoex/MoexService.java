package com.yurev.marketprice.retrofitmoex;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoexService {

    @GET("/iss/engines/{engine}/markets/{market}/boards/{board}/securities.json?iss.json=extended&iss.meta=off&iss.only=securities|marketdata&securities.columns=SECID,SHORTNAME,SECNAME&marketdata.columns=SECID,OPEN,LOW,HIGH,LAST,LASTTOPREVPRICE,CHANGE,VALTODAY_RUR,VALTODAY_USD")
    Call<List<QuotationModel>> loadQuotation(@Path("engine") String engine, @Path("market") String market, @Path("board") String board);

    @GET("/iss/engines/{engine}/markets/{market}/boards/{board}/securities.json?iss.json=extended&iss.meta=off&iss.only=securities|marketdata&securities.columns=SECID,SHORTNAME,SECNAME&marketdata.columns=SECID,OPEN,LOW,HIGH,LAST,LASTTOPREVPRICE,CHANGE,VALTODAY_RUR,VALTODAY_USD")
    Call<List<QuotationModel>> loadFavoritesQuotation(@Path("engine") String engine, @Path("market") String market, @Path("board") String board, @Query("securities") String securities);
}




