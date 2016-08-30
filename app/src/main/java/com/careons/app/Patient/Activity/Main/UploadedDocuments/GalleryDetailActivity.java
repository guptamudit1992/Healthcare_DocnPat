package com.careons.app.Patient.Activity.Main.UploadedDocuments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.Patient.Database.Models.Upload.UploadImage;
import com.careons.app.R;
import com.careons.app.Shared.Utils.DateTimeUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GalleryDetailActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    private Album album;
    private String url;
    private int i;
    private static List<UploadImage> uploadImageList;
    private TextView titleTextView, dateTestTextView, commentsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_detail);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        // Initialize
        titleTextView = (TextView) findViewById(R.id.title);
        dateTestTextView = (TextView) findViewById(R.id.test_date);
        commentsTextView = (TextView) findViewById(R.id.comments);

        // Back enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Fetch id from intent
        // Fetch category from intent
        Bundle extras = getIntent().getExtras();
        String id = extras.getString(StringConstants.KEY_ALBUM_ID);
        url = extras.getString(StringConstants.KEY_URL);

        // Fetch Album details
        i = 0;
        getAlbumDetails(getApplicationContext(), id);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(i);
    }


    /**
     * Function to fetch Album
     * @param context
     * @param id
     */
    public void getAlbumDetails(Context context, String id) {

        album = Album.find(Album.class, "album_id = ?", id).get(0);
        uploadImageList = album.getUploadImages();

        for(UploadImage uploadImage : uploadImageList) {
            if(uploadImage.getAzureServerUrl().equalsIgnoreCase(url)) {
                break;

            }
            i++;
        }

        // Substitute Values
        titleTextView.setText(album.getTitle());
        dateTestTextView.setText(DateTimeUtils.convertTimestampToDate(album.getDateOfTest()));
        commentsTextView.setText(album.getComments());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous activity
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_gallery_detail, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.section_label);
            Picasso
                    .with(imageView.getContext())
                    .load(uploadImageList.get(getArguments().getInt(ARG_SECTION_NUMBER) - 1).getAzureServerUrl())
                    .placeholder(R.drawable.dp_man)
                    //.error()
                    .into(imageView);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show total pages.
            return uploadImageList.size();
        }
    }
}
