package com.careons.app.Patient.Activity.Main.QuickUpload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Shared.Components.Blob.BlobStorage;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.Patient.Models.Upload.ImageModel;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.GalleryPathUtil;
import com.careons.app.databinding.ItemQuickUploadBinding;
import com.crashlytics.android.Crashlytics;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class QuickUploadActivity extends AppCompatActivity
        implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private RecyclerView snapshotRecyclerView;
    private FloatingActionButton saveFab;
    private TextView addImageCard, dateFieldTextView;
    private EditText titleFieldEditText, commentsFieldEditText;
    private Spinner tagSpinner;

    /**
     * Stores the storage connection string
     */
    public static final String storageConnectionString = String.format(ServiceUrls.AZURE_BLOB_URL,
            BuildProperties.azureAccountName,
            BuildProperties.azureAccountKey);

    private static ArrayList<File> imageFiles = new ArrayList<>();

    private long uploadTimestamp;
    private ArrayList<ImageModel> imageModels = new ArrayList<>();
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private ArrayList<String> imageFilePaths = new ArrayList<>();
    private static Adapter<ImageModel, ItemQuickUploadBinding> imageModelItemQuickUploadBindingAdapter;
    private Uri uri;
    private boolean isOpenOnCreate = true;
    private boolean isUploaded = false;
    private File dir, destImage, file;
    private String cameraFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_upload);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.quick_upload));
        }

        // Back enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Set spinner adapter
        tagSpinner = (Spinner) findViewById(R.id.tag_spinner);
        tagSpinner.setOnItemSelectedListener(this);
        bindSpinner();


        // Initialize placeholder
        snapshotRecyclerView = (RecyclerView) findViewById(R.id.snapshot_recycler_view);

        titleFieldEditText = (EditText) findViewById(R.id.title_field);
        addImageCard = (TextView) findViewById(R.id.add_more);
        saveFab = (FloatingActionButton) findViewById(R.id.fab_save);
        dateFieldTextView = (TextView) findViewById(R.id.date_field);
        commentsFieldEditText = (EditText) findViewById(R.id.comments_field);

        // Attach click listeners
        addImageCard.setOnClickListener(this);
        saveFab.setOnClickListener(this);
        dateFieldTextView.setOnClickListener(this);


        // Set Date Field
        uploadTimestamp = DateTimeUtils.getCurrentTimestamp();
        dateFieldTextView.setText(DateTimeUtils.convertTimestampToShortDate(DateTimeUtils.getCurrentTimestamp()));

        // Extract data from intent
        Intent intent = getIntent();
        int action = intent.getIntExtra(StringConstants.KEY_ACTION, 1);
        // Take action according to intent data passed
        if (action > 0) {
            if (action == StaticConstants.PICK_IMAGE_REQUEST) {
                selectImageViaGallery(getApplicationContext());
            } else {
                selectImageViaCamera(getApplicationContext());
            }
        }

        // Clear image file folder
        imageFiles.clear();


        // Attach focus change listeners on title and date
        titleFieldEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(!(titleFieldEditText.getText().toString().trim().length() > 0)) {

                        titleFieldEditText.setError(getString(R.string.required));

                    } else {

                        titleFieldEditText.setError(null);
                    }
                }
            }
        });

        dateFieldTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(!hasFocus) {
                        if(!(dateFieldTextView.getText().toString().trim().length() > 0)) {

                            dateFieldTextView.setError(getString(R.string.required));

                        } else {

                            dateFieldTextView.setError(null);
                        }
                    }
                }
            }
        });


        // Fetch category from intent
        Bundle extras = getIntent().getExtras();
        int category = extras.getInt(StringConstants.KEY_CATEGORY);
        tagSpinner.setSelection(category,true);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab_save:
                saveUploadedImage(getApplicationContext());
                break;

            case R.id.add_more:
                isOpenOnCreate = false;
                selectImage();
                break;

            case R.id.date_field:
                openDatePicker();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous activity
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(this);
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        uploadTimestamp = DateTimeUtils.convertTimeToTimestamp(year, monthOfYear, dayOfMonth, 0, 0);

        dateFieldTextView.setText(DateTimeUtils.convertTimestampToShortDate(uploadTimestamp));
    }


    /**
     * Function to bind values to the spinner
     */
    public void bindSpinner() {

        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tags, R.layout.item_spinner);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        tagSpinner.setAdapter(adapter);
    }


    /**
     * Function to implement date picker
     */
    public void openDatePicker() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                QuickUploadActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show(getFragmentManager(), "Datepickerdialog");
        dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
        dpd.showYearPickerFirst(true);
        dpd.setMaxDate(now);
        dpd.setYearRange(StaticConstants.MIN_DATE, now.get(Calendar.YEAR));
    }


    private void selectImage() {
        final CharSequence[] items = {getString(R.string.choose_gallery), getString(R.string.camera)};
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickUploadActivity.this);
        builder.setTitle(getString(R.string.add_another));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.choose_gallery))) {
                    selectImageViaGallery(getApplicationContext());
                } else {
                    selectImageViaCamera(getApplicationContext());
                }
            }
        });
        builder.show();
    }


    /**
     * Function to uplaod image via gallery
     *
     * @param context
     */
    public void selectImageViaGallery(Context context) {

        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), StaticConstants.PICK_IMAGE_REQUEST);
    }


    /**
     * Function to uplaod image via camera
     *
     * @param context
     */
    public void selectImageViaCamera(Context context) {

        String directoryName = String.format("/%s", getString(R.string.app_name));
        dir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "Careons");

        if (!dir.isDirectory())
            dir.mkdir();

        destImage = new File(dir, new Date().getTime() + ".png");
        cameraFile = destImage.getAbsolutePath();

        try {

            if(!destImage.createNewFile())
                Log.e("check", "unable to create empty file");

        } catch(IOException ex){
            ex.printStackTrace();
        }

        file = new File(destImage.getAbsolutePath());
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destImage));
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, StaticConstants.REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle image result
        if (resultCode == RESULT_OK) {
            if (requestCode == StaticConstants.PICK_IMAGE_REQUEST) {

                // Handle image display from gallery
                handleGalleryImage(getApplicationContext(), data);

            } else if (requestCode == StaticConstants.REQUEST_IMAGE_CAPTURE) {

                // Handle image display from camera
                handleGalleryCamera(getApplicationContext(), data);

            }
        } else if (isOpenOnCreate) {
            finish();
        }
    }


    /**
     * Function to handle image selected from gallery
     *
     * @param context
     * @param data
     */
    public void handleGalleryImage(Context context, Intent data) {
        uri = data.getData();

        try {

            /**
             * Fetch file path
             */
            if (Build.VERSION.SDK_INT > 18) {

                file = new File(GalleryPathUtil.getRealPathFromURI_API19(context, uri));

            } else {

                file = new File(GalleryPathUtil.getRealPathFromURI_API11to18(context, uri));
            }

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());


            /**
             * Resize Image
             */
            if(bitmap.getHeight() > 1200 && bitmap.getWidth() > 960) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            }

            resizeFileImage(bitmap);

            /**
             * Prepare to upload to Azure Blob
             */
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            ImageModel imageModel = new ImageModel(drawable);
            bitmaps.add(bitmap);
            imageModels.add(imageModel);
            updateSnapshotPlaceholder();

        } catch (Exception e) {

            // Log error
            Toast.makeText(getApplicationContext(), getString(R.string.err_problem_occurred), Toast.LENGTH_LONG).show();
            Crashlytics.logException(e);
        }
    }


    /**
     * Function to handle image selected from camera
     *
     * @param context
     * @param data
     */
    public void handleGalleryCamera(Context context, Intent data) {

        if(file != null){

            String selectedImagePath = file.getAbsolutePath();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
            resizeFileImage(bitmap);

            /**
             * Prepare image to be uploaded to Azure
             */
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            ImageModel imageModel = new ImageModel(drawable);
            bitmaps.add(bitmap);
            imageModels.add(imageModel);
            updateSnapshotPlaceholder();


        } else {
            // Log error
            Toast.makeText(this, getString(R.string.err_problem_occurred), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Function to update recycler view after addition of image
     */
    public void updateSnapshotPlaceholder() {

        // Set adapter
        imageModelItemQuickUploadBindingAdapter =
                new Adapter<>(imageModels, R.layout.item_quick_upload,
                        StaticConstants.QUICK_UPLOAD_ADAPTER);

        snapshotRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        snapshotRecyclerView.setAdapter(imageModelItemQuickUploadBindingAdapter);
    }


    /**
     * Function to save uploaded image
     *
     * @param context
     */
    public void saveUploadedImage(final Context context) {


        // Check Detail fields
        if (!(titleFieldEditText.getText().toString().trim().length() > 0)) {

            titleFieldEditText.setError(getString(R.string.required));

        } else if (!(dateFieldTextView.getText().toString().trim().length() > 0)) {

            dateFieldTextView.setError(getString(R.string.required));

        } else {

            // If all fields are ok
            boolean isMultiple = false;

            // Check if multiple images
            if (bitmaps.size() > 0) {
                isMultiple = true;
            }

            /**
             * Temporary Album
             */
            Album album = new Album(
                    SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                    "1",
                    titleFieldEditText.getText().toString(),
                    uploadTimestamp,
                    tagSpinner.getSelectedItem().toString().replace(" ", ""),
                    isMultiple,
                    bitmaps.size(),
                    commentsFieldEditText.getText().toString()
            );


            /**
             * Background Task - Upload to cloud
             */
            runBlobUpload(album, imageFiles);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isUploaded) {
            for (int i = 0; i < imageFilePaths.size(); i++) {
                File mFile = new File(imageFilePaths.get(i));
                mFile.delete();
            }
        }
    }



    /**
     * Function to save Image to local
     * @param bitmap
     */
    private void resizeFileImage(Bitmap bitmap) {

        String directoryName = String.format("/%s", getString(R.string.app_name));

        // Create Directory root
        File directory = new File(getApplication().getFilesDir(), directoryName);
        // Create directory if does not exists
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Define image file
        File imageFile = new File(directory.getPath(),
                String.valueOf(System.currentTimeMillis()).concat(".png"));

        // Encode the file as a PNG image.
        FileOutputStream outStream;

        try {

            outStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            // 100 to keep full quality of the image

            outStream.flush();
            outStream.close();

            // Add to Azure blob
            imageFiles.add(imageFile);

        } catch (FileNotFoundException e) {

            // Log error
            Toast.makeText(this, getString(R.string.err_problem_occurred), Toast.LENGTH_LONG).show();

        } catch (IOException e) {

            // Log error
            Toast.makeText(this, getString(R.string.err_problem_occurred), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Runs this when initiating upload command
     * @param album
     */
    public void runBlobUpload(Album album, ArrayList<File> _imageFiles) {
        new BlobStorage(this, album, _imageFiles, StaticConstants.GALLERY_ADAPTER).execute();
    }
}

