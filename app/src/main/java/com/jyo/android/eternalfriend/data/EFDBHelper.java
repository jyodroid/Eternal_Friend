package com.jyo.android.eternalfriend.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jyo.android.eternalfriend.data.EFContract.*;
/**
 * Created by JohnTangarife on 9/08/16.
 */
public class EFDBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 8;
    static final String DATABASE_NAME = "eternal_friend.db";

    public EFDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold profiles.
        final String SQL_CREATE_PROFILE_TABLE = "CREATE TABLE " + ProfileEntry.TABLE_NAME + " (" +
                ProfileEntry.COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ProfileEntry.COLUMN_PROFILE_IMAGE + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_PROFILE_NAME + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_PROFILE_BIRTH_DATE + " TEXT NOT NULL, " +
                ProfileEntry.COLUMN_PROFILE_BREED + " TEXT NOT NULL " +
                " );";

        // Create a table to hold gallery.
        final String SQL_CREATE_GALLERY_TABLE = "CREATE TABLE " + GalleryEntry.TABLE_NAME + " (" +
                GalleryEntry.COLUMN_GALLERY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GalleryEntry.COLUMN_PROFILE_ID + " INTEGER NOT NULL, " +
                GalleryEntry.COLUMN_GALLERY_IMAGE + " TEXT NOT NULL, " +
                GalleryEntry.COLUMN_GALLERY_AGE_RANGE + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + GalleryEntry.COLUMN_PROFILE_ID + ") REFERENCES " +
                ProfileEntry.TABLE_NAME + "(" + ProfileEntry.COLUMN_PROFILE_ID + ") ON DELETE CASCADE" +
                " );";

        // Create a table to hold clinical history.
        final String SQL_CREATE_CLINICAL_HISTORY_TABLE = "CREATE TABLE " + ClinicalHistoryEntry.TABLE_NAME + " (" +
                ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ClinicalHistoryEntry.COLUMN_PROFILE_ID + " INTEGER NOT NULL, " +
                ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_DATE + " TEXT NOT NULL, " +
                ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_HOSPITAL + " TEXT NOT NULL, " +
                ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_DIAGNOSTIC + " TEXT NOT NULL, " +
                ClinicalHistoryEntry.COLUMN_CLINICAL_HISTORY_TREATMENT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + ClinicalHistoryEntry.COLUMN_PROFILE_ID + ") REFERENCES " +
                ProfileEntry.TABLE_NAME + "(" + ProfileEntry.COLUMN_PROFILE_ID + ") ON DELETE CASCADE" +
                " );";

        // Create a table to hold clinical history.
        final String SQL_CREATE_VACCINATION_PLAN_TABLE = "CREATE TABLE " + VaccinationPlanEntry.TABLE_NAME + " (" +
                VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VaccinationPlanEntry.COLUMN_PROFILE_ID + " INTEGER NOT NULL, " +
                VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_DATE + " TEXT NOT NULL, " +
                VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_NAME + " TEXT NOT NULL, " +
                VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_STATUS + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + VaccinationPlanEntry.COLUMN_PROFILE_ID + ") REFERENCES " +
                ProfileEntry.TABLE_NAME + "(" + ProfileEntry.COLUMN_PROFILE_ID + ") ON DELETE CASCADE" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_PROFILE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CLINICAL_HISTORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GALLERY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VACCINATION_PLAN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Version 1, no needs yet to implement database migration
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GalleryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ClinicalHistoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VaccinationPlanEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
