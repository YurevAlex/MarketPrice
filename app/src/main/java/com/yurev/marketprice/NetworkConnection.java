package com.yurev.marketprice;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class NetworkConnection extends LiveData<Boolean> {

    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    public NetworkConnection (Context context){
        mContext = context;
        mConnectivityManager = (ConnectivityManager ) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkCallback = connectivityMangerCallback();
    }

    @Override
    protected void onActive() {
        super.onActive();
        updateConnection();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            mConnectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            lollipopNetWorkRequest();
        }else{
            mContext.registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY));
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
        }else{
            mContext.unregisterReceiver(mNetworkReceiver);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void lollipopNetWorkRequest () {

        NetworkRequest.Builder builder = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET);

        mConnectivityManager.registerNetworkCallback(builder.build(), mNetworkCallback);
    }

    private ConnectivityManager.NetworkCallback connectivityMangerCallback(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    postValue(true);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    postValue(false);
                }
            };
        }
        else {
            throw new IllegalAccessError("Error connectivityMangerCallback");
        }
    }

    private void updateConnection () {
        NetworkInfo activeNetWork = mConnectivityManager.getActiveNetworkInfo();
        if(activeNetWork != null && activeNetWork.isConnected() == true) {
            postValue(true);
        }else {
            postValue(false);
        }
    }

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateConnection();
        }
    };
}