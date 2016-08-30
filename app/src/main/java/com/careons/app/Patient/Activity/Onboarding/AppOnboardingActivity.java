package com.careons.app.Patient.Activity.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.careons.app.Patient.Activity.Signup.LoginActivity;
import com.careons.app.Patient.Activity.Signup.SignUpActivity;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Shared.Components.GoogleOAuth.GetUsernameTask;
import com.careons.app.R;
import com.careons.app.Shared.Utils.Validation;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class AppOnboardingActivity extends AppCompatActivity  implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

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
    private static ViewPager mViewPager;

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "IdTokenActivity";
    private static final int RC_GET_TOKEN = 9002;

    //Facebook CallbackManager
    private CallbackManager callbackManager;

    private CirclePageIndicator circlePageIndicator;
    private Timer timer;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_app_onboarding);

        // Gmail Button click listeners
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);


        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.content_home_view_pager_container);
        //mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Bind the circle indicator to the adapter
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.content_home_view_pager_indicator);
        circlePageIndicator.setViewPager(mViewPager);

        // Modify view pager arrangements
        mViewPager.setClipChildren(false);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        //int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40 * 2, getResources().getDisplayMetrics());
        //mViewPager.setPageMargin(-margin);


        /**
         * Function to stop view pager timer on touch
         */
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                timer.cancel();
                return false;
            }
        });



        /**
         * Facebook Registration
         */
        callbackManager = CallbackManager.Factory.create();
        LoginButton mFacebookLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        mFacebookLoginButton.setReadPermissions(Arrays.asList("user_about_me", "email"));

        // Callback registration
        mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                LoginActivity.signinWithFB(AppOnboardingActivity.this, getApplicationContext(), loginResult.getAccessToken().getToken());

                // Logout from Facebook
                LoginManager.getInstance().logOut();

            }
            @Override
            public void onCancel() {

                Toast.makeText(getApplicationContext(), getString(R.string.err_cancel_auth), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {

                Toast.makeText(getApplicationContext(), getString(R.string.err_facebook_auth), Toast.LENGTH_LONG).show();
            }
        });


        /**
         * Gmail Registration
         */
        // For sample only: make sure there is a valid server client ID.
        validateServerClientID();

        // [START configure_signin]
        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildProperties.googleDeveloperId)
                .requestEmail()
                .build();
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /**
         * Call - Auto Onboarding Card Slider
         */
        pageSwitcher();
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


    /**
     * Function to navigate to signup page
     * @param view
     */
    public void signupRequest(View view) {

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

        // close this activity
        //finish();
    }


    /**
     * Function to navigate to login page
     * @param view
     */
    public void loginRequest(View view) {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        // close this activity
        //finish();
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
            // Show 4 total pages.
            return 4;
        }
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);
            RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.onboarding_holder);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.onboarding_banner);
            TextView textView = (TextView) rootView.findViewById(R.id.desc_textview);


            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {

                case 1:
                    relativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    imageView.setImageDrawable(
                            getResources().getDrawable(R.drawable.onboarding_banner_1));
                    textView.setVisibility(View.VISIBLE);
                    break;

                case 2:
                    relativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    imageView.setImageDrawable(
                            getResources().getDrawable(R.drawable.onboarding_banner_2));
                    textView.setVisibility(View.GONE);
                    break;

                case 3:
                    relativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    imageView.setImageDrawable(
                            getResources().getDrawable(R.drawable.onboarding_banner_3));
                    textView.setVisibility(View.GONE);
                    break;

                case 4:
                    relativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    imageView.setImageDrawable(
                            getResources().getDrawable(R.drawable.onboarding_banner_4));
                    textView.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }

            return rootView;
        }
    }


    /**
     * View Pager animation class
     */
    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }


    /**
     * View Pager animation class
     */
    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                if (Validation.isConnected(getApplicationContext())) {
                    getIdToken();
                } else {

                    // If internet connection is not available
                    Toast.makeText(getApplicationContext(), getString(R.string.err_internet_not_available), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }



    /**
     * Start Gmail Auth methods
     */
    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "signOut:onResult:" + status);
                        updateUI(false);
                    }
                });
    }


    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "revokeAccess:onResult:" + status);
                        updateUI(false);
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {

                GoogleSignInAccount account = result.getSignInAccount();
                String idToken = account.getIdToken();


                String scope =  "oauth2:https://www.googleapis.com/auth/userinfo.profile";

                /**
                 * Fetch Access Token
                 */
                new GetUsernameTask(AppOnboardingActivity.this, account.getEmail(), scope).execute();

                updateUI(true);
                // Sign out
                signOut();

            } else {

                // Show signed-out UI.
                updateUI(false);
            }

        } else {

            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    private void updateUI(boolean signedIn) {
        if (signedIn) {
            //Toast.makeText(this, "Sign In", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "Sign out", Toast.LENGTH_LONG).show();

        }
    }


    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {
        String serverClientId = BuildProperties.googleDeveloperId;
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }



    /**
     * Function to auto swipe cards
     */
    public void pageSwitcher() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), StaticConstants.APP_ONBOARDING_SCREEN_TIMEOUT, StaticConstants.APP_ONBOARDING_SCREEN_TIMEOUT);
    }


    // this is an inner class...
    class RemindTask extends TimerTask {
        private int page = 0;

        @Override
        public void run() {

            // As the TimerTask run on a separate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {
                    if (page > 3) {

                        page = 0;
                        mViewPager.setCurrentItem(page++);

                    } else {

                        mViewPager.setCurrentItem(page++);
                    }
                }
            });

        }
    }
}