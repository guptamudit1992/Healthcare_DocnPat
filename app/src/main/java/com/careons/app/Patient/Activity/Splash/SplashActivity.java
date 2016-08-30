package com.careons.app.Patient.Activity.Splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.careons.app.Patient.Activity.Main.MainActivity;
import com.careons.app.Patient.Activity.Onboarding.AppOnboardingActivity;
import com.careons.app.Patient.Activity.Signup.CreateAccountActivity;
import com.careons.app.Patient.Activity.Signup.WelcomeActivity;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.R;
import com.careons.app.Shared.Utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity {

    private final Handler handler = new Handler();

    // Mixpanel variable
    private String projectToken = BuildProperties.mixpanelProjectToken;
    private MixpanelAPI mixpanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Expand full screen
        Utils.setFullScreen(this);

        // Animate splash screen logo
        ImageView imageView = (ImageView) findViewById(R.id.cm_splash_logo);
        Animation animation= AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_out);
        imageView.startAnimation(animation);

        // Initiate Fabric Crashlytics
        Fabric.with(this, new Crashlytics());

        // Splash activity handler
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // This method will be executed once the timer is over
                Intent intent;

                if((SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_TOKEN) != null)
                        && Boolean.valueOf(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_IS_ACCOUNT_CREATED))
                        && Boolean.valueOf(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_IS_ONBOARDING_COMPLETE))) {

                    // Token present -> Homepage (Main Activity)
                    intent = new Intent(SplashActivity.this, MainActivity.class);

                } else if(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_TOKEN) != null
                                && Boolean.valueOf(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_IS_ACCOUNT_CREATED))
                                && !Boolean.valueOf(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_IS_ONBOARDING_COMPLETE))) {


                    // Account Created -> True
                    intent = new Intent(SplashActivity.this, WelcomeActivity.class);

                } else if((SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_TOKEN) != null)
                            && !Boolean.valueOf(SharedPreferenceService.getValue(getApplicationContext(), StringConstants.KEY_IS_ACCOUNT_CREATED))) {

                    // Account not created -> Create Account
                    intent = new Intent(SplashActivity.this, CreateAccountActivity.class);

                } else {

                    // Token not present
                    intent = new Intent(SplashActivity.this, AppOnboardingActivity.class);
                }

                // Start Activity
                startActivity(intent);

                // close this activity
                finish();

            }
        }, StaticConstants.SPLASH_SCREEN_TIMEOUT);



        // Mixpanel Initialization
        mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
        /*try {

            JSONObject props = new JSONObject();
            props.put("Gender", "Female");
            props.put("Logged in", false);
            mixpanel.track("MainActivity - onCreate called", props);

        } catch (JSONException e) {

            Crashlytics.logException(e);
            Log.e("Caring Millions", "Unable to add properties to JSONObject", e);
        }*/
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mixpanel.flush();
    }
}
