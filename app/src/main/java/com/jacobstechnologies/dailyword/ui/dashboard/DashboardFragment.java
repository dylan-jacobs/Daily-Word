package com.jacobstechnologies.dailyword.ui.dashboard;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jacobstechnologies.dailyword.R;
import com.jacobstechnologies.dailyword.ui.home.HomeFragment;
import com.ortiz.touchview.TouchImageView;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // text views
        TextView v = root.findViewById(R.id.textView);
        TextView v2 = root.findViewById(R.id.textView2);
        TextView v3 = root.findViewById(R.id.textView3);
        TextView v4 = root.findViewById(R.id.textView4);

        // internet
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> new Handler(Looper.getMainLooper()).post(() -> {
            try {
                // spanish WOD
                String[] paths = new String[] {"#main-container-video > div > div:eq(1) > div > div:eq(1) > h3 > a", "#main-container-video > div > div:eq(1) > div > div:eq(1) > div", "#main-container-video > div > div:eq(1) > div > div:eq(1) > ol > li > div", "#main-container-video > div > div:eq(1) > div > div:eq(1) > ol > li > div:eq(1)"};
                String[] strings = HomeFragment.ScanInternet("https://spanishdict.com/wordoftheday", paths);
                v.setText(strings[0]);
                v2.setText(strings[1]);
                v3.setText(strings[2]);
                v4.setText(strings[3]);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        })
        );

        // set images
        {
            ViewPager mViewPager = root.findViewById(R.id.viewPager);
            int[] images = {R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4, R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8};
            PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getContext(), images);
            mViewPager.setAdapter(pagerAdapter);
            mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        }

        return root;
    }

    private static class ScreenSlidePagerAdapter extends PagerAdapter {

        Context context;
        int[] images;
        LayoutInflater mLayoutInflater;

        ScreenSlidePagerAdapter(Context c, int[] i){
            context = c;
            images = i;
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            // inflating the item.xml
            View itemView = mLayoutInflater.inflate(R.layout.slide_page, container, false);

            // referencing the image view from the item.xml file
            TouchImageView imageView = (TouchImageView) itemView.findViewById(R.id.imageView);

            // setting the image in the imageView
            imageView.setImageResource(images[position]);

            // Adding the View
            Objects.requireNonNull(container).addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {

            container.removeView((LinearLayout) object);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((LinearLayout) object);
        }
    }

    public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }
}