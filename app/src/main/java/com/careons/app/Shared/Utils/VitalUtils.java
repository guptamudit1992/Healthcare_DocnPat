package com.careons.app.Shared.Utils;

import android.content.Context;

import com.careons.app.R;

public class VitalUtils {

    /**
     * Function to calculate blood pressure range tag
     * @param context
     * @param systolicBp
     * @param diastolicBp
     * @return
     */
    public static String calculateBPRangeTag(Context context, int systolicBp, int diastolicBp) {

        String tag;

        if((systolicBp >= 90 && systolicBp <= 140) && (diastolicBp >= 60 && diastolicBp <= 90)) {

            // Normal Blood Pressure
            tag = context.getString(R.string.normal);

        } else if((systolicBp > 140 && systolicBp <= 179) || (diastolicBp > 90 && diastolicBp <= 109)) {

            // Normal Blood Pressure
            tag = context.getString(R.string.high);

        } else {

            // Else -> Alarming
            tag = context.getString(R.string.alarming);
        }

        return tag;
    }


    /**
     * Function to calculate blood glucose range tag
     * @param context
     * @param bloodGlucose
     * @param checkup
     * @return
     */
    public static String calculateBGRangeTag(Context context, int bloodGlucose, String checkup) {

        String tag;

        if(checkup.equalsIgnoreCase(context.getString(R.string.pre_meal))) {

            // Post Meal | Random Checkup
            if(bloodGlucose >= 80 && bloodGlucose <= 126) {

                tag = context.getString(R.string.normal);

            } else if(bloodGlucose >= 127 && bloodGlucose <= 180) {

                tag = context.getString(R.string.high);

            } else {

                tag = context.getString(R.string.alarming);
            }

        } else {

            // Pre Meal | Fasting Checkup
            if(bloodGlucose >= 99 && bloodGlucose <= 199) {

                tag = context.getString(R.string.normal);

            } else if(bloodGlucose >= 200 && bloodGlucose <= 350) {

                tag = context.getString(R.string.high);

            } else {

                tag = context.getString(R.string.alarming);
            }
        }

        return tag;
    }



    /**
     * Function to calculate body mass index range tag
     * @param context
     * @param bmi
     * @return
     */
    public static String calculateBMIRangeTag(Context context, float bmi) {

        String tag;

        /**
         * Underweight <18.5
        Normal 18.5 to be 24.9
        Overweight 25 to 29.9
        obese 30 or above
         **/


        if(bmi < 18.5) {

            tag = context.getString(R.string.underweight);

        } else if (bmi >= 18.5 && bmi <= 24.9) {

            tag = context.getString(R.string.normal);

        } else if (bmi >= 25 && bmi <= 29.9) {

            tag = context.getString(R.string.overweight);

        } else {

            tag = context.getString(R.string.obese);
        }


        return tag;
    }


    /**
     * Function to calculate BMI from weight and height
     * @param weight
     * @param heightFt
     * @param heightInc
     * @return
     */
    public static String calculateBMI(int heightFt, int heightInc, int weight) {

        double height = convertHeightToInches(heightFt, heightInc);
        double bmi = weight / (height * height);

        return String.format("%.2f", bmi);
    }


    /**
     * Function to convert height into meters
     * @param heightFt
     * @param heightInc
     * @return
     */
    public static double convertHeightToInches(int heightFt, int heightInc) {

        double height = 0.0254 * ((heightFt * 12) + heightInc);
        return height;
    }
}
