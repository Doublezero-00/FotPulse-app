package com.example.fotpulse;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NewsPagerAdapter extends FragmentStateAdapter {

    public NewsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return NewsCategoryFragment.newInstance("Sports");
            case 1: return NewsCategoryFragment.newInstance("Academic");
            case 2: return NewsCategoryFragment.newInstance("Events");
            default: return NewsCategoryFragment.newInstance("General");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
