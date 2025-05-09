package Class;

import com.google.firebase.Timestamp;

public class Review {
    private String userId;
    private String email;
    private float rating;
    private String text;
    private String title;
    private Timestamp timestamp;

    public Review() {
    }

    public Review(String userId, String email, float rating, String text, String title) {
        this.userId = userId;
        this.email = email;
        this.rating = rating;
        this.text = text;
        this.title = title;
        this.timestamp = Timestamp.now();
    }

    public String getUserId() { return userId; }
    public float getRating() { return rating; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

}
