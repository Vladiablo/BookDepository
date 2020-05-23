package ru.rksi.koleukhov.bookdepository;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PhotoFragment extends DialogFragment
{
    private static final String ARG_PATH = "path";

    private ImageView mPhotoView;

    public static PhotoFragment newInstance(String path)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PATH, path);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String path = getArguments().getString(ARG_PATH);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo, null);
        mPhotoView = (ImageView) v.findViewById(R.id.fragment_photo_image_view);
        Bitmap bitmap = PictureUtils.getScaledBitmap(path, getActivity());
        mPhotoView.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                //.setTitle(path)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
