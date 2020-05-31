package com.yurev.marketprice;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.yurev.marketprice.quotation.Quotation;

import java.util.List;

public class MarketDiffCallback extends DiffUtil.Callback{

    final List<Quotation> mOldList;
    final List<Quotation> mNewList;

    public MarketDiffCallback(List<Quotation> newList, List<Quotation> oldList) {
        mNewList = newList;
        mOldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return mOldList != null ? mOldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewList != null ? mNewList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition)); //compare by SecId
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Quotation oldQuotation = mOldList.get(oldItemPosition);
        Quotation newQuotation = mNewList.get(newItemPosition);
        return oldQuotation.getLast().equals(newQuotation.getLast()) &&
                oldQuotation.getShortName().equals(newQuotation.getShortName()) &&
                oldQuotation.getLastToPrevPrice().equals(newQuotation.getLastToPrevPrice());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Quotation oldQuotation = mOldList.get(oldItemPosition);
        Quotation newQuotation = mNewList.get(newItemPosition);
        Bundle diffBundle = new Bundle();
        if (!oldQuotation.getShortName().equals(newQuotation.getShortName())) {
            diffBundle.putString("shortName", newQuotation.getShortName());
        }
        if (!oldQuotation.getLast().equals(newQuotation.getLast())) {
            diffBundle.putString("lastPrice", newQuotation.getLast().toString());
        }
        if (!oldQuotation.getLastToPrevPrice().equals(newQuotation.getLastToPrevPrice())) {
            diffBundle.putString("lastToPrevPrice",newQuotation.getLastToPrevPrice().toString());
        }
        if (diffBundle.size() == 0) return null;
        return diffBundle;
    }
}
