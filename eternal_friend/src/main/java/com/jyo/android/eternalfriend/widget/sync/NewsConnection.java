package com.jyo.android.eternalfriend.widget.sync;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jyo.android.eternalfriend.R;

import org.json.JSONObject;

/**
 * Created by JohnTangarife on 16/08/16.
 */
public class NewsConnection {
    private static final String NEWS_SEARCH_URL =
            "http://api.nytimes.com/svc/search/v2/articlesearch.json?";
    private static final String KEY_PARAM_KEY = "api-key";
    private static final String FQ_PARAM_KEY = "fq";
    private static final String SOURCE_VALUE = "source:(\"The New York Times\")";
    private static final String SUBJECT_VALUE = "subject:(\"Pets\")";

    @NonNull
    public static JsonObjectRequest createNewsRequest(
            @NonNull final Context context,
            @NonNull final Response.Listener<JSONObject> onSuccessHandler,
            @NonNull final Response.ErrorListener onErrorHandler) {

        Uri url = Uri.parse(NEWS_SEARCH_URL)
                .buildUpon()
                .appendQueryParameter(KEY_PARAM_KEY, context.getString(R.string.nyt_api_key))
                .appendQueryParameter(FQ_PARAM_KEY, SOURCE_VALUE)
                .appendQueryParameter(FQ_PARAM_KEY, SUBJECT_VALUE)
                .build();

        return new JsonObjectRequest(
                Request.Method.GET, url.toString(), null, onSuccessHandler, onErrorHandler);

    }
}
