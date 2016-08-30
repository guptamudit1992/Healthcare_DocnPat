package com.careons.app.Patient.Database.Handlers.HealthSearch;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.careons.app.Patient.Activity.Main.HealthSearch.SearchActivity;
import com.careons.app.R;

public class HealthEducationHandler {

    /**
     * Function to handle click event on health search list
     * @param view
     */
    public void showSearchDetails(View view) {

        final Context context = view.getContext();

        // Extract value from health search card
        TextView hsProblemName = (TextView) view.findViewById(R.id.problem_name);
        String problemName = hsProblemName.getText().toString();

        // Extract hyperlink from health search card
        TextView hsHyperLink = (TextView) view.findViewById(R.id.problem_hyperlink);
        String hyperlink = hsHyperLink.getText().toString();

        // Load detail via web view
        SearchActivity.goToProblemDetail(context, problemName, hyperlink);
    }
}
