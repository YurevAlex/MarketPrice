package com.yurev.marketprice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import static com.yurev.marketprice.quotation.LocalQuotationLists.TypeOfSort;

public class SortSelectFragment extends DialogFragment {
    private SortSelectListener mListener;

    public SortSelectFragment() {
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Dialog));
        String [] variantOfSort = {"обороту за день","изменению цены за день","наименованию"};

        builder.setTitle(R.string.sort_select)
                .setItems(variantOfSort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TypeOfSort selectType;
                        switch (which){
                            case 1:
                                selectType = TypeOfSort.LASTTOPREVPRICE;
                                break;
                            case 2:
                                selectType = TypeOfSort.SHORTNAME;
                                break;
                            default:
                                selectType = TypeOfSort.VALTODAY;
                        }
                        mListener.setSortSelect(selectType);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (SortSelectListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement SortSelectListener");
        }
    }


    public interface SortSelectListener{
        void setSortSelect(TypeOfSort selectType);
    }
}
