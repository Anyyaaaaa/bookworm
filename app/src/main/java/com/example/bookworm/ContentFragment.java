package com.example.bookworm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import Class.Book;
import Paterns.BookAdapter;

public class ContentFragment extends Fragment {

    private RecyclerView recyclerview;
    private List<Book> booksList = new ArrayList<>();
    private BookAdapter bookAdapter;
    private int tabPosition;

    public ContentFragment(int tabPosition) {
        this.tabPosition = tabPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        recyclerview = view.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new GridLayoutManager(getContext(), 3));

        bookAdapter = new BookAdapter(getContext(), booksList);
        recyclerview.setAdapter(bookAdapter);

        loadBooksData();

        return view;
    }

    private void loadBooksData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collection = "";

        switch (tabPosition) {
            case 0:
                collection = "reading";  // "Читаю"
                break;
            case 1:
                collection = "toread";  // "Буду читати"
                break;
            case 2:
                collection = "favorites";  // "Улюблене"
                break;
        }

        db.collection(collection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    booksList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        booksList.add(book);
                    }
                    bookAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
