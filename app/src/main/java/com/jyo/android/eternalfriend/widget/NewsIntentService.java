package com.jyo.android.eternalfriend.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.data.EFContract;
import com.jyo.android.eternalfriend.widget.sync.model.News;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by JohnTangarife on 15/08/16.
 */
public class NewsIntentService extends IntentService {

    private static final String LOG_TAG = NewsIntentService.class.getSimpleName();
    private static final String[] NOTIFY_NEWS_PROJECTION = new String[]{
            EFContract.NewsEntry.COLUMN_NEWS_TITLE,
            EFContract.NewsEntry.COLUMN_NEWS_EXTRACT,
            EFContract.NewsEntry.COLUMN_NEWS_DATE,
            EFContract.NewsEntry.COLUMN_NEWS_BY_LINE,
            EFContract.NewsEntry.COLUMN_NEWS_IMAGE_URL,
            EFContract.NewsEntry.COLUMN_NEWS_ARTICLE_URL
    };

    // these indices must match the projection
    private static final int INDEX_NEWS_TITLE = 0;
    private static final int INDEX_NEWS_EXTRACT = 1;
    private static final int INDEX_NEWS_DATE = 2;
    private static final int INDEX_NEWS_BY_LINE = 3;
    private static final int INDEX_NEWS_IMAGE_URL = 4;
    private static final int INDEX_NEWS_ARTICLE_URL = 5;

    public NewsIntentService() {
        super("NewsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Cursor data =
                getContentResolver()
                        .query(EFContract.NewsEntry.CONTENT_URI, NOTIFY_NEWS_PROJECTION, null, null, null);

        if (data == null) {
            return;
        }

        News news = null;
        if (data.getCount() >0){
            news = new News();
            data.moveToFirst();

            news.setTitle(data.getString(INDEX_NEWS_TITLE));
            news.setExtract(data.getString(INDEX_NEWS_EXTRACT));
            news.setByLine(data.getString(INDEX_NEWS_BY_LINE));
            news.setArticleUrl(data.getString(INDEX_NEWS_ARTICLE_URL));
            news.setImageUrl(data.getString(INDEX_NEWS_IMAGE_URL));
            try {
                news.setDate(formatDate(data.getString(INDEX_NEWS_DATE)));
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Wrong date format", e);
            } finally {
                if (data != null) {
                    data.close();
                }
            }
        }

        // Perform this loop procedure for each widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        ComponentName componentName = new ComponentName(this, NewsWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_news;
            RemoteViews remoteViews = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            if (null != news) {
                try {
                    Bitmap imageBitmap =
                            Glide.
                                    with(getBaseContext()).
                                    load(news.getImageUrl()).
                                    asBitmap().
                                    into(100, 100). // Width and height
                                    get();
                    remoteViews.setImageViewBitmap(R.id.widget_image, imageBitmap);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "interrupted", e);
                } catch (ExecutionException e) {
                    Log.e(LOG_TAG, "Execution", e);
                }

                remoteViews.setTextViewText(R.id.widget_title, news.getTitle());
                remoteViews.setTextViewText(R.id.widget_byline, news.getByLine());
                remoteViews.setTextViewText(R.id.widget_date, news.getDate());
                remoteViews.setTextViewText(R.id.widget_extract, news.getExtract());

                // Create an Intent to launch Browser with article url
                Log.d(LOG_TAG, news.getArticleUrl());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getArticleUrl()));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, browserIntent, 0);
                remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }

    }

    private String formatDate(String oldFormatdate) throws ParseException {

        SimpleDateFormat oldDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//2016-07-11T00:00:00Z
        SimpleDateFormat newDateFormat = new SimpleDateFormat(getString(R.string.date_format));

        Date date = oldDateFormat.parse(oldFormatdate);
        return newDateFormat.format(date);
    }
}
