package com.careons.app.Doctor.Activity.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.careons.app.Doctor.Activity.Main.MainDoctorActivity;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.R;
import com.careons.app.Shared.Utils.Utils;

public class DoctorSplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_splash);

        // Expand full screen
        Utils.setFullScreen(this);

        // Animate splash screen logo
        ImageView imageView = (ImageView) findViewById(R.id.cm_splash_logo);
        Animation animation= AnimationUtils.loadAnimation(DoctorSplashActivity.this, R.anim.fade_out);
        imageView.startAnimation(animation);


        // Splash activity handler
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(DoctorSplashActivity.this, MainDoctorActivity.class);
                startActivity(intent);

                // close this activity
                finish();
            }
        }, StaticConstants.SPLASH_SCREEN_TIMEOUT);
    }

}
