package com.jyo.android.eternalfriend.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.commons.MediaHelper;
import com.jyo.android.eternalfriend.profile_summarize.model.Profile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    public static final String PROFILE_EXTRA= "profile_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        View rootView = findViewById(R.id.profile_view_container);
        ViewHolder viewHolder = new ViewHolder(rootView);

        Intent incoming = getIntent();
        Profile profile = incoming.getParcelableExtra(PROFILE_EXTRA);

        if (profile.getPicture() != null){
            byte[] image = MediaHelper.bitmapToArray(profile.getPicture());

            Glide
                    .with(this)
                    .load(image)
                    .error(R.drawable.ic_image_black_48dp)
                    .into(viewHolder.profilePicture);
        }

        viewHolder.name.setText(profile.getName());
        viewHolder.age.setText(profile.getAge());
        viewHolder.breed.setText(profile.getBreed());
    }

    @OnClick(R.id.gallery_button)
    public void goToGallery(){

    }

    @OnClick(R.id.clinical_history_button)
    public void goToClinicalHistory(){

    }

    @OnClick(R.id.vaccination_plan_button)
    public void goToVaccinationPlan(){

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
            ButterKnife.bind(this,view);
        }
    }
}
