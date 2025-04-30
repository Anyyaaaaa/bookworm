package Class;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import Interface.BookCallback;

public class Library {

    private FirebaseFirestore db;
    private String userId;

    public Library() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // Додавання книги до бібліотеки користувача
    public void addBookToLibrary(Book book, String collection) {
        db.collection("users").document(userId)
                .collection(collection)
                .add(book)
                .addOnSuccessListener(documentReference -> {
                    // Книга успішно додана
                })
                .addOnFailureListener(e -> {
                    // Обробка помилки
                });
    }

    // Отримання книг з бібліотеки користувача
    public void getBooksFromLibrary(String collection, final BookCallback callback) {
        db.collection("users").document(userId)
                .collection(collection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = queryDocumentSnapshots.toObjects(Book.class);
                    callback.onBooksLoaded(books);
                })
                .addOnFailureListener(e -> {
                    // Обробка помилки
                });
    }
}


