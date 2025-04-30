package Interface;
import Class.Book;

import java.util.List;

public interface BookCallback {
    void onBooksLoaded(List<Book> books);
}

