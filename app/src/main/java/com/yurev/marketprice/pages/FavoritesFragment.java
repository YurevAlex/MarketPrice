package com.yurev.marketprice.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yurev.marketprice.Adapters.FavoriteRecycleViewAdapter;
import com.yurev.marketprice.Adapters.VerticalSpaceItemDecoration;
import com.yurev.marketprice.ItemTouchHelper.OnStartDragListener;
import com.yurev.marketprice.ItemTouchHelper.SimpleItemTouchHelperCallback;
import com.yurev.marketprice.MarketDiffCallback;
import com.yurev.marketprice.R;
import com.yurev.marketprice.database.DbAdapter;
import com.yurev.marketprice.database.MarketContract;
import com.yurev.marketprice.quotation.LocalQuotationLists.TypeOfSort;
import com.yurev.marketprice.quotation.Quotation;
import com.yurev.marketprice.retrofitmoex.MoexService;
import com.yurev.marketprice.retrofitmoex.QuotationModel;
import com.yurev.marketprice.retrofitmoex.RetrofitClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;


public class FavoritesFragment extends Fragment  implements MarketPageInterface, OnStartDragListener {

    private final int VERTICAL_ITEM_SPACE = 17;

    private RecyclerView mRecyclerView;
    private FavoriteRecycleViewAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    private List<Quotation> mFavoritesList;

    private MutableLiveData<String> mLiveData;

    public boolean mIsPausedTask;
    public boolean mIsConnection;

    private LoadFavoritesTask mTask;

    //private TypeOfSort mSortOfType;
    private boolean mIsReverse;
    private  String mLastUpdateTime;

    private Context mContext;

    private static  volatile FavoritesFragment sInstanse;

    private Boolean mIsBlockUpdate = false;
    public void setIsBlockUpdate(Boolean isBlockUpdate) {
        this.mIsBlockUpdate = isBlockUpdate;
    }

    public static FavoritesFragment getInstance (){
        FavoritesFragment localInstance = sInstanse;
        if(localInstance == null){
            synchronized (FavoritesFragment.class){
                localInstance = sInstanse;
                if(localInstance == null){
                    sInstanse = localInstance = new FavoritesFragment();
                }
            }
        }
        return localInstance;
    }

    private FavoritesFragment(){
        //mSortOfType = TypeOfSort.VALTODAY;
        mIsReverse = true;
        mIsPausedTask = false;
        mIsConnection = false;
        mLastUpdateTime = "";
        mLiveData = new MutableLiveData<>();
        mFavoritesList = new ArrayList<>();
    };

    public List<Quotation> getFavoriteList() {
        synchronized (sInstanse){
            return mFavoritesList;
        }
    }

    public void setFavoriteList(List<Quotation> list) {
        synchronized (sInstanse){
            mFavoritesList.clear();
            mFavoritesList.addAll(list);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mLastUpdateTime = DbAdapter.get(mContext).getTime(0);
        mFavoritesList = new ArrayList<>(DbAdapter.get(mContext).getQuotations(MarketContract.FavoriteTable.NAME_TABLE));
        mTask = new LoadFavoritesTask();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1)
            mTask.execute();
        else
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_price, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.price_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        mAdapter = new FavoriteRecycleViewAdapter(mFavoritesList);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter, this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsPausedTask = false;
        updateUI(mFavoritesList);
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsPausedTask = true;
    }



