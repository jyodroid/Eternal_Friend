package com.jyo.android.eternalfriend.profile_summarize.async;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.data.EFContract.ProfileEntry;
import com.jyo.android.eternalfriend.profile_summarize.model.Profile;

/**
 * Created by JohnTangarife on 10/08/16.
 */
public class DeleteProfileTask extends AsyncTask<Void, Void, Integer> {

    public static final String LOG_TAG = DeleteProfileTask.class.getSimpleName();

    private Context mContext;
    private Profile mProfile;
    private View mSnackBarContainer;

    public DeleteProfileTask(Context context, Profile profile, View snackBarContainer) {
        mContext = context;
        mProfile = profile;
        mSnackBarContainer = snackBarContainer;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        ContentResolver resolver = mContext.getContentResolver();
        return resolver
                .delete(
                        ProfileEntry.CONTENT_URI,
                        ProfileEntry.COLUMN_PROFILE_ID + "= ?",
                        new String[]{String.valueOf(mProfile.getProfileId())});
    }

    protected void onPostExecute(Integer deletedRows) {
        Snackbar.make(
                mSnackBarContainer,
                String.format(
                        mContext.getString(R.string.delete_message), mProfile.getName()),
                Snackbar.LENGTH_LONG)
                .show();
        Log.d(LOG_TAG, "Deleted rows: " + deletedRows);
    }

}
