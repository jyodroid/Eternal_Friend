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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.commons.MediaHelper;
import com.jyo.android.eternalfriend.commons.PermissionsHelper;
import com.jyo.android.eternalfriend.data.EFContract.GalleryEntry;
import com.jyo.android.eternalfriend.gallery.async.SaveGalleryTask;
import com.jyo.android.eternalfriend.gallery.model.Gallery;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile.model.Profile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = GalleryActivity.class.getSimpleName();
    private static final String PICTURE_PREFIX = "Gallery";
    private static final String FILE_SEPARATOR = "_";
    private static final String AGES_KEY = "ages_key";
    private static final String AGE_SELECTION_KEY = "age_selection_key";
    private static final int GALLERY_LOADER = 1;
    private static final int AGES_LOADER = 2;

    //Query projection for gallery
    String[] GALLERY_COLUMNS = {
            GalleryEntry.COLUMN_GALLERY_ID,
            GalleryEntry.COLUMN_GALLERY_IMAGE
    };

    //Query projection for ages
    String[] AGES_COLUMNS = {
            GalleryEntry.COLUMN_GALLERY_AGE_RANGE
    };

    private String mFilePath;
    private boolean isAskingForPermission = false;
    private PermissionsHelper.HandleMessage handleMessage;
    private Profile mProfile;
    private ViewHolder mViewHolder;
    private GalleryAdapter mAdapter;
    private List<String> ages;
    private Menu mMenu;
    private String ageSelection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (savedInstanceState != null) {
            if (savedInstanceState.get(AGES_KEY) != null){
                ages = (List<String>) savedInstanceState.get(AGES_KEY);
            }
            if (savedInstanceState.get(AGE_SELECTION_KEY) != null){
                ageSelection = savedInstanceState.getString(AGE_SELECTION_KEY);
            }
        }
        ButterKnife.bind(this);

        View rootView = findViewById(R.id.gallery_container);
        mViewHolder = new ViewHolder(rootView);

        Intent incoming = getIntent();
        mProfile = incoming.getParcelableExtra(ProfileActivity.PROFILE_EXTRA);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mViewHolder
                .collapsingToolbarLayout
                .setTitle(
                        String.format(
                                getString(R.string.gallery_bar_title_format),
                                mProfile.getName()));

        Glide
                .with(this)
                .load(mProfile.getPicture())
                .error(R.drawable.ic_image_black_48dp)
                .into(mViewHolder.headerImage);

        //GridView
        ViewCompat.setNestedScrollingEnabled(mViewHolder.gridView, true);
        mAdapter = new GalleryAdapter(this, null);
        mViewHolder.gridView.setAdapter(mAdapter);
        mViewHolder.gridView.setEmptyView(findViewById(R.id.empty_message_holder));

        /*
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
        getSupportLoaderManager().initLoader(GALLERY_LOADER, null, this);

        Drawable backIcon =
                ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_arrow_back_white_24dp);

        getSupportActionBar().setHomeAsUpIndicator(backIcon);
    }

    @OnClick(R.id.take_picture_fab)
    public void goToCameraApp() {
        if (handleMessage == null) {
            handleMessage = new PermissionsHelper.HandleMessage();
        }
        isAskingForPermission = handleMessage.isAskingForPermission();
        try {
            final Activity activity = this;
            String prefix = PICTURE_PREFIX + FILE_SEPARATOR + mProfile.getName();
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
                    mFilePath = MediaHelper.useCameraIntent(activity, prefix);
                }
            } else if (!isAskingForPermission) {
                mFilePath = MediaHelper.useCameraIntent(activity, prefix);
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
                String prefix = PICTURE_PREFIX + FILE_SEPARATOR + mProfile.getName();
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
            Toast.makeText(context, getString(R.string.image_not_picked),
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
                        Uri.withAppendedPath(GalleryEntry.CONTENT_URI, String.valueOf(mProfile.getProfileId()) + ageSelection),    // Table to query
                        GALLERY_COLUMNS,     // Projection to return
                        null,            // selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            case AGES_LOADER:
                return new CursorLoader(
                        this,   // Parent activity context
                        Uri.withAppendedPath(GalleryEntry.CONTENT_URI, String.valueOf(mProfile.getProfileId())),    // Table to query
                        AGES_COLUMNS,     // Projection to return
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
        if (loader.getId() == GALLERY_LOADER){
            mAdapter.changeCursor(data);
        }
        if (loader.getId() == AGES_LOADER){
            ages.clear();
            ages.add(getString(R.string.gallery_image_all_ages));
            while (data.moveToNext()){
                int years = data.getInt(0);
                String ageStr = String.format(getString(R.string.gallery_image_years), years);
                if (years == 1){
                    ageStr = String.format(getString(R.string.gallery_image_year), years);
                }
                ages.add(ageStr);
            }
            onPrepareOptionsMenu(mMenu);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
        if (ages != null){
            ages.clear();
        }
        getSupportLoaderManager().restartLoader(AGES_LOADER, null, this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.age_spinner, menu);
        if (ages == null){
            ages = new ArrayList<>();
            getSupportLoaderManager().initLoader(AGES_LOADER, null, this);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.age_spinner);
        SubMenu ageMenu = item.getSubMenu();
        ageMenu.clear();

        for (String age:ages) {
            ageMenu.add(age);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }else
        if (item.getTitle()!= null){
            if (item.getTitle().equals(getString(R.string.gallery_image_all_ages))){
                ageSelection = "";
            }else {
                if (!item.getTitle().equals(getString(R.string.menu_age_title))){
                    String ageString = item.getTitle().toString().split("\\s+")[0];
                    ageSelection = "/" + ageString;
                }
            }
            getSupportLoaderManager().restartLoader(GALLERY_LOADER, null, this);
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(
                AGES_KEY, (ArrayList<String>) ages);
        outState.putString(
                AGE_SELECTION_KEY, ageSelection);
    }
}
