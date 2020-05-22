package ru.rksi.koleukhov.bookdepository;

import android.util.Log;

import java.util.Date;
import java.util.UUID;

public class Book
{
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mReaded;

    public Book()
    {
        this(UUID.randomUUID());
    }

    public Book(UUID id)
    {
        mId = id;
        mDate = new Date();
    }

    public void setTime(Date time)
    {
        mDate.setHours(time.getHours());
        mDate.setMinutes(time.getMinutes());
    }

    private void setTime(long time)
    {
        mDate.setTime(time);
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date)
    {
        //long time = mDate.getTime();
        mDate = date;
        //this.setTime(time);
    }

    public boolean isReaded() {
        return mReaded;
    }

    public void setReaded(boolean readed) {
        mReaded = readed;
    }
}
