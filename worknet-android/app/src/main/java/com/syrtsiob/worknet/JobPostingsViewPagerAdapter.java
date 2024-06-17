package com.syrtsiob.worknet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class JobPostingsViewPagerAdapter extends FragmentStateAdapter {
    public JobPostingsViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ViewJobPostingsFragment();
            case 1:
                return new ViewMyJobPostingsFragment();
            default:
                return new ViewJobPostingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
