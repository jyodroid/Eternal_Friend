package com.jyo.android.eternalfriend.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.commons.MediaHelper;
import com.jyo.android.eternalfriend.commons.PermissionsHelper;
import com.jyo.android.eternalfriend.data.EFContract.GalleryEntry;
import com.jyo.android.eternalfriend.gallery.async.SaveGalleryTask;
import com.jyo.android.eternalfriend.gallery.model.Gallery;
import com.jyo.android.eternalfriend.map.MapsActivity;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile_summarize.model.Profile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = GalleryActivity.class.getSimpleName();
    private static final String PICTURE_PREFIX = "Gallery";
    private static final int GALLERY_LOADER = 1;
    private static final int GALLERY_BY_AGE_LOADER = 2;

    int COLUMN_GALLERY_ID_INDEX = 0;
    int COLUMN_GALLERY_IMAGE_INDEX = 1;
    int COLUMN_GALLERY_AGE_RANGE = 2;

    //Query projection
    String[] GALLERY_COLUMNS = {
            GalleryEntry.COLUMN_GALLERY_ID,
            GalleryEntry.COLUMN_GALLERY_IMAGE,
            GalleryEntry.COLUMN_GALLERY_AGE_RANGE
    };

    private String mFilePath;
    private boolean isAskingForPermission = false;
    private PermissionsHelper.HandleMessage handleMessage;
    private Profile mProfile;
    private ViewHolder mViewHolder;
    private GalleryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        View rootView = findViewById(R.id.gallery_container);
        mViewHolder = new ViewHolder(rootView);

        Intent incoming = getIntent();
        mProfile = incoming.getParcelableExtra(ProfileActivity.PROFILE_EXTRA);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewHolder.collapsingToolbarLayout.setTitle(mProfile.getName());

        Glide
                .with(this)
                .load(mProfile.getPicture())
                .error(R.drawable.ic_image_black_48dp)
                .into(mViewHolder.headerImage);

        //GridView
        ViewCompat.setNestedScrollingEnabled(mViewHolder.gridView, true);
        mAdapter = new GalleryAdapter(this, null);
        mViewHolder.gridView.setAdapter(mAdapter);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emptyView = inflater.inflate(R.layout.delete_card_layout, null);

        mViewHolder.gridView.setEmptyView(findViewById(R.id.empty_message_holder));

        /*
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getSupportLoaderManager().initLoader(GALLERY_LOADER, null, this);

        Drawable backIcon =
                ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_arrow_back_white_24dp);

        getSupportActionBar().setHomeAsUpIndicator(backIcon);

        //Spinner
        Spinner spinner = (Spinner) findViewById(R.id.age_spinner);

        List<String> years = new ArrayList<>();
        years.add("age");
        years.add("1 year");
        years.add("2 year");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, years);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return (super.onOptionsItemSelected(item));
    }

    @OnClick(R.id.search_fab)
    public void goToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.take_picture_fab)
    public void goToCameraApp() {
        if (handleMessage == null) {
            handleMessage = new PermissionsHelper.HandleMessage();
        }
        isAskingForPermission = handleMessage.isAskingForPermission();
        try {
            final Activity activity = this;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isAskingForPermission) {
                //Obtain permission type required
                int requiredPermissionType = PermissionsHelper.obtainPermissionType(activity);
                if (requiredPermissionType != PermissionsHelper.MY_PERMISSIONS_UNKNOWN) {
                    //Prepare Thread to show message and handler to management
                    handleMessage = new PermissionsHelper.HandleMessage();
                    handleMessage.isAskingForPermission(true);
                    PermissionsHelper.requestFeaturePermissions(
                            requiredPermissionType, activity, handleMessage);
                } else {

                    mFilePath = MediaHelper.useCameraIntent(activity, PICTURE_PREFIX);
                }
            } else if (!isAskingForPermission) {
                mFilePath = MediaHelper.useCameraIntent(activity, PICTURE_PREFIX);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: snack bar
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        isAskingForPermission = false;
        boolean permissionRequiredGranted = false;
        Activity activity = this;
        if (requestCode == PermissionsHelper.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 &&
                    PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                Log.v(LOG_TAG, "Write external storage permission granted!!");
                permissionRequiredGranted = true;
            }
        }
        if (permissionRequiredGranted) {
            Log.v(LOG_TAG, "Permission granted!!");
            try {
                mFilePath = MediaHelper.useCameraIntent(activity, PICTURE_PREFIX);
            } catch (Exception e) {
                //TODO: snack bar
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            Log.v(LOG_TAG, "Permission not granted");

            //Verify if user check on "Never ask again" and take him to application settings to
            //Grant permission manually
            if (PermissionsHelper.isCheckedDontAskAgain(
                    activity, PermissionsHelper.WRITE_EXTERNAL_STORAGE_PERMISSION)) {
                if (handleMessage == null) {
                    handleMessage = new PermissionsHelper.HandleMessage();
                    handleMessage.isAskingForPermission(true);
                }
                PermissionsHelper.showPermissionMessage(
                        getString(R.string.permission_from_settings_info),
                        getString(R.string.permission_from_settings_ok_button),
                        getString(R.string.permission_cancel_button),
                        null,
                        activity,
                        PermissionsHelper.MY_PERMISSIONS_UNKNOWN,
                        handleMessage);
            } else {
                stopPermissionDialog();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Context context = this;
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK
                || null == data) {
            Toast.makeText(context, "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
            return;
        }
        try {
            if (requestCode == MediaHelper.REQUEST_TAKE_PICTURE) {
                // When an Image is picked

                Gallery gallery = new Gallery();

                gallery.setProfileId(mProfile.getProfileId());
                gallery.setImagePath(mFilePath);
                gallery.setRangeAge(mProfile.getBirthDate());

                new SaveGalleryTask(this, gallery, mViewHolder.containerView).execute();
                MediaHelper.galleryAddPic(mFilePath, this);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void stopPermissionDialog() {
        if (null != handleMessage) {

            //Close message dialog if is open
            if (handleMessage.getLooper().getThread().isAlive()) {
                //Ensures thread is still alive
                handleMessage.sendEmptyMessage(0);
                handleMessage.isAskingForPermission(false);
            }
            isAskingForPermission = false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
        /*
        * Takes action based on the ID of the Loader that's being created
        */
        switch (loaderID) {
            case GALLERY_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        Uri.withAppendedPath(GalleryEntry.CONTENT_URI, String.valueOf(mProfile.getProfileId())),    // Table to query
                        GALLERY_COLUMNS,     // Projection to return
                        null,            // selection clause
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
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    static class ViewHolder {

        @BindView(R.id.grid_view_pictures)
        GridView gridView;

        @BindView(R.id.profile_image)
        ImageView headerImage;

        @BindView(R.id.gallery_container)
        CoordinatorLayout containerView;

        @BindView(R.id.collapsing_toolbar)
        CollapsingToolbarLayout collapsingToolbarLayout;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
