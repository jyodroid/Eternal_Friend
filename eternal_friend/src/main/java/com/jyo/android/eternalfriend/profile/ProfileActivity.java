package com.jyo.android.eternalfriend.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.clinical_history.ClinicalHistoryActivity;
import com.jyo.android.eternalfriend.gallery.GalleryActivity;
import com.jyo.android.eternalfriend.map.MapsActivity;
import com.jyo.android.eternalfriend.profile.model.Profile;
import com.jyo.android.eternalfriend.vaccination_plan.VaccinationActivity;

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
        viewHolder.breed.setText(mProfile .getBreed());
        viewHolder.age.setText(String.format(getString(R.string.birth_date_format), mProfile.getBirthDate()));
    }

    @OnClick(R.id.gallery_button)
    public void goToGallery() {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra(PROFILE_EXTRA, mProfile);
        startActivity(intent);
    }

    @OnClick(R.id.clinical_history_button)
    public void goToClinicalHistory() {
        Intent intent = new Intent(this, ClinicalHistoryActivity.class);
        intent.putExtra(PROFILE_EXTRA, mProfile);
        startActivity(intent);
    }

    @OnClick(R.id.vaccination_plan_button)
    public void goToVaccinationPlan() {
        Intent intent = new Intent(this, VaccinationActivity.class);
        intent.putExtra(PROFILE_EXTRA, mProfile);
        startActivity(intent);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, ProfileSummarizeActivity.class);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
