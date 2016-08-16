package com.jyo.android.eternalfriend.widget.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.commons.NetworkHelper;
import com.jyo.android.eternalfriend.data.EFContract;
import com.jyo.android.eternalfriend.widget.NewsWidgetProvider;
import com.jyo.android.eternalfriend.widget.sync.model.Doc;
import com.jyo.android.eternalfriend.widget.sync.model.News;
import com.jyo.android.eternalfriend.widget.sync.model.NewsResponse;

import org.json.JSONObject;

import java.net.ConnectException;

/**
 * Created by JohnTangarife on 16/08/16.
 */
public class NewsSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String IMAGE_URL_BASE = "http://www.nytimes.com/";

    public final String LOG_TAG = NewsSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the news, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private Response.Listener<JSONObject> mOnSuccessHandler = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Gson gson = new GsonBuilder().create();
            NewsResponse newsResponse =
                    gson.fromJson(response.toString(), NewsResponse.class);

            if (newsResponse != null && newsResponse.getResponseObject() != null) {
                if (newsResponse.getResponseObject().getDocs() != null &&
                        newsResponse.getResponseObject().getDocs().size() > 0) {

                    Doc firstArticle = newsResponse.getResponseObject().getDocs().get(0);
                    News news = new News();

                    news.setTitle(firstArticle.getHeadline().getMain());
                    news.setArticleUrl(firstArticle.getWebUrl());
                    if (firstArticle.getMultimedia().size() > 0) {
                        news.setImageUrl(IMAGE_URL_BASE + firstArticle.getMultimedia().get(0).getUrl());
                    }
//                news.setByLine(firstArticle.getByLine().getOriginal());
                    news.setDate(firstArticle.getPubDate());
                    news.setExtract(firstArticle.getSnippet());

                    deleteNewsData();
                    saveNews(news);
                    updateWidgets();
                }

            }
        }
    };

    Response.ErrorListener mOnErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyLog.e("Error", error);
        }
    };
    private ContentResolver mContentResolver;

    public NewsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle bundle,
            String s,
            ContentProviderClient contentProviderClient,
            SyncResult syncResult) {

        Context context = getContext();
        NetworkHelper networkHelper = NetworkHelper.getInstance(context);
        JsonObjectRequest request =
                NewsConnection.createNewsRequest(
                        context, mOnSuccessHandler, mOnErrorHandler);

        try {
            networkHelper.addToRequestQueue(request);
        } catch (ConnectException e) {
            e.printStackTrace();
        }
    }

    private void deleteNewsData() {
        mContentResolver.delete(EFContract.NewsEntry.CONTENT_URI, null, null);
    }

    private void saveNews(News news) {

        ContentValues newsValues = new ContentValues();

        newsValues.put(EFContract.NewsEntry.COLUMN_NEWS_TITLE, news.getTitle());
        newsValues.put(EFContract.NewsEntry.COLUMN_NEWS_ARTICLE_URL, news.getArticleUrl());
        newsValues.put(EFContract.NewsEntry.COLUMN_NEWS_IMAGE_URL, news.getImageUrl());
        newsValues.put(EFContract.NewsEntry.COLUMN_NEWS_BY_LINE, news.getByLine());
        newsValues.put(EFContract.NewsEntry.COLUMN_NEWS_DATE, news.getDate());
        newsValues.put(EFContract.NewsEntry.COLUMN_NEWS_EXTRACT, news.getExtract());

        mContentResolver.insert(EFContract.NewsEntry.CONTENT_URI, newsValues);
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(NewsWidgetProvider.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = EFContract.CONTENT_AUTHORITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        NewsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, EFContract.CONTENT_AUTHORITY, true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                EFContract.CONTENT_AUTHORITY, bundle);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
