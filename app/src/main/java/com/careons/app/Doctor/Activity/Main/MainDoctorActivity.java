package com.careons.app.Doctor.Activity.Main;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.careons.app.Doctor.Fragments.Main.DoctorAccountFragment;
import com.careons.app.Doctor.Fragments.Main.DoctorCosultationFragment;
import com.careons.app.Doctor.Fragments.Main.DoctorHomeFragment;
import com.careons.app.Doctor.Fragments.Main.DoctorNotificationFragment;
import com.careons.app.Doctor.Fragments.Main.DoctorProfileFragment;
import com.careons.app.Doctor.Fragments.Main.DoctorSettingFragment;
import com.careons.app.Patient.Fragments.About.AboutUsFragment;
import com.careons.app.Patient.Fragments.Home.HomeFragment;
import com.careons.app.Patient.Fragments.Legal.LegalFragment;
import com.careons.app.R;
import com.careons.app.Shared.Interfaces.FragmentInteractionListener;

public class MainDoctorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListener {

    private static Activity activity;
    public static MainDoctorActivity mMainActivity;

    // Navigation Drawer Layout
    private DrawerLayout drawerLayout;
    private static TextView usernameTextView, emailTextView;

    // Navigation View
    private NavigationView navigationView;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_doctor);

        // Initialize activity
        activity = this;
        mMainActivity = this;


        // Toolbar Initialize
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDoctor));
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
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main_doctor);

        usernameTextView = (TextView) headerLayout.findViewById(R.id.user_name);
        emailTextView = (TextView) headerLayout.findViewById(R.id.user_email);
        ImageView userImageView = (ImageView) headerLayout.findViewById(R.id.user_image);

        // Substitute values
        updateNavigationDrawerHeaderValues(getApplicationContext());
        userImageView.setImageDrawable(getResources().getDrawable(R.drawable.param_dp));

        // Set Homepage Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, new DoctorHomeFragment()).commit();
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

                if (currentFragment instanceof DoctorHomeFragment) {

                    navigationView.getMenu().getItem(0).setChecked(true);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(1).setChecked(false);

                } else if (currentFragment instanceof DoctorNotificationFragment) {

                    navigationView.getMenu().getItem(1).setChecked(true);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(1).setChecked(false);

                } else if (currentFragment instanceof DoctorCosultationFragment) {

                    navigationView.getMenu().getItem(2).setChecked(true);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(1).setChecked(false);

                } else if (currentFragment instanceof DoctorAccountFragment) {

                    navigationView.getMenu().getItem(3).setChecked(true);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(1).setChecked(false);

                } else if (currentFragment instanceof DoctorProfileFragment) {

                    navigationView.getMenu().getItem(4).setChecked(true);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(1).setChecked(false);

                } else if (currentFragment instanceof DoctorSettingFragment) {

                    navigationView.getMenu().getItem(5).setChecked(true);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(1).setChecked(false);

                } else if (currentFragment instanceof AboutUsFragment) {

                    navigationView.getMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(2).setChecked(false);
                    navigationView.getMenu().getItem(3).setChecked(false);
                    navigationView.getMenu().getItem(4).setChecked(false);
                    navigationView.getMenu().getItem(5).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(1).setChecked(true);

                } else if (currentFragment instanceof LegalFragment) {

                    navigationView.getMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(1).setChecked(false);
                    navigationView.getMenu().getItem(2).setChecked(false);
                    navigationView.getMenu().getItem(3).setChecked(false);
                    navigationView.getMenu().getItem(4).setChecked(false);
                    navigationView.getMenu().getItem(5).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setChecked(false);
                    navigationView.getMenu().getItem(6).getSubMenu().getItem(1).setChecked(false);
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
                fragmentClass = DoctorHomeFragment.class;
                break;

            case R.id.nav_notification:
                fragmentClass = DoctorNotificationFragment.class;
                break;

            case R.id.nav_ch:
                fragmentClass = DoctorHomeFragment.class;
                break;

            case R.id.nav_as:
                fragmentClass = DoctorAccountFragment.class;
                break;

            case R.id.nav_profile:
                fragmentClass = DoctorProfileFragment.class;
                break;

            case R.id.nav_settings:
                fragmentClass = DoctorSettingFragment.class;
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

            fragmentManager.beginTransaction().replace(R.id.main_content, fragment)
                    .addToBackStack(fragmentClass.getName()).commit();
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
        usernameTextView.setText("Dr. Paramjeet Singh");
        emailTextView.setText("param@careons.com");
    }
}
