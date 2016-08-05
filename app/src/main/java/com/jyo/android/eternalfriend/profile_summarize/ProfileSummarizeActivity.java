package com.jyo.android.eternalfriend.profile_summarize;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jyo.android.eternalfriend.Map.MapsActivity;
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

        View rootView = findViewById(R.id.profile_container);

        ViewHolder viewHolder = new ViewHolder(rootView, this);

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

        Profile profile2 = new Profile();
        profile2.setBreed("Puddle");
        profile2.setName("Pako");

        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        profiles.add(profile2);

        profileAdapter = new ProfileAdapter(profiles, getBaseContext());
        viewHolder.recyclerView.setAdapter(profileAdapter);
    }

    static class ViewHolder {
        FloatingActionButton searchFab;
        FloatingActionButton addProfile;
        RecyclerView recyclerView;

        public ViewHolder(View view, final Activity acivity) {
            searchFab = (FloatingActionButton) view.findViewById(R.id.search_fab);
            searchFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(acivity, MapsActivity.class);
                    acivity.startActivity(intent);
                }
            });

            recyclerView = (RecyclerView) view.findViewById(R.id.profile_recycler_view);

        }
    }
}
