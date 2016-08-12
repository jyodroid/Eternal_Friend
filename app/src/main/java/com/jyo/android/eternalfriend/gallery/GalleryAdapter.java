package com.jyo.android.eternalfriend.gallery;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.gallery.model.Gallery;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JohnTangarife on 9/08/16.
 */
public class GalleryAdapter extends CursorAdapter {

    int COLUMN_GALLERY_ID_INDEX = 0;
    int COLUMN_GALLERY_IMAGE_INDEX = 1;
    int COLUMN_GALLERY_AGE_RANGE = 2;

    private Context mContext;

    public GalleryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.gallery_item_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template

        ViewHolder viewHolder = new ViewHolder(view);

        Glide
                .with(mContext)
                .load(cursor.getString(COLUMN_GALLERY_IMAGE_INDEX))
                .error(R.drawable.ic_image_black_48dp)
                .into(viewHolder.galleryImage);
    }

    static class ViewHolder {

        @BindView(R.id.gallery_image_item)
        ImageView galleryImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.share_img)
        public void share(){

        }
    }
}
