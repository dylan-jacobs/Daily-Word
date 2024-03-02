package com.jacobstechnologies.dailyword.ui.notifications;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jacobstechnologies.dailyword.R;
import com.jacobstechnologies.dailyword.ui.home.HomeFragment;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        TextView riddle = root.findViewById(R.id.riddle);
        TextView riddle_answer = root.findViewById(R.id.riddle_answer);
        Button showButton = root.findViewById(R.id.button);

        riddle_answer.setVisibility(View.GONE);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> new Handler(Looper.getMainLooper()).post(() -> {
            try {
                // riddle
                String[] paths = new String[] {"div.panel-body.lead", "div.collapse.mar_top_15"};
                String [] strings = HomeFragment.ScanInternet("https://www.riddles.com/", paths);
                int chopIndex = strings[0].indexOf("Answer");
                String riddle_string = strings[0].substring(0, chopIndex);
                riddle.setText(riddle_string);
                riddle_answer.setText(strings[1].replace("?", "?\n"));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }));

        showButton.setOnClickListener(l -> {
            if (riddle_answer.getVisibility() == View.GONE){
                riddle_answer.setVisibility(View.VISIBLE);
                showButton.setText(R.string.hide_answer);
            }
            else{
                riddle_answer.setVisibility(View.GONE);
                showButton.setText(R.string.reveal_answer);
            }
        });

        return root;
    }
}