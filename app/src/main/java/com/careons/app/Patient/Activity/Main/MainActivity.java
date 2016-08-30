package com.careons.app.Patient.Activity.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.careons.app.Patient.Activity.Onboarding.AppOnboardingActivity;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Fragments.About.AboutUsFragment;
import com.careons.app.Patient.Fragments.Documents.UDFragment;
import com.careons.app.Patient.Fragments.Home.HomeFragment;
import com.careons.app.Patient.Fragments.Home.PatientHomeFragment;
import com.careons.app.Patient.Fragments.Legal.LegalFragment;
import com.careons.app.Patient.Fragments.Profile.ProfileFragment;
import com.careons.app.Patient.Fragments.Share.ShareFragment;
import com.careons.app.Patient.Fragments.Vitals.VitalFragment;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.R;
import com.careons.app.Shared.Components.Crashlytics.LogUser;
import com.careons.app.Shared.Interfaces.FragmentInteractionListener;


public class MainActivity extends AppCompatActivity
                    implements NavigationView.OnNavigationItemSelectedListener,
                    FragmentInteractionListener {

    private static Activity activity;
    public static MainActivity mMainActivity;

    // Navigation Drawer Layout
    private DrawerLayout drawerLayout;
    private static TextView usernameTextView, emailTextView;

    // Navigation View
    private NavigationView navigationView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize activity
        activity = this;
        mMainActivity = this;

        /**
         * Log User Credentials to Crashlytics
         */
        LogUser.logUser(getApplicationContext());

        // Toolbar Initialize
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                               this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();


        // Initialize username and email in navigation drawer
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_profile);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotoProfile();
            }
        });

        usernameTextView = (TextView) headerLayout.findViewById(R.id.user_name);
        emailTextView = (TextView) headerLayout.findViewById(R.id.user_email);
        ImageView userImageView = (ImageView) headerLayout.findViewById(R.id.user_image);

        /**
         * Place Display Pic based on Gender
         */
        if(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_GENDER).equalsIgnoreCase(getString(R.string.male))) {
            userImageView.setImageDrawable(getResources().getDrawable(R.drawable.dp_man));
        } else {
            userImageView.setImageDrawable(getResources().getDrawable(R.drawable.dp_woman));
        }


        // Substitute values
        updateNavigationDrawerHeaderValues(getApplicationContext());

        // Set Homepage Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, new PatientHomeFragment()).commit();
    }


    @Override
    public void onBackPressed() {
        // Close Navigation Drawer if Open
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {

            FragmentManager manager = getSupportFragmentManager();

            if (manager.getBackStackEntryCount() > 0) {
                super.onBackPressed();

                Fragment currentFragment = manager.findFragmentById(R.id.main_content);

                if (currentFragment instanceof PatientHomeFragment) {

                    navigationView.getMenu().getItem(0).setChecked(true);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setChecked(false);

                } else if (currentFragment instanceof VitalFragment) {

                    navigationView.getMenu().getItem(1).setChecked(true);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setChecked(false);

                } else if (currentFragment instanceof UDFragment) {

                    navigationView.getMenu().getItem(2).setChecked(true);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setChecked(false);

                } else if (currentFragment instanceof ProfileFragment) {

                    navigationView.getMenu().getItem(3).setChecked(true);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setChecked(false);

                } else if (currentFragment instanceof ShareFragment) {

                    navigationView.getMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(2).setChecked(false);
                    navigationView.getMenu().getItem(3).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setChecked(true);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setChecked(false);

                } else if (currentFragment instanceof AboutUsFragment) {

                    navigationView.getMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(2).setChecked(false);
                    navigationView.getMenu().getItem(3).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setChecked(true);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setChecked(false);

                } else if (currentFragment instanceof LegalFragment) {

                    navigationView.getMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(2).setChecked(false);
                    navigationView.getMenu().getItem(3).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setChecked(true);
                }
            } else {

                super.onBackPressed();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass = null;

        menuItem.getItemId();
        switch (menuItem.getItemId()) {

            case R.id.nav_home:
                fragmentClass = PatientHomeFragment.class;
                break;

            case R.id.nav_vitals:
                fragmentClass = VitalFragment.class;
                break;

            case R.id.nav_ud:
                fragmentClass = UDFragment.class;
                break;

            case R.id.nav_profile:
                fragmentClass = ProfileFragment.class;
                break;

            case R.id.nav_share:
                fragmentClass = ShareFragment.class;
                break;

            case R.id.nav_about:
                fragmentClass = AboutUsFragment.class;
                break;

            case R.id.nav_legal:
                fragmentClass = LegalFragment.class;
                break;

            default: break;
        }


        // Setup fragment with fragment class initialized
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            // TODO: Handle exception
        }


        // Insert the fragment by replacing main content
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Manage Back stack
        if (fragment instanceof HomeFragment) {
            while (fragmentManager.getBackStackEntryCount() != 0) {
                fragmentManager.popBackStackImmediate();
            }
        } else {

            fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack(fragmentClass.getName()).commit();
        }

        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO: Implement Fragment Interaction
    }


    /**
     * Function to update username and email for navigation drawer header
     */
    public static void updateNavigationDrawerHeaderValues(Context context) {

        // Fetch and substitute values from Shared Preference Service
        usernameTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_USERNAME));
        emailTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_EMAIL));
    }


    /**
     * Function to navigate to vitals from home
     * @param view
     */
    public void gotoVitals(View view) {

        int index = 0;
        switch (view.getId()) {
            case (R.id.vitals_bp_card):
            case (R.id.bp_see_more):
            case (R.id.empty_vitals_bp_cta):
                index = 0;
                break;
            case (R.id.vitals_bg_card):
            case (R.id.bg_see_more):
            case (R.id.empty_vitals_bg_cta):
                index = 1;
                break;
            case (R.id.vitals_bmi_card):
            case (R.id.bmi_see_more):
            case (R.id.empty_vitals_bmi_cta):
                index = 2;
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(StringConstants.KEY_SELECTED_INDEX, index);
        Fragment fragment = null;
        Class fragmentClass = VitalFragment.class;
        // Setup fragment with fragment class initialized
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            // set Fragmentclass Arguments
            fragment.setArguments(bundle);
        } catch (Exception e) {
            // TODO: Handle exception
        }
        // Insert the fragment by replacing main content
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Manage Back stack
        if (fragment instanceof HomeFragment) {
            while (fragmentManager.getBackStackEntryCount() != 0) {
                fragmentManager.popBackStackImmediate();
            }
        } else {
            fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack(fragmentClass.getName()).commit();
        }
        /**
         * Highlight section in Navigation Drawer
         */
        navigationView.getMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(1).setChecked(true);
    }


    /**
     * Function to navigate to upload documents from home
     */
    public void gotoUploadDocuments() {

        Fragment fragment = null;
        Class fragmentClass = UDFragment.class;

        // Setup fragment with fragment class initialized
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            // TODO: Handle exception
        }

        // Insert the fragment by replacing main content
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Manage Back stack
        if (fragment instanceof HomeFragment) {
            while (fragmentManager.getBackStackEntryCount() != 0) {
                fragmentManager.popBackStackImmediate();
            }
        } else {
            fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack(fragmentClass.getName()).commit();
        }
        /**
         * Highlight section in Navigation Drawer
         */
        navigationView.getMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(2).setChecked(true);
    }


    /**
     * Function to navigate to Profile
     */
    public void gotoProfile(){
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = ProfileFragment.class;
        // Setup fragment with fragment class initialized
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (Exception e) {
            // TODO: Handle exception
        }

        // Insert the fragment by replacing main content
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack(fragmentClass.getName()).commit();

        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        navigationView.getMenu().getItem(3).setChecked(true);
        navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setChecked(false);
        navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setChecked(false);
    }



    /**
     * Function to logout from app
     */
    public void logoutRequest(View view) {

        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_logout, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        // Show dialog
        dialog.show();


        // Initialize Call to Actions
        CardView acceptCardView = (CardView) dialogView.findViewById(R.id.cta_accept);
        CardView cancelCardView = (CardView) dialogView.findViewById(R.id.cta_cancel);

        // Set on click listeners
        acceptCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dismiss Dialog
                dialog.dismiss();

                // Destroy User Session
                SharedPreferenceService.destroyUserSession(getApplicationContext());

                // App Onboarding Activity
                Intent intent = new Intent(getApplicationContext(), AppOnboardingActivity.class);
                startActivity(intent);

                // End this Activity
                finish();
            }
        });

        cancelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                //onBackPressed();
            }
        });
    }
}
