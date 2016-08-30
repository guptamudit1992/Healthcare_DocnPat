package com.careons.app.Patient.Activity.Onboarding;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.careons.app.Patient.Activity.Main.MainActivity;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.FamilyHistory;
import com.careons.app.Patient.Database.Models.Healthbook.Gynaecology;
import com.careons.app.Patient.Database.Models.Healthbook.Lifestyle;
import com.careons.app.Patient.Database.Models.Healthbook.MedicalProblem;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;

import java.util.List;

public class OnboardingListActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_list);

        // Check onboarding progress status
        checkCardStatus();

        // Set on click listener on cards
        findViewById(R.id.onb_mh).setOnClickListener(this);
        findViewById(R.id.onb_sh).setOnClickListener(this);
        findViewById(R.id.onb_ph).setOnClickListener(this);
        findViewById(R.id.onb_al).setOnClickListener(this);
        findViewById(R.id.onb_preg).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

        // Exit Confirmation Dialog
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.quit))
                .setMessage(getString(R.string.quit_message))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    @Override
    public void onClick(View v) {

        ImageView imageView;

        switch (v.getId()) {

            case R.id.onb_mp:
                imageView = (ImageView) findViewById(R.id.onb_mp_selector);
                if(imageView.getTag().equals("")) {
                    imageView.setTag("true");
                    imageView.setImageResource(R.drawable.correct);

                } else {

                    imageView.setTag("");
                    imageView.setImageResource(0);
                }
                break;

            case R.id.onb_fh:
                imageView = (ImageView) findViewById(R.id.onb_fh_selector);
                if(imageView.getTag().equals("")) {
                    imageView.setTag("true");
                    imageView.setImageResource(R.drawable.correct);

                } else {

                    imageView.setTag("");
                    imageView.setImageResource(0);
                }
                break;

            case R.id.onb_lf:
                imageView = (ImageView) findViewById(R.id.onb_lf_selector);
                if(imageView.getTag().equals("")) {
                    imageView.setTag("true");
                    imageView.setImageResource(R.drawable.correct);

                } else {

                    imageView.setTag("");
                    imageView.setImageResource(0);
                }
                break;

            case R.id.onb_gh:
                imageView = (ImageView) findViewById(R.id.onb_gh_selector);
                if(imageView.getTag().equals("")) {
                    imageView.setTag("true");
                    imageView.setImageResource(R.drawable.correct);

                } else {

                    imageView.setTag("");
                    imageView.setImageResource(0);
                }
                break;


            case R.id.onb_mh:
                imageView = (ImageView) findViewById(R.id.onb_mh_selector);
                if(imageView.getTag().equals("")) {
                    imageView.setTag("true");
                    imageView.setImageResource(R.drawable.correct);

                } else {

                    imageView.setTag("");
                    imageView.setImageResource(0);
                }
                break;

            case R.id.onb_sh:
                imageView = (ImageView) findViewById(R.id.onb_sh_selector);
                if(imageView.getTag().equals("")) {
                    imageView.setTag("true");
                    imageView.setImageResource(R.drawable.correct);

                } else {

                    imageView.setTag("");
                    imageView.setImageResource(0);
                }
                break;

            case R.id.onb_al:
                imageView = (ImageView) findViewById(R.id.onb_al_selector);
                if(imageView.getTag().equals("")) {
                    imageView.setTag("true");
                    imageView.setImageResource(R.drawable.correct);

                } else {

                    imageView.setTag("");
                    imageView.setImageResource(0);
                }
                break;

            case R.id.onb_ph:
                imageView = (ImageView) findViewById(R.id.onb_ph_selector);
                if(imageView.getTag().equals("")) {
                    imageView.setTag("true");
                    imageView.setImageResource(R.drawable.correct);

                } else {

                    imageView.setTag("");
                    imageView.setImageResource(0);
                }
                break;

            case R.id.onb_preg:
                imageView = (ImageView) findViewById(R.id.onb_preg_selector);
                if(imageView.getTag().equals("")) {
                    imageView.setTag("true");
                    imageView.setImageResource(R.drawable.correct);

                } else {

                    imageView.setTag("");
                    imageView.setImageResource(0);
                }
                break;

            default:
                break;
        }
    }


    /**
     * Function to check progress status
     */
    public void checkCardStatus() {

        /**
         * Check Medical Problem Onboarding
         */
        // Check if user has entered any problem
        List<MedicalProblem> medicalProblemList = MedicalProblem.listAll(MedicalProblem.class);
        if(medicalProblemList.size() > 0) {

            ImageView imageView = (ImageView) findViewById(R.id.onb_mp_selector);
            imageView.setTag("true");
            imageView.setImageResource(R.drawable.correct);

        } else {

            findViewById(R.id.onb_mp).setOnClickListener(this);
        }

        /**
         * Check Family Problem Onboarding
         */
        // Check if user has entered any problem
        List<FamilyHistory> familyHistoryList = FamilyHistory.listAll(FamilyHistory.class);
        if(familyHistoryList.size() > 0) {

            ImageView imageView = (ImageView) findViewById(R.id.onb_fh_selector);
            imageView.setTag("true");
            imageView.setImageResource(R.drawable.correct);

        } else {

            findViewById(R.id.onb_fh).setOnClickListener(this);
        }

        /**
         * Check Lifestyle Problem Onboarding
         */
        // Check if user has entered any problem
        List<Lifestyle> lifestyleList = Lifestyle.listAll(Lifestyle.class);
        if(lifestyleList.size() > 0) {

            ImageView imageView = (ImageView) findViewById(R.id.onb_lf_selector);
            imageView.setTag("true");
            imageView.setImageResource(R.drawable.correct);

        } else {

            findViewById(R.id.onb_lf).setOnClickListener(this);
        }


        /**
         * Check Gynaecology Problem Onboarding
         */
        // Check if user has entered any problem
        List<Gynaecology> gynaecologyList = Gynaecology.listAll(Gynaecology.class);
        if(gynaecologyList.size() > 0) {

            ImageView imageView = (ImageView) findViewById(R.id.onb_gh_selector);
            imageView.setTag("true");
            imageView.setImageResource(R.drawable.correct);

        } else {

            findViewById(R.id.onb_gh).setOnClickListener(this);
        }
    }


    /**
     * Function to navigate to homepage
     * @param view
     */
    public void gotoHomepage(View view) {

        // Update Shared Preference Memory flag
        SharedPreferenceService.storeUserDetails(getApplicationContext(),
                StringConstants.KEY_IS_ONBOARDING_COMPLETE, "true");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        // close this activity
        finish();
    }
}
