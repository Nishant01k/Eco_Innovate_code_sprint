package com.viwcompany.explorenepal.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.viwcompany.explorenepal.R;

public class ImageFragment extends Fragment {

    public static ImageFragment newInstance(int index) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ImageView imageView = new ImageView(requireContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        int index = getArguments() != null ? getArguments().getInt("index", 0) : 0;
        int[] images = {
                R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4 , R.drawable.img5, R.drawable.img6, R.drawable.img7,
                R.drawable.img8,R.drawable.img9,R.drawable.img10 , R.drawable.img11
        };

        if (index >= 0 && index < images.length) {
            imageView.setImageResource(images[index]);
        }

        return imageView;
    }
}
