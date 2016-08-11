package com.jyo.android.eternalfriend.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.jyo.android.eternalfriend.data.EFContract.ClinicalHistoryEntry;
import com.jyo.android.eternalfriend.data.EFContract.GalleryEntry;
import com.jyo.android.eternalfriend.data.EFContract.ProfileEntry;
import com.jyo.android.eternalfriend.data.EFContract.VacccinationPlanEntry;

public class EFContentProvider extends ContentProvider {


    private EFDBHelper mEFHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sQueryBuilder;

    private static final int PROFILE = 100;
    private static final int PROFILE_BY_ID = 101;
    private static final int CLINICAL_HISTORY = 200;
    private static final int CLINICAL_HISTORY_FOR_PROFILE = 201;
    private static final int GALLERY = 300;
    private static final int GALLERY_FOR_PROFILE = 301;
    private static final int VACCINATION_PLAN = 400;
    private static final int VACCINATION_PLAN_FOR_PROFILE = 401;

    private static final String PROFILE_TABLE_NAME = ProfileEntry.TABLE_NAME;
    private static final String CLINICAL_HISTORY_TABLE_NAME = ClinicalHistoryEntry.TABLE_NAME;
    private static final String GALLERY_TABLE_NAME = GalleryEntry.TABLE_NAME;
    private static final String VACCINATION_PLAN_TABLE_NAME = VacccinationPlanEntry.TABLE_NAME;

    private static final String sProfileSelection =
            PROFILE_TABLE_NAME + "." +
                    ProfileEntry.COLUMN_PROFILE_ID + " = ?";

    private static final String sClinicalHistorySelection =
            CLINICAL_HISTORY_TABLE_NAME + "." +
                    ClinicalHistoryEntry.COLUMN_PROFILE_ID + " = ?";

    private static final String sGallerySelection =
            GALLERY_TABLE_NAME + "." +
                    GalleryEntry.COLUMN_PROFILE_ID + " = ?";

    private static final String sVaccinationPlanSelection =
            VACCINATION_PLAN_TABLE_NAME + "." +
                    VacccinationPlanEntry.COLUMN_PROFILE_ID + " = ?";

    static{
        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setDistinct(true);
    }

    public EFContentProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mEFHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case PROFILE:
                rowsDeleted = db.delete(
                        PROFILE_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PROFILE:
                return ProfileEntry.CONTENT_TYPE;
            case PROFILE_BY_ID:
                return ProfileEntry.CONTENT_TYPE;
            case CLINICAL_HISTORY:
                return ClinicalHistoryEntry.CONTENT_TYPE;
            case CLINICAL_HISTORY_FOR_PROFILE:
                return ClinicalHistoryEntry.CONTENT_TYPE;
            case GALLERY:
                return GalleryEntry.CONTENT_TYPE;
            case GALLERY_FOR_PROFILE:
                return GalleryEntry.CONTENT_TYPE;
            case VACCINATION_PLAN:
                return VacccinationPlanEntry.CONTENT_TYPE;
            case VACCINATION_PLAN_FOR_PROFILE:
                return VacccinationPlanEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mEFHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PROFILE: {
                long _id = db.insert(PROFILE_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ProfileEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CLINICAL_HISTORY: {
                long _id = db.insert(CLINICAL_HISTORY_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ClinicalHistoryEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case GALLERY: {
                long _id = db.insert(GALLERY_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = GalleryEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VACCINATION_PLAN: {
                long _id = db.insert(VACCINATION_PLAN_TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = VacccinationPlanEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        mEFHelper = new EFDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case PROFILE:
            {
                retCursor = mEFHelper.getReadableDatabase().query(
                        PROFILE_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PROFILE_BY_ID: {
                retCursor = getProfileById(uri, projection, sortOrder);
                break;
            }
            case CLINICAL_HISTORY_FOR_PROFILE: {
                retCursor = getClinicalHistoryForProfile(uri, projection, sortOrder);
                break;
            }
            case GALLERY_FOR_PROFILE: {
                retCursor = getGalleryForProfile(uri, projection, sortOrder);
                break;
            }
            case VACCINATION_PLAN_FOR_PROFILE: {
                retCursor = getVaccinationPlanForProfile(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EFContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, EFContract.PATH_PROFILE, PROFILE);
        matcher.addURI(authority, EFContract.PATH_PROFILE + "/#", PROFILE_BY_ID);

        matcher.addURI(authority, EFContract.PATH_CLINICAL_HISTORY, CLINICAL_HISTORY);
        matcher.addURI(authority, EFContract.PATH_CLINICAL_HISTORY + "/#", CLINICAL_HISTORY_FOR_PROFILE);

        matcher.addURI(authority, EFContract.PATH_GALLERY, GALLERY);
        matcher.addURI(authority, EFContract.PATH_GALLERY + "/#", GALLERY_FOR_PROFILE);

        matcher.addURI(authority, EFContract.PATH_VACCINATION_PLAN, VACCINATION_PLAN);
        matcher.addURI(authority, EFContract.PATH_VACCINATION_PLAN + "/#", VACCINATION_PLAN_FOR_PROFILE);

        return matcher;
    }

    private Cursor getProfileById(Uri uri, String[] projection, String sortOrder){

        sQueryBuilder.setTables(PROFILE_TABLE_NAME);

        String profileId = EFContract.getProfileIdFromUri(uri);

        String[] selectionArgs = new String[]{profileId};

        return sQueryBuilder.query(mEFHelper.getReadableDatabase(),
                projection,
                sProfileSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getClinicalHistoryForProfile(Uri uri, String[] projection, String sortOrder) {

        sQueryBuilder.setTables(PROFILE_TABLE_NAME);

        String profileId = EFContract.getProfileIdFromUri(uri);

        String[] selectionArgs = new String[]{profileId};

        return sQueryBuilder.query(mEFHelper.getReadableDatabase(),
                projection,
                sClinicalHistorySelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getGalleryForProfile(Uri uri, String[] projection, String sortOrder) {

        sQueryBuilder.setTables(GALLERY_TABLE_NAME);

        String profileId = EFContract.getProfileIdFromUri(uri);

        String[] selectionArgs = new String[]{profileId};

        return sQueryBuilder.query(mEFHelper.getReadableDatabase(),
                projection,
                sGallerySelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getVaccinationPlanForProfile(Uri uri, String[] projection, String sortOrder) {

        sQueryBuilder.setTables(VACCINATION_PLAN_TABLE_NAME);

        String profileId = EFContract.getProfileIdFromUri(uri);

        String[] selectionArgs = new String[]{profileId};

        return sQueryBuilder.query(mEFHelper.getReadableDatabase(),
                projection,
                sVaccinationPlanSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
}