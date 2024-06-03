package com.syrtsiob.worknet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProfileViewPagerAdapter extends FragmentStateAdapter {
    public ProfileViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EducationFragment();
            case 1:
                return new SkillsFragment();
            case 2:
                return new WorkExperienceFragment();
            default:
                return new EducationFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
