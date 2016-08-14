package com.jyo.android.eternalfriend.gallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jyo.android.eternalfriend.R;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JohnTangarife on 9/08/16.
 */
public class GalleryAdapter extends CursorAdapter {

    int COLUMN_GALLERY_IMAGE_INDEX = 1;

    private static final String IMAGE_FORMAT = "image/jpeg";
    private int visible = 5;

    public GalleryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.gallery_item_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template

        String imagePath = cursor.getString(COLUMN_GALLERY_IMAGE_INDEX);
        ViewHolder viewHolder = new ViewHolder(view, imagePath, context);

        Glide
                .with(context)
                .load(imagePath)
                .error(R.drawable.ic_image_black_48dp)
                .into(viewHolder.galleryImage);
    }

    static class ViewHolder {

        @BindView(R.id.gallery_image_item)
        ImageView galleryImage;

        private String mImagePath;
        private Context mContext;

        public ViewHolder(View view, String imagePath, Context context) {
            ButterKnife.bind(this, view);
            mImagePath = imagePath;
            mContext = context;
        }

        @OnClick(R.id.share_img)
        public void share(){
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            File file = new File(mImagePath);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareIntent.setType(IMAGE_FORMAT);
            mContext.startActivity(
                    Intent.createChooser(shareIntent, mContext.getString(R.string.share_with)));
        }

        @OnClick(R.id.gallery_image_item)
        public void openInGallery(){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(mImagePath);
            intent.setDataAndType(Uri.fromFile(file), IMAGE_FORMAT);
            mContext.startActivity(intent);
        }
    }
}
