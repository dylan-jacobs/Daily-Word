package com.jacobstechnologies.dailyword;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.jacobstechnologies.dailyword.databinding.ActivityNavigationBinding;

import java.util.Calendar;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.jacobstechnologies.dailyword.databinding.ActivityNavigationBinding binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        // alarm/notification setup
        setupChannels();
        setTimer(getApplicationContext());

        FirebaseInstanceService firebaseInstanceService = new FirebaseInstanceService();
        firebaseInstanceService.GetToken(getApplicationContext());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigation);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        Toast.makeText(this, "What's up??", Toast.LENGTH_LONG).show();
    }

    public static void setTimer(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(context, DailyReceiver.class);
        int ALARM1_ID = 10000;

        boolean alarmNotSet = PendingIntent.getBroadcast(context, ALARM1_ID, new Intent(context, DailyReceiver.class), PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) == null;

        if (alarmNotSet) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DAY_OF_MONTH) + 1);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);

            Log.v("jjj", "SET NEW ALARM!! Time: " + calendar.getTime());
        }
        else{
            Log.v("jjj", "SET");
        }
    }

    private void setupChannels(){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to device notification";
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("10000", adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

}