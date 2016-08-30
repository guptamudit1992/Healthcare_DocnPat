package com.careons.app.Shared.Components.Blob;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Main.QuickUpload.QuickUploadActivity;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.Patient.Database.Models.Upload.UploadImage;
import com.careons.app.Patient.Enums.AlbumTag;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.crashlytics.android.Crashlytics;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class BlobStorage extends AsyncTask<String, Void, Void> {

    private static Activity activity;
    private static Album album;
    private static ArrayList<File> imageFiles = new ArrayList<>();
    private static ArrayList<String> uploadImagePaths = new ArrayList<>();

    // Progress Dialog Popup
    private static ProgressDialog popup;
    private int requestSender;



    /**
     * Parameterized Constructor
     * @param activity
     */
    public BlobStorage(Activity activity, Album album, ArrayList<File> imageFiles, int requestSender) {
        BlobStorage.activity = activity;
        BlobStorage.album = album;
        BlobStorage.imageFiles = imageFiles;
        this.requestSender = requestSender;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // Progress Dialog Popup
        popup = new ProgressDialog(activity);
        popup.setMessage(activity.getString(R.string.uploading_doc));
        popup.show();
    }


    @Override
    protected Void doInBackground(String... params) {
        try {

            // Setup the cloud storage account.
            CloudStorageAccount account = CloudStorageAccount.parse(QuickUploadActivity.storageConnectionString);

            // Create a blob service client
            CloudBlobClient blobClient = account.createCloudBlobClient();

            // Get a reference to a container
            // The container name must be lower case
            // Append a random UUID to the end of the container name so that
            // this sample can be run more than once in quick succession.
            CloudBlobContainer container = blobClient.getContainerReference(BuildProperties.azureBlob);

            // Create the container if it does not exist
            container.createIfNotExists();

            // Make the container public
            // Create a permissions object
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

            // Include public access in the permissions object
            containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

            // Set the permissions on the container
            //container.uploadPermissions(containerPermissions);

            // Clear
            uploadImagePaths.clear();

            for (int i = 0; i < imageFiles.size(); i++) {

                // check if file
                if (imageFiles.get(i).isFile()) {

                    try {

                        // set file name as local file name slash means
                        // adding directory
                        CloudBlockBlob blob = container.getBlockBlobReference(
                                SharedPreferenceService.getValue(activity.getApplicationContext(), StringConstants.KEY_PATIENT_ID)
                                        + "/" + album.getTag() + "/" + album.getDateOfTest() + i);

                        // upload file
                        blob.upload(new FileInputStream(imageFiles.get(i)), imageFiles.get(i).length());

                        // set new properties
                        blob.getProperties().getContentType();
                        blob.uploadProperties();

                        // Add path to upload documents
                        uploadImagePaths.add(blob.getUri().toString());

                    } catch (Exception error) {

                        // Log error
                        Crashlytics.logException(error);
                    }
                }
            }
            /**
             * Call Album Data API to sync data to backend
             */
            createUploadDocuments(activity.getApplicationContext());

        } catch (Exception error) {

            // Log error
            Crashlytics.logException(error);
            // Progress Dialog Popup
            popup.dismiss();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Progress Dialog Popup
        popup.dismiss();
    }

    /**
     * Function to create upload documents
     * @param context
     */
    public void createUploadDocuments(final Context context) {

        final Album _album = album;

        if (Validation.isConnected(context)) {

            // Service URL
            final String url = ServiceUrls.KEY_UPLOAD_DOCUMENTS.concat(StringConstants.KEY_CREATE);

            // Set Headers
            final HashMap<String, String> headers = new HashMap<>();
            headers.put(StaticConstants.KEY_AUTHORIZATION,
                    StaticConstants.KEY_BEARER
                            .concat(" ")
                            .concat(SharedPreferenceService.getValue(context, StringConstants.KEY_TOKEN)));

            // Constructing request body
            final HashMap<String, Object> data = new HashMap<>();
            data.put(StringConstants.KEY_PATIENT_ID, SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
            data.put(StringConstants.API_KEY_NAME, album.getTitle());
            data.put(StringConstants.API_KEY_UPLOAD_DATE, DateTimeUtils.convertTimestampToUTC(album.getDateOfTest()));
            data.put(StringConstants.KEY_TAG, album.getTag());
            data.put(StringConstants.KEY_COMMENT, album.getComments());
            data.put(StringConstants.API_KEY_IMAGE_URI, uploadImagePaths);
            data.put(StringConstants.KEY_TIMESTAMP, album.getDateOfTest());

            /**
             * API Call
             */
            APICallService.PostAPICall(activity, activity.getApplicationContext(), url, data, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        /**
                         * Save to Local Database
                         */
                        Album album = new Album(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                                response.getString(StringConstants.KEY_ID),
                                _album.getTitle(),
                                _album.getDateOfTest(),
                                AlbumTag.valueOf(_album.getTag()).getAlbumTag(),
                                _album.isAlbum(),
                                _album.getCount(),
                                _album.getComments()
                        );
                        album.save();


                        /**
                         * Save to Local Database
                         */
                        for (int i = 0; i < imageFiles.size(); i++) {

                            JSONArray imageIds = response.getJSONArray(StringConstants.KEY_IMAGE_ID);

                            // Save to image to database
                            UploadImage uploadImage = new UploadImage(
                                    response.getString(StringConstants.KEY_ID),
                                    imageIds.getString(i),
                                    imageFiles.get(i).toString(),
                                    uploadImagePaths.get(i)
                            );

                            uploadImage.save();
                        }


                        Toast.makeText(activity, activity.getString(R.string.upload_success),
                                Toast.LENGTH_LONG).show();

                        if(requestSender == StaticConstants.GALLERY_ADAPTER) {

                            // Navigate to Gallery on success
                            Intent intent = new Intent();
                            intent.putExtra("selectedTag", AlbumTag.valueOf(_album.getTag()).getAlbumTag());
                            intent.putExtra("selectedUrl", uploadImagePaths.get(0));
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finish();

                        } else {

                            Intent intent = new Intent();
                            intent.putExtra("selectedTitle", _album.getTitle());
                            intent.putExtra("selectedUrl", uploadImagePaths.get(0));
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finish();
                        }

                        // Dismiss item_loader
                        //popup.dismiss();


                    } catch (Exception e) {

                        // Log error
                        Crashlytics.logException(e);
                        ErrorHandlers.handleError(activity);
                        // Progress Dialog Popup
                        popup.dismiss();
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Progress Dialog Popup
                    popup.dismiss();
                }
            });

        } else {

            // No Internet Connection
            ErrorHandlers.handleInternetConnectionFailure(activity);
            // Progress Dialog Popup
            popup.dismiss();
        }
    }
}
