package com.example.bookworm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import Class.CircleTransform;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private TextView profileName;
    private ImageView profileAvatar;

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileName = view.findViewById(R.id.profileName);
        profileAvatar = view.findViewById(R.id.profileAvatar);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profileAvatar.setOnClickListener(v -> openGallery());
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Завантажуємо дані користувача з Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userName = document.getString("login");
                                if (userName != null && !userName.isEmpty()) {
                                    profileName.setText(userName);
                                } else {
                                    profileName.setText(currentUser.getEmail());
                                }

                                // Завантажуємо фото профілю, якщо є URL
                                String imageUrl = document.getString("profileImageUrl");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Picasso.get()
                                            .load(imageUrl)
                                            .transform(new CircleTransform())
                                            .into(profileAvatar);
                                }
                            } else {
                                profileName.setText(currentUser.getEmail());
                            }
                        } else {
                            profileName.setText("Помилка завантаження даних");
                        }
                    });
        } else {
            profileName.setText("Гість");
        }

        return view;
    }

    // Відкриття галереї для вибору фото
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Обробка результату вибору зображення
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                Bitmap circularBitmap = getCircularBitmap(bitmap);
                profileAvatar.setImageBitmap(circularBitmap);
                uploadImageToFirebase(imageUri);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Не вдалося завантажити зображення", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Завантаження зображення в Firebase Storage
    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            StorageReference fileReference = storageReference.child("avatarUsers/" + currentUser.getUid() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveImageUrlToFirestore(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseError", "Error uploading image", e);
                        Toast.makeText(getActivity(), "Не вдалося завантажити фото", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Збереження URL зображення в Firestore
    private void saveImageUrlToFirestore(String imageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid())
                    .update("profileImageUrl", imageUrl)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Фото профілю оновлено", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Не вдалося оновити фото профілю", Toast.LENGTH_SHORT).show());
        }
    }

    // Метод для створення круглого зображення
    public Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Path path = new Path();
        path.addCircle(width / 2f, width / 2f, width / 2f, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return output;
    }
}

