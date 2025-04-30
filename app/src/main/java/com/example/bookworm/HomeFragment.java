package com.example.bookworm;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import Class.Book;
import Paterns.BookAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerBooks;
    private RecyclerView carouselBooks;
    private RecyclerView recyclerBooks_2;
    private RecyclerView carouselBooks_2;
    private EditText searchField;

    private BookAdapter recommendationsAdapter;
    private List<Book> recommendationsList = new ArrayList<>();
    private List<Book> filteredRecommendationsList = new ArrayList<>();

    private BookAdapter popularAdapter;
    private List<Book> popularList = new ArrayList<>();
    private List<Book> filteredPopularList = new ArrayList<>();

    private BookAdapter thrillerAdapter;
    private List<Book> thrillerList = new ArrayList<>();
    private List<Book> filteredThrillerList = new ArrayList<>();

    private BookAdapter fantasyAdapter;
    private List<Book> fantasyList = new ArrayList<>();
    private List<Book> filteredFantasyList = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        searchField = view.findViewById(R.id.searchField);

        // Рекомендації
        recyclerBooks = view.findViewById(R.id.recyclerBooks);
        recyclerBooks.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recommendationsAdapter = new BookAdapter(getContext(), filteredRecommendationsList);
        recyclerBooks.setAdapter(recommendationsAdapter);

        // Популярне
        carouselBooks = view.findViewById(R.id.carouselBooks);
        carouselBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularAdapter = new BookAdapter(getContext(), filteredPopularList);
        carouselBooks.setAdapter(popularAdapter);

        // Триллери/детективи
        recyclerBooks_2 = view.findViewById(R.id.recyclerBooks_2);
        recyclerBooks_2.setLayoutManager(new GridLayoutManager(getContext(), 3));
        thrillerAdapter = new BookAdapter(getContext(), filteredThrillerList);
        recyclerBooks_2.setAdapter(thrillerAdapter);

        // Романтичне фентезі
        carouselBooks_2 = view.findViewById(R.id.carouselBooks_2);
        carouselBooks_2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        fantasyAdapter = new BookAdapter(getContext(), filteredFantasyList);
        carouselBooks_2.setAdapter(fantasyAdapter);

        loadPopularBooks();
        loadRecommendations();
        loadThrillers();
        loadFantasy();

        // Пошук
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                filterRecommendations(query);
                filterPopular(query);
                filterThriller(query);
                filterFantasy(query);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    private void loadPopularBooks() {
        db.collection("popular_books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        popularList.add(book);
                    }
                    filteredPopularList.addAll(popularList);
                    popularAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> showToast(e.getMessage()));
    }

    private void loadRecommendations() {
        db.collection("book")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        recommendationsList.add(book);
                    }
                    filteredRecommendationsList.addAll(recommendationsList);
                    recommendationsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> showToast(e.getMessage()));
    }

    private void loadThrillers() {
        db.collection("thriller")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        thrillerList.add(book);
                    }
                    filteredThrillerList.addAll(thrillerList);
                    thrillerAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> showToast(e.getMessage()));
    }

    private void loadFantasy() {
        db.collection("fantasy")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Book book = document.toObject(Book.class);
                        fantasyList.add(book);
                    }
                    filteredFantasyList.addAll(fantasyList);
                    fantasyAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> showToast(e.getMessage()));
    }

    private void filterRecommendations(String query) {
        filteredRecommendationsList.clear();
        for (Book book : recommendationsList) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredRecommendationsList.add(book);
            }
        }
        recommendationsAdapter.notifyDataSetChanged();
    }

    private void filterPopular(String query) {
        filteredPopularList.clear();
        for (Book book : popularList) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredPopularList.add(book);
            }
        }
        popularAdapter.notifyDataSetChanged();
    }

    private void filterThriller(String query) {
        filteredThrillerList.clear();
        for (Book book : thrillerList) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredThrillerList.add(book);
            }
        }
        thrillerAdapter.notifyDataSetChanged();
    }

    private void filterFantasy(String query) {
        filteredFantasyList.clear();
        for (Book book : fantasyList) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredFantasyList.add(book);
            }
        }
        fantasyAdapter.notifyDataSetChanged();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }
}
