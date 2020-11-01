package com.yurev.marketprice.pages;

import com.yurev.marketprice.quotation.LocalQuotationLists.TypeOfSort;

public interface MarketPageInterface {
    void sort(TypeOfSort selectType);
    boolean getDirectionSort();
    void setConnectionStatus(boolean isConnection);
}

