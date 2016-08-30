package com.careons.app.Patient.Fragments.Vitals;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Shared.Components.Graphs.GraphFragment;
import com.careons.app.Patient.Database.Models.Vitals.BMI;
import com.careons.app.Patient.Database.Models.Vitals.BloodGlucose;
import com.careons.app.Patient.Database.Models.Vitals.BloodPressure;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VitalFragment extends Fragment {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static View rootView;

    private static Fragment fragment;
    private static TabLayout tabLayout;

    private static int graphScaleSelected = StaticConstants.VITALS_RANDOM;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_vital, container, false);

        // Assign fragment
        fragment = VitalFragment.this;

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.vital_container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // Tab Layout
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Set on fragment Change Listener
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // Create graph
                renderGraph(getContext(), position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Set Selected item
        if(getArguments() != null) {
            int selectedIndex = getArguments().getInt(StringConstants.KEY_SELECTED_INDEX, 0);
            mViewPager.setCurrentItem(selectedIndex);
        }

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Set action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.navigation_drawer_vitals);
    }


    /**
     * Function to create graph
     * @param labels
     * @param values
     */
    public static void createGraph(ArrayList<String> labels, List<ArrayList<Float>> values, int section) {

        fragment.getFragmentManager().beginTransaction()
                .replace(R.id.graph_bp_container, new GraphFragment(labels, values, section))
                .commit();
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case 0:
                    return BPFragment.newInstance();
                case 1:
                    return BGFragment.newInstance();
                case 2:
                    return BMIFragment.newInstance();

                default:
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.blood_pressure);
                case 1:
                    return getString(R.string.blood_glucose);
                case 2:
                    return getString(R.string.bmi);
            }
            return null;
        }
    }







    /**
     * Function to render graph
     */
    public static void renderGraph() {

        renderGraph(rootView.getContext(),tabLayout.getSelectedTabPosition());
    }


    /**
     * Function to render graph
     * @param position
     */
    public static void renderGraph(Context context, int position) {

        switch (position) {

            case 0:

                /**
                 * Read from database table
                 */
                List<BloodPressure> bloodPressureList = BloodPressure.find(BloodPressure.class,
                        "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));

                renderGraphBloodPressure(bloodPressureList);
                break;

            case 1:

                // Render Graph
                renderGraphBloodGlucose(context, graphScaleSelected);
                break;

            case 2:

                /**
                 * Read from database table
                 */
                List<BMI> bmiList = BMI.find(BMI.class,
                        "patient_id = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID));
                // Render Graph
                renderGraphBMI(bmiList);
                break;

            default:
                break;
        }
    }


    /**
     * Function to render updated Graph
     * @param bloodPressures
     */
    public static void renderGraphBloodPressure(List<BloodPressure> bloodPressures) {

        // Initializing Graph Variables
        ArrayList<String> labels = new ArrayList<>();
        List<ArrayList<Float>> values = new ArrayList<>();
        ArrayList<Float> valueSystolicBP = new ArrayList<>();
        ArrayList<Float> valueDiastolicBP = new ArrayList<>();


        /**
         * Sort list before binding
         */
        Collections.sort(bloodPressures, Collections.reverseOrder());


        // Extract Line Graph Data Points
        for(int i = 0; i < bloodPressures.size(); i++) {

            BloodPressure bloodPressure = bloodPressures.get(i);
            labels.add(DateTimeUtils.convertTimestampToShortDate(bloodPressure.getTimestamp()));
            valueSystolicBP.add(Float.parseFloat(bloodPressure.getSystolicBp()));
            valueDiastolicBP.add(Float.parseFloat(bloodPressure.getDiastolicBp()));
        }

        /**
         * Add Arraylist to array
         */
        values.add(valueSystolicBP);
        values.add(valueDiastolicBP);

        // Render Line Graph
        createGraph(labels, values, StaticConstants.VITALS_BLOOD_PRESSURE_ADAPTER);
    }


    /**
     * Function to render updated Graph
     * @param context
     * @param graphScale
     */
    public static void renderGraphBloodGlucose(Context context, int graphScale) {

        List<BloodGlucose> bloodGlucoseList = new ArrayList<>();
        bloodGlucoseList.clear();

        /**
         * Keep Selected Graph Scale
         */
        if(graphScale != 0) {
            graphScaleSelected = graphScale;
        }


        switch (graphScaleSelected) {

            case StaticConstants.VITALS_POST_MEAL:

                /**
                 * Read from database table
                 */
                bloodGlucoseList = BloodGlucose.find(BloodGlucose.class,
                        "patient_id = ? and checkup = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        context.getString(R.string.post_meal));
                break;

            case StaticConstants.VITALS_PRE_MEAL:

                /**
                 * Read from database table
                 */
                bloodGlucoseList = BloodGlucose.find(BloodGlucose.class,
                        "patient_id = ? and checkup = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        context.getString(R.string.pre_meal));
                break;

            case StaticConstants.VITALS_RANDOM:
                /**
                 * Read from database table
                 */
                bloodGlucoseList = BloodGlucose.find(BloodGlucose.class,
                        "patient_id = ? and checkup = ?", SharedPreferenceService.getValue(context, StringConstants.KEY_PATIENT_ID),
                        context.getString(R.string.random));
                break;

            default:
                break;
        }

        // Initializing Graph Variables
        ArrayList<String> labels = new ArrayList<>();
        List<ArrayList<Float>> values = new ArrayList<>();
        ArrayList<Float> valueGlucose = new ArrayList<>();


        /**
         * Sort list before binding
         */
        Collections.sort(bloodGlucoseList, Collections.reverseOrder());


        // Extract Line Graph Data Points
        for(int i = 0; i < bloodGlucoseList.size(); i++) {

            BloodGlucose bloodGlucose = bloodGlucoseList.get(i);
            labels.add(DateTimeUtils.convertTimestampToShortDate(bloodGlucose.getTimestamp()));
            valueGlucose.add(Float.parseFloat(bloodGlucose.getBloodGlucose()));
        }


        /**
         * Add Arraylist to array
         */
        values.add(valueGlucose);

        // Render Line Graph
        createGraph(labels, values, StaticConstants.VITALS_BLOOD_GLUCOSE_ADAPTER);
    }


    /**
     * Function to render updated Graph
     * @param bmis
     */
    public static void renderGraphBMI(List<BMI> bmis) {

        // Initializing Graph Variables
        ArrayList<String> labels = new ArrayList<>();
        List<ArrayList<Float>> values = new ArrayList<>();
        ArrayList<Float> valueBMI = new ArrayList<>();
        ArrayList<Float> highestDangerLevel = new ArrayList<>();
        ArrayList<Float> highDangerLevel = new ArrayList<>();
        ArrayList<Float> normalLevel = new ArrayList<>();
        ArrayList<Float> lowDangerLevel = new ArrayList<>();


        /**
         * Sort list before binding
         */
        Collections.sort(bmis, Collections.reverseOrder());


        // Extract Line Graph Data Points
        for(int i = 0; i < bmis.size(); i++) {

            BMI bmi = bmis.get(i);
            labels.add(DateTimeUtils.convertTimestampToShortDate(bmi.getTimestamp()));
            valueBMI.add(Float.parseFloat(bmi.getBmi()));
        }

        // Set Danger levels
        lowDangerLevel.add(18.5f);
        normalLevel.add(25f);
        highDangerLevel.add(30f);
        highestDangerLevel.add(40f);

        /**
         * Add Array list to array
         */
        values.add(valueBMI);
        values.add(lowDangerLevel);
        values.add(normalLevel);
        values.add(highDangerLevel);
        values.add(highestDangerLevel);

        // Render Line Graph
        createGraph(labels, values, StaticConstants.VITALS_BMI_ADAPTER);
    }
}
