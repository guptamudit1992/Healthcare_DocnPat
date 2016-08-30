package com.careons.app.Patient.Fragments.Home.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.careons.app.Patient.Activity.Main.HealthBook.HealthbookActivity;
import com.careons.app.Patient.Activity.Main.HealthSearch.SearchActivity;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Healthbook.Allergy;
import com.careons.app.Patient.Database.Models.Healthbook.ChildhoodHistory;
import com.careons.app.Patient.Database.Models.Healthbook.FamilyHistory;
import com.careons.app.Patient.Database.Models.Healthbook.Gynaecology;
import com.careons.app.Patient.Database.Models.Healthbook.MedicalProblem;
import com.careons.app.Patient.Database.Models.Healthbook.Medication;
import com.careons.app.Patient.Database.Models.Healthbook.SurgicalHistory;
import com.careons.app.Patient.Models.HealthSearch.HealthEducation;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.databinding.CardHsBinding;

import java.util.ArrayList;
import java.util.List;

public class HealthEducationFragment extends Fragment
        implements View.OnClickListener {

    private View rootView;
    private RecyclerView hsProblemListRecyclerView;
    private LinearLayout loader, emptyRecyclerView, dataRecycelerView;
    private TextView emptyHSCta;
    private EditText searchBarContainer;

    private static Adapter<HealthEducation, CardHsBinding> healthSearchCardAdapter;
    private static ArrayList<HealthEducation> healthEducations = new ArrayList<>();

    public static HealthEducationFragment newInstance() {
        return new HealthEducationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_health_education, container, false);

        loader = (LinearLayout) rootView.findViewById(R.id.loader);
        emptyRecyclerView = (LinearLayout) rootView.findViewById(R.id.hs_problems_empty);
        dataRecycelerView = (LinearLayout) rootView.findViewById(R.id.hs_problems);
        emptyHSCta = (TextView) rootView.findViewById(R.id.empty_hs_cta);
        emptyHSCta.setOnClickListener(this);
        searchBarContainer = (EditText) rootView.findViewById(R.id.search_bar_field);
        searchBarContainer.setFocusable(false);
        searchBarContainer.setOnClickListener(this);
        hsProblemListRecyclerView = (RecyclerView) rootView.findViewById(R.id.hs_recycler_view);

        // Fetch problems, medications and allergies
        readHeathSearchList(getContext());

        return rootView;
    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.empty_hs_cta:
                intent = new Intent(getContext(), HealthbookActivity.class);
                intent.putExtra(StringConstants.KEY_SELECTED_INDEX, 0);
                startActivity(intent);
                break;


            case R.id.search_bar_field:
                intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }








    /**
     * Function to fetch health search cards
     * @param context
     */
    public void readHeathSearchList(final Context context) {

        // List of Id
        List<String> idList = new ArrayList<>();

        // Empty list
        healthEducations.clear();


        /**
         * Read from database table
         */
        List<MedicalProblem> medicalProblemList = MedicalProblem.find(MedicalProblem.class, "patient_id = ?",
                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        List<Allergy> allergyList = Allergy.find(Allergy.class, "patient_id = ?",
                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        List<FamilyHistory> familyHistoryList = FamilyHistory.find(FamilyHistory.class, "patient_id = ?",
                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        List<Medication> medicationList = Medication.find(Medication.class, "patient_id = ?",
                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        List<SurgicalHistory> surgicalHistoryList = SurgicalHistory.find(SurgicalHistory.class, "patient_id = ?",
                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        List<ChildhoodHistory> childhoodHistoryList = ChildhoodHistory.find(ChildhoodHistory.class, "patient_id = ?",
                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
        List<Gynaecology> gynaecologyList = Gynaecology.find(Gynaecology.class, "patient_id = ?",
                SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));


        /**
         * Medical Problem
         */
        for(int i = 0; i < medicalProblemList.size(); i++) {

            HealthEducation healthEducation = new HealthEducation(
                    String.valueOf(medicalProblemList.get(i).getPId()),
                    medicalProblemList.get(i).getProblemName(),
                    getString(R.string.medical_problems),
                    medicalProblemList.get(i).getHyperlink());

            idList.add(medicalProblemList.get(i).getProblemId());
            healthEducations.add(healthEducation);
        }

        /**
         * Allergy
         */
        for(int i = 0; i < allergyList.size(); i++) {

            HealthEducation healthEducation = new HealthEducation(
                    String.valueOf(allergyList.get(i).getAllergyId()),
                    allergyList.get(i).getAllergyName(),
                    getString(R.string.allergy),
                    allergyList.get(i).getHyperlink());

            healthEducations.add(healthEducation);
        }

        /**
         * Family History
         */
        for(int i = 0; i < familyHistoryList.size(); i++) {

            if(!(idList.indexOf(familyHistoryList.get(i).getProblemId()) > -1)) {
                HealthEducation healthEducation = new HealthEducation(
                        String.valueOf(familyHistoryList.get(i).getpId()),
                        familyHistoryList.get(i).getProblemName(),
                        getString(R.string.family_history),
                        familyHistoryList.get(i).getHyperlink());

                idList.add(familyHistoryList.get(i).getProblemId());
                healthEducations.add(healthEducation);
            }
        }

        /**
         * Medication
         */
        for(int i = 0; i < medicationList.size(); i++) {

            HealthEducation healthEducation = new HealthEducation(
                    String.valueOf(medicationList.get(i).getpId()),
                    medicationList.get(i).getMedicationName(),
                    getString(R.string.medication),
                    medicationList.get(i).getHyperlink());

            healthEducations.add(healthEducation);
        }

        /**
         * Surgical History
         */
        for(int i = 0; i < surgicalHistoryList.size(); i++) {

            HealthEducation healthEducation = new HealthEducation(
                    String.valueOf(surgicalHistoryList.get(i).getpId()),
                    surgicalHistoryList.get(i).getProblemName(),
                    getString(R.string.surgical_history),
                    surgicalHistoryList.get(i).getHyperlink());

            healthEducations.add(healthEducation);
        }


        /**
         * Childhood History
         */
        for(int i = 0; i < childhoodHistoryList.size(); i++) {

            if(!(idList.indexOf(childhoodHistoryList.get(i).getProblemId()) > -1)) {
                HealthEducation healthEducation = new HealthEducation(
                        String.valueOf(childhoodHistoryList.get(i).getpId()),
                        childhoodHistoryList.get(i).getProblemName(),
                        getString(R.string.paediatric_history),
                        childhoodHistoryList.get(i).getHyperlink());

                idList.add(childhoodHistoryList.get(i).getProblemId());
                healthEducations.add(healthEducation);
            }
        }


        /**
         * Check Gender in case of Gynaecology
         */
        if(SharedPreferenceService.getValue(getContext(), StringConstants.KEY_GENDER).equalsIgnoreCase(getString(R.string.female))) {

            /**
             * Gynaecology Problem
             */
            for(int i = 0; i < gynaecologyList.size(); i++) {
                if(!(idList.indexOf(gynaecologyList.get(i).getProblemId()) > -1)) {
                    HealthEducation healthEducation = new HealthEducation(
                            String.valueOf(gynaecologyList.get(i).getpId()),
                            gynaecologyList.get(i).getProblemName(),
                            getString(R.string.gynae),
                            gynaecologyList.get(i).getHyperlink());

                    idList.add(gynaecologyList.get(i).getProblemId());
                    healthEducations.add(healthEducation);
                }
            }
        }


        /**
         * Check if database contains healthbook entries
         */
        if(healthEducations.size() > 0) {

            // Set adapter
            healthSearchCardAdapter =
                    new Adapter<>(healthEducations, R.layout.card_hs,
                            StaticConstants.HEALTH_SEARCH_ADAPTER);

            hsProblemListRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            hsProblemListRecyclerView.setAdapter(healthSearchCardAdapter);

            // Show entries
            loader.setVisibility(View.GONE);
            dataRecycelerView.setVisibility(View.VISIBLE);

        } else {

            // In case of no entries
            loader.setVisibility(View.GONE);
            emptyRecyclerView.setVisibility(View.VISIBLE);
        }
    }

}
