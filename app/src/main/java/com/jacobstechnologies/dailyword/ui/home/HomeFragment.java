package com.jacobstechnologies.dailyword.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jacobstechnologies.dailyword.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        TextView v = root.findViewById(R.id.textView);
        TextView v2 = root.findViewById(R.id.textView2);
        TextView v3 = root.findViewById(R.id.textView3);
        TextView v4 = root.findViewById(R.id.textView4);
        TextView v5 = root.findViewById(R.id.joke);

        // internet
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> new Handler(Looper.getMainLooper()).post(() -> {
            try {
                // english WOD
                String[] paths = new String[] {"div.word-and-pronunciation > h2", "span.word-syllables", "div.wod-definition-container > p", "div.wod-definition-container > p:eq(2)"};
                String[] strings = ScanInternet("https://www.merriam-webster.com/word-of-the-day", paths);
                v.setText(strings[0]);
                v2.setText(strings[1]);
                v3.setText(strings[2]);
                v4.setText(strings[3].replace("//", ""));

                // joke
                paths = new String[] {"div.jd-innercontainer > div > div > p"};
                strings = ScanInternet("https://www.ajokeaday.com/", paths);
                v5.setText(strings[0].replace("?", "?\n"));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }));
        return root;
    }

    public static String[] ScanInternet(String url, String[] paths) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CallableClass callableClass = new CallableClass(url, paths);
        return executor.submit(callableClass).get();
    }

    public static class CallableClass implements Callable<String[]>{

        public volatile String[] s;
        private final String url;
        private final String[] paths;

        CallableClass(String url, String[] paths){
            this.url = url;
            this.paths = paths;
        }

        @Override
        public String[] call() throws IOException {
            Connection c = Jsoup.connect(url);
            s = new String[paths.length];
            for (int i = 0; i < paths.length; i++){
                Element e = c
                        .get()
                        .selectFirst(paths[i]);
                if (e != null) {
                    s[i] = e.text();
                }
                else{
                    return null;
                }
            }
            return s;
        }
    }
}