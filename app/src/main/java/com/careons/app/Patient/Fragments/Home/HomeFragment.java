package com.careons.app.Patient.Fragments.Home;

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

import com.careons.app.Patient.Fragments.Home.Main.HealthBookFragment;
import com.careons.app.Patient.Fragments.Home.Main.HealthEducationFragment;
import com.careons.app.Patient.Fragments.Home.Main.UploadDocumentsFragment;
import com.careons.app.Patient.Fragments.Home.Main.VitalInformationFragment;
import com.careons.app.R;

public class HomeFragment extends Fragment {

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

    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Tab Layout
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        // Set action bar title
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(R.string.navigation_drawer_home);
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
                    return HealthBookFragment.newInstance();

                case 1:
                    return HealthEducationFragment.newInstance();

                case 2:
                    return VitalInformationFragment.newInstance();

                case 3:
                    return UploadDocumentsFragment.newInstance();

                default:
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.health_book);
                case 1:
                    return getString(R.string.health_search);
                case 2:
                    return getString(R.string.vitals_section);
                case 3:
                    return getString(R.string.upload_doc);
            }
            return null;
        }
    }
}