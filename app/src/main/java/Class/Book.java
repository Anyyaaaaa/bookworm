package Class;
public class Book {
    private String title;
    private String author;
    private String genre;
    private String imageUrl;
    private String description;
    private String bookUrl;




    public Book() {}

    public Book(String title,String author, String imageUrl,String bookUrl,String genre) {
        this.title = title;
        this.author = author;
        this.genre=genre;
        this.imageUrl = imageUrl;
        this.bookUrl=bookUrl;
    }

    public String getTitle() { return title; }
    public String getAuthor(){return author;}
    public String getGenre(){return genre;}
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public String getBookUrl(){return bookUrl;}
}
