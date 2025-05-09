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



    public void addBookToLibrary(Book book, String category) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection(category)
                .add(book);
    }

    public void getBooksFromLibrary(String collection, final BookCallback callback) {
        db.collection("users").document(userId)
                .collection(collection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> books = queryDocumentSnapshots.toObjects(Book.class);
                    callback.onBooksLoaded(books);
                })
                .addOnFailureListener(e -> {
                });
    }
}


