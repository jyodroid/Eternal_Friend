package com.jyo.android.eternalfriend.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by JohnTangarife on 15/08/16.
 */
public class NewsWidgetProvider extends AppWidgetProvider{

    public static final String ACTION_DATA_UPDATED =
            "com.jyo.android.eternalfriend.ACTION_DATA_UPDATED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, NewsIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, NewsIntentService.class));
        }
    }
}
