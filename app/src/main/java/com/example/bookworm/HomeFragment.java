package com.example.bookworm;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import Class.Book;
import Class.BookAdapter;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;

    private RecyclerView carouselRecyclerView;
    private List<Book> carouselList;
    private BookAdapter carouselAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerBooks);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        bookList = new ArrayList<>();
        adapter = new BookAdapter(getContext(), bookList);
        recyclerView.setAdapter(adapter);


        carouselRecyclerView = view.findViewById(R.id.carouselBooks);
        carouselRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        carouselList = new ArrayList<>();
        carouselAdapter = new BookAdapter(getContext(), carouselList);
        carouselRecyclerView.setAdapter(carouselAdapter);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("popular_books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        carouselList.add(book);
                    }
                    carouselAdapter.notifyDataSetChanged();
                });


        db.collection("book")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        bookList.add(book);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        return view;




    }
}



