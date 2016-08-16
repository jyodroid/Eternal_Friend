package com.jyo.android.eternalfriend.vaccination_plan;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.vaccination_plan.async.UpdateVaccinationTask;
import com.jyo.android.eternalfriend.vaccination_plan.model.Vaccination;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JohnTangarife on 14/08/16.
 */
public class VaccinationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private Cursor mCursor;

    public VaccinationAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vaccination_card_layout, parent, false);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Vaccination vaccination = createVaccination(position);
        ViewHolder vaccinationHolder = (ViewHolder)holder;

        vaccinationHolder.setVaccination(vaccination);
        vaccinationHolder.setContext(mContext);

        vaccinationHolder.date.setText(vaccination.getDate());
        vaccinationHolder.name.setText(vaccination.getName());

        if (vaccination.getStatus() == 1){
            vaccinationHolder.status.setChecked(true);
        }else {
            vaccinationHolder.status.setChecked(false);
        }
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

        @BindView(R.id.vaccination_date)
        TextView date;

        @BindView(R.id.vaccination_name)
        TextView name;

        @BindView(R.id.vaccination_status)
        CheckBox status;

        Vaccination mVaccination;
        Context mContext;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.vaccination_status)
        public void changeStatus() {
            int statusValue = 0;
            if (status.isChecked()){
                statusValue = 1;
            }
            new UpdateVaccinationTask(mContext, mVaccination, statusValue).execute();
        }

        public void setVaccination(Vaccination vaccination){
            mVaccination = vaccination;
        }

        public void setContext(Context context){
            mContext = context;
        }
    }

    private Vaccination createVaccination(int position) {

        final int COLUMN_PROFILE_ID_INDEX = 0;
        final int COLUMN_VACCINATION_ID_INDEX = 1;
        final int COLUMN_VACCINATION_DATE_INDEX = 2;
        final int COLUMN_VACCINATION_NAME_INDEX = 3;
        final int COLUMN_VACCINATION_STATUS_INDEX = 4;

        Vaccination vaccination = new Vaccination();

        mCursor.moveToPosition(position);

        vaccination.setProfileId(mCursor.getInt(COLUMN_PROFILE_ID_INDEX));
        vaccination.setVaccinationId(mCursor.getInt(COLUMN_VACCINATION_ID_INDEX));
        vaccination.setDate(mCursor.getString(COLUMN_VACCINATION_DATE_INDEX));
        vaccination.setName(mCursor.getString(COLUMN_VACCINATION_NAME_INDEX));
        vaccination.setStatus(mCursor.getInt(COLUMN_VACCINATION_STATUS_INDEX));

        return vaccination;
    }
}
