package com.careons.app.Patient.Fragments.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.careons.app.Patient.Activity.Consultation.ConsultationHistoryActivity;
import com.careons.app.Patient.Activity.Consultation.ConsultationPersonActivity;
import com.careons.app.Patient.Activity.Main.EHRActivity;
import com.careons.app.Patient.Activity.Main.EducationActivity;
import com.careons.app.R;

public class PatientHomeFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private LinearLayout instantChat, consulationHistory, healthRecords, healthEducation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_patient_home, container, false);

        // Initialize layouts
        instantChat = (LinearLayout) rootView.findViewById(R.id.instant_chat);
        consulationHistory = (LinearLayout) rootView.findViewById(R.id.consultation_history);
        healthRecords = (LinearLayout) rootView.findViewById(R.id.health_records);
        healthEducation = (LinearLayout) rootView.findViewById(R.id.health_education);

        // Set onClick listener events
        instantChat.setOnClickListener(this);
        consulationHistory.setOnClickListener(this);
        healthRecords.setOnClickListener(this);
        healthEducation.setOnClickListener(this);



        /**
         * Disable scroll bar
         */
        rootView.findViewById(R.id.home_scrollview).setVerticalScrollBarEnabled(false);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Set action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.navigation_drawer_home);
    }


    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {

            case R.id.instant_chat:
                intent = new Intent(getActivity(), ConsultationPersonActivity.class);
                startActivity(intent);
                break;

            case R.id.consultation_history:
                intent = new Intent(getActivity(), ConsultationHistoryActivity.class);
                startActivity(intent);
                break;

            case R.id.health_records:
                intent = new Intent(getActivity(), EHRActivity.class);
                startActivity(intent);
                break;

            case R.id.health_education:
                intent = new Intent(getActivity(), EducationActivity.class);
                startActivity(intent);
                break;
        }
    }
}