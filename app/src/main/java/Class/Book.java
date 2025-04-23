package Class;

import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String author;
    private int pagesRead;
    private int totalPages;
    private String coverUri; // поле для збереження URI обкладинки

    // Конструктор
    public Book(String title, String author, int pagesRead, int totalPages, String coverUri) {
        this.title = title;
        this.author = author;
        this.pagesRead = pagesRead;
        this.totalPages = totalPages;
        this.coverUri = coverUri;
    }

    // Геттери та сеттери для полів
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPagesRead() {
        return pagesRead;
    }

    public void setPagesRead(int pagesRead) {
        this.pagesRead = pagesRead;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getCoverUri() {
        return coverUri; // цей метод повертає URI обкладинки
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }
}

