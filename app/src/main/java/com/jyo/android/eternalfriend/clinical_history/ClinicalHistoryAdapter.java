package com.jyo.android.eternalfriend.clinical_history;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.clinical_history.model.ClinicalHistory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by johntangarife on 8/5/16.
 */
public class ClinicalHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String LOG_TAG = ClinicalHistoryAdapter.class.getSimpleName();

    private static int COLUMN_CLINICAL_ID_INDEX = 0;
    private static int COLUMN_CLINICAL_DATE_INDEX = 1;
    private static int COLUMN_CLINICAL_HOSPITAL_INDEX = 2;
    private static int COLUMN_CLINICAL_DIAGNOSTIC_INDEX = 3;
    private static int COLUMN_CLINICAL_TREATMENT_INDEX = 4;

    private Context mContext;
    private Cursor mCursor;

    public ClinicalHistoryAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.clinical_history_card_layout, parent, false);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ClinicalHistory history = createHistory(position);
        ViewHolder clinicalHolder = (ViewHolder)holder;

        clinicalHolder.date.setText(history.getDate());
        clinicalHolder.hospital.setText(history.getHospital());
        clinicalHolder.diagnostic.setText(history.getDiagnostic());
        clinicalHolder.treatment.setText(history.getTreatment());
    }

    @Override
    public int getItemCount() {

        if (null != mCursor) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.clinical_history_date)
        TextView date;

        @BindView(R.id.clinical_history_hospital)
        TextView hospital;

        @BindView(R.id.clinical_history_diagnostic)
        TextView diagnostic;

        @BindView(R.id.clinical_history_treatment)
        TextView treatment;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private ClinicalHistory createHistory(int position) {

        ClinicalHistory history = new ClinicalHistory();

        mCursor.moveToPosition(position);

        history.setClinicalHistoryId(mCursor.getInt(COLUMN_CLINICAL_ID_INDEX));
        history.setDate(mCursor.getString(COLUMN_CLINICAL_DATE_INDEX));
        history.setHospital(mCursor.getString(COLUMN_CLINICAL_HOSPITAL_INDEX));
        history.setDiagnostic(mCursor.getString(COLUMN_CLINICAL_DIAGNOSTIC_INDEX));
        history.setTreatment(mCursor.getString(COLUMN_CLINICAL_TREATMENT_INDEX));

        return history;
    }
}
