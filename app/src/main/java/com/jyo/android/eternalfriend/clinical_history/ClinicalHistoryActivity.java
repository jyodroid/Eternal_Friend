package com.jyo.android.eternalfriend.clinical_history;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.data.EFContract.ClinicalHistoryEntry;
import com.jyo.android.eternalfriend.map.MapsActivity;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile.model.Profile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClinicalHistoryActivity extends AppCompatActivity {

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

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mViewHolder.collapsingToolbarLayout.setTitle(mProfile.getName());

        Glide
                .with(this)
                .load(mProfile.getPicture())
                .error(R.drawable.ic_image_black_48dp)
                .into(mViewHolder.headerImage);

        Drawable backIcon =
                ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_arrow_back_white_24dp);

        getSupportActionBar().setHomeAsUpIndicator(backIcon);
    }

    @OnClick(R.id.search_fab)
    public void goToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
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
