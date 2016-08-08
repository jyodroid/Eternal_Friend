package com.jyo.android.eternalfriend.profile_summarize;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile_summarize.model.Profile;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by johntangarife on 8/5/16.
 */
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private List<Profile> mProfiles;
    private Context mContext;

    public ProfileAdapter(List<Profile> profiles, Context context) {
        mProfiles = profiles;
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

        Glide
                .with(mContext)
                .load("http://goo.gl/gEgYUd")
                .error(R.drawable.ic_image_black_48dp)
                .into(holder.petPicture);

        holder.petAge.setText(mProfiles.get(position).getAge());
        holder.petName.setText(mProfiles.get(position).getName());
        holder.petBreed.setText(mProfiles.get(position).getBreed());
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profile_pet_picture)
        ImageView petPicture;

        @BindView(R.id.profile_pet_name)
        TextView petName;

        @BindView(R.id.profile_pet_age)
        TextView petAge;

        @BindView(R.id.profile_pet_breed)
        TextView petBreed;

        @BindView(R.id.profile_card)
        CardView profileCard;

        public ViewHolder(View itemView, final Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            profileCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }
}
