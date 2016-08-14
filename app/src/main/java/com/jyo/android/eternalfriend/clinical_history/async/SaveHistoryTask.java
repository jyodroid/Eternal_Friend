package com.jyo.android.eternalfriend.clinical_history.async;

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
import com.jyo.android.eternalfriend.clinical_history.ClinicalHistoryActivity;
import com.jyo.android.eternalfriend.clinical_history.model.ClinicalHistory;
import com.jyo.android.eternalfriend.data.EFContract.ClinicalHistoryEntry;

/**
 * Created by JohnTangarife on 10/08/16.
 */
public class SaveHistoryTask extends AsyncTask<Void, Void, Long> {

    public static final String LOG_TAG = SaveHistoryTask.class.getSimpleName();
    private static final Long ERROR_SAVING = -1L;

    private Context mContext;
    private ClinicalHistory mHistory;
    private View mSnackBarContainer;

    public SaveHistoryTask(Context context, ClinicalHistory history, View snackBarContainer) {
        mContext = context;
        mHistory = history;
        mSnackBarContainer = snackBarContainer;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        ContentResolver resolver = mContext.getContentResolver();

        ContentValues profileValues = new ContentValues();

        profileValues.put(ClinicalHistoryEntry.COLUMN_PROFILE_ID, mHistory.getProfileId());
        profileValues.put(ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_DATE, mHistory.getDate());
        profileValues.put(ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_HOSPITAL, mHistory.getHospital());
        profileValues.put(ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_DIAGNOSTIC, mHistory.getDiagnostic());
        profileValues.put(ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_TREATMENT, mHistory.getTreatment());

        Uri insertedUri = null;
        try {
            insertedUri =
                    resolver.insert(ClinicalHistoryEntry.CONTENT_URI, profileValues);
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
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.dismiss, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
        }else {
            Snackbar.make(
                    mSnackBarContainer,
                    mContext.getString(R.string.clinical_history_added),
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.go_to_histories, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ClinicalHistoryActivity.class);
                            mContext.startActivity(intent);
                        }
                    }).show();
        }
        Log.d(LOG_TAG, "Inserted URI: " + insertedUri);
    }

}
