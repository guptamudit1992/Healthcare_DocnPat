/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.careons.app.Patient.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.careons.app.Patient.Activity.Main.HealthBook.HealthbookActivity;
import com.careons.app.Patient.Activity.Main.MainActivity;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.R;
import com.careons.app.Patient.Services.SharedPreferenceService;
import com.careons.app.Shared.Utils.DateTimeUtils;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private static Context context;

    private static final int FULL = 1, HALF = 2;

    private String[] mDataSet;

    public static class FullViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameTextView, ageTextView, genderTextView, bloodGroupTextView;
        public ImageView userImageView;

        public FullViewHolder(final View view) {
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.mMainActivity.gotoProfile();
                }
            });

            userImageView = (ImageView) view.findViewById(R.id.user_image);
            userNameTextView = (TextView) view.findViewById(R.id.user_name);
            ageTextView = (TextView) view.findViewById(R.id.age);
            genderTextView = (TextView) view.findViewById(R.id.gender);
            bloodGroupTextView = (TextView) view.findViewById(R.id.blood_group);

        }

    }

    public static class HalfViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public ImageView imageView;
        public TextView textView, countTextView;

        public HalfViewHolder(final View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), HealthbookActivity.class);
                    intent.putExtra(StringConstants.KEY_SELECTED_INDEX, getPosition()-1);
                    v.getContext().startActivity(intent);
                }
            });


            cardView = (CardView) view.findViewById(R.id.card_view);
            countTextView = (TextView) view.findViewById(R.id.count);
            imageView = (ImageView) view.findViewById(R.id.image);
            textView = (TextView) view.findViewById(R.id.name);
        }

    }


    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(String[] dataSet, Context context) {

        mDataSet = dataSet;
        CustomAdapter.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        RecyclerView.ViewHolder v = null;

        switch (viewType) {
            case FULL:
                v = new FullViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.content_vitals, viewGroup, false));
                break;
            case HALF:
                v = new HalfViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.card_healthbook, viewGroup, false));
                break;
        }

        return v;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HalfViewHolder viewHolder = null;
        if (holder instanceof HalfViewHolder) {
            viewHolder = (HalfViewHolder) holder;
        }
        int pos = position - 1;

        switch (position) {
            case 0:

                FullViewHolder vHolder = (FullViewHolder) holder;


                /**
                 * Place Display Pic based on Gender
                 */
                if(SharedPreferenceService.getValue(context, StringConstants.KEY_GENDER).equalsIgnoreCase(context.getString(R.string.male))) {

                    vHolder.userImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.dp_man));

                } else {

                    vHolder.userImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.dp_woman));
                }

                vHolder.userNameTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_USERNAME));
                vHolder.ageTextView.setText(
                        DateTimeUtils.calculateAgeFromDOB(DateTimeUtils.convertDateLoginToTimestamp(
                                SharedPreferenceService.getValue(context, StringConstants.KEY_DOB)))
                                .concat(" ")
                                .concat(context.getString(R.string.years)));
                vHolder.genderTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_GENDER));
                vHolder.bloodGroupTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_BLOOD_GROUP));
                break;

            case 1:
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.mp));
                viewHolder.textView.setText(mDataSet[pos]);
                viewHolder.imageView.setImageResource(R.drawable.ic_hb_med);

                /**
                 * Check Problem Count
                 */
                if(Integer.parseInt(SharedPreferenceService.getValue(context, StringConstants.KEY_PROBLEM_COUNT)) > 0) {
                    viewHolder.countTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_PROBLEM_COUNT));
                    viewHolder.countTextView.setTextColor(context.getResources().getColor(R.color.mp));
                } else {

                    viewHolder.countTextView.setVisibility(View.GONE);
                }
                break;

            case 2:
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.al));
                viewHolder.textView.setText(mDataSet[pos]);
                viewHolder.imageView.setImageResource(R.drawable.ic_hb_allergy);

                /**
                 * Check Allergy Count
                 */
                if(Integer.parseInt(SharedPreferenceService.getValue(context, StringConstants.KEY_ALLERGY_COUNT)) > 0) {
                    viewHolder.countTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_ALLERGY_COUNT));
                    viewHolder.countTextView.setTextColor(context.getResources().getColor(R.color.al));
                } else {

                    viewHolder.countTextView.setVisibility(View.GONE);
                }
                break;

            case 3:
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.fh));
                viewHolder.textView.setText(mDataSet[pos]);
                viewHolder.imageView.setImageResource(R.drawable.ic_hb_family);

                /**
                 * Check Family Count
                 */
                if(Integer.parseInt(SharedPreferenceService.getValue(context, StringConstants.KEY_FAMILY_COUNT)) > 0) {
                    viewHolder.countTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_FAMILY_COUNT));
                    viewHolder.countTextView.setTextColor(context.getResources().getColor(R.color.fh));
                } else {

                    viewHolder.countTextView.setVisibility(View.GONE);
                }
                break;

            case 4:
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.mh));
                viewHolder.textView.setText(mDataSet[pos]);
                viewHolder.imageView.setImageResource(R.drawable.ic_hb_medication);

                /**
                 * Check Medication Count
                 */
                if(Integer.parseInt(SharedPreferenceService.getValue(context, StringConstants.KEY_MEDICATION_COUNT)) > 0) {
                    viewHolder.countTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_MEDICATION_COUNT));
                    viewHolder.countTextView.setTextColor(context.getResources().getColor(R.color.mh));
                } else {

                    viewHolder.countTextView.setVisibility(View.GONE);
                }
                break;

            case 5:
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.lf));
                viewHolder.textView.setText(mDataSet[pos]);
                viewHolder.imageView.setImageResource(R.drawable.ic_hb_lifestyle);

                /**
                 * Check Lifestyle Count
                 */
                if(Integer.parseInt(SharedPreferenceService.getValue(context, StringConstants.KEY_LIFESTYLE_COUNT)) > 0) {
                    viewHolder.countTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_LIFESTYLE_COUNT));
                    viewHolder.countTextView.setTextColor(context.getResources().getColor(R.color.lf));
                } else {

                    viewHolder.countTextView.setVisibility(View.GONE);
                }
                break;

            case 6:
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.sh));
                viewHolder.textView.setText(mDataSet[pos]);
                viewHolder.imageView.setImageResource(R.drawable.ic_hb_surgery);

                /**
                 * Check Surgical Count
                 */
                if(Integer.parseInt(SharedPreferenceService.getValue(context, StringConstants.KEY_SURGICAL_COUNT)) > 0) {
                    viewHolder.countTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_SURGICAL_COUNT));
                    viewHolder.countTextView.setTextColor(context.getResources().getColor(R.color.sh));
                } else {

                    viewHolder.countTextView.setVisibility(View.GONE);
                }
                break;

            case 7:
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.ch));
                viewHolder.textView.setText(mDataSet[pos]);
                viewHolder.imageView.setImageResource(R.drawable.ic_hb_ph);

                /**
                 * Check Childhood Count
                 */
                if(Integer.parseInt(SharedPreferenceService.getValue(context, StringConstants.KEY_CHILDHOOD_COUNT)) > 0) {
                    viewHolder.countTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_CHILDHOOD_COUNT));
                    viewHolder.countTextView.setTextColor(context.getResources().getColor(R.color.ch));
                } else {

                    viewHolder.countTextView.setVisibility(View.GONE);
                }
                break;

            case 8:
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.preg));
                viewHolder.textView.setText(mDataSet[pos]);
                viewHolder.imageView.setImageResource(R.drawable.ic_hb_preg);
                viewHolder.countTextView.setVisibility(View.GONE);
                break;

            case 9:
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.gh));
                viewHolder.textView.setText(mDataSet[pos]);
                viewHolder.imageView.setImageResource(R.drawable.ic_hb_gyne);

                /**
                 * Check Gynaecology Count
                 */
                if(Integer.parseInt(SharedPreferenceService.getValue(context, StringConstants.KEY_GYNAECOLOGY_COUNT)) > 0) {
                    viewHolder.countTextView.setText(SharedPreferenceService.getValue(context, StringConstants.KEY_GYNAECOLOGY_COUNT));
                    viewHolder.countTextView.setTextColor(context.getResources().getColor(R.color.gh));
                } else {

                    viewHolder.countTextView.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mDataSet.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? FULL : HALF;
    }


    /**
     * Function to navigate to healthbook detail
     *
     * @param context
     * @param section
     */
    public static void gotoHealthbookDetails(Context context, int section) {

        Intent intent = new Intent(context, HealthbookActivity.class);
        intent.putExtra(StringConstants.KEY_SELECTED_INDEX, section);
        context.startActivity(intent);
    }
}
