package com.careons.app.Patient.Fragments.Home.Main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Vitals.BMI;
import com.careons.app.Patient.Database.Models.Vitals.BloodGlucose;
import com.careons.app.Patient.Database.Models.Vitals.BloodPressure;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.databinding.FragmentVitalInformationBinding;

import java.util.Collections;
import java.util.List;

public class VitalInformationFragment extends Fragment {

    private static View rootView;
    private static TextView emptyVitalsCta;
    private static LinearLayout loader, emptyVitals, vitalsSummary;

    private static CardView bpCardView;
    private static LinearLayout emptyBPVitals;
    private static TextView emptyBPVitalsCta, bPSeeMore;

    private static CardView bgCardView;
    private static LinearLayout emptyBGVitals;
    private static TextView emptyBGVitalsCta, updateBG, bGSeeMore;

    private static CardView bmiCardView;
    private static LinearLayout emptyBMIVitals;
    private static TextView emptyBMIVitalsCta, bMISeeMore;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_vital_information, container, false);

        // Initialize ui components
        loader = (LinearLayout) rootView.findViewById(R.id.loader);
        emptyVitals = (LinearLayout) rootView.findViewById(R.id.vitals_empty);
        vitalsSummary = (LinearLayout) rootView.findViewById(R.id.vitals_summary);

        // Initialize and bind empty vitals text
        emptyVitalsCta = (TextView) rootView.findViewById(R.id.empty_vitals_cta);


        // Blood Pressure Section
        bpCardView = (CardView) rootView.findViewById(R.id.vitals_bp_card);
        emptyBPVitals = (LinearLayout) rootView.findViewById(R.id.vitals_bp_empty);
        bPSeeMore = (TextView) rootView.findViewById(R.id.bp_see_more);
        emptyBPVitalsCta = (TextView) rootView.findViewById(R.id.empty_vitals_bp_cta);

        // Blood Glucose Section
        bgCardView = (CardView) rootView.findViewById(R.id.vitals_bg_card);
        emptyBGVitals = (LinearLayout) rootView.findViewById(R.id.vitals_bg_empty);
        updateBG = (TextView) rootView.findViewById(R.id.update_blood_glucose_edittext);
        bGSeeMore = (TextView) rootView.findViewById(R.id.bg_see_more);
        emptyBGVitalsCta = (TextView) rootView.findViewById(R.id.empty_vitals_bg_cta);


        // BMI Section
        bmiCardView = (CardView) rootView.findViewById(R.id.vitals_bmi_card);
        emptyBMIVitals = (LinearLayout) rootView.findViewById(R.id.vitals_bmi_empty);
        bMISeeMore = (TextView) rootView.findViewById(R.id.bmi_see_more);
        emptyBMIVitalsCta = (TextView) rootView.findViewById(R.id.empty_vitals_bmi_cta);



        // Fetch vitals details
        fetchVitalSummary(getContext());

        return rootView;
    }


    /**
     * Function to create new instance
     * @return
     */
    public static VitalInformationFragment newInstance() {
        return new VitalInformationFragment();
    }




    /**
     * Function to fetch vitals details
     * @param context
     */
    public static void fetchVitalSummary(Context context) {

        boolean hasBPContentFlag = true;
        boolean hasBGContentFlag = true;
        boolean hasBMIContentFlag = true;

        /**
         * Blood Pressure
         */
        // Read from database table
        List<BloodPressure> bloodPressureList =
                BloodPressure.find(BloodPressure.class, "patient_id = ?",
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        if(bloodPressureList.size() > 0) {

            // Sort list before binding
            Collections.sort(bloodPressureList);

            // Substitute values in placeholder
            FragmentVitalInformationBinding fragmentVitalInformationBinding = DataBindingUtil.bind(rootView);
            fragmentVitalInformationBinding.setDataBP(bloodPressureList.get(0));
            bpCardView.setVisibility(View.VISIBLE);

        } else {
            //  set content flag to false
            hasBPContentFlag = false;
            emptyBPVitals.setVisibility(View.VISIBLE);
        }


        /**
         * Blood Glucose
         */
        // Read from database table
        List<BloodGlucose> bloodGlucoseList =
                BloodGlucose.find(BloodGlucose.class, "patient_id = ?",
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        if(bloodGlucoseList.size() > 0) {

            // Sort list before binding
            Collections.sort(bloodGlucoseList);

            // Substitute values in placeholder
            FragmentVitalInformationBinding fragmentVitalInformationBinding = DataBindingUtil.bind(rootView);
            fragmentVitalInformationBinding.setDataBG(bloodGlucoseList.get(0));
            bgCardView.setVisibility(View.VISIBLE);

        } else {
            //  set content flag to false
            hasBGContentFlag = false;
            emptyBGVitals.setVisibility(View.VISIBLE);
        }



        /**
         * BMI
         */
        // Read from database table
        List<BMI> bmiList =
                BMI.find(BMI.class, "patient_id = ?",
                        SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

        if(bmiList.size() > 0) {

            // Sort list before binding
            Collections.sort(bmiList);

            // Substitute values in placeholder
            FragmentVitalInformationBinding fragmentVitalInformationBinding = DataBindingUtil.bind(rootView);
            fragmentVitalInformationBinding.setDataBMI(bmiList.get(0));
            bmiCardView.setVisibility(View.VISIBLE);

        } else {
            //  set content flag to false
            hasBMIContentFlag = false;
            emptyBMIVitals.setVisibility(View.VISIBLE);
        }









        // Switch states according to availability flag
        if(hasBPContentFlag || hasBGContentFlag || hasBMIContentFlag) {

            loader.setVisibility(View.GONE);
            vitalsSummary.setVisibility(View.VISIBLE);

        } else {

            loader.setVisibility(View.GONE);
            emptyVitals.setVisibility(View.VISIBLE);
        }
    }
}
