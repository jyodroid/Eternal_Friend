package com.jyo.android.eternalfriend.vaccination_plan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.clinical_history.model.ClinicalHistory;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile.model.Profile;
import com.jyo.android.eternalfriend.vaccination_plan.async.SaveVaccinationTask;
import com.jyo.android.eternalfriend.vaccination_plan.model.Vaccination;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddVaccinationActivity extends AppCompatActivity
        implements
        DatePickerDialog.OnDateSetListener {

    private static final String LOG_TAG = AddVaccinationActivity.class.getSimpleName();

    private DatePickerDialog mDatePickerDialog;
    private ViewHolder mViewHolder;
    private Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_vaccination);
        ButterKnife.bind(this);

        Intent incoming = getIntent();
        mProfile = incoming.getParcelableExtra(ProfileActivity.PROFILE_EXTRA);

        View rootView = findViewById(R.id.vaccination_form_container);
        mViewHolder = new ViewHolder(rootView);

        mViewHolder.plannedDate.setText(getInitialDateText());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setDatePickerTextView(@NonNull Date shownDate) {

        //Show date with format
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(getString(R.string.date_format));

        String text = String.format(
                getString(R.string.date_picker_date_text),
                simpleDateFormat.format(shownDate));

        Spanned underLinedText;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            underLinedText = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            underLinedText = Html.fromHtml(text);
        }
        mViewHolder.plannedDate.setText(underLinedText);
    }

    @OnClick(R.id.add_vaccination_button)
    public void addVaccination() {

        final String EMPTY = "";
        boolean validPlannedDate = true;
        boolean validName = true;

        Vaccination vaccination = new Vaccination();
        vaccination.setProfileId(mProfile.getProfileId());

        if (getString(R.string.pet_birth_value).equals(mViewHolder.plannedDate.getText().toString())) {
            mViewHolder.plannedDate.setError(getString(R.string.invalid_attendance_date));
            validPlannedDate = false;
        } else {
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat(getString(R.string.date_format));
            try {
                Date attendanceDate = simpleDateFormat.parse(mViewHolder.plannedDate.getText().toString());
                if (null != attendanceDate) {
                    String date = ClinicalHistory.dateFormat.format(attendanceDate);
                    vaccination.setDate(date);
                } else {
                    Log.e(LOG_TAG, "Parsed attendance date is null");
                    validPlannedDate = false;
                }
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Parse date exception", e);
                validPlannedDate = false;
            }
        }

        if (EMPTY.equals(mViewHolder.name.getText().toString())) {
            mViewHolder.name.setError(getString(R.string.invalid_hospital));
            validName = false;
        } else {
            vaccination.setName(mViewHolder.name.getText().toString());
        }

        if (validName && validPlannedDate) {
            vaccination.setStatus(0);
            new SaveVaccinationTask(this, vaccination, mViewHolder.viewContainer).execute();
            mViewHolder.plannedDate.setText(getInitialDateText());
            mViewHolder.name.setText(EMPTY);
        } else {
            Snackbar.make(
                    mViewHolder.viewContainer,
                    getString(R.string.vaccination_invalid_values),
                    Snackbar.LENGTH_LONG)
                    .show();
        }

    }

    private Spanned getInitialDateText() {
        String text = String.format(
                getString(R.string.date_picker_date_text),
                getString(R.string.pet_birth_value));

        Spanned underLinedText;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            underLinedText = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            underLinedText = Html.fromHtml(text);
        }
        return underLinedText;
    }

    @OnClick(R.id.planned_date_value)
    public void showCalendar() {
        if (mDatePickerDialog == null) {

            final Calendar currentDateCalendar = Calendar.getInstance();

            //get  current month and year
            int year = currentDateCalendar.get(Calendar.YEAR);
            int month = currentDateCalendar.get(Calendar.MONTH);
            //get current maximum day of the current month
            int day = currentDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            mDatePickerDialog
                    = new DatePickerDialog(this, this, year, month, day);
        }
        mDatePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar selectedDate = new GregorianCalendar(year, month, day);

        setDatePickerTextView(selectedDate.getTime());

        mDatePickerDialog.dismiss();
    }

    static class ViewHolder {

        @BindView(R.id.planned_date_value)
        TextView plannedDate;

        @BindView(R.id.vaccination_name_value)
        EditText name;

        View viewContainer;

        public ViewHolder(View view) {
            viewContainer = view;
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
