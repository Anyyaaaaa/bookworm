package com.example.bookworm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ReaderActivity extends AppCompatActivity {
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        pdfView = findViewById(R.id.pdfView);

        Intent intent = getIntent();
        String bookPath = intent.getStringExtra("book_path");
        String bookType = intent.getStringExtra("book_type");

        if (bookPath != null) {
            File file = new File(bookPath);

            if (file.exists()) {
                if (bookType.equals("pdf")) {
                    openPDF(file);
                } else {
                    Toast.makeText(this, "Підтримуються тільки PDF-файли", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Файл книги не знайдено", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Немає шляху до книги", Toast.LENGTH_SHORT).show();
        }
    }
    private void openPDF(File file) {
        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .load();
    }
}
