package com.yurev.marketprice.quotation;

import android.content.Context;
import android.util.Log;

import com.yurev.marketprice.database.DbAdapter;
import com.yurev.marketprice.database.MarketContract;
import com.yurev.marketprice.retrofitmoex.MoexService;
import com.yurev.marketprice.retrofitmoex.QuotationModel;
import com.yurev.marketprice.retrofitmoex.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalQuotationLists {

    private static List<Quotation> sFavoriteList = new ArrayList<Quotation>();
    private static List<Quotation> sStockList = new ArrayList<Quotation>();
    private static List<Quotation> sCurrencyList = new ArrayList<Quotation>();
    private static List<Quotation> sFuturesList = new ArrayList<Quotation>();

    private static HashMap<Integer, String> sLastUpdateTimes = new HashMap<>();

    private TypeOfSort mFavoriteSort;
    private TypeOfSort mStockSort;
    private TypeOfSort mCurrencySort;
    private TypeOfSort mFuturesSort;

    private boolean mIsReverseFavorite;
    private boolean mIsReverseStock;
    private boolean mIsReverseCurrency;
    private boolean mIsReverseFutures;

    private Context mContext;

    public LocalQuotationLists(Context context) {
        mFavoriteSort = TypeOfSort.VALTODAY;
        mStockSort = TypeOfSort.VALTODAY;
        mCurrencySort = TypeOfSort.VALTODAY;
        mFuturesSort = TypeOfSort.VALTODAY;

        mIsReverseFavorite = true;
        mIsReverseStock = true;
        mIsReverseCurrency = true;
        mIsReverseFutures = true;

        sLastUpdateTimes.putAll(DbAdapter.get(context).getTimes());
        mContext = context;
        createLocalLists(mContext);
    }

    private static void createLocalLists (Context context){

        setFavoriteList( DbAdapter.get(context).getQuotations(MarketContract.FavoriteTable.NAME_TABLE));


        MoexService moexService = RetrofitClient.getInstance().create(MoexService.class);
        Call<List<QuotationModel>>  call = moexService.loadQuotation("stock", "shares", "TQBR");
        call.enqueue(new Callback<List<QuotationModel>>() {
            @Override
            public void onResponse(Call<List<QuotationModel>> call, Response<List<QuotationModel>> response) {
                List<QuotationModel> qm = response.body();
                setStockList(new ArrayList<Quotation>(qm.get(1).convertToQuotation()));
                Comparator<Quotation> comp = Comparator.comparing(Quotation::getValToDay);
                getStockList().sort(comp.reversed());
            }
            @Override
            public void onFailure(Call<List<QuotationModel>> call, Throwable t) {
                setStockList(DbAdapter.get(context).getQuotations(MarketContract.StockTable.NAME_TABLE));
                Log.d("TAG1","createLocalLists - stock fail");
            }
        });


        call = moexService.loadQuotation("currency", "selt", "CETS");
        call.enqueue(new Callback<List<QuotationModel>>() {
            @Override
            public void onResponse(Call<List<QuotationModel>> call, Response<List<QuotationModel>> response) {
                List<QuotationModel> qm = response.body();

                ArrayList<Quotation> newData = new ArrayList<Quotation>(qm.get(1).convertToQuotation());
                for(int i = 0; i < newData.size(); i++){
                    String name = newData.get(i).getShortName();
                    if(!name.endsWith("_TOM")) {
                        newData.remove(newData.get(i));
                        i--;
                    }else{
                        StringBuilder nameBilder = new StringBuilder(name);
                        nameBilder.insert(3," / ");
                        newData.get(i).setShortName(nameBilder.substring(0,9).toString());
                    }
                }
                setCurrencyList(newData);
                Comparator<Quotation> comp = Comparator.comparing(Quotation::getValToDay);
                getCurrencyList().sort(comp.reversed());
            }
            @Override
            public void onFailure(Call<List<QuotationModel>> call, Throwable t) {
                setCurrencyList( DbAdapter.get(context).getQuotations(MarketContract.CurrencyTable.NAME_TABLE));
                Log.d("TAG1","createLocalLists - currency fail");
            }
        });

        call = moexService.loadQuotation("futures", "forts", "RFUD");
        call.enqueue(new Callback<List<QuotationModel>>() {
            @Override
            public void onResponse(Call<List<QuotationModel>> call, Response<List<QuotationModel>> response) {
                List<QuotationModel> qm = response.body();
                setFuturesList( new ArrayList<Quotation>(qm.get(1).convertToQuotation()));
                Comparator<Quotation> comp = Comparator.comparing(Quotation::getValToDay);
                getFuturesList().sort(comp.reversed());
            }
            @Override
            public void onFailure(Call<List<QuotationModel>> call, Throwable t) {
                setFuturesList( DbAdapter.get(context).getQuotations(MarketContract.FuturesTable.NAME_TABLE));
                Log.d("TAG1","createLocalLists - futures fail");
            }
        });
    }

    public static void loadFromDBase(Context context) {
        sStockList = DbAdapter.get(context).getQuotations(MarketContract.StockTable.NAME_TABLE);
        sFavoriteList = DbAdapter.get(context).getQuotations(MarketContract.FavoriteTable.NAME_TABLE);
        sCurrencyList = DbAdapter.get(context).getQuotations(MarketContract.CurrencyTable.NAME_TABLE);
        sFuturesList = DbAdapter.get(context).getQuotations(MarketContract.FuturesTable.NAME_TABLE);

        sLastUpdateTimes.putAll(DbAdapter.get(context).getTimes());
    }


    public static String getLastUpdateTime(int currentList) {
        synchronized (LocalQuotationLists.class){
            return sLastUpdateTimes.get(currentList);
        }
    }

    public static void setLastUpdateTime(int currentList, String lastUpdateTime) {
        synchronized (LocalQuotationLists.class) {
            LocalQuotationLists.sLastUpdateTimes.put(currentList, lastUpdateTime);
        }
    }


    public static List<Quotation> getFavoriteList() {
        synchronized (LocalQuotationLists.class){
        return sFavoriteList;
        }
    }

    public static void setFavoriteList(List<Quotation> sFavoriteList) {
        synchronized (LocalQuotationLists.class) {
            LocalQuotationLists.sFavoriteList = sFavoriteList;
        }
    }

    public static List<Quotation> getStockList() {
        synchronized (LocalQuotationLists.class) {
            return sStockList;
        }
    }

    public static void setStockList(List<Quotation> sStockList) {
        synchronized (LocalQuotationLists.class){
            LocalQuotationLists.sStockList = sStockList;
        }
    }

    public static List<Quotation> getCurrencyList() {
        synchronized (LocalQuotationLists.class){
            return sCurrencyList;
        }
    }

    public static void setCurrencyList(List<Quotation> sCurrencyList) {
        synchronized (LocalQuotationLists.class){
            LocalQuotationLists.sCurrencyList = sCurrencyList;
        }
    }

    public static List<Quotation> getFuturesList() {
        synchronized (LocalQuotationLists.class){
            return sFuturesList;
        }
    }

    public static void setFuturesList(List<Quotation> sFuturesList) {
        synchronized (LocalQuotationLists.class){
            LocalQuotationLists.sFuturesList = sFuturesList;
        }
    }


    public TypeOfSort getFavoriteSort() {
        return mFavoriteSort;
    }

    public void setFavoriteSort(TypeOfSort FavoriteSort) {
        this.mFavoriteSort = FavoriteSort;
    }

    public TypeOfSort getStockSort() {
        return mStockSort;
    }

    public void setStockSort(TypeOfSort stockSort) {
        this.mStockSort = stockSort;
    }

    public TypeOfSort getCurrencySort() {
        return mCurrencySort;
    }

    public void setCurrencySort(TypeOfSort currencySort) {
        this.mCurrencySort = currencySort;
    }

    public TypeOfSort getFuturesSort() {
        return mFuturesSort;
    }

    public void setFuturesSort(TypeOfSort futuresSort) {
        this.mFuturesSort = futuresSort;
    }

    public boolean isReverseFavorite() {
        return mIsReverseFavorite;
    }

    public void setReverseFavorite(boolean isReverseFavorite) {
        this.mIsReverseFavorite = isReverseFavorite;
    }

    public boolean isReverseStock() {
        return mIsReverseStock;
    }

    public void setReverseStock(boolean isReverseStock) {
        this.mIsReverseStock = isReverseStock;
    }

    public boolean isReverseCurrency() {
        return mIsReverseCurrency;
    }

    public void setReverseCurrency(boolean isReverseCurrency) {
        this.mIsReverseCurrency = isReverseCurrency;
    }

    public boolean isReverseFutures() {
        return mIsReverseFutures;
    }

    public void setReverseFutures(boolean isReverseFutures) {
        this.mIsReverseFutures = isReverseFutures;
    }

    public void currentQuotationsSort (TypeOfSort selectType, int currentPage){

        //define list, which need sort
        List<Quotation> quotations;
        TypeOfSort currentTypeOfSort;
        boolean currentDirectionSort;
        switch (currentPage){
            case 0:
                quotations = LocalQuotationLists.getFavoriteList();
                if(selectType != TypeOfSort.REVERSE) mFavoriteSort = selectType;
                currentTypeOfSort = mFavoriteSort;
                currentDirectionSort = mIsReverseFavorite;
                break;
            case 1:
                quotations =  LocalQuotationLists.getStockList();
                if(selectType != TypeOfSort.REVERSE) mStockSort = selectType;
                currentTypeOfSort = mStockSort;
                currentDirectionSort = mIsReverseStock;
                break;
            case 2:
                quotations =  LocalQuotationLists.getCurrencyList();
                if(selectType != TypeOfSort.REVERSE) mCurrencySort = selectType;
                currentTypeOfSort = mCurrencySort;
                currentDirectionSort = mIsReverseCurrency;
                break;
            case 3:
                quotations =  LocalQuotationLists.getFuturesList();
                if(selectType != TypeOfSort.REVERSE) mFuturesSort = selectType;
                currentTypeOfSort = mFuturesSort;
                currentDirectionSort = mIsReverseFutures;
                break;
            default:
                quotations = null;
                currentDirectionSort = false;
        }

        //define comparator type
        if(quotations != null) {
            Comparator<Quotation> comp;
            switch (selectType) {
                case REVERSE:
                    Collections.reverse(quotations);
                    break;
                case LASTTOPREVPRICE:
                    comp = Comparator.comparing(Quotation::getLastToPrevPrice);
                    currentTypeOfSort = TypeOfSort.LASTTOPREVPRICE;
                    if (currentDirectionSort) quotations.sort(comp.reversed());
                    else quotations.sort(comp);
                    break;
                case SHORTNAME:
                    comp = Comparator.comparing(Quotation::getShortName);
                    currentTypeOfSort = TypeOfSort.SHORTNAME;
                    if (!currentDirectionSort) quotations.sort(comp.reversed());
                    else quotations.sort(comp);
                    break;
                default:
                    comp = Comparator.comparing(Quotation::getValToDay);
                    currentTypeOfSort = TypeOfSort.VALTODAY;
                    if (currentDirectionSort) quotations.sort(comp.reversed());
                    else quotations.sort(comp);
            }
        }

    }
    public enum TypeOfSort{
        VALTODAY,
        LASTTOPREVPRICE,
        SHORTNAME,
        REVERSE
    }
}

