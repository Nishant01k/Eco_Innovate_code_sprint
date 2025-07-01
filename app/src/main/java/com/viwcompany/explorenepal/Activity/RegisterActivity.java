package com.viwcompany.explorenepal.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.viwcompany.explorenepal.R;
import com.viwcompany.explorenepal.databinding.ActivityRegisterBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private final int[] avatars = {
            R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4,
            R.drawable.avatar5, R.drawable.avatar6, R.drawable.avatar7,
            R.drawable.avatar8, R.drawable.avatar9, R.drawable.avatar10
    };

    private CircleImageView userAvatar;
    private SharedPreferences sharedPreferences;
    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private SweetAlertDialog progressDialog;
    private CheckBox ch;

    private String name, email, password , country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);

        userAvatar = findViewById(R.id.user_avatar);
        ch = findViewById(R.id.privacyCheckbox);

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        int savedAvatar = sharedPreferences.getInt("avatar", avatars[0]);
        userAvatar.setImageResource(savedAvatar);

        userAvatar.setOnClickListener(v -> showAvatarSelectionPopup());

        binding.registerBtn.setOnClickListener(v -> {
            if (ch != null && ch.isChecked()) {
                validateData();
            } else {
                Toast.makeText(this, "Please accept the Privacy Policy.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateData() {
        name = binding.fullNameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        country = binding.countryEt.getText().toString().trim();

        String cPassword = binding.confirmPasswordEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.fullNameEt.setError("Enter your full name");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.setError("Enter a valid email");
        } else if (TextUtils.isEmpty(password)) {
            binding.passwordEt.setError("Enter a password");
        } else if (TextUtils.isEmpty(cPassword)) {
            binding.confirmPasswordEt.setError("Confirm your password");
        } else if (!password.equals(cPassword)) {
            binding.confirmPasswordEt.setError("Passwords do not match");
        } else if (TextUtils.isEmpty(country)) {
            binding.confirmPasswordEt.setError("Enter Your Country Name");
        } else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        progressDialog.setTitleText("Creating account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> updateUserInfo())
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserInfo() {
        progressDialog.setTitleText("Saving info...");

        String uid = Objects.requireNonNull(firebaseAuth.getUid());
        String dateString = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                .format(Calendar.getInstance().getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", ""); // optional, or can save avatar URI if needed
        hashMap.put("userType", "user");
        hashMap.put("password", password); // ⚠️ Security issue: don't store plain passwords
        hashMap.put("timestamp", System.currentTimeMillis());
        hashMap.put("accountCreated", dateString);
        hashMap.put("Country", country);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).setValue(hashMap)
                .addOnSuccessListener(unused -> sendVerificationEmail())
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendVerificationEmail() {
        Objects.requireNonNull(firebaseAuth.getCurrentUser())
                .sendEmailVerification()
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    showSuccessDialog();
                    Toast.makeText(this, "Verification email sent!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Email failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showSuccessDialog() {
        SweetAlertDialog successDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText("Success!");
        successDialog.setContentText("Account created successfully.");
        successDialog.setConfirmText("Login");
        successDialog.setConfirmClickListener(dialog -> {
            dialog.dismissWithAnimation();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
        successDialog.setCancelable(false);
        successDialog.show();
    }

    private void showAvatarSelectionPopup() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_avatar_selection);

        GridView gridView = dialog.findViewById(R.id.avatar_grid);
        gridView.setAdapter(new AvatarAdapter(this, avatars));

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedAvatar = avatars[position];
            userAvatar.setImageResource(selectedAvatar);
            sharedPreferences.edit().putInt("avatar", selectedAvatar).apply();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private static class AvatarAdapter extends BaseAdapter {
        private final Context context;
        private final int[] avatars;

        AvatarAdapter(Context context, int[] avatars) {
            this.context = context;
            this.avatars = avatars;
        }

        @Override
        public int getCount() {
            return avatars.length;
        }

        @Override
        public Object getItem(int position) {
            return avatars[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = (convertView == null) ? new ImageView(context) : (ImageView) convertView;
            imageView.setImageResource(avatars[position]);
            return imageView;
        }
    }
}
