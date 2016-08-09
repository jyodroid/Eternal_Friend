package com.jyo.android.eternalfriend.gallery;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jyo.android.eternalfriend.R;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        View rootView = findViewById(R.id.gallery_container);

        View gridView = rootView.findViewById(R.id.grid_view_pictures);
        ViewCompat.setNestedScrollingEnabled(gridView,true);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle("Collapsing title");
    }
}
