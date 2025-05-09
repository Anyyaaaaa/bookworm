package paterns.adapters.facade;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.bookworm.ReaderActivity;

import java.io.File;
import java.io.IOException;

public class bookUtils {

    public static void downloadAndOpenBook(Activity activity, String bookUrl) {
        try {
            java.net.URL url = new java.net.URL(bookUrl);
            String fileExtension = bookUrl.contains(".pdf") ? ".pdf" : ".epub";
            File localFile = File.createTempFile("tempBook", fileExtension, activity.getCacheDir());

            new Thread(() -> {
                try (java.io.InputStream in = url.openStream();
                     java.io.FileOutputStream out = new java.io.FileOutputStream(localFile)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    activity.runOnUiThread(() -> {
                        Intent intent = new Intent(activity, ReaderActivity.class);
                        intent.putExtra("book_path", localFile.getAbsolutePath());
                        intent.putExtra("book_type", fileExtension.equals(".pdf") ? "pdf" : "epub");
                        activity.startActivity(intent);
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> Toast.makeText(activity, "Не вдалося завантажити книгу", Toast.LENGTH_SHORT).show());
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Помилка URL", Toast.LENGTH_SHORT).show();
        }
    }
}
