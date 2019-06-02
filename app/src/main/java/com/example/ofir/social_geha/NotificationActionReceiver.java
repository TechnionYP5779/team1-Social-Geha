package com.example.ofir.social_geha;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        String fromPersonID = intent.getStringExtra("fromPerson");
        int unique_id = intent.getIntExtra("not_id", -1);
        if(action.equals("YES")){
            accept(context, fromPersonID);
        }
        else if(action.equals("NO")){
            decline(context, fromPersonID);
        }

        //Dismiss the notification
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(unique_id != -1) {
            notificationManager.cancel(unique_id);
        }

    }

    //HERE - SEND CONTROL MESSAGE BACK to fromPersonID
    public void accept(Context context, String fromPersonID){
        Toast.makeText(context, "אישרת ל-" + fromPersonID, Toast.LENGTH_SHORT).show();

    }

    //DO NOTHING - OR WHATEVER YOU FIND FANCY
    public void decline(Context context, String fromPersonID){
        Toast.makeText(context, "סירבת ל-" + fromPersonID, Toast.LENGTH_SHORT).show();
    }
}
