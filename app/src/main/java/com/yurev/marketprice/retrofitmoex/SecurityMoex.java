package com.yurev.marketprice.retrofitmoex;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SecurityMoex {

    @SerializedName("SECID")
    @Expose
    private String secId;
    @SerializedName("SHORTNAME")
    @Expose
    private String shortName;
    @SerializedName("SECNAME")
    @Expose
    private String secName;

    public String getSecId() {
        return secId;
    }

    public void setSecId(String SecId) {
        this.secId = SecId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String ShortName) {
        this.shortName = ShortName;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String SecName) {
        this.secName = SecName;
    }

}