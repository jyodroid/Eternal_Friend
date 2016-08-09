package com.jyo.android.eternalfriend.profile_summarize;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jyo.android.eternalfriend.map.MapsActivity;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.profile_summarize.model.Profile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileSummarizeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_summarize);
        ButterKnife.bind(this);

        View rootView = findViewById(R.id.profile_container);

        ViewHolder viewHolder = new ViewHolder(rootView);

        //improve performance
        viewHolder.recyclerView.setHasFixedSize(true);

        //Design manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        viewHolder.recyclerView.setLayoutManager(layoutManager);

        //Adapter
        RecyclerView.Adapter profileAdapter;

        Profile profile = new Profile();
        profile.setBreed("Schnauzer");
        profile.setName("Kevin");
        profile.setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.ic_veterinarian));

        Profile profile2 = new Profile();
        profile2.setBreed("Puddle");
        profile2.setName("Pako");
        profile2.setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.ic_pet_shop));

        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        profiles.add(profile2);

        profileAdapter = new ProfileAdapter(profiles, this);
        viewHolder.recyclerView.setAdapter(profileAdapter);
    }

    @OnClick(R.id.search_fab)
    public void goToMap(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.add_fab)
    public void goToAddProfile(){
        Intent intent = new Intent(this, AddProfileActivity.class);
        startActivity(intent);
    }

    static class ViewHolder {
        @BindView(R.id.profile_recycler_view)
        RecyclerView recyclerView;
        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
