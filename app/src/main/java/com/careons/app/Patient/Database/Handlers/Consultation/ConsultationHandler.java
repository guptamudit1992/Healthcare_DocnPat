package com.careons.app.Patient.Database.Handlers.Consultation;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.careons.app.Patient.Activity.Consultation.ConsultationProblemActivity;
import com.careons.app.R;

import java.util.ArrayList;

public class ConsultationHandler {

    private static ArrayList<String> symptomList = new ArrayList<>();

    /**
     * Function to handle click event on symptoms
     * @param view
     */
    public void symptomSelectionToggle(View view) {

        TextView symptomsTextView = (TextView) view.findViewById(R.id.symptom_name);
        ImageView selectedImageView = (ImageView) view.findViewById(R.id.symptom_selected);
        ImageView unselectedImageView = (ImageView) view.findViewById(R.id.symptom_unselected);

        Boolean isSelected = Boolean.valueOf(symptomsTextView.getTag().toString());

        /**
         * Visual Change
         */
        if(!isSelected) {

            // Symptom Selected
            symptomsTextView.setTextColor(view.getResources().getColor(R.color.colorPrimary));
            symptomsTextView.setTag(true);
            selectedImageView.setVisibility(View.VISIBLE);
            unselectedImageView.setVisibility(View.GONE);
            symptomList.add(symptomsTextView.getText().toString());

        } else {

            // Symptom unSelected
            symptomsTextView.setTextColor(view.getResources().getColor(R.color.text_color));
            symptomsTextView.setTag(false);
            unselectedImageView.setVisibility(View.VISIBLE);
            selectedImageView.setVisibility(View.GONE);
            symptomList.remove(symptomsTextView.getText().toString());
        }

        /**
         * Populate Symptom List
         */
        ConsultationProblemActivity.selectSymptoms(symptomList);
    }

}
