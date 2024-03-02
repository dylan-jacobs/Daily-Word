package com.jacobstechnologies.dailyword;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.jacobstechnologies.dailyword.ui.home.HomeFragment;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class DailyReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null){
            Log.d("jjj", "NULL INTENT ACTION!!!!");
        }
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)){
            NavigationActivity.setTimer(context);
        }
        else {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = new Intent(context, NavigationActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Log.v("jjj", "BROADCAST RECEIVED!!!");
            new Handler(Looper.getMainLooper()).post(() -> {
                // english WOD
                String[] paths = new String[]{"div.word-and-pronunciation > h2", "span.word-syllables", "div.wod-definition-container > p", "div.wod-definition-container > p:eq(2)"};
                try {
                    String[] strings = HomeFragment.ScanInternet("https://www.merriam-webster.com/word-of-the-day", paths);
                    String bigText;
                    String contentText;
                    if (strings != null) {
                        bigText = strings[0] + ": " + strings[1];
                        contentText = "Your daily word is " + strings[0] + "!";
                    }
                    else{
                        bigText = "Your daily word is here!";
                        contentText = "Click to discover your new word that we picked out just for you!";
                    }
                    NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context, "10000")
                            .setSmallIcon(R.mipmap.app_icon)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(bigText))
                            .setContentTitle("A Word a Day")
                            .setContentText(contentText)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);

                    notificationManager.notify(10000, mNotifyBuilder.build());
                    Log.d("jjj", "NOTIFICATION SENT!");
                } catch (ExecutionException | InterruptedException e) {
                    Log.d("jjj", "ERROR OCCURRED!");
                    e.printStackTrace();
                }
            });
        }
    }
}
