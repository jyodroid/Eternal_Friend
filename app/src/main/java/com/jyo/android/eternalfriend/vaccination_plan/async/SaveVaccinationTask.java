package com.jyo.android.eternalfriend.vaccination_plan.async;

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
import com.jyo.android.eternalfriend.data.EFContract.VacccinationPlanEntry;
import com.jyo.android.eternalfriend.vaccination_plan.model.Vaccination;

/**
 * Created by JohnTangarife on 10/08/16.
 */
public class SaveVaccinationTask extends AsyncTask<Void, Void, Long> {

    public static final String LOG_TAG = SaveVaccinationTask.class.getSimpleName();
    private static final Long ERROR_SAVING = -1L;

    private Context mContext;
    private Vaccination mVaccination;
    private View mSnackBarContainer;

    public SaveVaccinationTask(Context context, Vaccination vaccination, View snackBarContainer) {
        mContext = context;
        mVaccination = vaccination;
        mSnackBarContainer = snackBarContainer;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        ContentResolver resolver = mContext.getContentResolver();

        ContentValues profileValues = new ContentValues();

        profileValues.put(VacccinationPlanEntry.COLUMN_PROFILE_ID, mVaccination.getProfileId());
        profileValues.put(VacccinationPlanEntry.COLUMN_VACCINATION_PLAN_DATE, mVaccination.getDate());
        profileValues.put(VacccinationPlanEntry.COLUMN_VACCINATION_PLAN_NAME, mVaccination.getName());
        profileValues.put(VacccinationPlanEntry.COLUMN_VACCINATION_PLAN_STATUS, mVaccination.getStatus());

        Uri insertedUri = null;
        try {
            insertedUri =
                    resolver.insert(VacccinationPlanEntry.CONTENT_URI, profileValues);
        }catch (Exception e){
            Log.e(LOG_TAG, "Can't save on database", e);
        }
        if (insertedUri == null){
            return ERROR_SAVING;
        }
        return ContentUris.parseId(insertedUri);
    }

    protected void onPostExecute(Long insertedUri) {
        if (insertedUri == ERROR_SAVING){
            Snackbar.make(
                    mSnackBarContainer,
                    mContext.getString(R.string.error_saving),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.dismiss, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
        }else {
            Snackbar.make(
                    mSnackBarContainer,
                    mContext.getString(R.string.vaccination_added),
                    Snackbar.LENGTH_LONG).show();
        }
        Log.d(LOG_TAG, "Inserted URI: " + insertedUri);
    }

}
