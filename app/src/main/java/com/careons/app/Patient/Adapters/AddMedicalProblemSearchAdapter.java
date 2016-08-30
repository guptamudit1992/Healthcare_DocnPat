package com.careons.app.Patient.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.careons.app.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AddMedicalProblemSearchAdapter extends BaseAdapter {
	ArrayList<HashMap<String, String>> addressMap;
	Activity context;
	LayoutInflater inflater;
	private int textColor = 1;

	public AddMedicalProblemSearchAdapter(Activity context,
										  ArrayList<HashMap<String, String>> balanceMap, int textColor) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.addressMap = balanceMap;
		this.textColor = textColor;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return addressMap.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return addressMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.list_item_search,
					null);
			holder.textViewProblemName = (TextView) convertView
					.findViewById(R.id.textViewProblemName);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(textColor == 1) {
			holder.textViewProblemName.setTextColor(convertView.getRootView().getResources().getColor(R.color.colorPrimary));
		}else if(textColor == 2){
			holder.textViewProblemName.setTextColor(convertView.getRootView().getResources().getColor(R.color.hb_allergy));
		}else if(textColor == 3){
			holder.textViewProblemName.setTextColor(convertView.getRootView().getResources().getColor(R.color.hb_surgical_history));
		}else if(textColor == 4){
			holder.textViewProblemName.setTextColor(convertView.getRootView().getResources().getColor(R.color.hb_childhood_history));
		}else if(textColor == 5){
			holder.textViewProblemName.setTextColor(convertView.getRootView().getResources().getColor(R.color.hb_gynaecological_issue));
		}else if(textColor == 6){
			holder.textViewProblemName.setTextColor(convertView.getRootView().getResources().getColor(R.color.hb_family_history));
		}else if(textColor == 7){
			holder.textViewProblemName.setTextColor(convertView.getRootView().getResources().getColor(R.color.hb_lifestyle));
		}
		String addTitle = addressMap.get(position).get("value");
		holder.textViewProblemName.setText(addTitle);
		return convertView;
	}

	class ViewHolder {
		TextView textViewProblemName;
	}

}
