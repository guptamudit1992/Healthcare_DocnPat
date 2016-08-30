package com.careons.app.Patient.Activity.Main.HealthBook;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Fragments.HealthBook.AllergyFragment;
import com.careons.app.Patient.Fragments.HealthBook.ChildhoodFragment;
import com.careons.app.Patient.Fragments.HealthBook.FamilyFragment;
import com.careons.app.Patient.Fragments.HealthBook.GynaecologicalFragment;
import com.careons.app.Patient.Fragments.HealthBook.LifestyleFragment;
import com.careons.app.Patient.Fragments.HealthBook.MedicalProblemFragment;
import com.careons.app.Patient.Fragments.HealthBook.MedicationFragment;
import com.careons.app.Patient.Fragments.HealthBook.PregnancyFragment;
import com.careons.app.Patient.Fragments.HealthBook.SurgicalFragment;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;

public class HealthbookActivity extends AppCompatActivity {

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
    private static Toolbar toolbar;
    private static TabLayout tabLayout;
    private static HealthbookActivity mHealthbookActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthbook);
        mHealthbookActivity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //mSectionsPagerAdapter.r
            }

            @Override
            public void onPageSelected(int position) {
                setToolbarColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int selectedIndex = getIntent().getIntExtra(StringConstants.KEY_SELECTED_INDEX, 0);
        mViewPager.setCurrentItem(selectedIndex);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Fragment Manager for Delete FIX
    private static HealthbookActivity healthbookActivity;

    public HealthbookActivity() {
        healthbookActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_z_z, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */


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
            Fragment fragment = null;

            switch (position) {

                case 0:
                    fragment = MedicalProblemFragment.newInstance();
                    break;
                case 1:
                    fragment = AllergyFragment.newInstance();
                    break;
                case 2:
                    fragment = FamilyFragment.newInstance();
                    break;
                case 3:
                    fragment = MedicationFragment.newInstance();
                    break;
                case 4:
                    fragment = LifestyleFragment.newInstance();
                    break;
                case 5:
                    fragment = SurgicalFragment.newInstance();
                    break;
                case 6:
                    fragment = ChildhoodFragment.newInstance();
                    break;
                case 7:
                    fragment = PregnancyFragment.newInstance();
                    break;
                case 8:
                    fragment = GynaecologicalFragment.newInstance();
                    break;
                default:
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {

            /**
             * Initialize healthbook sections data based on Gender
             */
            if(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_GENDER).equalsIgnoreCase(getString(R.string.male))) {
                return 7;
            } else {
                return 9;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.medical_problem_title);
                case 1:
                    return getString(R.string.allergies_title);
                case 2:
                    return getString(R.string.family_history_title);
                case 3:
                    return getString(R.string.medication_title);
                case 4:
                    return getString(R.string.lifestyle_title);
                case 5:
                    return getString(R.string.surgical_history_title);
                case 6:
                    return getString(R.string.childhood_history_title);
                case 7:
                    return getString(R.string.pregnancy_title);
                case 8:
                    return getString(R.string.gynae_title);
            }
            return null;
        }
    }

    public static void setToolbarColor() {
        setToolbarColor(tabLayout.getSelectedTabPosition());
    }
    /**
     *Set Toolbar Color
     */

    public static void setToolbarColor(int position) {
        int colorCode = R.color.mp;
        int statusBarColorCode = R.color.darker_mp;
        switch (position) {
            case 0:
                colorCode = R.color.mp;
                statusBarColorCode = R.color.darker_mp;
                break;
            case 1:
                colorCode = R.color.al;
                statusBarColorCode = R.color.darker_al;
                break;
            case 2:
                colorCode = R.color.fh;
                statusBarColorCode = R.color.darker_fh;
                break;
            case 3:
                colorCode = R.color.mh;
                statusBarColorCode = R.color.darker_mh;
                break;
            case 4:
                colorCode = R.color.lf;
                statusBarColorCode = R.color.darker_lf;
                break;
            case 5:
                colorCode = R.color.sh;
                statusBarColorCode = R.color.darker_sh;
                break;
            case 6:
                colorCode = R.color.ch;
                statusBarColorCode = R.color.darker_ch;
                break;
            case 7:
                colorCode = R.color.preg;
                statusBarColorCode = R.color.darker_preg;
                break;
            case 8:
                colorCode = R.color.gh;
                statusBarColorCode = R.color.darker_gh;
                break;
            default:
                break;
        }
        // Set Tool Bar Color
        toolbar.setBackgroundColor(mHealthbookActivity.getResources().getColor(colorCode));

        // Set Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mHealthbookActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(mHealthbookActivity.getResources().getColor(statusBarColorCode));
        }
        if(tabLayout != null) {
            // Set Tablayout Color
            tabLayout.setBackgroundColor(mHealthbookActivity.getResources().getColor(colorCode));
        }
    }

}
