package com.jyo.android.eternalfriend.profile_summarize;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.data.EFContract;
import com.jyo.android.eternalfriend.map.MapsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileSummarizeActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifies a particular Loader being used in this component
    private static final int PROFILE_LOADER = 0;
    private ProfileAdapter mProfileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_summarize);
        ButterKnife.bind(this);

        /*
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getSupportLoaderManager().initLoader(PROFILE_LOADER, null, this);

        View rootView = findViewById(R.id.profile_container);

        ViewHolder viewHolder = new ViewHolder(rootView);

        //improve performance
        viewHolder.recyclerView.setHasFixedSize(true);

        //Design manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        viewHolder.recyclerView.setLayoutManager(layoutManager);

        mProfileAdapter = new ProfileAdapter(this);
        viewHolder.recyclerView.setAdapter(mProfileAdapter);
    }

    @OnClick(R.id.search_fab)
    public void goToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.add_fab)
    public void goToAddProfile() {
        Intent intent = new Intent(this, AddProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        /*
        * Takes action based on the ID of the Loader that's being created
        */
        switch (loaderID) {
            case PROFILE_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        EFContract.ProfileEntry.CONTENT_URI,    // Table to query
                        null,     // Projection to return
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
        mProfileAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProfileAdapter.changeCursor();
    }

    static class ViewHolder {
        @BindView(R.id.profile_recycler_view)
        RecyclerView recyclerView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
