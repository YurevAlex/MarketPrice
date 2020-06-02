package com.yurev.marketprice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yurev.marketprice.quotation.Quotation;

public class DetailsFragment extends DialogFragment {
    private Quotation mQuotation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public DetailsFragment(Quotation q) {
        mQuotation = q;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);

        TextView secId = v.findViewById(R.id.details_secId);
        TextView secName = v.findViewById(R.id.details_secName);
        TextView open = v.findViewById(R.id.details_open);
        TextView high = v.findViewById(R.id.details_high);
        TextView low = v.findViewById(R.id.details_low);
        TextView last = v.findViewById(R.id.details_last);
        TextView lastToPrevPrice = v.findViewById(R.id.details_changePercent);
        TextView change = v.findViewById(R.id.details_change);
        TextView valToDay = v.findViewById(R.id.details_valToDay);

         secId.setText(mQuotation.getSecId());
         secName.setText(mQuotation.getSecName());
         open.setText(mQuotation.getOpen().toString());
         high.setText(mQuotation.getHigh().toString());
         low.setText(mQuotation.getLow().toString());
         last.setText(mQuotation.getLast().toString());
         lastToPrevPrice.setText(mQuotation.getLastToPrevPrice().toString()+ '%');
         change.setText(mQuotation.getChange().toString());
         double volume = mQuotation.getValToDay();
         String s;
         if(volume / 1000_000_000 > 1){
             s = String.format("%.3f" + " bln", volume / 1000_000_000);
         }else if(volume / 1000_000 > 1){
             s = String.format("%.3f" + " mil", volume / 1000_000);
         }else {
             s = String.format("%.0f", volume);
         }
         valToDay.setText(s);
        return v;
    }
}
