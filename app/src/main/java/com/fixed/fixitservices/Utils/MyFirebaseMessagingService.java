package com.fixed.fixitservices.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import android.util.Log;


import com.fixed.fixitservices.Activities.ModifiedOrder;
import com.fixed.fixitservices.Activities.MyOrders;
import com.fixed.fixitservices.Notifications.NotificationHistory;
import com.fixed.fixitservices.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by AliAh on 01/03/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String msg;
    String title, message, type;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String username;
    private String Id;
    private String OtherUserId;
    private String AdId;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("message payload", "Message data payload: " + remoteMessage.getData());
            msg = "" + remoteMessage.getData();

            Map<String, String> map = remoteMessage.getData();

            message = map.get("Message");
            title = map.get("Title");
            type = map.get("Type");
            AdId = map.get("AdId");
            OtherUserId = map.get("OtherUserId");
//            username = map.get("Username");
            Id = map.get("Id");
            handleNow(title, message, type);
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow(msg);
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("body", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void handleNow(String notificationTitle, String messageBody, String type) {

        int num = (int) System.currentTimeMillis();
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = null;
        if (type.equalsIgnoreCase("Modify")) {
            resultIntent = new Intent(this, ModifiedOrder.class);
            resultIntent.putExtra("orderId", Id);

        } else if (type.equalsIgnoreCase("jobDone")) {
            resultIntent = new Intent(this, MyOrders.class);
            resultIntent.putExtra("orderId", Id);

        } else if (type.equalsIgnoreCase("totalBill")) {
            resultIntent = new Intent(this, MyOrders.class);
            resultIntent.putExtra("orderId", Id);

        } else if (type.equalsIgnoreCase("jobStart")) {
            resultIntent = new Intent(this, MyOrders.class);
            resultIntent.putExtra("orderId", Id);

        } else if (type.equalsIgnoreCase("Arrived")) {
            resultIntent = new Intent(this, MyOrders.class);
//            resultIntent.putExtra("orderId", Id);

        } else if (type.equalsIgnoreCase("paymentReceived")) {
            resultIntent = new Intent(this, MyOrders.class);
            resultIntent.putExtra("orderId", Id);
            resultIntent.putExtra("rating", 1);

        } else if (type.equalsIgnoreCase("marketing")) {
            resultIntent = new Intent(this, NotificationHistory.class);


        } else {

            resultIntent = new Intent(this, MyOrders.class);

        }

//        resultIntent = new Intent(this, MainActivity.class);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(notificationTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(num /* Request Code */, mBuilder.build());
    }
}
