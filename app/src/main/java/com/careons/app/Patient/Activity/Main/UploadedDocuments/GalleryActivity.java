package com.careons.app.Patient.Activity.Main.UploadedDocuments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Activity.Main.QuickUpload.QuickUploadActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.CardGalleryBinding;
import com.crashlytics.android.Crashlytics;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {

    public static GalleryActivity galleryActivity;
    public static int via;

    private Toolbar toolbar;
    private static RecyclerView mRecyclerView;
    private FloatingActionMenu fab;
    private Spinner categorySpinner, sortSpinner;

    private static Adapter<Album, CardGalleryBinding> cardGalleryBindingAdapter;
    private static ArrayList<Album> albums;
    private static String categorySelected;
    private int mRequestCode = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Initialize instance
        galleryActivity = this;


        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Back enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Spinner
        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        bindSpinner();


        // FAB Menu Icon toggle
        fab = (FloatingActionMenu) findViewById(R.id.fab_menu);
        fab.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_ud_cloud_upload));
        fab.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                int drawableId;
                if (opened) {
                    drawableId = R.drawable.ic_add_white;
                } else {
                    drawableId = R.drawable.ic_ud_cloud_upload;
                }
                Drawable drawable = getResources().getDrawable(drawableId);
                fab.getMenuIconView().setImageDrawable(drawable);
            }
        });


        // Set upload options
        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabGallery.setOnClickListener(this);
        fabCamera.setOnClickListener(this);


        // Gallery staggered grid layout recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.masonry_grid);

        // Fetch category from intent
        Bundle extras = getIntent().getExtras();
        String category = extras.getString(StringConstants.KEY_CATEGORY);
        via = extras.getInt(StringConstants.KEY_VIA, 0);

        // Set initial category dropdown selector
        selectCategory(category);
        categorySelected = category;

        /**
         * Category Selector Listener
         */
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Fetch Data
                getGallery(getApplicationContext(), parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /**
         * Sort Selector Listener
         */
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                boolean sort = true;
                if (position != 0) {

                    sort = false;
                }

                // Sort Gallery
                sortGallery(getApplicationContext(), sort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Fetch gallery images
        getGallery(getApplicationContext(), categorySelected);
    }


    /**
     * Function to bind values to the spinner
     */
    public void bindSpinner() {

        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.tags, R.layout.item_spinner_toolbar);
        // Specify the layout to use when the list of choices appears
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySpinner.setAdapter(categoryAdapter);


        // Create an ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort, R.layout.item_spinner_gallery_filter);
        // Specify the layout to use when the list of choices appears
        sortAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sortSpinner.setAdapter(sortAdapter);
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

        Intent intent = null;
        int drawableId = R.drawable.ic_ud_cloud_upload;
        Drawable drawable = getResources().getDrawable(drawableId);

        switch (v.getId()) {

            case R.id.fab_gallery:
                fab.getMenuIconView().setImageDrawable(drawable);
                fab.close(true);
                intent = new Intent(this, QuickUploadActivity.class);
                intent.putExtra(StringConstants.KEY_CATEGORY, categorySpinner.getSelectedItemPosition());
                intent.putExtra(StringConstants.KEY_ACTION, StaticConstants.PICK_IMAGE_REQUEST);
                //finish();
                break;

            case R.id.fab_camera:
                fab.getMenuIconView().setImageDrawable(drawable);
                fab.close(true);
                intent = new Intent(this, QuickUploadActivity.class);
                intent.putExtra(StringConstants.KEY_CATEGORY, categorySpinner.getSelectedItemPosition());
                intent.putExtra(StringConstants.KEY_ACTION, StaticConstants.REQUEST_IMAGE_CAPTURE);
                //finish();
                break;

            default:
                break;
        }

        startActivityForResult(intent, mRequestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (requestCode == mRequestCode) {
                String selectedTag = data.getStringExtra("selectedTag");
                selectCategory(selectedTag);
                getGallery(getApplicationContext(), selectedTag);

            } else if(requestCode == StaticConstants.SEND_CHAT_IMAGE_REQUEST) {

                Intent intent = new Intent();
                intent.putExtra("selectedTitle", data.getStringExtra("selectedTitle"));
                intent.putExtra("selectedUrl",  data.getStringExtra("selectedUrl"));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }



    /**
     * Function to select initially selected category
     * @param category
     */
    public void selectCategory(String category) {

        switch (category) {

            case StringConstants.KEY_BILLS:
                categorySpinner.setSelection(0,true);
                break;

            case StringConstants.KEY_MEDICINE:
                categorySpinner.setSelection(1,true);
                break;

            case StringConstants.KEY_LAB_REPORTS:
                categorySpinner.setSelection(2,true);
                break;

            case StringConstants.KEY_PRESCRIPTIONS:
                categorySpinner.setSelection(3,true);
                break;

            case StringConstants.KEY_OTHERS:
                categorySpinner.setSelection(4,true);
                break;

            default:
                break;
        }
    }


    /**
     * Function to fetch gallery data
     * @param context
     * @param category
     */
    public static void getGallery(Context context, String category) {

        // Fetch Albums from database
        albums = (ArrayList<Album>) Album.find(Album.class, "patient_id = ? and tag = ?",
                SharedPreferenceService.getValue(galleryActivity, StringConstants.KEY_PATIENT_ID),
                category);

        categorySelected = category;
        sortGallery(context, true);
    }


    /**
     * Fetch gallery images
     * @param context
     * @param sortByLatest
     */
    public static void sortGallery(Context context, boolean sortByLatest) {


        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        /**
         *  Sort album list
         */

        if (sortByLatest) {
            Collections.sort(albums);
        } else {
            Collections.sort(albums, Collections.reverseOrder());
        }

        cardGalleryBindingAdapter = new Adapter<>(albums, R.layout.card_gallery, StaticConstants.GALLERY_ADAPTER);
        mRecyclerView.setAdapter(cardGalleryBindingAdapter);
    }


    /**
     * Function to delete Album
     * @param albumId
     */
    public static void deleteAlbum(final String albumId) {

        // Url Creation
        String url = ServiceUrls.KEY_UPLOAD_DOCUMENTS
                .concat(StringConstants.KEY_DELETE)
                .concat(StringConstants.KEY_SEPARATOR)
                .concat(albumId);

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_AUTHORIZATION,
                StaticConstants.KEY_BEARER
                        .concat(" ")
                        .concat(SharedPreferenceService.getValue(galleryActivity, StringConstants.KEY_TOKEN)));


        if (Validation.isConnected(galleryActivity)) {

            // API Call
            APICallService.DeleteAPICall(galleryActivity, galleryActivity, url, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        // Delete from local database
                        Album album =
                                Album.find(Album.class, "album_id = ?", albumId).get(0);
                        album.delete();
                        album.setCount(album.getCount() - 1);

                        // Notify adapter
                        getGallery(galleryActivity, categorySelected);


                    } catch (Exception e) {

                        // Log error
                        ErrorHandlers.handleError(galleryActivity);
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        } else {

            ErrorHandlers.handleInternetConnectionFailure(galleryActivity);
        }

    }
}
