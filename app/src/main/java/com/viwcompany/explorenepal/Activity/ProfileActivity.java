package com.viwcompany.explorenepal.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import com.viwcompany.explorenepal.R;
import com.viwcompany.explorenepal.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    private static final int DEFAULT_AVATAR = R.drawable.avatar2;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());

        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        int savedAvatar = sharedPreferences.getInt("avatar", DEFAULT_AVATAR);
        binding.profileImage.setImageResource(savedAvatar);

        loadUserInfo();

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserInfo() {
        String uid = firebaseAuth.getUid();

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);

                if (email != null) binding.userEmail.setText(email);
                if (name != null) binding.userName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load user info: " + error.getMessage());
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
