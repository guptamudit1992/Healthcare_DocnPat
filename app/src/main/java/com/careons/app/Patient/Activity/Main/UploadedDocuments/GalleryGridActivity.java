package com.careons.app.Patient.Activity.Main.UploadedDocuments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.Patient.Database.Models.Upload.UploadImage;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.CardGridGalleryBinding;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GalleryGridActivity extends AppCompatActivity {

    public static GalleryGridActivity galleryGridActivity;
    public static int via;
    public static String albumId;

    private static Toolbar toolbar;
    private static RecyclerView mRecyclerView;


    private static Adapter<UploadImage, CardGridGalleryBinding> cardGridGalleryBindingAdapter;
    private static ArrayList<UploadImage> uploadImages = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_grid);

        // Initialize instance
        galleryGridActivity = this;

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Back enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Gallery staggered grid layout recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.masonry_grid);


        // Fetch id from intent
        // Fetch category from intent
        Bundle extras = getIntent().getExtras();
        albumId = extras.getString(StringConstants.KEY_ALBUM_ID);
        via = extras.getInt(StringConstants.KEY_VIA, 0);

        // Fetch Album details
        getAlbumDetails(getApplicationContext(), albumId);
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


    /**
     * Initialize instance
     * @return
     */
    public static GalleryGridActivity getInstance() {

        return galleryGridActivity;
    }



    /**
     * Function to fetch Album
     * @param context
     * @param id
     */
    public static void getAlbumDetails(Context context, String id) {

        Album album = Album.find(Album.class, "album_id = ?", id).get(0);
        uploadImages = (ArrayList<UploadImage>) album.getUploadImages();

        /**
         * Set Toolbar title
         */
        toolbar.setTitle(album.getTitle());

        /**
         * Substitute images
         */
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        cardGridGalleryBindingAdapter = new Adapter<>(uploadImages, R.layout.card_grid_gallery, StaticConstants.GALLERY_GRID_ADAPTER);
        mRecyclerView.setAdapter(cardGridGalleryBindingAdapter);
    }


    /**
     * Function to delete Album
     * @param imageId
     */
    public static void deleteImage(final String imageId) {

        // Url Creation
        String url = ServiceUrls.KEY_UPLOAD_DOCUMENTS
                .concat(StringConstants.KEY_DELETE_IMAGES)
                .concat(StringConstants.KEY_SEPARATOR)
                .concat(imageId);

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_AUTHORIZATION,
                StaticConstants.KEY_BEARER
                        .concat(" ")
                        .concat(SharedPreferenceService.getValue(galleryGridActivity, StringConstants.KEY_TOKEN)));


        if (Validation.isConnected(galleryGridActivity)) {

            // API Call
            APICallService.DeleteAPICall(galleryGridActivity, galleryGridActivity, url, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Delete from local database
                        UploadImage uploadImage =
                                UploadImage.find(UploadImage.class, "image_id = ?", imageId).get(0);
                        uploadImage.delete();

                        /**
                         * Check If Required to delete Album
                         */
                        Album album =
                                Album.find(Album.class, "album_id = ?", albumId).get(0);

                        if(album.getCount() == 1) {
                            album.delete();

                            galleryGridActivity.finish();

                        } else {

                            album.setCount(album.getCount() - 1);
                            album.save();

                            // Notify adapter
                            getAlbumDetails(galleryGridActivity, albumId);
                        }


                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(galleryGridActivity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(galleryGridActivity);
        }

    }
}
