package com.jyo.android.eternalfriend.profile_summarize;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.data.EFContract;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile_summarize.model.Profile;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by johntangarife on 8/5/16.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private static final String LOG_TAG = ProfileAdapter.class.getSimpleName();
    private List<Profile> mProfiles;
    private Context mContext;

    public ProfileAdapter(Context context) {
        mProfiles = new ArrayList<>();
        mContext = context;
    }

    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_card_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(layoutView, mContext);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Profile profile = mProfiles.get(position);
        holder.setProfile(profile);

        Glide
                .with(mContext)
                .load(profile.getPicture())
                .error(R.drawable.ic_image_black_48dp)
                .into(holder.petPicture);

        try {
            holder.petAge.setText(profile.getAge());
        } catch (Exception e) {
            Log.e(LOG_TAG, "invalid birth date", e);
        }
        holder.petName.setText(profile.getName());
        holder.petBreed.setText(profile.getBreed());
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private Profile mProfile;

        @BindView(R.id.profile_pet_picture)
        ImageView petPicture;

        @BindView(R.id.profile_pet_name)
        TextView petName;

        @BindView(R.id.profile_pet_age)
        TextView petAge;

        @BindView(R.id.profile_pet_breed)
        TextView petBreed;

        public ViewHolder(View itemView, final Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
        }

        @OnClick(R.id.profile_card)
        public void goToProfile() {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            intent.putExtra(ProfileActivity.PROFILE_EXTRA, mProfile);
            mContext.startActivity(intent);
        }

        public void setProfile(Profile profile) {
            mProfile = profile;
        }
    }

    public void changeCursor(Cursor cursor) {

        final int profileIdIndx = cursor.getColumnIndex(EFContract.ProfileEntry.COLUMN_PROFILE_ID);
        final int profileNameIndx = cursor.getColumnIndex(EFContract.ProfileEntry.COLUMN_PROFILE_NAME);
        final int profileBirthDateIndx = cursor.getColumnIndex(EFContract.ProfileEntry.COLUMN_PROFILE_BIRTH_DATE);
        final int profileImageIndx = cursor.getColumnIndex(EFContract.ProfileEntry.COLUMN_PROFILE_IMAGE);
        final int profileBreedIndx = cursor.getColumnIndex(EFContract.ProfileEntry.COLUMN_PROFILE_BREED);

        try {
            while (cursor.moveToNext()) {

                Profile profile = new Profile();

                profile.setProfileId(cursor.getInt(profileIdIndx));
                profile.setName(cursor.getString(profileNameIndx));
                profile.setPicture(cursor.getString(profileImageIndx));
                Date birthDate = profile.dateFormat.parse(cursor.getString(profileBirthDateIndx));
                profile.setBirthDate(birthDate);
                profile.setBreed(cursor.getString(profileBreedIndx));

                mProfiles.add(profile);
            }
        } catch (ParseException pe) {
            Log.e(LOG_TAG, "Date parse exception", pe);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }

    public void changeCursor() {
        mProfiles.clear();
    }

    public List<Profile> getProfiles(){
        return mProfiles;
    }

    public void setProfiles(List<Profile> profiles){
        mProfiles = profiles;
    }

}
