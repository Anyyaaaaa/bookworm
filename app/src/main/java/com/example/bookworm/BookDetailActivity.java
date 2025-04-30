package com.example.bookworm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView bookImage;
    private TextView bookTitle, bookAuthor, bookDescription;
    private Button readButton, buttonGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        bookImage = findViewById(R.id.detailImage);
        bookTitle = findViewById(R.id.detailTitle);
        bookAuthor = findViewById(R.id.detailAuthor);
        bookDescription = findViewById(R.id.detailDescription);
        readButton = findViewById(R.id.readButton);
        buttonGenre=findViewById(R.id.buttonGenre);

        // Отримуємо дані з Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String imageUrl = intent.getStringExtra("imageUrl");
        String descriptionText = intent.getStringExtra("description");
        String bookUrl = intent.getStringExtra("bookUrl");
        String genre = intent.getStringExtra("genre");

        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookDescription.setText(descriptionText);

        if (genre != null && !genre.isEmpty()) {
            buttonGenre.setText(genre);
        } else {
            buttonGenre.setText("Невідомо");
        }

        Picasso.get().load(imageUrl).into(bookImage);

        readButton.setOnClickListener(v -> {
            if (bookUrl != null && !bookUrl.isEmpty()) {
                downloadAndOpenBook(bookUrl);
            } else {
                Toast.makeText(BookDetailActivity.this, "URL книги недоступний", Toast.LENGTH_SHORT).show();
            }
        });

        TextView showMore = findViewById(R.id.showMore);

        showMore.setOnClickListener(v -> {
            if (bookDescription.getMaxLines() == 4) {
                bookDescription.setMaxLines(Integer.MAX_VALUE);
                showMore.setText("Показати менше");
            } else {
                bookDescription.setMaxLines(4);
                showMore.setText("Показати більше");
            }
        });
    }

    private void downloadAndOpenBook(String bookUrl) {
        try {
            java.net.URL url = new java.net.URL(bookUrl);
            String fileExtension = bookUrl.contains(".pdf") ? ".pdf" : ".epub";
            File localFile = File.createTempFile("tempBook", fileExtension);

            new Thread(() -> {
                try (java.io.InputStream in = url.openStream();
                     java.io.FileOutputStream out = new java.io.FileOutputStream(localFile)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }

                    runOnUiThread(() -> {
                        Intent intent = new Intent(this, ReaderActivity.class);
                        intent.putExtra("book_path", localFile.getAbsolutePath());
                        intent.putExtra("book_type", fileExtension.equals(".pdf") ? "pdf" : "epub");
                        startActivity(intent);
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Не вдалося завантажити книгу", Toast.LENGTH_SHORT).show());
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Помилка URL", Toast.LENGTH_SHORT).show();
        }
    }
}
