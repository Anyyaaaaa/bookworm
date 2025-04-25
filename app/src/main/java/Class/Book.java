package Class;
public class Book {
    private String title;
    private String author;
    private String imageUrl;
    private String description;




    public Book() {}

    public Book(String title,String author, String imageUrl) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getAuthor(){return author;}
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
}
