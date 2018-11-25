package com.example.vlad.androidtestapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentActions {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static int sectionNumber = 0;
    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mSectionsPagerAdapter.addFragment(this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    protected void onPostResume() {
        int page = getIntent().getIntExtra(PlaceholderFragment.ARG_SECTION_NUMBER, 0);
        Log.d(LOG_TAG, "Current page " + page);
        mViewPager.setCurrentItem(page);
        super.onPostResume();
    }

    @Override
    public void addFragment() {
        mSectionsPagerAdapter.addFragment(this);
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
    }

    @Override
    public void removeFragment(int position) {

        mSectionsPagerAdapter.removeFragment(position);
        mViewPager.setCurrentItem(position-1);

    }

    public static class PlaceholderFragment extends Fragment{

        private static final String ARG_SECTION_NUMBER = "section_number";
        private NotificationManager notificationManager;
        private int index = 0;
        List<Integer> notificationList;
        FragmentActions mFragmentActions;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(FragmentActions activity, int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            fragment.setFragmentActions(activity);
            return fragment;
        }

        public void setFragmentActions(FragmentActions activity){
            mFragmentActions = activity;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER, 0);
            notificationManager =
                    (NotificationManager) rootView.getContext().getSystemService(NOTIFICATION_SERVICE);
            notificationList = new ArrayList<>();

            TextView textView = (TextView) rootView.findViewById(R.id.tvPageNumber);
            textView.setText(getString(R.string.section_format, sectionNumber+1));

            Button btnNewNotification = (Button) rootView.findViewById(R.id.btnNewNotification);
            btnNewNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultIntent = new Intent(rootView.getContext(), MainActivity.class);
                    resultIntent.putExtra(ARG_SECTION_NUMBER, sectionNumber);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(rootView.getContext(), 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(rootView.getContext())
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle(getString(R.string.chat_heads_active))
                                    .setContentText(getString(R.string.notification_number, sectionNumber+1))
                            .setContentIntent(resultPendingIntent)
                            .setAutoCancel(true);


                    Notification  notification = mBuilder.build();
                    int id = Integer.parseInt(String.valueOf(sectionNumber).concat(String.valueOf(index++)));
                    notificationManager.notify(id, notification);
                    notificationList.add(id);
                    Log.d(LOG_TAG, id + " Notification pushed " + sectionNumber);
                }
            });

            FloatingActionButton fabMinus = (FloatingActionButton) rootView.findViewById(R.id.fabMinus);
            if(sectionNumber == 0) {
                fabMinus.hide();
            }
            fabMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "Minus button click");
                    mFragmentActions.removeFragment(sectionNumber);
                }
            });

            FloatingActionButton fabPlus = (FloatingActionButton) rootView.findViewById(R.id.fabPlus);
            fabPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG, "Plus button click");
                    mFragmentActions.addFragment();
                }
            });
            return rootView;
        }

        @Override
        public void onDestroy() {
            for (Integer integer : notificationList) {
                notificationManager.cancel(integer);
            }
            super.onDestroy();
        }
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        public void addFragment(FragmentActions activity) {
            fragmentList.add(PlaceholderFragment.newInstance(activity, sectionNumber++));
            notifyDataSetChanged();
        }

        public void removeFragment(int position) {
            fragmentList.get(position).onDestroy();
            fragmentList.remove(position);
            notifyDataSetChanged();
        }

    }
}
