package com.yurev.marketprice.Adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yurev.marketprice.ItemTouchHelper.ItemTouchHelperAdapter;
import com.yurev.marketprice.ItemTouchHelper.ItemTouchHelperViewHolder;
import com.yurev.marketprice.R;
import com.yurev.marketprice.pages.FavoritesFragment;
import com.yurev.marketprice.quotation.Quotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FavoriteRecycleViewAdapter extends RecyclerView.Adapter<FavoriteRecycleViewAdapter.FavoriteViewHolder>
                                        implements ItemTouchHelperAdapter {
    private List<Quotation> mItemList;
    private Context mContext;
    private HashMap<String,Integer> mPriceChangeMap;

    public FavoriteRecycleViewAdapter(List<Quotation> itemList) {
        //mItemList = itemList;
        mItemList = new ArrayList<>(itemList);
        mPriceChangeMap = new HashMap<>();
        for(Quotation q : itemList) {
            mPriceChangeMap.put(q.getSecId(), 0);
        }
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_list_item, parent, false);
        FavoriteViewHolder vh = new FavoriteViewHolder(v);
        mContext = parent.getContext();
        return vh;
    }


    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        super.onBindViewHolder(holder, position, null);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads == null){return;}
        holder.bind(position,payloads);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public List<Quotation> getItemList() {
        return mItemList;
    }


    public void setItemList(List<Quotation> itemList) {

        if(mItemList == null){
            for(Quotation q : itemList) {
                mPriceChangeMap.put(q.getSecId(), 0);
            }
        }else{
            for(Quotation q : itemList) {
                int priceChangeValue = 0;
                int index = mItemList.indexOf(q);
                if(index == -1){
                    priceChangeValue = 0;
                }else if(q.getLast() > mItemList.get(index).getLast()){
                    priceChangeValue = 1;
                }else if(q.getLast() < mItemList.get(index).getLast()){
                    priceChangeValue = -1;
                }else {
                    priceChangeValue = 0;
                }
                mPriceChangeMap.put(q.getSecId(), priceChangeValue);
            }
        }
        mItemList.clear();
        mItemList.addAll(itemList);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItemList, i, i+1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItemList, i, i - 1);
            }
        }
        FavoritesFragment.getInstance().setFavoriteList(mItemList);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mItemList.remove(position);
        FavoritesFragment.getInstance().setFavoriteList(mItemList);
        notifyItemRemoved(position);
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        private Quotation mQuotation;
        private TextView mName;
        private TextView mShortName;
        private TextView mPrice;
        private TextView mDiffLastDay;

        public FavoriteViewHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.item_name);
            mShortName = v.findViewById(R.id.item_shortname);
            mPrice = v.findViewById(R.id.item_price);
            mDiffLastDay = v.findViewById(R.id.item_diffLastDay);
        }

        public void bind(int position, @NonNull List<Object> payloads) {

            if (payloads.isEmpty()) {
                mQuotation = mItemList.get(position);

                mName.setText(mQuotation.getShortName());
                mShortName.setText(mQuotation.getSecId());

                mPrice.setText(mQuotation.getLast().toString());
                int priceChangeValue = mPriceChangeMap.get(mQuotation.getSecId());
                if (priceChangeValue == 1) {
                    AnimatorSet animator  = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.animator_background_green);
                    animator.setTarget(mPrice);
                    animator.start();
                }else if (priceChangeValue == -1){
                    AnimatorSet animator  = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.animator_background_red);
                    animator.setTarget(mPrice);
                    animator.start();
                }

                if (mQuotation.getLastToPrevPrice() >= 0) {
                    mDiffLastDay.setText("+" + mQuotation.getLastToPrevPrice().toString() + "%");
                    mDiffLastDay.setTextColor(Color.GREEN);
                } else {
                    mDiffLastDay.setText(mQuotation.getLastToPrevPrice().toString() + "%");
                    mDiffLastDay.setTextColor(Color.RED);
                }
            } else {
                Bundle bundle = (Bundle) payloads.get(0);
                for (String key : bundle.keySet()) {
                    String payload = bundle.getString(key);

                    if (key.equals("shortName")) {
                        mShortName.setText(payload);
                    }
                    if (key.equals("lastPrice")) {
                        mPrice.setText(payload);
                        mQuotation = mItemList.get(position);
                        int priceChangeValue = mPriceChangeMap.get(mQuotation.getSecId());
                        if (priceChangeValue == 1) {
                            AnimatorSet animator  = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.animator_background_green);
                            animator.setTarget(mPrice);
                            animator.start();
                        }else if (priceChangeValue == -1){
                            AnimatorSet animator  = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, R.animator.animator_background_red);
                            animator.setTarget(mPrice);
                            animator.start();
                        }
                    }
                    if (key.equals("lastToPrevPrice")) {
                        if (mQuotation.getLastToPrevPrice() >= 0) {
                            mDiffLastDay.setText("+" + payload + "%");
                            mDiffLastDay.setTextColor(Color.GREEN);
                        } else {
                            mDiffLastDay.setText(payload + "%");
                            mDiffLastDay.setTextColor(Color.RED);
                        }
                    }
                }
            }


        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundResource(R.drawable.item_recycleview_selected);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundResource(R.drawable.item_recycleview);
        }
    }

}