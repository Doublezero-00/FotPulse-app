package com.example.fotpulse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fotpulse.NewsPagerAdapter;
import com.example.fotpulse.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class NewsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private String[] tabTitles = {"Sports", "Academic", "Events"};

    private int[] tabIcons = {
            R.drawable.ic_sports_tab_black_24dp,
            R.drawable.ic_academic_black_24dp,
            R.drawable.ic_events_tab_black_24dp
    };

    public NewsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        NewsPagerAdapter pagerAdapter = new NewsPagerAdapter(this, tabTitles);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setText(tabTitles[position]);
                    tab.setIcon(tabIcons[position]);
                }
        ).attach();

        return view;
    }
}