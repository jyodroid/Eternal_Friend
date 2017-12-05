package com.jyo.android.eternalfriend.vaccination_plan.async;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.jyo.android.eternalfriend.data.EFContract.VaccinationPlanEntry;
import com.jyo.android.eternalfriend.vaccination_plan.model.Vaccination;

/**
 * Created by JohnTangarife on 10/08/16.
 */
public class UpdateVaccinationTask extends AsyncTask<Void, Void, Integer> {

    public static final String LOG_TAG = UpdateVaccinationTask.class.getSimpleName();

    private Context mContext;
    private Vaccination mVaccination;
    private int mStatus;

    public UpdateVaccinationTask(Context context, Vaccination vaccination, int status) {
        mContext = context;
        mVaccination = vaccination;
        mStatus = status;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        ContentResolver resolver = mContext.getContentResolver();

        ContentValues vaccinationValues = new ContentValues();
        vaccinationValues.put(VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_STATUS, mStatus);

        String whereClause =
                VaccinationPlanEntry.COLUMN_PROFILE_ID + " = " + mVaccination.getProfileId() +
                        " AND " +
                        VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_ID + " = " + mVaccination.getVaccinationId();

        int updatedRows = 0;
        try {
            updatedRows =
                    resolver.update(VaccinationPlanEntry.CONTENT_URI, vaccinationValues, whereClause, null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't save on database", e);
        }

        return updatedRows;
    }

    protected void onPostExecute(Integer updatedRows) {
        Log.d(LOG_TAG, "rows updated: " + updatedRows);
    }

}
