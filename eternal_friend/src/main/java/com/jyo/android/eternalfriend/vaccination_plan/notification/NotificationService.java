package com.jyo.android.eternalfriend.vaccination_plan.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.data.EFContract;
import com.jyo.android.eternalfriend.data.EFContract.ProfileEntry;
import com.jyo.android.eternalfriend.profile.ProfileActivity;
import com.jyo.android.eternalfriend.profile.model.Profile;
import com.jyo.android.eternalfriend.vaccination_plan.VaccinationActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JohnTangarife on 15/08/16.
 */
public class NotificationService extends Service {
    //Notification code as package name hashcode
    public static final int NOTIFICATION_ID = -521327359;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Context mContext;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                //get Pets profiles
                List<Profile> profiles = obtainProfiles();

                StringBuilder namesBuilder = new StringBuilder();

                //Search for a pet with vaccination status 0
                for (int i = 0; i < profiles.size(); i++) {
                    boolean isVaccinationPending = checkVaccinationPending(profiles.get(i).getProfileId());
                    if (isVaccinationPending){
                        namesBuilder.append(profiles.get(i).getName());
                        if (i != profiles.size() -1){
                            namesBuilder.append(", ");
                        }
                    }
                }

                if (namesBuilder.length() != 0){
                    createNotification(namesBuilder.toString(), profiles.get(0));
                }
            } catch (Exception e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mContext = this;

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    private List<Profile> obtainProfiles(){
        List<Profile> profiles = new ArrayList<>();

        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor =
                resolver.query(ProfileEntry.CONTENT_URI, null, null, null, null);

        //Obtain favorite movies
        try {
            int profileIdIndx = cursor.getColumnIndex(ProfileEntry.COLUMN_PROFILE_ID);
            int profileNameIndx = cursor.getColumnIndex(ProfileEntry.COLUMN_PROFILE_NAME);
            int profilePictureIndx = cursor.getColumnIndex(ProfileEntry.COLUMN_PROFILE_IMAGE);
            int profileBreedIndx = cursor.getColumnIndex(ProfileEntry.COLUMN_PROFILE_BREED);
            int profileBDIndx = cursor.getColumnIndex(ProfileEntry.COLUMN_PROFILE_BIRTH_DATE);

            while(cursor.moveToNext()){

                Profile profile = new Profile();
                profile.setProfileId(cursor.getInt(profileIdIndx));
                profile.setName(cursor.getString(profileNameIndx));
                profile.setPicture(cursor.getString(profilePictureIndx));
                profile.setBreed(cursor.getString(profileBreedIndx));
                profile.setBirthDate(cursor.getString(profileBDIndx));

                profiles.add(profile);
            }
        }finally {
            if (null != cursor){
                cursor.close();
            }
        }
        return profiles;
    }

    private boolean checkVaccinationPending(int profileId) {

        boolean hasPendingVaccinations = false;

        ContentResolver resolver = mContext.getContentResolver();

        Uri uri = Uri.withAppendedPath(EFContract.VaccinationPlanEntry.CONTENT_URI, String.valueOf(profileId));

        Cursor cursor = resolver.query(
                uri,
                new String[]{EFContract.VaccinationPlanEntry.COLUMN_VACCINATION_PLAN_STATUS},
                null,
                null,
                null);

        try {
            while (cursor.moveToNext()){
                if (cursor.getInt(0) != 1){
                    hasPendingVaccinations = true;
                    return hasPendingVaccinations;
                }
            }
        } finally {
            if (null != cursor){
                cursor.close();
            }
        }

        return hasPendingVaccinations;
    }

    private void createNotification(String petsList, Profile petProfile) {
        Intent resultIntent = new Intent(mContext, VaccinationActivity.class);
        resultIntent.putExtra(ProfileActivity.PROFILE_EXTRA, petProfile);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher );

        String notificationText = String.format(getString(R.string.notification_content), petsList);

        //Notification
        Notification notification = new Notification.Builder(mContext)
                .setContentTitle(getString(R.string.notification_title))
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.ic_stat_schnauzer)
                .setContentIntent(resultPendingIntent)
                .setContentText(notificationText)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[] { 100, 1000, 100 })
                .setStyle(new Notification.BigTextStyle()
                        .bigText(notificationText))
                .build();

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(NOTIFICATION_ID,notification);
    }
}
