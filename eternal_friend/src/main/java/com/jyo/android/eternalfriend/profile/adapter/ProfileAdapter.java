package com.jyo.android.eternalfriend.profile.adapter;

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
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile.async.DeleteProfileTask;
import com.jyo.android.eternalfriend.profile.model.Profile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by johntangarife on 8/5/16.
 */
public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private static final String LOG_TAG = ProfileAdapter.class.getSimpleName();
    private Context mContext;
    private Cursor mCursor;
    private View mViewContainer;
    public static int deletePosition = -1;

    private static final int TYPE_PROFILE_CARD = 0;
    private static final int TYPE_DELETE_CARD = 1;

    int COLUMN_PROFILE_ID_INDEX = 0;
    int COLUMN_PROFILE_NAME_INDEX = 1;
    int COLUMN_PROFILE_BIRTH_DATE_INDEX = 2;
    int COLUMN_PROFILE_IMAGE_INDEX = 3;
    int COLUMN_PROFILE_BREED_INDEX = 4;


    public ProfileAdapter(Context context, View viewContainer) {
        mContext = context;
        mViewContainer = viewContainer;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_PROFILE_CARD:
                View profileLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_card_layout, parent, false);
                return new CardViewHolder(profileLayoutView, mContext);
            case TYPE_DELETE_CARD:
                View deleteLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.delete_card_layout, parent, false);
                return new DeleteViewHolder(deleteLayoutView, mContext, mViewContainer, this);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType +
                " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        Profile profile = createProfile(position);
        if (holder instanceof CardViewHolder) {

            CardViewHolder cardViewHolder = ((CardViewHolder) holder);

            cardViewHolder.setProfile(profile);

            Glide
                    .with(mContext)
                    .load(profile.getPicture())
                    .bitmapTransform(new CropCircleTransformation(mContext))
                    .error(R.drawable.ic_image_black_48dp)
                    .into(cardViewHolder.petPicture);
            try {
                cardViewHolder.petAge.setText(profile.getAge());
            } catch (Exception e) {
                Log.e(LOG_TAG, "invalid birth date", e);
            }
            cardViewHolder.petName.setText(profile.getName());
            cardViewHolder.petBreed.setText(profile.getBreed());
        } else if (holder instanceof DeleteViewHolder) {
            DeleteViewHolder deleteViewHolder = ((DeleteViewHolder) holder);
            deleteViewHolder.setProfile(profile);
            deleteViewHolder.deletePrompt
                    .setText(
                            String.format(
                                    mContext.getString(R.string.delete_prompt), profile.getName()));
        }
    }

    @Override
    public int getItemCount() {

        if (null != mCursor) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {

        //If is the Card Count Type
        if (deletePosition == position) {
            return TYPE_DELETE_CARD;
        }
        return TYPE_PROFILE_CARD;
    }

    @Override
    public void onItemDismiss(int position) {
        deletePosition = position;
        notifyItemChanged(position);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

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

        public CardViewHolder(View itemView, final Context context) {
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

    public static class DeleteViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private Profile mProfile;
        private View mViewContainer;
        private ProfileAdapter mProfileAdapter;

        @BindView(R.id.delete_text_prompt)
        TextView deletePrompt;

        public DeleteViewHolder(
                View itemView,
                final Context context,
                View viewContainer,
                ProfileAdapter profileAdapter) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            mViewContainer = viewContainer;
            mProfileAdapter = profileAdapter;
        }

        @OnClick(R.id.delete_button)
        public void deleteProfile() {
            new DeleteProfileTask(mContext, mProfile, mViewContainer).execute();
            deletePosition = -1;
        }

        @OnClick(R.id.cancel_delete_button)
        public void cancelDelete() {
            int position = deletePosition;
            deletePosition = -1;
            mProfileAdapter.notifyItemChanged(position);
        }

        public void setProfile(Profile profile) {
            mProfile = profile;
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    private Profile createProfile(int position) {

        Profile profile = new Profile();

        mCursor.moveToPosition(position);

        profile.setProfileId(mCursor.getInt(COLUMN_PROFILE_ID_INDEX));
        profile.setName(mCursor.getString(COLUMN_PROFILE_NAME_INDEX));
        profile.setPicture(mCursor.getString(COLUMN_PROFILE_IMAGE_INDEX));
        profile.setBirthDate(mCursor.getString(COLUMN_PROFILE_BIRTH_DATE_INDEX));
        profile.setBreed(mCursor.getString(COLUMN_PROFILE_BREED_INDEX));

        return profile;
    }
}
