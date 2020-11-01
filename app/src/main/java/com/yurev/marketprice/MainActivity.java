package com.yurev.marketprice;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.yurev.marketprice.Adapters.MarketPagerAdapter;
import com.yurev.marketprice.pages.CurrenciesFragment;
import com.yurev.marketprice.pages.FavoritesFragment;
import com.yurev.marketprice.pages.FuturesFragment;
import com.yurev.marketprice.pages.MarketPageInterface;
import com.yurev.marketprice.pages.StocksFragment;

import static com.yurev.marketprice.quotation.LocalQuotationLists.TypeOfSort;


public class MainActivity extends AppCompatActivity  implements SortSelectFragment.SortSelectListener {
    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;
    private FloatingActionButton mFab;
    private TextView mTextViewStatus;

    private NetworkConnection mNetworkConnection;
    private MarketPageInterface mCurrentPage;
    private String mStatus = "";

/*
    static long startTime = 0;
    static long endTime = 0;
            long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        Log.d("TAG11", (endTime - startTime) + "ms!!!!");*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewStatus =  findViewById(R.id.text_status);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager2 = findViewById(R.id.view_pager);
        mFab =  findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isDirection;
                try {
                    mCurrentPage.sort(TypeOfSort.REVERSE);
                    isDirection = mCurrentPage.getDirectionSort();

                    mFab.setImageResource(isDirection ? R.drawable.sort_anim_up : R.drawable.sort_anim_down);
                    Drawable drawable = mFab.getDrawable();
                    if(drawable instanceof Animatable) {
                        ((Animatable) drawable).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mFab.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                SortSelectFragment myDialogFragment = new SortSelectFragment();
                myDialogFragment.show(manager, "myDialog");
                return true;
            }
        });

        mViewPager2.setAdapter(new MarketPagerAdapter(this));
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if(state == 1) mFab.animate().alphaBy(1).alpha(0).setDuration(300).start();
                else if (state == 0)mFab.animate().alphaBy(0).alpha(1).setDuration(300).start();

                if(mViewPager2.getCurrentItem() == 0){
                    FavoritesFragment.getInstance().setIsBlockUpdate(false);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mCurrentPage = (MarketPageInterface) FavoritesFragment.getInstance();
                        break;
                    case 1:
                        mCurrentPage = (MarketPageInterface) StocksFragment.getInstance();
                        break;
                    case 2:
                        mCurrentPage = (MarketPageInterface) CurrenciesFragment.getInstance();
                        break;
                    case 3:
                        mCurrentPage = (MarketPageInterface) FuturesFragment.getInstance();
                        break;
                    default:
                        Toast.makeText(getParent().getApplicationContext(), "Error mCurrentPage", Toast.LENGTH_SHORT).show();
                }

                try {
                    boolean isDirection = mCurrentPage.getDirectionSort();
                    mFab.setImageResource(isDirection ? R.drawable.sort_anim_up : R.drawable.sort_anim_down);
                    Drawable drawable = mFab.getDrawable();
                    if(drawable instanceof Animatable) {
                        ((Animatable) drawable).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
            mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Favorites");
                        break;
                    case 1:
                        tab.setText("Stocks");
                        break;
                    case 2:
                        tab.setText("Currencies");
                        break;
                    case 3:
                        tab.setText("Futures");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();

        LiveData<String> liveDataStocks = StocksFragment.getInstance().getStatus();
        LiveData<String> liveDataFavorites = FavoritesFragment.getInstance().getStatus();
        LiveData<String> liveDataCurrencies = CurrenciesFragment.getInstance().getStatus();
        LiveData<String> liveDataFutures = FuturesFragment.getInstance().getStatus();
        MediatorLiveData<String> mediatorLiveData = new MediatorLiveData<>();

        Observer <String> observer = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String value) {
                /*if(mStatus.equals(value)) {
                    return;}*/
                if(value.equals("OK")) {
                    mTextViewStatus.setVisibility(View.GONE);
                }else{
                    mTextViewStatus.setVisibility(View.VISIBLE);
                    mTextViewStatus.setText(value);
                }
                mStatus = value;
            }
        };
        liveDataFutures.observe(this, observer);
        liveDataStocks.observe(this, observer);
        liveDataFavorites.observe(this, observer);
        liveDataCurrencies.observe(this, observer);




        mNetworkConnection = new NetworkConnection(getApplicationContext());
        mNetworkConnection.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                try {
                    FavoritesFragment.getInstance().setConnectionStatus(aBoolean);
                    StocksFragment.getInstance().setConnectionStatus(aBoolean);
                    CurrenciesFragment.getInstance().setConnectionStatus(aBoolean);
                    FuturesFragment.getInstance().setConnectionStatus(aBoolean);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager2.setAdapter(null);
        mViewPager2.removeAllViews();
    }

    @Override
    public void setSortSelect(TypeOfSort selectType) {
        mCurrentPage.sort(selectType);
    }
}