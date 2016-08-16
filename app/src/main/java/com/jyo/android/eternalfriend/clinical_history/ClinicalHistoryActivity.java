package com.jyo.android.eternalfriend.clinical_history;

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
import com.jyo.android.eternalfriend.data.EFContract.ClinicalHistoryEntry;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile.model.Profile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClinicalHistoryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    ViewHolder mViewHolder;
    Profile mProfile;

    // Identifies a particular Loader being used in this component
    private static final int HISTORY_LOADER = 3;
    private ClinicalHistoryAdapter mHistoryAdapter;

    //Query projection
    String[] HISTORY_COLUMNS = {
            ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_ID,
            ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_DATE,
            ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_HOSPITAL,
            ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_DIAGNOSTIC,
            ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_TREATMENT
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinical_history);
        ButterKnife.bind(this);
        View rootView = findViewById(R.id.clinical_history_container);
        mViewHolder = new ViewHolder(rootView);

        Intent incoming = getIntent();
        mProfile = incoming.getParcelableExtra(ProfileActivity.PROFILE_EXTRA);

        mHistoryAdapter = new ClinicalHistoryAdapter(this);
        //Design manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mViewHolder.recyclerView.setLayoutManager(layoutManager);
        mViewHolder.recyclerView.setAdapter(mHistoryAdapter);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mViewHolder
                .collapsingToolbarLayout
                .setTitle(
                        String.format(
                                getString(R.string.clinical_bar_title_format),
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
        getSupportLoaderManager().initLoader(HISTORY_LOADER, null, this);
    }

    @OnClick(R.id.add_fab)
    public void goToAddHistory() {
        Intent intent = new Intent(this, AddHistoryActivity.class);
        intent.putExtra(ProfileActivity.PROFILE_EXTRA, mProfile);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        /*
        * Takes action based on the ID of the Loader that's being created
        */
        switch (loaderID) {
            case HISTORY_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        Uri.withAppendedPath(ClinicalHistoryEntry.CONTENT_URI, String.valueOf(mProfile.getProfileId())),    // Table to query
                        HISTORY_COLUMNS,     // Projection to return
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
             /*
     * Moves the query results into the adapter, causing the
     * ListView fronting this adapter to re-display
     */
        if (data.getCount() == 0){
            mViewHolder.emptyMessage.setVisibility(View.VISIBLE);
        }else {
            mViewHolder.emptyMessage.setVisibility(View.GONE);
        }
        mHistoryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mHistoryAdapter.swapCursor(null);
    }

    static class ViewHolder {

        @BindView(R.id.clinical_history_recycler_view)
        RecyclerView recyclerView;

        @BindView(R.id.clinical_history_container)
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
            finish();
        }

        return (super.onOptionsItemSelected(item));
    }
}
