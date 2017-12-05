package com.jyo.android.eternalfriend.map.places;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.jyo.android.eternalfriend.R;

import org.json.JSONObject;

/**
 * Created by JohnTangarife on 1/08/16.
 */
public class PlacesConnection {

    private static final String PLACES_SEARCH_URL =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final int MAX_RADIUS_METERS_QUERY = 5000;
    private static final String COMMA = ",";
    private static final String KEY_PARAM_KEY = "key";
    private static final String LOCATION_PARAM_KEY = "location";
    private static final String RADIUS_PARAM_KEY = "radius";
    private static final String TYPES_PARAM_KEY = "types";
    private static final String TYPES_PARAM_VALUE = "pet_store|veterinary_care";

    @NonNull
    public static JsonObjectRequest createPlacesRequest(
            @NonNull final Context context,
            @NonNull final Response.Listener<JSONObject> onSuccessHandler,
            @NonNull final Response.ErrorListener onErrorHandler,
            @NonNull final LatLng location) {

        StringBuilder locationParameterBuilder = new StringBuilder();
        locationParameterBuilder
                .append(location.latitude)
                .append(COMMA)
                .append(location.longitude);

        Uri url = Uri.parse(PLACES_SEARCH_URL)
                .buildUpon()
                .appendQueryParameter(KEY_PARAM_KEY, context.getString(R.string.google_places_key))
                .appendQueryParameter(LOCATION_PARAM_KEY, locationParameterBuilder.toString())
                .appendQueryParameter(RADIUS_PARAM_KEY, String.valueOf(MAX_RADIUS_METERS_QUERY))
                .appendQueryParameter(TYPES_PARAM_KEY, TYPES_PARAM_VALUE)
                .build();

        return new JsonObjectRequest(
                Request.Method.GET, url.toString(), null, onSuccessHandler, onErrorHandler);

    }
}
