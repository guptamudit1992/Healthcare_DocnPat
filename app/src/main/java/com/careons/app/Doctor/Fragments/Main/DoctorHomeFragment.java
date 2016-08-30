package com.careons.app.Doctor.Fragments.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.careons.app.Doctor.Activity.Chat.DoctorChatActivity;
import com.careons.app.R;
import com.careons.app.Shared.Interfaces.FragmentInteractionListener;


public class DoctorHomeFragment extends Fragment {

    private FragmentInteractionListener mListener;
    private LinearLayout startChatLinearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home_doctor, container, false);

        startChatLinearLayout = (LinearLayout) rootView.findViewById(R.id.chat_history_1);
        startChatLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), DoctorChatActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Set action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle("Home");
    }


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
}
