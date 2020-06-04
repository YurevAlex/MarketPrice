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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yurev.marketprice.Adapters.MarketRecicleViewAdapter;
import com.yurev.marketprice.Adapters.VerticalSpaceItemDecoration;
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
import retrofit2.Callback;
import retrofit2.Response;

public class FuturesFragment extends Fragment implements MarketPageInterface {

    private final int VERTICAL_ITEM_SPACE = 17;

    private RecyclerView mRecyclerView;
    private MarketRecicleViewAdapter mAdapter;

    private List<Quotation> mFuturesList;

    private MutableLiveData<String> mLiveData;

    public boolean mIsPausedTask;
    public boolean mIsConnection;

    private LoadFuturesTask mTask;

    private TypeOfSort mSortOfType;
    private boolean mIsReverse;
    private  String mLastUpdateTime;

    private Context mContext;


    private static  volatile FuturesFragment sInstanse;

    public static FuturesFragment getInstance (){
        FuturesFragment localInstance = sInstanse;
        if(localInstance == null){
            synchronized (FuturesFragment.class){
                localInstance = sInstanse;
                if(localInstance == null){
                    sInstanse = localInstance = new FuturesFragment();
                }
            }
        }
        return localInstance;
    }

    private FuturesFragment(){
        mSortOfType = TypeOfSort.VALTODAY;
        mIsReverse = true;
        mIsPausedTask = false;
        mIsConnection = false;
        mLastUpdateTime = "";
        mLiveData = new MutableLiveData<>();
        mFuturesList = new ArrayList<>();
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mLastUpdateTime = DbAdapter.get(mContext).getTime(3);

        MoexService moexService = RetrofitClient.getInstance().create(MoexService.class);
        Call<List<QuotationModel>>  call = moexService.loadQuotation("futures", "forts", "RFUD");
        call.enqueue(new Callback<List<QuotationModel>>() {
            @Override
            public void onResponse(Call<List<QuotationModel>> call, Response<List<QuotationModel>> response) {
                List<QuotationModel> qm = response.body();
                mFuturesList =  new ArrayList<Quotation>(qm.get(1).convertToQuotation());
                Comparator<Quotation> comp = Comparator.comparing(Quotation::getValToDay);
                mFuturesList.sort(comp.reversed());
            }
            @Override
            public void onFailure(Call<List<QuotationModel>> call, Throwable t) {
                mFuturesList = new ArrayList<>( DbAdapter.get(mContext).getQuotations(MarketContract.FuturesTable.NAME_TABLE));
                Log.d("TAG1","createLocalLists - futures fail");
            }
        });

        mTask = new LoadFuturesTask();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR1)
            mTask.execute();
        else
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_price, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.price_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));
        mAdapter = new MarketRecicleViewAdapter(mFuturesList);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsPausedTask = false;
        updateUI(mFuturesList);
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsPausedTask = true;
        mAdapter.resetExpandedPosition();
    }



    @Override
    public void onStop() {
        super.onStop();
        DbAdapter.get(mContext).updateDb(MarketContract.FuturesTable.NAME_TABLE, mFuturesList);
        DbAdapter.get(mContext).updateDb(MarketContract.TimeTable.NAME_TABLE, 3, mLastUpdateTime);
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
        mAdapter.resetExpandedPosition();
        updateUI(mFuturesList);
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
                Collections.reverse(mFuturesList);
                break;
            case LASTTOPREVPRICE:
                comp = Comparator.comparing(Quotation::getLastToPrevPrice);
                mSortOfType = TypeOfSort.LASTTOPREVPRICE;
                if (mIsReverse) mFuturesList.sort(comp.reversed());
                else mFuturesList.sort(comp);
                break;
            case SHORTNAME:
                comp = Comparator.comparing(Quotation::getShortName);
                mSortOfType = TypeOfSort.SHORTNAME;
                if (!mIsReverse) mFuturesList.sort(comp.reversed());
                else mFuturesList.sort(comp);
                break;
            default:
                comp = Comparator.comparing(Quotation::getValToDay);
                mSortOfType = TypeOfSort.VALTODAY;
                if (mIsReverse) mFuturesList.sort(comp.reversed());
                else mFuturesList.sort(comp);
        }
    }

    private void updateUI(List<Quotation> quotations){
        if(quotations == null || mRecyclerView == null){
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


    class LoadFuturesTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onProgressUpdate(String... message) {
            super.onProgressUpdate(message);
            if(message[0].equals("OK")) {
                updateUI(mFuturesList);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (!isCancelled()) {
                if(mIsConnection == true && mIsPausedTask == false) {
                    Log.d("TASK","-----FuturesTask-----");
                    MoexService moexService = RetrofitClient.getInstance().create(MoexService.class);
                    Call<List<QuotationModel>> call = moexService.loadQuotation("futures", "forts", "RFUD");
                    try {
                        Response<List<QuotationModel>> response = call.execute();
                        List<QuotationModel> qm = response.body();
                        ArrayList<Quotation> newData = new ArrayList<Quotation>(qm.get(1).convertToQuotation());

                        if(newData.size() > 1){
                            mFuturesList = newData;
                            sortList(mSortOfType);
                            //возможно более оптимально записывать время?!
                            mLastUpdateTime = new SimpleDateFormat("dd.MM.yy г.  HH:mm:ss").format(Calendar.getInstance().getTime());
                            publishProgress("OK");
                            mLiveData.postValue("OK");
                        }else{
                            mLiveData.postValue("MOEX is fault");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        mLiveData.postValue("MOEX don't response");
                    }

                }else if(mIsConnection == false && mIsPausedTask == false ){
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