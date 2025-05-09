package com.example.bookworm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;
import java.util.Arrays;
import java.util.List;

import Class.Library;
import Class.Book;
import Class.Review;
import paterns.adapters.facade.bookUtils;
import paterns.adapters.facade.reviewUtils;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView bookImage;
    private TextView bookTitle, bookAuthor, bookDescription, lastReview, ratingAve;
    private Button readButton, buttonGenre, submitReviewButton, listenButton;
    private EditText reviewInput;
    private RatingBar reviewRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String imageUrl = intent.getStringExtra("imageUrl");
        String bookUrl = intent.getStringExtra("bookUrl");
        String genre = intent.getStringExtra("genre");

        bookImage = findViewById(R.id.detailImage);
        bookTitle = findViewById(R.id.detailTitle);
        bookAuthor = findViewById(R.id.detailAuthor);
        bookDescription = findViewById(R.id.detailDescription);
        lastReview = findViewById(R.id.lastReview);
        ratingAve = findViewById(R.id.ratingAve);

        readButton = findViewById(R.id.readButton);
        buttonGenre = findViewById(R.id.buttonGenre);
        ImageButton favoriteButton = findViewById(R.id.favoriteButton);

        reviewInput = findViewById(R.id.reviewInput);
        reviewRating = findViewById(R.id.reviewRating);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        listenButton = findViewById(R.id.listenButton);

        listenButton.setOnClickListener(v -> {
            String selectedTitle = title;
            AppCompatActivity activity = this;

            FirebaseFirestore.getInstance()
                    .collection("audioBook")
                    .whereEqualTo("title", selectedTitle)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String bookId = document.getId();

                            Intent intents = new Intent(activity, AudioBookActivity.class);
                            intents.putExtra("bookId", bookId);
                            intents.putExtra("title", selectedTitle);
                            activity.startActivity(intents);
                        } else {
                            Toast.makeText(activity, "Аудіокнига відсутня", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(activity, "Помилка з'єднання з базою", Toast.LENGTH_SHORT).show();
                    });
        });

        submitReviewButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                float rating = reviewRating.getRating();
                String text = reviewInput.getText().toString().trim();

                if (text.isEmpty()) {
                    Toast.makeText(this, "Будь ласка, введіть текст відгуку", Toast.LENGTH_SHORT).show();
                    return;
                }

                Review review = new Review(
                        user.getUid(),
                        user.getEmail(),
                        rating,
                        text,
                        title
                );

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("reviews")
                        .add(review)
                        .addOnSuccessListener(doc -> {
                            Toast.makeText(this, "Відгук збережено!", Toast.LENGTH_SHORT).show();
                            reviewInput.setText("");
                            reviewRating.setRating(0f);
                            reviewUtils.updateAverageRating(BookDetailActivity.this, title, ratingAve);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        favoriteButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(BookDetailActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.category_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedCategory = "";

                if (item.getItemId() == R.id.menu_reading) {
                    selectedCategory = "Читаю";
                } else if (item.getItemId() == R.id.menu_toread) {
                    selectedCategory = "Буду читати";
                } else if (item.getItemId() == R.id.menu_favorites) {
                    selectedCategory = "Улюблене";
                } else if (item.getItemId() == R.id.menu_finished) {
                    selectedCategory = "Прочитано";
                }

                if (!selectedCategory.isEmpty()) {
                    Book book = new Book(title, author, imageUrl, bookUrl, genre);
                    moveBookToCollection(book, selectedCategory);
                }

                return true;
            });
            popupMenu.show();
        });

        String descriptionText = intent.getStringExtra("description");
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
                bookUtils.downloadAndOpenBook(BookDetailActivity.this, bookUrl);
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

        reviewUtils.displayLastReview(BookDetailActivity.this, title, lastReview);
        reviewUtils.updateAverageRating(BookDetailActivity.this, title, ratingAve);
    }

    private void moveBookToCollection(Book book, String targetCollection) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String bookId = book.getTitle();  // 👈 Використовуємо назву як ID (або свій ID, якщо маєш)

        // Список всіх колекцій
        List<String> collections = Arrays.asList("Читаю", "Буду читати", "Прочитано", "Улюблене");

        db.collection("users")
                .document(userId)
                .collection(targetCollection)
                .document(bookId)
                .get()
                .addOnSuccessListener(targetDoc -> {
                    if (targetDoc.exists()) {
                        // Книга вже є в цій колекції
                        Toast.makeText(BookDetailActivity.this, "Книга вже є в цій колекції", Toast.LENGTH_SHORT).show();
                    } else {
                        // Додаємо книгу до цільової колекції
                        db.collection("users")
                                .document(userId)
                                .collection(targetCollection)
                                .document(bookId)
                                .set(book)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(BookDetailActivity.this, "Книгу додано до \"" + targetCollection + "\"", Toast.LENGTH_SHORT).show();

                                    // Видаляємо книгу з інших колекцій
                                    for (String collection : collections) {
                                        if (!collection.equals(targetCollection)) {
                                            db.collection("users")
                                                    .document(userId)
                                                    .collection(collection)
                                                    .document(bookId)
                                                    .get()
                                                    .addOnSuccessListener(sourceDoc -> {
                                                        if (sourceDoc.exists()) {
                                                            db.collection("users")
                                                                    .document(userId)
                                                                    .collection(collection)
                                                                    .document(bookId)
                                                                    .delete()
                                                                    .addOnSuccessListener(aVoid1 -> {
                                                                        // Успішно видалено з попередньої колекції
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Toast.makeText(BookDetailActivity.this, "Помилка при видаленні з \"" + collection + "\"", Toast.LENGTH_SHORT).show();
                                                                    });
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(BookDetailActivity.this, "Помилка при додаванні книги", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookDetailActivity.this, "Помилка при перевірці цільової колекції", Toast.LENGTH_SHORT).show();
                });
    }
}
