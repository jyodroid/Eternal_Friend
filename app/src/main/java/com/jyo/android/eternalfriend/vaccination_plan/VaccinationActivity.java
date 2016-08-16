package com.jyo.android.eternalfriend.vaccination_plan;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.data.EFContract.VaccinationPlanEntry;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile.model.Profile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VaccinationActivity extends AppCompatActivity
        implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifies a particular Loader being used in this component
    private static final int VACCINATION_LOADER = 4;

    ViewHolder mViewHolder;
    Profile mProfile;
    private VaccinationAdapter mVaccinationAdapter;

    //Query projection
    String[] VACCINATION_COLUMNS = {
            VaccinationPlanEntry.COLUMN_PROFILE_ID,
            VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_ID,
            VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_DATE,
            VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_NAME,
            VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_STATUS,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccination);

        ButterKnife.bind(this);
        View rootView = findViewById(R.id.vaccination_plan_container);
        mViewHolder = new ViewHolder(rootView);

        Intent incoming = getIntent();
        mProfile = incoming.getParcelableExtra(ProfileActivity.PROFILE_EXTRA);

        //RecyclerView setup
        mVaccinationAdapter = new VaccinationAdapter(this);
        //Design manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mViewHolder.recyclerView.setLayoutManager(layoutManager);
        mViewHolder.recyclerView.setAdapter(mVaccinationAdapter);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mViewHolder
                .collapsingToolbarLayout
                .setTitle(
                        String.format(
                                getString(R.string.vaccination_bar_title_format),
                                mProfile.getName()));

        Glide
                .with(this)
                .load(mProfile.getPicture())
                .error(R.drawable.ic_image_black_48dp)
                .into(mViewHolder.headerImage);

        Drawable backIcon =
                ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_arrow_back_white_24dp);

        getSupportActionBar().setHomeAsUpIndicator(backIcon);

        /*
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getSupportLoaderManager().initLoader(VACCINATION_LOADER, null, this);
    }

    @OnClick(R.id.add_fab)
    public void goToAddVaccination() {
        Intent intent = new Intent(this, AddVaccinationActivity.class);
        intent.putExtra(ProfileActivity.PROFILE_EXTRA, mProfile);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
                /*
        * Takes action based on the ID of the Loader that's being created
        */
        switch (loaderID) {
            case VACCINATION_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        Uri.withAppendedPath(VaccinationPlanEntry.CONTENT_URI, String.valueOf(mProfile.getProfileId())),    // Table to query
                        VACCINATION_COLUMNS,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            mViewHolder.emptyMessage.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.emptyMessage.setVisibility(View.GONE);
        }
        mVaccinationAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mVaccinationAdapter.swapCursor(null);
    }

    static class ViewHolder {

        @BindView(R.id.vaccination_plan_recycler_view)
        RecyclerView recyclerView;

        @BindView(R.id.vaccination_plan_container)
        CoordinatorLayout containerView;

        @BindView(R.id.profile_image)
        ImageView headerImage;

        @BindView(R.id.collapsing_toolbar)
        CollapsingToolbarLayout collapsingToolbarLayout;

        @BindView(R.id.empty_message_holder)
        TextView emptyMessage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(ProfileActivity.PROFILE_EXTRA, mProfile);
            startActivity(intent);
            finish();
        }

        return (super.onOptionsItemSelected(item));
    }
}
