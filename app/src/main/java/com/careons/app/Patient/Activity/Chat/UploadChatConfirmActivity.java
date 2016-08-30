package com.careons.app.Patient.Activity.Chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Shared.Components.Blob.BlobStorage;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.Validation;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UploadChatConfirmActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView chatImageView;
    private EditText chatTitleEditText;

    private static Activity activity;
    private Uri uri;
    private ArrayList<File> imageFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_chat_confirm);

        // Initialize activity
        activity = this;

        /**
         * Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.send_photo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /**
         * Initialize
         */
        chatImageView = (ImageView) findViewById(R.id.chat_image);
        chatTitleEditText = (EditText) findViewById(R.id.chat_title);

        /**
         * CTA set on click listeners
         */
        findViewById(R.id.cta_send).setOnClickListener(this);
        findViewById(R.id.cta_cancel).setOnClickListener(this);


        /**
         * Fetch Data from intent
         */
        Intent intent = getIntent();
        Intent data = intent.getParcelableExtra(StringConstants.KEY_DATA);
        int requestCode = intent.getIntExtra(StringConstants.KEY_REQUEST_CODE, 0);
        handleImage(requestCode, data);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous acitivity
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.cta_send:

                if(!Validation.isEmpty(chatTitleEditText.getText().toString())) {
                    sendImage();

                } else {

                    Toast.makeText(this, getString(R.string.err_msg_chat_title), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.cta_cancel:
                finish();
                break;
        }
    }





    /**
     * Function to handle selected image
     * @param data
     */
    public void handleImage(int requestCode, Intent data) {

        Bitmap bitmap = null;
        if (requestCode == StaticConstants.PICK_IMAGE_REQUEST) {

            // Case - Gallery Upload
            uri = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            } catch (IOException e) {

                // Log error
                Toast.makeText(getApplicationContext(), getString(R.string.err_problem_occurred), Toast.LENGTH_LONG).show();
                Crashlytics.logException(e);
            }

        } else if (requestCode == StaticConstants.REQUEST_IMAGE_CAPTURE) {

            // Case - Camera Upload
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
        }


        /**
         * Save bitmap to local
         */
        resizeFileImage(bitmap);
        Drawable drawable = new BitmapDrawable(getResources(), optimizePic(bitmap));

        /**
         * Set Drawable in ImageView
         */
        chatImageView.setImageDrawable(drawable);
    }



    /**
     * Function to save Image to local
     * @param bitmap
     */
    private void resizeFileImage(Bitmap bitmap) {
        boolean temp = true;

        String directoryName = String.format("/%s", getString(R.string.app_name));

        // Create Directory root
        File directory = new File(getApplication().getFilesDir(), directoryName);
        // Create directory if does not exists
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (temp) {

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

                // Handle error
                temp = false;
                // Log error
                Toast.makeText(this, getString(R.string.err_problem_occurred), Toast.LENGTH_LONG).show();

            } catch (IOException e) {

                // Handle error
                temp = false;
                // Log error
                Toast.makeText(this, getString(R.string.err_problem_occurred), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Adjust image to optimize functionality
     */
    private Bitmap optimizePic(Bitmap bitmap) {

        // Get the dimensions of the View
        int targetW = 100;
        int targetH = 100;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int photoW = bitmap.getWidth();
        int photoH = bitmap.getHeight();

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return bitmap;
    }


    /**
     * Function to send image
     */
    public void sendImage() {

        /**
         * Temporary Album
         */
        Album album = new Album(
                SharedPreferenceService.getValue(this, StringConstants.KEY_PATIENT_ID),
                "1",
                chatTitleEditText.getText().toString(),
                DateTimeUtils.getCurrentTimestamp(),
                "Others",
                false,
                1,
                null
        );


        /**
         * Background Task - Upload to cloud
         */
        runBlobUpload(album, imageFiles);
    }


    /**
     * Runs this when initiating upload command
     * @param album
     */
    public void runBlobUpload(Album album, ArrayList<File> _imageFiles) {
        new BlobStorage(this, album, _imageFiles, StaticConstants.CHAT_ADAPTER).execute();
    }
}
