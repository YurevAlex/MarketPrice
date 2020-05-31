package com.yurev.marketprice.Adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yurev.marketprice.R;
import com.yurev.marketprice.fragments.FavoritesFragment;
import com.yurev.marketprice.quotation.Quotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MarketRecicleViewAdapter extends RecyclerView.Adapter<MarketRecicleViewAdapter.MarketViewHolder> {
    private List<Quotation> mItemList;
    private int mExpandedPosition = -1;
    private ViewGroup rootView;
    private Context mContext;
    private HashMap<String,Integer> mPriceChangeMap;

    public MarketRecicleViewAdapter(List<Quotation> itemList) {
        mItemList = new ArrayList<>(itemList);
        mPriceChangeMap = new HashMap<>();
        for(Quotation q : mItemList) {
            mPriceChangeMap.put(q.getSecId(), 0);
        }
    }

    @Override
    public MarketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.market_list_item, parent, false);
        MarketViewHolder vh = new MarketViewHolder(v);
        mContext = parent.getContext();

        return vh;
    }

    @Override
    public void onBindViewHolder(MarketViewHolder holder, int position) {
        super.onBindViewHolder(holder, position, null);
    }

    @Override
    public void onBindViewHolder(@NonNull MarketViewHolder holder, int position, @NonNull List<Object> payloads) {
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

    public void resetExpandedPosition() {
        notifyItemChanged(mExpandedPosition);
        mExpandedPosition = -1;

    }

    class MarketViewHolder extends RecyclerView.ViewHolder {

        private Quotation mQuotation;
        private TextView mName;
        private TextView mShortName;
        private TextView mPrice;
        private TextView mDiffLastDay;
        private ImageView mImageFavorite;
        private ImageView mImageStatistics;
        private ImageView mImageGraph;

        private FrameLayout mFavoritesFrame;
        private FrameLayout mDetailsFrame;
        private FrameLayout mGraphFrame;

        public MarketViewHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.item_name);
            mShortName = v.findViewById(R.id.item_shortname);
            mPrice = v.findViewById(R.id.item_price);
            mDiffLastDay = v.findViewById(R.id.item_diffLastDay);

            mImageFavorite = v.findViewById(R.id.item_favorite);
            mImageStatistics = v.findViewById(R.id.item_statistics);
            mImageGraph = v.findViewById(R.id.item_graph);

            mFavoritesFrame = v.findViewById(R.id.frame_favorite);
            mGraphFrame = v.findViewById(R.id.frame_graph);
            mDetailsFrame = v.findViewById(R.id.frame_details);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mItemList.indexOf(mQuotation);
                    final boolean isExpanded = position == mExpandedPosition;
                    mExpandedPosition = isExpanded ? -1:position;
                    notifyDataSetChanged();
                }
            });

            mFavoritesFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (FavoritesFragment.getInstance().getFavoriteList().contains(mQuotation)) {
                        FavoritesFragment.getInstance().getFavoriteList().remove(mQuotation);
                    }
                    else{
                        FavoritesFragment.getInstance().getFavoriteList().add(mQuotation);
                    }
                    int position = mItemList.indexOf(mQuotation);
                    notifyItemChanged(position);
                }
            });
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

                final boolean isExpanded = position == mExpandedPosition;
                itemView.setActivated(isExpanded);
                mFavoritesFrame.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                mGraphFrame.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                mDetailsFrame.setVisibility(isExpanded?View.VISIBLE:View.GONE);

                if(mFavoritesFrame.getVisibility() == View.VISIBLE) {
                    mImageFavorite.setImageResource(FavoritesFragment.getInstance().getFavoriteList().contains(mQuotation) ?
                            R.drawable.ic_menu_favorite_on : R.drawable.ic_menu_favorite_off);
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

    }

}