package com.careons.app.Patient.Fragments.Documents;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.careons.app.Patient.Activity.Main.UploadedDocuments.GalleryActivity;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Patient.Database.Models.Upload.Album;
import com.careons.app.Shared.Interfaces.FragmentInteractionListener;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;

import java.util.List;

public class UDFragment extends Fragment implements View.OnClickListener {

    private FragmentInteractionListener mListener;
    private static TextView billCountTextView, medicineCountTextView, labCountTextView, prescriptionsCountTextView, othersCountTextView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_uploaded_doc, container, false);

        // Category click listeners
        CardView cardBills = (CardView) rootView.findViewById(R.id.bills);
        cardBills.setOnClickListener(this);
        CardView cardMedicines = (CardView) rootView.findViewById(R.id.medicines);
        cardMedicines.setOnClickListener(this);
        CardView cardLabReports = (CardView) rootView.findViewById(R.id.lab_reports);
        cardLabReports.setOnClickListener(this);
        CardView cardPrescriptions = (CardView) rootView.findViewById(R.id.prescriptions);
        cardPrescriptions.setOnClickListener(this);
        CardView cardOthers = (CardView) rootView.findViewById(R.id.others);
        cardOthers.setOnClickListener(this);


        /**
         * Initialize count placeholders
         */
        billCountTextView = (TextView) rootView.findViewById(R.id.bill_count);
        medicineCountTextView = (TextView) rootView.findViewById(R.id.medicine_count);
        labCountTextView = (TextView) rootView.findViewById(R.id.lab_count);
        prescriptionsCountTextView = (TextView) rootView.findViewById(R.id.prescriptions_count);
        othersCountTextView = (TextView) rootView.findViewById(R.id.others_count);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Set action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.navigation_drawer_ud);

        /**
         * Set album count
         */
        updateAlbumCount();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Function to create new instance
     * @return
     */
    public static UDFragment newInstance() {
        return new UDFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            mListener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {

            case R.id.bills:
                intent = new Intent(getActivity(), GalleryActivity.class);
                intent.putExtra(StringConstants.KEY_CATEGORY, StringConstants.KEY_BILLS);
                break;

            case R.id.medicines:
                intent = new Intent(getActivity(), GalleryActivity.class);
                intent.putExtra(StringConstants.KEY_CATEGORY, StringConstants.KEY_MEDICINE);
                break;

            case R.id.lab_reports:
                intent = new Intent(getActivity(), GalleryActivity.class);
                intent.putExtra(StringConstants.KEY_CATEGORY, StringConstants.KEY_LAB_REPORTS);
                break;

            case R.id.prescriptions:
                intent = new Intent(getActivity(), GalleryActivity.class);
                intent.putExtra(StringConstants.KEY_CATEGORY, StringConstants.KEY_PRESCRIPTIONS);
                break;

            case R.id.others:
                intent = new Intent(getActivity(), GalleryActivity.class);
                intent.putExtra(StringConstants.KEY_CATEGORY, StringConstants.KEY_OTHERS);
                break;

            default:
                break;
        }

        startActivity(intent);
    }


    /**
     * Function to populate count in albums
     */
    public void updateAlbumCount() {

        /**
         * Bills
         */
        List<Album> billAlbums = Album.find(Album.class, "patient_id = ? and tag = ?",
                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID),
                StringConstants.KEY_BILLS);

        if(billAlbums.size() > 0) {
            billCountTextView.setText(String.valueOf(billAlbums.size()));
        } else {
            billCountTextView.setVisibility(View.GONE);
        }


        /**
         * Medicines
         */
        List<Album> medicineAlbums = Album.find(Album.class, "patient_id = ? and tag = ?",
                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID),
                StringConstants.KEY_MEDICINE);

        if(medicineAlbums.size() > 0) {
            medicineCountTextView.setText(String.valueOf(medicineAlbums.size()));
        } else {
            medicineCountTextView.setVisibility(View.GONE);
        }


        /**
         * Lab Reports
         */
        List<Album> labAlbums = Album.find(Album.class, "patient_id = ? and tag = ?",
                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID),
                StringConstants.KEY_LAB_REPORTS);

        if(labAlbums.size() > 0) {
            labCountTextView.setText(String.valueOf(labAlbums.size()));
        } else {
            labCountTextView.setVisibility(View.GONE);
        }


        /**
         * Prescriptions
         */
        List<Album> prescriptionsAlbums = Album.find(Album.class, "patient_id = ? and tag = ?",
                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID),
                StringConstants.KEY_PRESCRIPTIONS);

        if(prescriptionsAlbums.size() > 0) {
            prescriptionsCountTextView.setText(String.valueOf(prescriptionsAlbums.size()));
        } else {
            prescriptionsCountTextView.setVisibility(View.GONE);
        }


        /**
         * Others
         */
        List<Album> othersAlbums = Album.find(Album.class, "patient_id = ? and tag = ?",
                SharedPreferenceService.getValue(getContext(), StringConstants.KEY_PATIENT_ID),
                StringConstants.KEY_OTHERS);

        if(othersAlbums.size() > 0) {
            othersCountTextView.setText(String.valueOf(othersAlbums.size()));
        } else {
            othersCountTextView.setVisibility(View.GONE);
        }
    }
}

















