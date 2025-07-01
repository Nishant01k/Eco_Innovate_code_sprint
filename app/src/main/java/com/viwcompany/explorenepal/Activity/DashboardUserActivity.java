package com.viwcompany.explorenepal.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.viwcompany.explorenepal.Adapter.ImagePagerAdapter;
import com.viwcompany.explorenepal.R;
import com.viwcompany.explorenepal.databinding.ActivityDashboardUserBinding;

public class DashboardUserActivity extends AppCompatActivity {

    private ActivityDashboardUserBinding binding;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImagePagerAdapter adapter;

    private final int[] imageResIds = {
            R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4 , R.drawable.img5, R.drawable.img6, R.drawable.img7,
            R.drawable.img8,R.drawable.img9,R.drawable.img10 , R.drawable.img11
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new ImagePagerAdapter(this, imageResIds.length);
        viewPager.setAdapter(adapter);

        // Attach TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setIcon(imageResIds[position]); // Set image as tab icon
        }).attach();


    }
}