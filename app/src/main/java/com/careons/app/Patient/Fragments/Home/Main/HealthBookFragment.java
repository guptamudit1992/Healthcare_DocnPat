package com.careons.app.Patient.Fragments.Home.Main;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.careons.app.Patient.Adapters.CustomAdapter;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;

public class HealthBookFragment extends Fragment {

    private View rootView;

    private GridView gridView;

    private static RecyclerView recyclerViewList;

    // Healthbook Grid Data
    private static String[] healthbookGridData = null;

    public static HealthBookFragment newInstance() {
        return new HealthBookFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_health_book, container, false);

        /**
         * Initialize healthbook grid data based on Gender
         */
        if(SharedPreferenceService.getValue(getContext(), StringConstants.KEY_GENDER).equalsIgnoreCase(getString(R.string.male))) {
            healthbookGridData = getActivity().getResources().getStringArray(R.array.healthbook_male);
        } else {
            healthbookGridData = getActivity().getResources().getStringArray(R.array.healthbook_female);
        }

        // Recycler view
        recyclerViewList = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        // Set adapter
        CustomAdapter adapter =
                new CustomAdapter(healthbookGridData, getActivity().getApplicationContext());

        // Create a grid layout with two columns
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);

        // Create a custom SpanSizeLookup where the first item spans both columns
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1;
            }
        });
        recyclerViewList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                    outRect.left = 10;
                    outRect.right = 10;
                    outRect.bottom = 10;
                    outRect.top = 10;
            }
        });
        recyclerViewList.setLayoutManager(layoutManager);
        recyclerViewList.setAdapter(adapter);

        return rootView;
    }


}
