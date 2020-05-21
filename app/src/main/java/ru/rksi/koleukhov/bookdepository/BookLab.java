package ru.rksi.koleukhov.bookdepository;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookLab
{
    private static BookLab sBookLab;
    private List<Book> mBooks;

    public static BookLab get(Context context)
    {
        if(sBookLab == null)
        {
            sBookLab = new BookLab(context);
        }
        return sBookLab;
    }

    private BookLab(Context context)
    {
        mBooks = new ArrayList<>();
    }

    public void addBook(Book b)
    {
        mBooks.add(b);
    }

    public List<Book> getBooks()
    {
        return mBooks;
    }

    public Book getBook(UUID id)
    {
        for(Book book : mBooks)
        {
            if(book.getId().equals(id))
            {
                return book;
            }
        }
        return null;
    }
}
