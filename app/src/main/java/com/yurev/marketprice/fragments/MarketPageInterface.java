package com.yurev.marketprice.fragments;

import com.yurev.marketprice.quotation.LocalQuotationLists.TypeOfSort;

public interface MarketPageInterface {
    void sort(TypeOfSort selectType);
    void pauseTask();
    void resumeTask();
    boolean getDirectionSort();
    void setConnectionStatus(boolean isConnection);
}

