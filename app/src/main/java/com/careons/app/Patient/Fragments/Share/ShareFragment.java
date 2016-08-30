package com.careons.app.Patient.Fragments.Share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Shared.Interfaces.FragmentInteractionListener;
import com.careons.app.R;

public class ShareFragment extends Fragment
                implements View.OnClickListener{

    private FragmentInteractionListener mListener;

    public ShareFragment() {
        // Required empty public constructor
    }


    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_share, container, false);

        // Set on click listener on Textview
        TextView shareTextView = (TextView) rootview.findViewById(R.id.share);
        shareTextView.setOnClickListener(this);

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.navigation_drawer_share);
    }

    // TODO: Rename method, updateFamilyHistoryProblem argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

        switch (v.getId()) {

            case R.id.share:
                shareMessage();
                break;

            default:
                break;
        }
    }



    /**
     * Function to share application
     */
    private void shareMessage() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String title = "Careons ";
        String ShortDesc = getString(R.string.short_desc);
        String ShareURL = BuildProperties.googlePlayStoreLink;
        sharingIntent.putExtra(Intent.EXTRA_TITLE, ShortDesc+" "+ShareURL);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, ShortDesc + " " + ShareURL);
        sharingIntent.putExtra("contentTitle", title);
        sharingIntent.putExtra("contentDescription ", ShortDesc+" "+ShareURL);
        sharingIntent.putExtra("contentURL", ShareURL);
        startActivityForResult(Intent.createChooser(sharingIntent, "Share via"),1000);
    }
}


