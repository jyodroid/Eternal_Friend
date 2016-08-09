package com.jyo.android.eternalfriend.profile_summarize;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jyo.android.eternalfriend.map.MapsActivity;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.commons.MediaHelper;
import com.jyo.android.eternalfriend.commons.PermissionsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddProfileActivity extends AppCompatActivity
        implements
        DatePickerDialog.OnDateSetListener,
        AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = AddProfileActivity.class.getSimpleName();

    private static final String CATS_BREED_JSON = "cats_breed_list.json";
    private static final String CATS_BREED_LIST_NAME = "cats";
    private static final String DOGS_BREED_JSON = "dog_breed_list.json";
    private static final String DOGS_BREED_LIST_NAME = "dogs";
    private static final String JSON_FORMAT = "UTF-8";
    private static final String PICTURE_PREFIX = "Profile";
    private static final String FILE_PROVIDER_AUTHORITY = "com.jyo.android.eternalfriend.fileprovider";

    public static final int REQUEST_TAKE_PICTURE = 1;
    public static final int REQUEST_OBTAIN_FROM_GALLERY = 2;

    private DatePickerDialog mDatePickerDialog;
    private ViewHolder mViewHolder;
    private String[] mDogsBreedList;
    private String[] mCatsBreedList;
    private boolean isCatsSelected;
    private String mFilePath;

    private boolean isAskingForPermission = false;
    private PermissionsHelper.HandleMessage handleMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_profile);
        ButterKnife.bind(this);

        View rootView = findViewById(R.id.profile_form_container);
        mViewHolder = new ViewHolder(rootView);

        isCatsSelected = true;

        String text = String.format(
                getString(R.string.date_picker_date_text),
                getString(R.string.pet_birth_value));

        Spanned underLinedText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            underLinedText = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            underLinedText = Html.fromHtml(text);
        }

        mViewHolder.birdDate.setText(underLinedText);
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            underLinedText = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            underLinedText = Html.fromHtml(text);
        }
        mViewHolder.birdDate.setText(underLinedText);
    }

    @OnClick(R.id.birth_date_value)
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

    @OnClick(R.id.search_fab)
    public void goToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.profile_pet_picture_set)
    public void obtainImage() {
        final String[] items = new String[]{"Take a picture", "Choose from Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, items);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle("Profile Image");
        final Activity activity = this;

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    if (handleMessage == null) {
                        handleMessage = new PermissionsHelper.HandleMessage();
                    }
                    isAskingForPermission = handleMessage.isAskingForPermission();
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
                            useCameraIntent(activity);
                        }
                    } else if (!isAskingForPermission) {
                        useCameraIntent(activity);
                    }
                } else {
                    // Create intent to Open Image applications like Gallery, Google Photos
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // Start the Intent
                    startActivityForResult(galleryIntent, REQUEST_OBTAIN_FROM_GALLERY);
                }

                dialog.cancel();
            }
        });

        android.app.AlertDialog pickerDialog = builder.create();
        pickerDialog.show();
    }

    @OnClick(R.id.breed_list)
    public void showBreeds() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final String[] breedList;
        String dialogTitle;
        if (isCatsSelected) {
            breedList = obtainBreedList(CATS_BREED_LIST_NAME);
            dialogTitle = "Cat Breeds";
        } else {
            breedList = obtainBreedList(DOGS_BREED_LIST_NAME);
            dialogTitle = "Dog Breeds";
        }
        dialogBuilder.setTitle(dialogTitle);
        dialogBuilder.setItems(breedList, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mViewHolder.breed.setText(breedList[which]);
            }

        });

        dialogBuilder.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar selectedDate = new GregorianCalendar(year, month, day);

        setDatePickerTextView(selectedDate.getTime());

        mDatePickerDialog.dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String selection = adapterView.getItemAtPosition(pos).toString();
        mViewHolder.breed.setText(selection);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    static class ViewHolder {
        @BindView(R.id.birth_date_value)
        TextView birdDate;

        @BindView(R.id.breed_value)
        EditText breed;

        @BindView(R.id.breed_list)
        ImageButton breedList;

        @BindView(R.id.profile_pet_picture_set)
        ImageView petPicture;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void onResume() {
        if (handleMessage != null) {
            handleMessage.isAskingForPermission(false);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopPermissionDialog();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE:
                    // When an Image is picked
                    MediaHelper.setPic(mViewHolder.petPicture, mFilePath);
                    galleryAddPic();
                    break;
                case REQUEST_OBTAIN_FROM_GALLERY:
                    Uri selectedImage = data.getData();

                    Bitmap mImage = BitmapFactory.decodeFile(mFilePath);
                    File pictureFile = new File(mFilePath);
                    //Adjust the portrait view
                    if (mImage.getWidth() > mImage.getHeight() && pictureFile != null) {
                        mImage = MediaHelper.portraitRotation(mImage, pictureFile);
                    }

                    mViewHolder.petPicture.setImageBitmap(mImage);
                    break;

            }

        } catch (Exception e) {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
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
            useCameraIntent(activity);
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        mViewHolder.breedList.setEnabled(true);
        mViewHolder.breedList.setVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.radio_cat:
                if (checked)
                    isCatsSelected = true;
                break;
            case R.id.radio_dog:
                if (checked)
                    isCatsSelected = false;
                break;
            case R.id.radio_other:
                if (checked)
                    mViewHolder.breedList.setEnabled(false);
                mViewHolder.breedList.setVisibility(View.GONE);
                break;
        }
    }


    private String[] obtainBreedList(String listName) {
        if (CATS_BREED_LIST_NAME.equals(listName)) {
            if (mCatsBreedList == null) {
                mCatsBreedList = loadBreedsFromJSON(CATS_BREED_JSON, listName);
            }
            return mCatsBreedList;
        } else {
            if (mDogsBreedList == null) {
                mDogsBreedList = loadBreedsFromJSON(DOGS_BREED_JSON, listName);
            }
            return mDogsBreedList;
        }
    }


    private String[] loadBreedsFromJSON(String jsonName, String listName) {

        List<String> breeds = new ArrayList<>();
        try {
            InputStream is = this.getAssets().open(jsonName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, JSON_FORMAT);

            JSONObject obj = new JSONObject(json);
            JSONArray jsonArray = obj.getJSONArray(listName);

            for (int i = 0; i < jsonArray.length(); i++) {
                breeds.add(jsonArray.getString(i));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] arrayBreed = new String[breeds.size()];
        arrayBreed = breeds.toArray(arrayBreed);
        return arrayBreed;
    }

    private void galleryAddPic() throws IOException {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mFilePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void useCameraIntent(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = MediaHelper.createImageFile(activity, PICTURE_PREFIX);
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "Error obtaining file dir", ioe);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mFilePath = photoFile.getAbsolutePath();
                Uri photoURI =
                        FileProvider.getUriForFile(activity,
                                FILE_PROVIDER_AUTHORITY,
                                photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
            }
        } else {
            Log.e(LOG_TAG, "Can't use device's camera");
            //TODO: snackbar
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
