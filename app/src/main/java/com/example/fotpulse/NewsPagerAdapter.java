package com.example.fotpulse;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity; // For FragmentActivity constructor (if needed)
import androidx.fragment.app.FragmentManager;   // For FragmentManager constructor
import androidx.lifecycle.Lifecycle;             // For Lifecycle constructor
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Import your category-specific news fragments (e.g., WorldNewsFragment, BusinessNewsFragment)
// You might need to create these if they don't exist, similar to how you created NewsCategoryFragment previously.
import com.example.fotpulse.fragments.NewsCategoryFragment; // Assuming a generic fragment for categories

public class NewsPagerAdapter extends FragmentStateAdapter {

    private String[] tabTitles;

    public NewsPagerAdapter(@NonNull FragmentActivity fragmentActivity, String[] tabTitles) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
    }

    public NewsPagerAdapter(@NonNull Fragment fragment, String[] tabTitles) {
        super(fragment);
        this.tabTitles = tabTitles;
    }

    public NewsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String[] tabTitles) {
        super(fragmentManager, lifecycle);
        this.tabTitles = tabTitles;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return NewsCategoryFragment.newInstance(tabTitles[position]);
    }

    @Override
    public int getItemCount() {
        return tabTitles.length;
    }
}