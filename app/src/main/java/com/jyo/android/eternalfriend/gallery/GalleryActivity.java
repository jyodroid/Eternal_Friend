package com.jyo.android.eternalfriend.gallery;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.map.MapsActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        View rootView = findViewById(R.id.gallery_container);

        View gridView = rootView.findViewById(R.id.grid_view_pictures);
        ViewCompat.setNestedScrollingEnabled(gridView,true);
        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.gallery_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);

        Drawable backIcon =
                ContextCompat.getDrawable(getBaseContext(), R.drawable.abc_ic_ab_back_material);

        getSupportActionBar().setHomeAsUpIndicator(backIcon);

        collapsingToolbarLayout.setTitle("Collapsing title");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return (super.onOptionsItemSelected(item));
    }

    @OnClick(R.id.search_fab)
    public void goToMap(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
