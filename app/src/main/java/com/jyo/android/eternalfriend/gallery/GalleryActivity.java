package com.jyo.android.eternalfriend.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.commons.MediaHelper;
import com.jyo.android.eternalfriend.commons.PermissionsHelper;
import com.jyo.android.eternalfriend.map.MapsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryActivity extends AppCompatActivity {

    private static final String LOG_TAG = GalleryActivity.class.getSimpleName();
    private static final String PICTURE_PREFIX = "Gallery";

    private String mFilePath;
    private boolean isAskingForPermission = false;
    private PermissionsHelper.HandleMessage handleMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        View rootView = findViewById(R.id.gallery_container);

        View gridView = rootView.findViewById(R.id.grid_view_pictures);
        ViewCompat.setNestedScrollingEnabled(gridView, true);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Drawable backIcon =
                ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_arrow_back_white_24dp);

        getSupportActionBar().setHomeAsUpIndicator(backIcon);

        Spinner spinner = (Spinner) findViewById(R.id.age_spinner);

        List<String> years = new ArrayList<>();
        years.add("age");
        years.add("1 year");
        years.add("2 year");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, years);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        collapsingToolbarLayout.setTitle("Collapsing title");
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
//                    MediaHelper.setPic(mViewHolder.petPicture, mFilePath);
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
}
