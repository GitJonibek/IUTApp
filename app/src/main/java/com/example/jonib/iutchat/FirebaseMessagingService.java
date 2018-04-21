package com.example.jonib.iutchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_body = remoteMessage.getNotification().getBody();

        String clickActon = remoteMessage.getNotification().getClickAction();

        String user_id = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_round))
                .setContentTitle(notification_title)
                .setContentText(notification_body)
                .setAutoCancel(true);

        int mNotifId = (int) System.currentTimeMillis();

        Intent resultIntent = new Intent(clickActon);
        resultIntent.putExtra("user_id", user_id);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifManager.notify(mNotifId, mBuilder.build());

    }
}
