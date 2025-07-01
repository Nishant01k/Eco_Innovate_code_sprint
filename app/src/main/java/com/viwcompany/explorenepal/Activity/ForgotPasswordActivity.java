package com.viwcompany.explorenepal.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.viwcompany.explorenepal.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEt;
    private Button recoverBtn;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        emailEt = findViewById(R.id.emailEt);
        recoverBtn = findViewById(R.id.recoverBtn);

        // Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // Button click
        recoverBtn.setOnClickListener(v -> recoverPassword());
    }

    private void recoverPassword() {
        String email = emailEt.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEt.setError("Enter a valid email address");
            emailEt.requestFocus();
            return;
        }

        progressDialog.setMessage("Sending password reset email...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
