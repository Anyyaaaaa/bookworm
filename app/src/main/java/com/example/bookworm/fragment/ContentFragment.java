package com.example.bookworm.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Class.Book;
import paterns.adapters.adapters.BookAdapter;

public class ContentFragment extends Fragment {

    private static final String ARG_COLLECTION_NAME = "collection_name";

    private String collectionName;
    private RecyclerView recyclerview;
    private List<Book> booksList = new ArrayList<>();
    private BookAdapter bookAdapter;

    public ContentFragment() {
        // Обов'язковий публічний конструктор без параметрів
    }

    public static ContentFragment newInstance(String collectionName) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLLECTION_NAME, collectionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            collectionName = getArguments().getString(ARG_COLLECTION_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection(collectionName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    booksList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        booksList.add(book);
                    }
                    bookAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}

