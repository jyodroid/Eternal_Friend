package com.jyo.android.eternalfriend.gallery.async;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.data.EFContract.GalleryEntry;
import com.jyo.android.eternalfriend.gallery.model.Gallery;

/**
 * Created by JohnTangarife on 10/08/16.
 */
public class SaveGalleryTask extends AsyncTask<Void, Void, Long> {

    public static final String LOG_TAG = SaveGalleryTask.class.getSimpleName();

    private Context mContext;
    private Gallery mGallery;
    private View mSnackBarContainer;

    public SaveGalleryTask(Context context, Gallery gallery, View snackBarContainer) {
        mContext = context;
        mGallery = gallery;
        mSnackBarContainer = snackBarContainer;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        ContentResolver resolver = mContext.getContentResolver();

        ContentValues galleryValues = new ContentValues();

        galleryValues.put(GalleryEntry.COLUMN_PROFILE_ID, mGallery.getProfileId());
        galleryValues.put(GalleryEntry.COLUMN_GALLERY_IMAGE, mGallery.getImagePath());
        galleryValues.put(GalleryEntry.COLUMN_GALLERY_AGE_RANGE, mGallery.getRangeAge());

        Uri insertedUri =
                resolver.insert(GalleryEntry.CONTENT_URI, galleryValues);
        return ContentUris.parseId(insertedUri);
    }

    protected void onPostExecute(Long insertedUri) {
        Snackbar.make(
                mSnackBarContainer,
                String.format(
                        mContext.getString(R.string.gallery_save_message), mGallery.getRangeAge()),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
        Log.d(LOG_TAG, "Inserted URI: " + insertedUri);
    }

}
