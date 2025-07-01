package com.viwcompany.explorenepal.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.viwcompany.explorenepal.R;
import com.viwcompany.explorenepal.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private FirebaseAuth firebaseAuth;

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        VideoView videoView = binding.videoView;

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.hack);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(mp -> {
            mp.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        });

        videoView.setOnCompletionListener(mp -> checkUser());

        videoView.start();
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userType = snapshot.child("userType").getValue(String.class);

                    if ("user".equals(userType)) {
                        startActivity(new Intent(SplashActivity.this, DashboardUserActivity.class));
                    } else if ("admin".equals(userType)) {
                        startActivity(new Intent(SplashActivity.this, DashboardAdminActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "DatabaseError: " + error.getMessage());
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
    }
}
