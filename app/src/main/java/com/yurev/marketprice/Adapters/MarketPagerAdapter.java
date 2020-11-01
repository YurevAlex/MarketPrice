package com.yurev.marketprice.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.yurev.marketprice.pages.CurrenciesFragment;
import com.yurev.marketprice.pages.FavoritesFragment;
import com.yurev.marketprice.pages.FuturesFragment;
import com.yurev.marketprice.pages.StocksFragment;

public class MarketPagerAdapter extends FragmentStateAdapter {

    public MarketPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
        case 0: return FavoritesFragment.getInstance();
        case 1: return StocksFragment.getInstance();
        case 2: return CurrenciesFragment.getInstance();
        default: return FuturesFragment.getInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
