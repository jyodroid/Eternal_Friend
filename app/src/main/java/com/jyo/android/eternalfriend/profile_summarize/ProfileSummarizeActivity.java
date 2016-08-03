package com.jyo.android.eternalfriend.profile_summarize;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.jyo.android.eternalfriend.Map.MapsActivity;
import com.jyo.android.eternalfriend.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileSummarizeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_summarize);

        View rootView = findViewById(R.id.profile_container);

        ViewHolder viewHolder = new ViewHolder(rootView, this);
    }


    @OnClick(R.id.search_fab)
    public void submit(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        this.startActivity(intent);
    }


    static class ViewHolder {
        FloatingActionButton searchFab;
        FloatingActionButton addProfile;

        public ViewHolder(View view, final Activity acivity) {
            ButterKnife.bind(this, view);

            searchFab = (FloatingActionButton) view.findViewById(R.id.search_fab);
            searchFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(acivity, MapsActivity.class);
                    acivity.startActivity(intent);
                }
            });

        }
    }
}
