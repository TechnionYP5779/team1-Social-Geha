package com.example.ofir.social_geha;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class GehaMessagingService extends FirebaseMessagingService {
    private static String TAG = "CEMService";
    private String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
    private static String FROM_PERSON_ID = "fromPersonID";
    private FirebaseAuth auth;
    FirebaseFirestore database;
    public GehaMessagingService() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Got notification "+remoteMessage.getNotification().getTitle());
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage){
        initChannels(this);
        long[] vPattern = {0, 1000};
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(vPattern)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        String clickAction = remoteMessage.getNotification().getClickAction();
        handleIntent(notificationBuilder, clickAction, remoteMessage);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int)System.currentTimeMillis(), notificationBuilder.build());
    }

    private void handleIntent(NotificationCompat.Builder notificationBuilder, String clickAction, RemoteMessage remoteMessage){
        Intent resultIntent = new Intent(clickAction);
        String fromPersonID = remoteMessage.getData().get(FROM_PERSON_ID);
        Log.d(TAG, "handleIntent: the message was from: "+fromPersonID);
        if(fromPersonID != null){
            resultIntent.putExtra(FROM_PERSON_ID, fromPersonID);
        }
        // Create an Intent for the activity you want to start
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);
        notificationBuilder.setAutoCancel(true);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        storeToken(auth, database, s);
    }

    public static void storeToken(FirebaseAuth auth, FirebaseFirestore database, String devToken){
        if(auth.getCurrentUser() != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("deviceToken",devToken);

            Log.d("GETNEWTOKEN", "storeToken: STORING in:" +auth.getUid()+" the token: "+devToken);
            database.collection("deviceTokens")
                    .document(auth.getUid())
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: UPDATED TOKEN");
                        }
                    });
        }
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "notifications channel",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }
}
