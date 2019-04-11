package com.course.android.uimessages;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.course.android.uimessages.ACTION_UPDATE_NOTIFICATION";
    private static final String ACTION_DISMISS_NOTIFICATION =
            "com.course.android.uimessages.ACTION_DISMISS_NOTIFICATION";

    public static final int NOTIFICATION_ID = 0;

    private View mParentLayout;
    private NotificationManager mNotifyManager;
    private Button mButtonNotify;
    private Button mButtonCancelNotif;
    private Button mButtonUpdateNotif;
    private NotificationReceiver mReceiver = new NotificationReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mParentLayout = findViewById(android.R.id.content);
        mButtonNotify = findViewById(R.id.notification);
        mButtonCancelNotif = findViewById(R.id.cancelNotification);
        mButtonUpdateNotif = findViewById(R.id.updateNotification);

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        createNotificationChannel();
        setNotificationButtonState(true, false, false);

        registerReceiver(mReceiver,new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        registerReceiver(mReceiver,new IntentFilter(ACTION_DISMISS_NOTIFICATION));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void toastExample(View view) {
        Toast.makeText(getApplicationContext(), getString(R.string.toast_example), Toast.LENGTH_LONG).show();
    }

    public void snackbarExample(View view) {
        final Snackbar snackbar = Snackbar.make(mParentLayout, R.string.snackbar_example, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public void notificationExample(View view) {
        NotificationCompat.Builder builder = getNotificationBuilder();
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.addAction(R.drawable.ic_update, getString(R.string.update_notification), updatePendingIntent);
        mNotifyManager.notify(NOTIFICATION_ID, builder.build());
        setNotificationButtonState(false, true, true);
    }

    public void cancelNotification(View view) {
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false);
    }

    public void updateNotification(View view) {
        NotificationCompat.Builder builder = getNotificationBuilder();
        Bitmap androidImage = BitmapFactory
                .decodeResource(getResources(),R.drawable.mascot_1);
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage).setBigContentTitle(getString(R.string.notification_updated)));
        mNotifyManager.notify(NOTIFICATION_ID, builder.build());
        setNotificationButtonState(false, false, true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    getString(R.string.example_notification_channel), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(getString(R.string.notification_channel_description));
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }


    public  NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentDelete = PendingIntent.getBroadcast(this, NOTIFICATION_ID, new Intent(ACTION_DISMISS_NOTIFICATION), PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_active_blue)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentIntent(pendingIntent)
                .setDeleteIntent(pendingIntentDelete)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return builder;
    }


    void setNotificationButtonState(Boolean isNotifyEnabled,
                                    Boolean isUpdateEnabled,
                                    Boolean isCancelEnabled) {
        mButtonNotify.setEnabled(isNotifyEnabled);
        mButtonUpdateNotif.setEnabled(isUpdateEnabled);
        mButtonCancelNotif.setEnabled(isCancelEnabled);
    }


    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            if (intent != null && ACTION_UPDATE_NOTIFICATION.equals(intent.getAction())) {
                updateNotification(null);
            } else if (intent != null && ACTION_DISMISS_NOTIFICATION.equals(intent.getAction())) {
                cancelNotification(null);
            }

        }

    }

}

