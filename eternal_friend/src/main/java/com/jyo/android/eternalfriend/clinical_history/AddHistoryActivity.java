package com.jyo.android.eternalfriend.clinical_history;

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
import com.jyo.android.eternalfriend.clinical_history.async.SaveHistoryTask;
import com.jyo.android.eternalfriend.clinical_history.model.ClinicalHistory;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile.model.Profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddHistoryActivity extends AppCompatActivity
        implements
        DatePickerDialog.OnDateSetListener {

    private static final String LOG_TAG = AddHistoryActivity.class.getSimpleName();

    private DatePickerDialog mDatePickerDialog;
    private ViewHolder mViewHolder;
    private Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_history);
        ButterKnife.bind(this);

        Intent incoming = getIntent();
        mProfile = incoming.getParcelableExtra(ProfileActivity.PROFILE_EXTRA);

        View rootView = findViewById(R.id.clinical_history_form_container);
        mViewHolder = new ViewHolder(rootView);

        mViewHolder.attendanceDate.setText(getInitialDateText());
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
        mViewHolder.attendanceDate.setText(underLinedText);
    }

    @OnClick(R.id.add_history_button)
    public void addHistory() {

        final String EMPTY = "";
        boolean validAttendanceDate = true;
        boolean validHospital = true;
        boolean validDiagnostic = true;
        boolean validTreatment = true;

        ClinicalHistory history = new ClinicalHistory();
        history.setProfileId(mProfile.getProfileId());

        if (getString(R.string.pet_birth_value).equals(mViewHolder.attendanceDate.getText().toString())) {
            mViewHolder.hospital.setError(getString(R.string.invalid_attendance_date));
            validAttendanceDate = false;
        } else {
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat(getString(R.string.date_format));
            try {
                Date attendanceDate = simpleDateFormat.parse(mViewHolder.attendanceDate.getText().toString());
                if (null != attendanceDate) {
                    String date = ClinicalHistory.dateFormat.format(attendanceDate);
                    history.setDate(date);
                } else {
                    Log.e(LOG_TAG, "Parsed attendance date is null");
                    validAttendanceDate = false;
                }
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Parse date exception", e);
                validAttendanceDate = false;
            }
        }

        if (EMPTY.equals(mViewHolder.hospital.getText().toString())) {
            mViewHolder.hospital.setError(getString(R.string.invalid_hospital));
            validHospital = false;
        } else {
            history.setHospital(mViewHolder.hospital.getText().toString());
        }

        if (EMPTY.equals(mViewHolder.diagnostic.getText().toString())) {
            mViewHolder.diagnostic.setError(getString(R.string.invalid_diagnostic));
            validDiagnostic = false;
        } else {
            history.setDiagnostic(mViewHolder.diagnostic.getText().toString());
        }

        if (EMPTY.equals(mViewHolder.tratment.getText().toString())) {
            mViewHolder.tratment.setError(getString(R.string.invalid_treatment));
            validTreatment = false;
        } else {
            history.setTreatment(mViewHolder.tratment.getText().toString());
        }

        if (validAttendanceDate && validHospital && validDiagnostic && validTreatment) {
            new SaveHistoryTask(this, history, mViewHolder.viewContainer).execute();
            mViewHolder.attendanceDate.setText(getInitialDateText());
            mViewHolder.hospital.setText(EMPTY);
            mViewHolder.diagnostic.setText(EMPTY);
            mViewHolder.tratment.setText(EMPTY);

        } else {
            Snackbar.make(
                    mViewHolder.viewContainer,
                    getString(R.string.history_invalid_values),
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

    @OnClick(R.id.attendance_date_value)
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
            mDatePickerDialog.getDatePicker().setMaxDate(currentDateCalendar.getTime().getTime());
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

        @BindView(R.id.attendance_date_value)
        TextView attendanceDate;

        @BindView(R.id.hospital_value)
        EditText hospital;

        @BindView(R.id.diagnostic_value)
        EditText diagnostic;

        @BindView(R.id.treatment_value)
        EditText tratment;

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
