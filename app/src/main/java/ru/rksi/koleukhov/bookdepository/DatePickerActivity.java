package ru.rksi.koleukhov.bookdepository;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity
{
    public static final String EXTRA_DATE = "ru.rksi.koleukhov.bookdepository.date";

    public static Intent newIntent(Context packageContext, Date date)
    {
        Intent intent = new Intent(packageContext, DatePickerActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected Fragment createFragment()
    {
        return DatePickerFragment.newInstance(new Date());
    }
}
