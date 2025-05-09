package com.example.bookworm.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.bookworm.R;
import com.example.bookworm.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    private EditText profileName;
    private ImageView profileAvatar;
    private Button logoutButton, saveButton;
    private LinearLayout editProfileLayout;
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileName = view.findViewById(R.id.profileName); // Тепер EditText
        profileAvatar = view.findViewById(R.id.profileAvatar);
        logoutButton = view.findViewById(R.id.logoutButton);
        saveButton = view.findViewById(R.id.saveButton); // Кнопка збереження
        editProfileLayout = view.findViewById(R.id.editProfileLayout);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Натискання на кнопку редагування профілю
        editProfileLayout.setOnClickListener(v -> enableProfileEditing());

        // Вихід з Firebase
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireActivity(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        // Завантажуємо дані користувача з Firestore
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userName = document.getString("login");
                                profileName.setText(userName != null && !userName.isEmpty() ? userName : currentUser.getEmail());

                                String imageUrl = document.getString("profileImageUrl");
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Picasso.get().load(imageUrl).into(profileAvatar);
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

        saveButton.setOnClickListener(v -> saveProfileChanges());

        // Один раз додаємо обробник на клік для аватара
        profileAvatar.setOnClickListener(v -> {
            if (editProfileLayout.getVisibility() == View.VISIBLE) {
                openGallery();  // дозволяємо змінювати фото тільки в режимі редагування
            } else {
                Toast.makeText(getActivity(), "Для зміни фото профілю спочатку активуйте редагування.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void enableProfileEditing() {
        if (editProfileLayout.getVisibility() == View.VISIBLE) {
            profileName.setFocusableInTouchMode(true);
            profileName.setFocusable(true);
            profileName.setClickable(true);

            editProfileLayout.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        }
    }

    private void saveProfileChanges() {
        String newUserName = profileName.getText().toString().trim();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && !newUserName.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid())
                    .update("login", newUserName)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Логін оновлено", Toast.LENGTH_SHORT).show();
                        profileName.setFocusable(false);
                        editProfileLayout.setVisibility(View.VISIBLE);  // Показуємо кнопку редагування
                        saveButton.setVisibility(View.GONE);  // Сховуємо кнопку збереження
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Не вдалося оновити логін", Toast.LENGTH_SHORT).show();
                    });
        }
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
        canvas.drawBitmap(bitmap, (width - bitmap.getWidth()) / 2f, (width - bitmap.getHeight()) / 2f, null);
        return output;
    }
}
