package com.example.ofir.social_geha;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.ofir.social_geha.Activities_and_Fragments.AllChatsActivity;
import com.example.ofir.social_geha.Activities_and_Fragments.ChatActivity;
import com.example.ofir.social_geha.Activities_and_Fragments.FileHandlers.ContactListFileHandler;
import com.example.ofir.social_geha.Activities_and_Fragments.activity_main_drawer;
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
    private static String CHAT_REQUEST = "com.example.ofir.social_geha.REQUEST_HELP";
    private static int YES_REQUEST_CODE = 1;
    private static int NO_REQUEST_CODE = 2;
    private FirebaseAuth auth;
    FirebaseFirestore database;
    public GehaMessagingService() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Got notification ");
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage){
        initChannels(this);
        long[] vPattern = {0, 1000};
        Map<String, String> data = remoteMessage.getData();
        String clickAction = data.get("click_action");
        int unique_id = (int)System.currentTimeMillis();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setVibrate(vPattern)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setWhen(0)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);
        if(clickAction.equals(CHAT_REQUEST)){
            Log.d(TAG, "showNotification: CHAT REQUEST");
            Intent yesAction = new Intent(this, NotificationActionReceiver.class);
            yesAction.putExtra("fromPerson", data.get("fromPersonID"));
            yesAction.putExtra("action", "YES");
            yesAction.putExtra("not_id", unique_id);
            PendingIntent pYesAction = PendingIntent.getBroadcast(this, YES_REQUEST_CODE, yesAction, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent noAction = new Intent(this, NotificationActionReceiver.class);
            noAction.putExtra("fromPerson", data.get("fromPersonID"));
            noAction.putExtra("action", "NO");
            noAction.putExtra("not_id", unique_id);
            PendingIntent pNoAction = PendingIntent.getBroadcast(this, NO_REQUEST_CODE, noAction, PendingIntent.FLAG_UPDATE_CURRENT);


            notificationBuilder.addAction(R.drawable.ic_yes_option, "כן", pYesAction);
            notificationBuilder.addAction(R.drawable.ic_no_option, "לא", pNoAction);
        }
        else {
            handleIntent(notificationBuilder, clickAction, remoteMessage);
        }
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(unique_id, notificationBuilder.build());
    }

    private void handleIntent(NotificationCompat.Builder notificationBuilder, String clickAction, RemoteMessage remoteMessage){
        Intent resultIntent = new Intent(this, ChatActivity.class);
        String fromPersonID = remoteMessage.getData().get(FROM_PERSON_ID);
        Log.d(TAG, "handleIntent: the message was from: "+fromPersonID);
        if(fromPersonID != null){
            resultIntent.putExtra("EXTRA_PERSON_ID", fromPersonID);
            ContactListFileHandler contactHandler = new ContactListFileHandler(this);
            ContactListFileHandler.Contact contact = contactHandler.getContact(fromPersonID);
            if(contact == null) {
                resultIntent = new Intent(this, activity_main_drawer.class);
                notificationBuilder.setContentTitle("משתמש חדש יצר איתך קשר")
                        .setContentText("לראותו/ה לחץ כאן");
            }
            else {
                String realName = contact.getRealName();
                if (!realName.equals(ContactListFileHandler.Contact.UNKNOWN_NAME)) {
                    resultIntent.putExtra("EXTRA_NAME", realName);
                } else {
                    resultIntent.putExtra("EXTRA_NAME", contact.getAnonID().getName());
                }
                resultIntent.putExtra("EXTRA_PHOTO_URL", contact.getAnonID().getImageName());
                resultIntent.putExtra("EXTRA_PHOTO_COLOR", contact.getAnonID().getImageColor());
            }
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
