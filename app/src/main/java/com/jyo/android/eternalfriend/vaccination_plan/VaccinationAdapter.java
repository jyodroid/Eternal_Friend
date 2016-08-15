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
import com.jyo.android.eternalfriend.vaccination_plan.model.Vaccination;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JohnTangarife on 14/08/16.
 */
public class VaccinationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static int COLUMN_VACCINATION_ID_INDEX = 0;
    private static int COLUMN_VACCINATION_DATE_INDEX = 1;
    private static int COLUMN_VACCINATION_NAME_INDEX = 2;
    private static int COLUMN_VACCINATION_STATUS_INDEX = 3;

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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private Vaccination createVaccination(int position) {

        Vaccination vaccination = new Vaccination();

        mCursor.moveToPosition(position);

        vaccination.setVaccinationId(mCursor.getInt(COLUMN_VACCINATION_ID_INDEX));
        vaccination.setDate(mCursor.getString(COLUMN_VACCINATION_DATE_INDEX));
        vaccination.setName(mCursor.getString(COLUMN_VACCINATION_NAME_INDEX));
        vaccination.setStatus(mCursor.getInt(COLUMN_VACCINATION_STATUS_INDEX));

        return vaccination;
    }
}
