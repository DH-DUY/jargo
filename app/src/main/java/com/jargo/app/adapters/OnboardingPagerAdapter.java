package com.jargo.app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.jargo.app.fragments.FieldSelectionFragment;
import com.jargo.app.fragments.LevelSelectionFragment;
import com.jargo.app.fragments.WelcomeFragment;

/**
 * OnboardingPagerAdapter - Adapter cho ViewPager2 trong OnboardingActivity
 * Quản lý 3 fragments: Welcome, FieldSelection, LevelSelection
 */
public class OnboardingPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;

    public OnboardingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return WelcomeFragment.newInstance();
            case 1:
                return FieldSelectionFragment.newInstance();
            case 2:
                return LevelSelectionFragment.newInstance();
            default:
                return WelcomeFragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
