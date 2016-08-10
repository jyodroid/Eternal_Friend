package com.jyo.android.eternalfriend.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by JohnTangarife on 9/08/16.
 */
public class EFContract {

    public static final String CONTENT_AUTHORITY = "com.jyo.android.eternalfriend";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PROFILE = "profile";
    public static final String PATH_GALLERY = "gallery";
    public static final String PATH_CLINICAL_HISTORY = "clinical_history";
    public static final String PATH_VACCINATION_PLAN = "vaccination_plan";

    public static final class ProfileEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PROFILE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROFILE;

        // Table name
        public static final String TABLE_NAME = "profile";

        //Columns
        public static final String COLUMN_PROFILE_ID = "profile_id";
        public static final String COLUMN_PROFILE_IMAGE = "profile_image";
        public static final String COLUMN_PROFILE_NAME = "profile_name";
        public static final String COLUMN_PROFILE_BIRTH_DATE = "profile_birth_date";
        public static final String COLUMN_PROFILE_BREED = "profile_breed";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GalleryEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GALLERY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROFILE;

        // Table name
        public static final String TABLE_NAME = "gallery";

        //Columns
        public static final String COLUMN_PROFILE_ID = "profile_id";
        public static final String COLUMN_GALLERY_ID = "gallery_image_id";
        public static final String COLUMN_GALLERY_IMAGE = "gallery_image";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ClinicalHistoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLINICAL_HISTORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLINICAL_HISTORY;

        // Table name
        public static final String TABLE_NAME = "clinical_history";

        //Columns
        public static final String COLUMN_PROFILE_ID = "profile_id";
        public static final String COLUMN_CLINICAL_HISTORY_ID = "clinical_history_id";
        public static final String COLUMN_CLINICAL_HISTORY_DATE = "clinical_history_date";
        public static final String COLUMN_CLINICAL_HISTORY_HOSPITAL = "clinical_history_hospital";
        public static final String COLUMN_CLINICAL_HISTORY_PROGNOSTIC = "clinical_history_prognostic";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class VacccinationPlanEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VACCINATION_PLAN).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VACCINATION_PLAN;

        // Table name
        public static final String TABLE_NAME = "vaccination_plan";

        //Columns
        public static final String COLUMN_PROFILE_ID = "profile_id";
        public static final String COLUMN_VACCINATION_PLAN_ID = "vaccination_plan_id";
        public static final String COLUMN_VACCINATION_PLAN_DATE = "vaccination_plan_date";
        public static final String COLUMN_VACCINATION_PLAN_NAME = "vaccination_plan_name";
        public static final String COLUMN_VACCINATION_PLAN_STATUS = "vaccination_plan_status";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static String getProfileIdFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }
}
