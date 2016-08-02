package com.jyo.android.eternalfriend.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.ConnectException;

/**
 * Created by JohnTangarife on 1/08/16.
 */
public class NetworkHelper {

    private static final String LOG_TAG = NetworkHelper.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private static NetworkHelper mInstance;
    private Context mContext;

    private NetworkHelper(Context context) {
        if (mRequestQueue == null) {
            this.mContext = context;
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
    }

    public static synchronized NetworkHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkHelper(context);
        }
        return mInstance;
    }

    public  RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request)
            throws ConnectException{
        request.setTag(LOG_TAG);
        if (!isInternetAvailable(mContext)){
            throw new ConnectException("there is no internet available");
        }
        mRequestQueue.add(request);
    }

    /**
     * Cancels all requests in the queue of this network helper
     */
    public void cancelPendingRequests(){
        if (mRequestQueue != null){
            mRequestQueue.cancelAll(LOG_TAG);
        }
    }

    public static boolean isInternetAvailable(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
