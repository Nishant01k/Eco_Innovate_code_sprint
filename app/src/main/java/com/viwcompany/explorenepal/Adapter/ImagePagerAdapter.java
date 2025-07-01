package com.viwcompany.explorenepal.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ImagePagerAdapter extends FragmentStateAdapter {

    private final int pageCount;

    public ImagePagerAdapter(@NonNull FragmentActivity fragmentActivity, int pageCount) {
        super(fragmentActivity);
        this.pageCount = pageCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ImageFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return pageCount;
    }
}
