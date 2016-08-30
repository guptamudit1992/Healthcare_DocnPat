package com.careons.app.Shared.Components.Graphs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class GraphFragment extends Fragment implements OnChartValueSelectedListener {

    private static View rootView;
    private static ArrayList<String> labels = new ArrayList<>();
    private static List<ArrayList<Float>> values = new ArrayList<>();
    private static int section;


    public GraphFragment() {
        // Default Constructor
    }

    // Parameterized Constructor
    public GraphFragment(ArrayList<String> labels, List<ArrayList<Float>> values, int section) {

        // Substitute values
        GraphFragment.labels = labels;
        GraphFragment.values = values;
        GraphFragment.section = section;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.graph_chart, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LineChart lineChart = (LineChart) rootView.findViewById(R.id.linechart);
        TextView graphNoDataTextView = (TextView) rootView.findViewById(R.id.no_data_graph);
        lineChart.setOnChartValueSelectedListener(this);

        lineChart.setVisibility(View.GONE);
        if(labels.size() > 0) {

            // Data present -> Plot graph
            graphNoDataTextView.setVisibility(View.GONE);

            /**
             * Background task - Bind Data and Plot Chart
             */
            LineData data = getData();
            setupChart(lineChart, data);
            lineChart.setVisibility(View.VISIBLE);

        } else {

            // No Data Available
            graphNoDataTextView.setVisibility(View.VISIBLE);
        }
    }






    /**
     * Function to bind data with the graph
     * @return
     */
    private LineData getData() {

        switch (section) {

            case StaticConstants.VITALS_BLOOD_PRESSURE_ADAPTER:
                return bindBloodPressureGraphData();

            case StaticConstants.VITALS_BLOOD_GLUCOSE_ADAPTER:
                return bindBloodGlucoseGraphData();

            case StaticConstants.VITALS_BMI_ADAPTER:
                return bindBMIGraphData();

            default:
                break;
        }

        return null;
    }


    /**
     * Function to bind Blood Pressure Graph Data
     * @return
     */
    public LineData bindBloodPressureGraphData() {

        ArrayList<Float> valueSet1 = values.get(0);
        ArrayList<Float> valueSet2 = values.get(1);

        // Bind Values to Y - Axis
        ArrayList<Entry> _valueSet1 = new ArrayList<>();
        for(int i = 0; i < valueSet1.size(); i++) {
            _valueSet1.add(new Entry(valueSet1.get(i), i));
        }

        // Bind Values to Y - Axis
        ArrayList<Entry> _valueSet2 = new ArrayList<>();
        for(int i = 0; i < valueSet2.size(); i++) {
            _valueSet2.add(new Entry(valueSet2.get(i), i));
        }


        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(_valueSet1, getString(R.string.graph_legend_Sys));
        LineDataSet set2 = new LineDataSet(_valueSet2, getString(R.string.graph_legend_Dys));


        /**
         * Render Line - Width and Circle Format (Set 1)
         */
        set1.setLineWidth(3f);
        set1.setCircleRadius(5f);
        set1.setDrawCircleHole(true);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setCircleColorHole(getResources().getColor(R.color.colorPrimary));
        set1.setDrawCircleHole(true);
        set1.setDrawCircles(true);
        set1.setHighLightColor(getResources().getColor(R.color.white));
        set1.setDrawValues(false);

        /**
         * Render Line - Width and Circle Format (Set 2)
         */
        set2.setLineWidth(2f);
        set2.enableDashedLine(10, 5, 10);
        set2.setCircleRadius(5f);
        set2.setDrawCircleHole(true);
        set2.setColor(getResources().getColor(R.color.vitals_bp_diastolic));
        set2.setCircleColor(getResources().getColor(R.color.vitals_bp_diastolic));
        set2.setCircleColorHole(getResources().getColor(R.color.colorPrimary));
        set2.setDrawCircleHole(true);
        set2.setDrawCircles(true);
        set2.setHighLightColor(getResources().getColor(R.color.vitals_bp_diastolic));
        set2.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        // create a data object with the datasets
        LineData data = new LineData(labels, dataSets);

        return data;
    }


    /**
     * Function to bind Blood Glucose Graph Data
     * @return
     */
    public LineData bindBloodGlucoseGraphData() {

        ArrayList<Float> valueSet1 = values.get(0);

        // Bind Values to Y - Axis
        ArrayList<Entry> _valueSet1 = new ArrayList<>();
        for(int i = 0; i < valueSet1.size(); i++) {
            _valueSet1.add(new Entry(valueSet1.get(i), i));
        }


        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(_valueSet1, getString(R.string.blood_glucose));

        /**
         * Render Line - Width and Circle Format (Set 1)
         */
        set1.setLineWidth(3f);
        set1.setCircleRadius(5f);
        set1.setDrawCircleHole(true);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setCircleColorHole(getResources().getColor(R.color.colorPrimary));
        set1.setDrawCircleHole(true);
        set1.setDrawCircles(true);
        set1.setHighLightColor(getResources().getColor(R.color.white));
        set1.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);

        // create a data object with the datasets
        LineData data = new LineData(labels, dataSets);

        return data;
    }


    /**
     * Function to bind BMI Graph Data
     * @return
     */
    public LineData bindBMIGraphData() {

        ArrayList<Float> valueSet1 = values.get(0);
        ArrayList<Float> valueSet2 = values.get(1);
        ArrayList<Float> valueSet3 = values.get(2);
        ArrayList<Float> valueSet4 = values.get(3);
        ArrayList<Float> valueSet5 = values.get(4);

        // Bind Values to Y - Axis
        ArrayList<Entry> _valueSet1 = new ArrayList<>();
        for(int i = 0; i < valueSet1.size(); i++) {
            _valueSet1.add(new Entry(valueSet1.get(i), i));
        }

        // Bind Values to Y - Axis
        ArrayList<Entry> _valueSet2 = new ArrayList<>();
        for(int i = 0; i < valueSet1.size(); i++) {
            _valueSet2.add(new Entry(valueSet2.get(0), i));
        }

        // Bind Values to Y - Axis
        ArrayList<Entry> _valueSet3 = new ArrayList<>();
        for(int i = 0; i < valueSet1.size(); i++) {
            _valueSet3.add(new Entry(valueSet3.get(0), i));
        }

        // Bind Values to Y - Axis
        ArrayList<Entry> _valueSet4 = new ArrayList<>();
        for(int i = 0; i < valueSet1.size(); i++) {
            _valueSet4.add(new Entry(valueSet4.get(0), i));
        }

        // Bind Values to Y - Axis
        ArrayList<Entry> _valueSet5 = new ArrayList<>();
        for(int i = 0; i < valueSet1.size(); i++) {
            _valueSet5.add(new Entry(valueSet5.get(0), i));
        }


        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(_valueSet1, getString(R.string.graph_legend_BMI));
        LineDataSet set2 = new LineDataSet(_valueSet2, getString(R.string.underweight));
        LineDataSet set3 = new LineDataSet(_valueSet3, getString(R.string.normal));
        LineDataSet set4 = new LineDataSet(_valueSet4, getString(R.string.overweight));
        LineDataSet set5 = new LineDataSet(_valueSet5, getString(R.string.obese));


        /**
         * Render Line - Width and Circle Format (Set 1)
         */
        set1.setLineWidth(3f);
        set1.setCircleRadius(5f);
        set1.setDrawCircleHole(true);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setCircleColorHole(getResources().getColor(R.color.transparent));
        set1.setDrawCircleHole(true);
        set1.setDrawCircles(true);
        set1.setHighLightColor(getResources().getColor(R.color.white));
        set1.setDrawValues(false);

        /**
         * Render Line - Width and Circle Format (Set 2)
         */
        set2.setColor(Color.YELLOW);
        set2.setLineWidth(0f);
        set2.setFillAlpha(200);
        set2.setDrawFilled(true);
        set2.setFillColor(Color.YELLOW);
        set2.setDrawValues(false);
        set2.setDrawCircles(false);


        /**
         * Render Line - Width and Circle Format (Set 3)
         */
        set3.setColor(Color.GREEN);
        set3.setLineWidth(0f);
        set3.setFillAlpha(150);
        set3.setDrawFilled(true);
        set3.setFillColor(Color.GREEN);
        set3.setDrawValues(false);
        set3.setDrawCircles(false);


        /**
         * Render Line - Width and Circle Format (Set 4)
         */
        set4.setColor(getResources().getColor(R.color.vitals_bmi_orange));
        set4.setLineWidth(0f);
        set4.setFillAlpha(110);
        set4.setDrawFilled(true);
        set4.setFillColor(getResources().getColor(R.color.vitals_bmi_orange));
        set4.setDrawValues(false);
        set4.setDrawCircles(false);


        /**
         * Render Line - Width and Circle Format (Set 5)
         */
        set5.setColor(Color.RED);
        set5.setLineWidth(0f);
        set5.setFillAlpha(100);
        set5.setDrawFilled(true);
        set5.setFillColor(Color.RED);
        set5.setDrawValues(false);
        set5.setDrawCircles(false);



        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set5);
        dataSets.add(set4);
        dataSets.add(set3);
        dataSets.add(set2);
        dataSets.add(set1);

        // create a data object with the datasets
        LineData data = new LineData(labels, dataSets);

        return data;
    }






    /**
     * Function to render the graph
     * @param chart
     * @param data
     */
    private void setupChart(LineChart chart, LineData data) {

        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription("");


        // enable / disable grid background
        chart.setDrawGridBackground(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        /**
         * Pinch Zoom Action
         * if disabled, scaling can be done on x- and y-axis separately
         */
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(true);

        /**
         * Set Background Color
         */
        chart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        /**
         * Chart Padding
         */
        chart.setViewPortOffsets(60, 25, 50, 50);

        /**
         * Bind data to chart
         */
        chart.setData(data);

        /**
         * Legend
         */
        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setTextColor(getResources().getColor(R.color.white));
        l.setEnabled(true);
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);

        /**
         * X - Axis
         */
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(getResources().getColor(R.color.white));
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineWidth(2f);
        xAxis.setAxisLineColor(getResources().getColor(R.color.white));
        xAxis.setSpaceBetweenLabels(5);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawGridLines(false);

        /**
         * Y - Axis (left and right)
         */
        YAxis leftYAxis = chart.getAxisLeft();
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftYAxis.setTextColor(getResources().getColor(R.color.white));
        leftYAxis.setDrawAxisLine(true);
        leftYAxis.setAxisLineWidth(2f);
        leftYAxis.setEnabled(true);
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.white));
        leftYAxis.setDrawGridLines(false);

        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setDrawAxisLine(false);
        rightYAxis.setEnabled(false);
        rightYAxis.setDrawGridLines(false);

        /**
         * Section based fixes
         */
        switch (section) {

            case StaticConstants.VITALS_BMI_ADAPTER:
                //leftYAxis.setEnabled(false);
                break;

            default:
                break;
        }


        /**
         * Auto scale Min - Max Data
         */
        chart.setAutoScaleMinMaxEnabled(true);

        /**
         * Animate Chart Loading
         */
        //chart.animateX(1000);
        chart.animateY(1000);
    }



    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = null;
        AlertDialog dialog = null;

        switch (section) {

            case StaticConstants.VITALS_BLOOD_PRESSURE_ADAPTER:
                dialogView = inflater.inflate(R.layout.dialog_graph_bp, null);
                builder.setView(dialogView);
                dialog = builder.create();

                TextView dateBPTextView = (TextView) dialogView.findViewById(R.id.dialog_date);
                TextView systolicTextView = (TextView) dialogView.findViewById(R.id.dialog_sys);
                TextView diastolicTextView = (TextView) dialogView.findViewById(R.id.dialog_dys);

                dateBPTextView.setText(getString(R.string.dated).concat(" ").concat(labels.get(e.getXIndex())));
                systolicTextView.setText(values.get(0).get(e.getXIndex()).toString());
                diastolicTextView.setText(values.get(1).get(e.getXIndex()).toString());
                break;


            case StaticConstants.VITALS_BLOOD_GLUCOSE_ADAPTER:
                dialogView = inflater.inflate(R.layout.dialog_graph_bg, null);
                builder.setView(dialogView);
                dialog = builder.create();

                TextView dateBGTextView = (TextView) dialogView.findViewById(R.id.dialog_date);
                TextView bloodGlucoseTextView = (TextView) dialogView.findViewById(R.id.dialog_bg_value);

                dateBGTextView.setText(getString(R.string.dated).concat(" ").concat(labels.get(e.getXIndex())));
                bloodGlucoseTextView.setText(values.get(0).get(e.getXIndex()).toString());
                break;


            case StaticConstants.VITALS_BMI_ADAPTER:
                dialogView = inflater.inflate(R.layout.dialog_graph_bmi, null);
                builder.setView(dialogView);
                dialog = builder.create();

                TextView dateBMITextView = (TextView) dialogView.findViewById(R.id.dialog_date);
                TextView bmiTextView = (TextView) dialogView.findViewById(R.id.dialog_bmi_value);

                dateBMITextView.setText(getString(R.string.dated).concat(" ").concat(labels.get(e.getXIndex())));
                bmiTextView.setText(values.get(0).get(e.getXIndex()).toString());
                break;

            default:
                break;
        }

        // Show dialog
        dialog.show();
    }


    @Override
    public void onNothingSelected() {

    }
}
