package com.jyo.android.eternalfriend.profile.async;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.data.EFContract.ProfileEntry;
import com.jyo.android.eternalfriend.profile.ProfileSummarizeActivity;
import com.jyo.android.eternalfriend.profile.model.Profile;

/**
 * Created by JohnTangarife on 10/08/16.
 */
public class SaveProfileTask extends AsyncTask<Void, Void, Long> {

    public static final String LOG_TAG = SaveProfileTask.class.getSimpleName();

    private Context mContext;
    private Profile mProfile;
    private View mSnackBarContainer;

    public SaveProfileTask(Context context, Profile profile, View snackBarContainer) {
        mContext = context;
        mProfile = profile;
        mSnackBarContainer = snackBarContainer;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        ContentResolver resolver = mContext.getContentResolver();

        ContentValues profileValues = new ContentValues();

        profileValues.put(ProfileEntry.COLUMN_PROFILE_NAME, mProfile.getName());
        profileValues.put(ProfileEntry.COLUMN_PROFILE_BIRTH_DATE, mProfile.getBirthDate());
        profileValues.put(ProfileEntry.COLUMN_PROFILE_BREED, mProfile.getBreed());
        profileValues.put(ProfileEntry.COLUMN_PROFILE_IMAGE, mProfile.getPicture());

        Uri insertedUri =
                resolver.insert(ProfileEntry.CONTENT_URI, profileValues);
        return ContentUris.parseId(insertedUri);
    }

    protected void onPostExecute(Long insertedUri) {
        Snackbar.make(
                mSnackBarContainer,
                String.format(
                        mContext.getString(R.string.save_message), mProfile.getName()),
                Snackbar.LENGTH_LONG)
                .setAction(R.string.go_to_profiles, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ProfileSummarizeActivity.class);
                        mContext.startActivity(intent);
                    }
                }).show();
        Log.d(LOG_TAG, "Inserted URI: " + insertedUri);
    }

}
