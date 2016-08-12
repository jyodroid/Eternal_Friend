package com.jyo.android.eternalfriend.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.gallery.GalleryActivity;
import com.jyo.android.eternalfriend.map.MapsActivity;
import com.jyo.android.eternalfriend.profile_summarize.model.Profile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    public static final String PROFILE_EXTRA = "profile_extra";
    private static final String LOG_TAG = ProfileActivity.class.getSimpleName();

    private Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        View rootView = findViewById(R.id.profile_view_container);
        ViewHolder viewHolder = new ViewHolder(rootView);

        Intent incoming = getIntent();
        mProfile = incoming.getParcelableExtra(PROFILE_EXTRA);

        Glide
                .with(this)
                .load(mProfile.getPicture())
                .error(R.drawable.ic_image_black_48dp)
                .into(viewHolder.profilePicture);

        viewHolder.name.setText(mProfile.getName());
        try {
            viewHolder.age.setText(mProfile.getAge());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Invalid age", e);
        }
        viewHolder.breed.setText(mProfile .getBreed());
    }

    @OnClick(R.id.gallery_button)
    public void goToGallery() {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra(PROFILE_EXTRA, mProfile);
        startActivity(intent);
    }

    @OnClick(R.id.clinical_history_button)
    public void goToClinicalHistory() {

    }

    @OnClick(R.id.vaccination_plan_button)
    public void goToVaccinationPlan() {

    }

    @OnClick(R.id.search_fab)
    public void goToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


    static class ViewHolder {

        @BindView(R.id.profile_pet_picture)
        ImageView profilePicture;

        @BindView(R.id.profile_pet_name)
        TextView name;

        @BindView(R.id.profile_pet_age)
        TextView age;

        @BindView(R.id.profile_pet_breed)
        TextView breed;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
