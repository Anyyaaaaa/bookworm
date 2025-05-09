package com.example.bookworm;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import paterns.adapters.adapters.ChaptersAdapter;
import Class.Chapter;

public class AudioBookActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView timeTextView;
    private ImageView coverImageView;
    private RecyclerView chaptersRecyclerView;
    private ChaptersAdapter adapter;
    private List<Chapter> chapters = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private Button pauseButton;
    private boolean isPlaying = false;
    private int currentPlayingIndex = -1;
    private Handler handler = new Handler();
    private ProgressBar progressBar;

    private Runnable updateTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_book);

        titleTextView = findViewById(R.id.audioBookTitle);
        timeTextView = findViewById(R.id.timeTextView);
        coverImageView = findViewById(R.id.audioBookCover);
        chaptersRecyclerView = findViewById(R.id.chaptersRecyclerView);
        pauseButton = findViewById(R.id.playButton);
        progressBar = findViewById(R.id.progressBar);

        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    isPlaying = false;
                    pauseButton.setBackgroundResource(R.drawable.ic_pause);
                } else {
                    mediaPlayer.start();
                    isPlaying = true;
                    pauseButton.setBackgroundResource(R.drawable.ic_play);
                    handler.post(updateTimeTask);
                }
                adapter.setCurrentPlayingPosition(currentPlayingIndex);
            }
        });

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (chapters != null && !chapters.isEmpty() && currentPlayingIndex < chapters.size() - 1) {
                currentPlayingIndex++;
                Chapter nextChapter = chapters.get(currentPlayingIndex);
                playAudio(nextChapter.getUrl());
                adapter.setCurrentPlayingPosition(currentPlayingIndex);
            } else {
                Toast.makeText(this, "Це останній розділ", Toast.LENGTH_SHORT).show();
            }
        });

        Button previousButton = findViewById(R.id.lastButton);
        previousButton.setOnClickListener(v -> {
            if (chapters != null && !chapters.isEmpty() && currentPlayingIndex > 0) {
                currentPlayingIndex--;
                Chapter previousChapter = chapters.get(currentPlayingIndex);
                playAudio(previousChapter.getUrl());
                adapter.setCurrentPlayingPosition(currentPlayingIndex);
            } else {
                Toast.makeText(this, "Це перший розділ", Toast.LENGTH_SHORT).show();
            }
        });

        Button forButton = findViewById(R.id.forButton);
        forButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int rewindPosition = Math.max(currentPosition - 10000, 0);
                mediaPlayer.seekTo(rewindPosition);
            }
        });

        Button forButton2 = findViewById(R.id.forButton2);
        forButton2.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                int forwardPosition = Math.min(currentPosition + 10000, duration);
                mediaPlayer.seekTo(forwardPosition);
            }
        });




        updateTimeTask = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    timeTextView.setText(formatTime(currentPosition));
                    progressBar.setProgress(currentPosition);
                    handler.postDelayed(this, 1000);
                }
            }
        };

        String bookId = getIntent().getStringExtra("bookId");
        String title = getIntent().getStringExtra("title");

        titleTextView.setText(title);

        adapter = new ChaptersAdapter(chapters, chapter -> {
            currentPlayingIndex = chapters.indexOf(chapter);
            playAudio(chapter.getUrl());
            adapter.setCurrentPlayingPosition(currentPlayingIndex);
        });

        chaptersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chaptersRecyclerView.setAdapter(adapter);

        loadBookData(bookId);
    }

    private String formatTime(int millis) {
        int seconds = (millis / 1000) % 60;
        int minutes = (millis / (1000 * 60)) % 60;
        int hours = millis / (1000 * 60 * 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void playAudio(String url) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
            );
        } else {
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                pauseButton.setBackgroundResource(R.drawable.ic_pause);
                progressBar.setMax(mp.getDuration());
                handler.post(updateTimeTask);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                handler.removeCallbacks(updateTimeTask);
                pauseButton.setBackgroundResource(R.drawable.ic_play);
            });

        } catch (IOException e) {
            Toast.makeText(this, "Помилка програвання", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadBookData(String bookId) {
        FirebaseFirestore.getInstance()
                .collection("audioBook")
                .document(bookId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String coverUrl = documentSnapshot.getString("cover");
                        if (coverUrl != null && !coverUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(coverUrl)
                                    .into(coverImageView);
                        }

                        List<Map<String, Object>> chaptersList =
                                (List<Map<String, Object>>) documentSnapshot.get("chapters");

                        if (chaptersList != null) {
                            chapters.clear();
                            for (Map<String, Object> ch : chaptersList) {
                                String title = (String) ch.get("title");
                                String url = (String) ch.get("url");
                                chapters.add(new Chapter(title, url));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Розділи не знайдено", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Документ книги не знайдено", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Помилка завантаження книги", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateTimeTask);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
