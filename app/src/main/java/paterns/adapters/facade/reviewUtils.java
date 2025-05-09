package paterns.adapters.facade;

import android.app.Activity;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import Class.*;

public class reviewUtils {
    public static void displayLastReview(Activity activity, String title, TextView lastReview) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews")
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        StringBuilder allReviews = new StringBuilder("Всі відгуки:\n\n");
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Review review = document.toObject(Review.class);
                            if (review != null) {
                                String userId = review.getUserId();

                                db.collection("users")
                                        .document(userId)
                                        .get()
                                        .addOnSuccessListener(userDoc -> {
                                            String login = userDoc.contains("login") ? userDoc.getString("login") : "Невідомо";

                                            allReviews.append("Автор: ").append(login).append("\n")
                                                    .append("Рейтинг: ").append(review.getRating()).append("/5\n")
                                                    .append(review.getText()).append("\n\n");

                                            lastReview.setText(allReviews.toString().trim());
                                        });
                            }
                        }
                        lastReview.setText(allReviews.toString().trim());
                    } else {
                        lastReview.setText("Відгуків ще немає...");
                    }
                })
                .addOnFailureListener(e -> {
                    lastReview.setText("Помилка завантаження відгуків: " + e.getMessage());
                });
    }

    public static void updateAverageRating(Activity activity, String title, TextView ratingAve) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews")
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        float totalRating = 0;
                        int count = 0;

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Review review = document.toObject(Review.class);
                            if (review != null) {
                                totalRating += review.getRating();
                                count++;
                            }
                        }

                        if (count > 0) {
                            float averageRating = totalRating / count;
                            ratingAve.setText("Рейтинг: " + String.format("%.1f⭐", averageRating));
                        } else {
                            ratingAve.setText("Рейтинг: 0.0");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    ratingAve.setText("Помилка завантаження рейтингу");
                });
    }
}
