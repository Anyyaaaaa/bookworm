package com.example.bookworm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView bookImage;
    private TextView bookTitle, bookAuthor, bookDescription;
    private Button readButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        bookImage = findViewById(R.id.detailImage);
        bookTitle = findViewById(R.id.detailTitle);
        bookAuthor = findViewById(R.id.detailAuthor);
        bookDescription = findViewById(R.id.detailDescription);
        readButton = findViewById(R.id.readButton);

        // Отримуємо дані з Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String imageUrl = intent.getStringExtra("imageUrl");
        String description = intent.getStringExtra("description");

        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookDescription.setText(description);

        Picasso.get().load(imageUrl).into(bookImage);

        readButton.setOnClickListener(v -> {
            // Дія при натисканні на "Читати"
            // Наприклад, відкриття PDF або просто тост
            // startActivity(new Intent(this, ReaderActivity.class));
        });
    }
}