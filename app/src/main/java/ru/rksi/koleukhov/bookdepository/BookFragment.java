package ru.rksi.koleukhov.bookdepository;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

public class BookFragment extends Fragment
{
    private static final String ARG_BOOK_ID = "book_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO = "DialogPhoto";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_IMAGE = 3;
    private Book mBook;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mReportButton;
    private CheckBox mReadedCheckBox;
    private ImageButton mPhotoButton;
    private ImageButton mImageButton;
    private ImageView mPhotoView;

    public static BookFragment newInstance(UUID bookId)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK_ID, bookId);
        BookFragment fragment = new BookFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID bookId = (UUID) getArguments().getSerializable(ARG_BOOK_ID);
        mBook = BookLab.get(getActivity()).getBook(bookId);
        mPhotoFile = BookLab.get(getActivity()).getPhotoFile(mBook);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        BookLab.get(getActivity()).updateBook(mBook);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_book, container, false);
        mTitleField = (EditText) v.findViewById(R.id.book_title);
        mTitleField.setText(mBook.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                mBook.setTitle(mTitleField.getText().toString());
            }
        });

        mDateButton = (Button) v.findViewById(R.id.book_date);
        updateDate();
        mTimeButton = (Button) v.findViewById(R.id.book_time);
        updateTime();
        mDateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //FragmentManager manager = getFragmentManager();
                //DatePickerFragment dialog = DatePickerFragment.newInstance(mBook.getDate());
                //dialog.setTargetFragment(BookFragment.this, REQUEST_DATE);
                Intent intent = DatePickerActivity.newIntent(getActivity(), mBook.getDate());
                startActivityForResult(intent, REQUEST_DATE);
                //dialog.show(manager, DIALOG_DATE);
            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mBook.getDate());
                dialog.setTargetFragment(BookFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mReadedCheckBox = (CheckBox) v.findViewById(R.id.book_readed);
        mReadedCheckBox.setChecked(mBook.isReaded());
        mReadedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                mBook.setReaded(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.book_report);
        mReportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());
                intentBuilder.setType("text/plain");
                intentBuilder.setText(getBookReport());
                intentBuilder.setSubject(getString(R.string.book_report_subject));
                intentBuilder.setChooserTitle(R.string.send_report);
                startActivity(intentBuilder.createChooserIntent());

                //Intent i = new Intent(Intent.ACTION_SEND);
                //i.setType("text/plain");
                //i.putExtra(Intent.EXTRA_TEXT, getBookReport());
                //i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.book_report_subject));
                //i = Intent.createChooser(i, getString(R.string.send_report));
                //startActivity(i);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.book_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = getActivity().getPackageManager();
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        if(canTakePhoto)
        {
            Uri uri;
            if(Build.VERSION.SDK_INT < 24)
                uri = Uri.fromFile(mPhotoFile);
            else
                uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            mPhotoButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivityForResult(captureImage, REQUEST_PHOTO);
                }
            });
        }

        mImageButton = (ImageButton) v.findViewById(R.id.book_gallery);
        final Intent getImage = new Intent(Intent.ACTION_PICK);
        getImage.setType("image/*");
        getImage.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.book_gallery_subject));
        if(mPhotoFile != null)
        {
            Uri uri;
            if(Build.VERSION.SDK_INT < 24)
                uri = Uri.fromFile(mPhotoFile);
            else
                uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", mPhotoFile);
            mImageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    startActivityForResult(Intent.createChooser(getImage, getString(R.string.book_gallery)), REQUEST_IMAGE);
                }
            });
        }

        mPhotoView = (ImageView) v.findViewById(R.id.book_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager manager = getFragmentManager();
                PhotoFragment fragment = PhotoFragment.newInstance(mPhotoFile.getPath());
                fragment.show(manager, DIALOG_PHOTO);
            }
        });

        ViewTreeObserver observer = mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                updatePhotoView();
            }
        });
        return v;
    }

    private void updateDate() {
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(mBook.getDate()));
    }

    private void updateTime()
    {
        mTimeButton.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(mBook.getDate()));
    }

    private String getBookReport()
    {
        String readedString = null;
        if(mBook.isReaded())
        {
            readedString = getString(R.string.book_report_readed);
        }
        else
        {
            readedString = getString(R.string.book_report_unreaded);
        }

        String dateFormat =  "EEE, MMM dd";
        String dateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(mBook.getDate());
        String report = getString(R.string.book_report, mBook.getTitle(), dateString, readedString);
        return report;
    }

    private void updatePhotoView()
    {
        if(mPhotoFile == null || !mPhotoFile.exists())
        {
            mPhotoView.setImageDrawable(null);
        }
        else
        {
            Bitmap bitmap = null;
            if(mPhotoView.isDirty())
                bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            else
                bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoView.getWidth(), mPhotoView.getHeight());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }
        if(requestCode == REQUEST_DATE)
        {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBook.setDate(date);
            updateDate();
            updateTime();
        }
        else if(requestCode == REQUEST_TIME)
        {
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mBook.setTime(time);
            updateTime();
        }
        else if(requestCode == REQUEST_PHOTO)
        {
            updatePhotoView();
        }
        else if(requestCode == REQUEST_IMAGE)
        {
            Uri selectedImage = data.getData();
            String imagePath = PictureUtils.getPicturePath(getContext(), selectedImage);
            Bitmap image = BitmapFactory.decodeFile(imagePath);
            if(image == null)
            {
                Log.e("BookDepository", "Read external storage permission not granted");
                return;
            }
            try
            {
                mPhotoFile.createNewFile();
            }
            catch (IOException e)
            {
                Log.e("BookDepository", "Failed to create file!");
                e.printStackTrace();
            }
            FileOutputStream output = null;
            try
            {
                output = new FileOutputStream(mPhotoFile);
            }
            catch (FileNotFoundException e)
            {
                Log.e("BookDepository", "File not found");
                e.printStackTrace();
            }

            if(output != null)
            {
                image.compress(Bitmap.CompressFormat.PNG, 75, output);
                try
                {
                    output.close();
                    updatePhotoView();
                }
                catch (IOException e)
                {
                    Log.wtf("BookDepository", "Failed to close stream!");
                }
            }
            else
            {
                Log.e("BookDepository", "Failed to open image.");
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_item_delete_book:
                BookLab.get(getActivity()).deleteBook(mBook);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
