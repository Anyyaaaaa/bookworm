package com.example.bookworm;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import Class.UserProfile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegActivity extends AppCompatActivity {

    private EditText loginEditText, emailEditText, passwordEditText, guessPasswordEditText;
    private Button regButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reg);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginEditText = findViewById(R.id.loginEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        guessPasswordEditText = findViewById(R.id.guessPasswordEditText);

        regButton = findViewById(R.id.regButton);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


    }
    private void registerUser() {
        String login = loginEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = guessPasswordEditText.getText().toString().trim();

        if(TextUtils.isEmpty(login)) {
            emailEditText.setError("Введіть логін");
            return;
        }

        if(TextUtils.isEmpty(email)) {
            emailEditText.setError("Введіть email");
            return;
        }

        if(TextUtils.isEmpty(password)) {
            passwordEditText.setError("Введіть пароль");
            return;
        }

        if (!password.equals(confirmPassword)) {
            guessPasswordEditText.setError("Паролі не збігаються");
            return;
        }

        if(password.length() < 8) {
            passwordEditText.setError("Пароль має бути не менше 8 символів");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Додаємо додаткову інформацію про користувача в Firestore
                        if (user != null) {
                            // Створення об'єкта для збереження в Firestore
                            UserProfile userProfile = new UserProfile(login, email);

                            // Зберігаємо логін разом з email в Firestore
                            db.collection("users").document(user.getUid())
                                    .set(userProfile)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegActivity.this, "Реєстрація успішна!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegActivity.this, SignInActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegActivity.this, "Помилка збереження додаткових даних", Toast.LENGTH_SHORT).show();
                                    });
                        }

                    } else {
                        Toast.makeText(RegActivity.this, "Помилка реєстрації: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}