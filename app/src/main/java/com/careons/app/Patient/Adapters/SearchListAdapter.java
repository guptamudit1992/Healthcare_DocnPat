package com.careons.app.Patient.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.R;

import java.util.HashMap;
import java.util.List;

public class SearchListAdapter extends BaseAdapter {

    private Activity context;
    private LayoutInflater inflater;

    private List<HashMap<String, String>> problemList;
    private int section = 1;

    public SearchListAdapter(Activity context, List<HashMap<String, String>> problemList, int section) {

        this.context = context;
        this.problemList = problemList;
        this.section = section;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return problemList.size();
    }

    @Override
    public Object getItem(int position) {
        return problemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SearchListObjectHolder holder = new SearchListObjectHolder();

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_item_search_surgical, null);
            holder.problemId = (TextView) convertView.findViewById(R.id.problem_id);
            holder.problemName = (TextView) convertView.findViewById(R.id.textViewProblemName);
            holder.problemCategory = (TextView) convertView.findViewById(R.id.textViewProblemCategory);
            holder.problemHyperlink = (TextView) convertView.findViewById(R.id.textViewProblemHyperlink);
            convertView.setTag(holder);

        } else {

            holder = (SearchListObjectHolder) convertView.getTag();
        }

        String addProblemId = problemList.get(position).get(StringConstants.KEY_ID);
        String addTitle = problemList.get(position).get(StringConstants.KEY_NAME);
        String addCategory = problemList.get(position).get(StringConstants.KEY_CATEGORY);
        String addHyperlink = problemList.get(position).get(StringConstants.KEY_SEARCH_LIST_HYPERLINK);

        holder.problemId.setText(addProblemId);
        holder.problemName.setText(addTitle);
        holder.problemCategory.setText(addCategory);
        holder.problemHyperlink.setText(addHyperlink);
        return convertView;
    }
}