    @Override
    public void onStop() {
        super.onStop();
        DbAdapter.get(mContext).updateDb(MarketContract.FavoriteTable.NAME_TABLE, mFavoritesList);
        DbAdapter.get(mContext).updateDb(MarketContract.TimeTable.NAME_TABLE, 0, mLastUpdateTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTask.cancel(true);
    }

    public LiveData<String> getStatus() {
        return mLiveData;
    }

    @Override
    public void sort(TypeOfSort selectType) {
        if(selectType != null){
            sortList (selectType);
        }
        updateUI(mFavoritesList);
    }

    @Override
    public boolean getDirectionSort() {
        return mIsReverse;
    }

    @Override
    public void setConnectionStatus(boolean isConnection) {
        mIsConnection = isConnection;
    }

    @Override
    public void pauseTask() {
        mIsPausedTask = true;
    }

    @Override
    public void resumeTask() {
        mIsPausedTask =false;
    }

    private void sortList (TypeOfSort selectType){
        Comparator<Quotation> comp;
        switch (selectType) {
            case REVERSE:
                mIsReverse = !mIsReverse;
                Collections.reverse(mFavoritesList);
                break;
            case LASTTOPREVPRICE:
                comp = Comparator.comparing(Quotation::getLastToPrevPrice);
                //mSortOfType = TypeOfSort.LASTTOPREVPRICE;
                if (mIsReverse) mFavoritesList.sort(comp.reversed());
                else mFavoritesList.sort(comp);
                break;
            case SHORTNAME:
                comp = Comparator.comparing(Quotation::getShortName);
                //mSortOfType = TypeOfSort.SHORTNAME;
                if (!mIsReverse) mFavoritesList.sort(comp.reversed());
                else mFavoritesList.sort(comp);
                break;
            default:
                comp = Comparator.comparing(Quotation::getValToDay);
                //mSortOfType = TypeOfSort.VALTODAY;
                if (mIsReverse) mFavoritesList.sort(comp.reversed());
                else mFavoritesList.sort(comp);
        }
    }


    private void updateUI(List<Quotation> quotations){
        //Log.d("FAVO","--------updateUI fragment----------");
        if(quotations == null || mRecyclerView == null || mIsBlockUpdate){
            return;
        }
        // GET current scroll position
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int firstItem = manager.findFirstVisibleItemPosition();
        int topOffset = 0;
        if (firstItem > 0) {
            View firstItemView = manager.findViewByPosition(firstItem);
            topOffset = (int) firstItemView.getTop();
        }

        MarketDiffCallback marketDiffCallback = new MarketDiffCallback(quotations, mAdapter.getItemList());
        DiffUtil.DiffResult productDiffResult = DiffUtil.calculateDiff(marketDiffCallback);
        productDiffResult.dispatchUpdatesTo(mAdapter);
        manager.scrollToPositionWithOffset(firstItem, topOffset);
        mAdapter.setItemList(quotations);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


    class LoadFavoritesTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onProgressUpdate(String... message) {
            super.onProgressUpdate(message);
            if(message[0].equals("OK")) {
                updateUI(mFavoritesList);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (!isCancelled()) {
                if(mIsConnection == true && mIsPausedTask == false) {
                    Log.d("TASK","-----FavoritesTask-----");
                    MoexService moexService = RetrofitClient.getInstance().create(MoexService.class);
                                        //create Quest "securities"
                    StringBuilder securities = new StringBuilder();
                    getFavoriteList().forEach(quotation -> securities.append(quotation.getSecId()).append(','));
                    //securities.deleteCharAt(securities.length()-1);

                    Call<List<QuotationModel>> stockCall = moexService.loadFavoritesQuotation("stock", "shares", "TQBR", securities.toString());
                    Call<List<QuotationModel>> currencyCall = moexService.loadFavoritesQuotation("currency", "selt", "CETS", securities.toString());
                    Call<List<QuotationModel>> futuresCall = moexService.loadFavoritesQuotation("futures", "forts", "RFUD", securities.toString());
                    try {
                        Response<List<QuotationModel>> response = stockCall.execute();
                        List<QuotationModel> qm = response.body();
                        List<Quotation> stockPartList = new ArrayList<Quotation>(qm.get(1).convertToQuotation());

                        response = currencyCall.execute();
                        qm = response.body();
                        List<Quotation> currencyPartList = new ArrayList<Quotation>(qm.get(1).convertToQuotation());

                        response = futuresCall.execute();
                        qm = response.body();
                        List<Quotation> futuresPartList = new ArrayList<Quotation>(qm.get(1).convertToQuotation());

                        List<Quotation> favoritesList = new ArrayList<>(getFavoriteList());
                        //update quotations in FavoritesList
                            stockPartList.forEach(quotation -> {
                            int i = 0;
                            int max = getFavoriteList().size();
                            while (i < max){
                                if (quotation.getSecId().equals(favoritesList.get(i).getSecId())){
                                    favoritesList.set(i, quotation);
                                    break;
                                } i++;
                            }
                        });
                        currencyPartList.forEach(quotation -> {
                            int i = 0;
                            int max = getFavoriteList().size();
                            while (i < max){
                                if (quotation.getSecId().equals(favoritesList.get(i).getSecId())){
                                    favoritesList.set(i, quotation);
                                    String name = favoritesList.get(i).getShortName();
                                    StringBuilder nameBilder = new StringBuilder(name);
                                    nameBilder.insert(3," / ");
                                    favoritesList.get(i).setShortName(nameBilder.substring(0,9).toString());
                                    break;
                                } i++;
                            }
                        });
                        futuresPartList.forEach(quotation -> {
                            int i = 0;
                            int max = getFavoriteList().size();
                            while (i < max){
                                if (quotation.getSecId().equals(favoritesList.get(i).getSecId())){
                                    favoritesList.set(i, quotation);
                                    break;
                                } i++;
                            }
                        });
                        setFavoriteList(favoritesList);
                        //возможно более оптимально записывать время?!
                        mLastUpdateTime = new SimpleDateFormat("dd.MM.yy г.  HH:mm:ss").format(Calendar.getInstance().getTime());
                        publishProgress("OK");
                        mLiveData.postValue("OK");
                    } catch (IOException e) {
                        e.printStackTrace();
                        mLiveData.postValue("MOEX don't response");
                    }
                }else if(mIsConnection == false && mIsPausedTask == false){
                    mLiveData.postValue("Internet is disconnected. Last update: " + mLastUpdateTime);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
