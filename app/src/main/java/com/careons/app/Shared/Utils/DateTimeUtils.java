package com.careons.app.Shared.Utils;

import android.content.Context;

import com.careons.app.Patient.Commons.StaticConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {

    /**
     * Function to get current systems date
     * @param context
     * @return
     */
    public static String getDate(Context context) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConstants.DEFAULT_DATE_TIME_FORMAT);
        return dateFormat.format(date);
    }


    /**
     * Function to get current systems simple date
     * @param context
     * @return
     */
    public static String getSimpleDate(Context context) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConstants.SIMPLE_DATE_FORMAT);
        return dateFormat.format(date);
    }

    /**
     * Function to get current systems time
     * @param context
     * @return
     */
    public static String getTime(Context context) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConstants.DEFAULT_TIME_FORMAT);
        return dateFormat.format(date);
    }


    /**
     * Function to get current systems time (Chat)
     * @param context
     * @return
     */
    public static String getChatTime(Context context) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConstants.CHAT_TIME_FORMAT);
        return dateFormat.format(date);
    }


    /**
     * Function to convert timestamp to date
     * @param timestamp
     * @return
     */
    public static String convertTimestampToDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConstants.SIMPLE_DATE_FORMAT);
        return dateFormat.format(date);
    }

    /**
     * Function to convert timestamp to short date
     * @param timestamp
     * @return
     */
    public static String convertTimestampToShortDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConstants.SHORT_DATE_FORMAT);
        return dateFormat.format(date);
    }

    /**
     * Function to convert timestamp to time
     * @param timestamp
     * @return
     */
    public static String convertTimestampToTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConstants.DEFAULT_TIME_FORMAT);
        return dateFormat.format(date);
    }


    /**
     * Function to convert timestamp to UTC time
     * @param timestamp
     * @return
     */
    public static String convertTimestampToUTCTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(StaticConstants.UTC_TIME_FORMAT);
        return dateFormat.format(date);
    }

    /**
     * Function to convert timestamp to UTC
     * @param timestamp
     * @return
     */
    public static String convertTimestampToUTC(long timestamp) {

        Date date = new Date(timestamp);
        DateFormat format = new SimpleDateFormat(StaticConstants.UTC_DATE_TIME_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formatted = format.format(date);
        return formatted.replace(" ", "T");
    }


    /**
     * Function to convert timestamp to Login Date
     * @param timestamp
     * @return
     */
    public static String convertTimestampToLoginDate(long timestamp) {

        Date date = new Date(timestamp);
        DateFormat format = new SimpleDateFormat(StaticConstants.LOGIN_DATE_TIME_FORMAT);
        String formatted = format.format(date);
        return formatted;
    }


    /**
     * Function to convert login date to timestamp
     * @return
     */
    public static long convertDateLoginToTimestamp(String date) {

        long timestamp = 0;
        Date result;
        DateFormat df = new SimpleDateFormat(StaticConstants.LOGIN_DATE_TIME_FORMAT);

        try {

            result =  df.parse(date);
            timestamp = result.getTime();

        } catch (ParseException e) {

            // TODO: Handle Exception
        }

        return timestamp;
    }

    /**
     * Function to convert UTC date to timestamp
     * @return
     */
    public static long convertDateUTCToTimestamp(String dateUTC) {

        long timestamp = 0;
        Date result;
        dateUTC = dateUTC.replace("T", " ");
        DateFormat df = new SimpleDateFormat(StaticConstants.UTC_DATE_TIME_FORMAT_SHORTHAND);

        try {

            result =  df.parse(dateUTC);
            timestamp = result.getTime();

        } catch (ParseException e) {

            // TODO: Handle Exception
        }

        return timestamp;
    }


    /**
     * Function to convert UTC date to timestamp
     * @return
     */
    public static long convertDateSimpleToTimestamp(String dateUTC) {

        long timestamp = 0;
        Date result;
        DateFormat df = new SimpleDateFormat(StaticConstants.SIMPLE_DATE_FORMAT);

        try {

            result =  df.parse(dateUTC);
            timestamp = result.getTime();

        } catch (ParseException e) {

            // TODO: Handle Exception
        }

        return timestamp;
    }


    /**
     * Function to convert date to timestamp
     * @param date
     * @return
     */
    public static long convertDateToTimestamp(String date) {

        long timestamp = 0l;
        Date result;
        DateFormat df = new SimpleDateFormat(StaticConstants.DEFAULT_DATE_TIME_FORMAT);

        try {

            result =  df.parse(date);
            timestamp = result.getTime();

        } catch (ParseException e) {

            // TODO: Handle Exception
        }

        return timestamp;
    }


    /**
     * Fubction to convert date from Default Format to Graph Format
     * @param date
     * @return
     */
    public static String convertDateFormat(String date) {

        long timestamp = convertDateToTimestamp(date);
        DateFormat dateFormat = new SimpleDateFormat(StaticConstants.GRAPH_DATE_TIME_FORMAT);
        return dateFormat.format(timestamp);
    }


    /**
     * Function to convert year, month, day, hour and minute to timestamp
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @return
     */
    public static long convertTimeToTimestamp(int year, int month, int day, int hour, int minute) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    /**
     * Function to get current timestamp of the system
     * @return
     */
    public static long getCurrentTimestamp() {
        Date date = new Date();
        return date.getTime();
    }

    /**
     * Function to calculate age from date of birth
     * @param dobTimestamp
     * @return
     */
    public static String calculateAgeFromDOB(long dobTimestamp) {


        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(dobTimestamp);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);


        if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
            age--;
        } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }

        return String.valueOf(age);
    }

    public static long addDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTimeInMillis();
    }

    public static long addMonth(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, i);
        return cal.getTimeInMillis();
    }

    public static long addYear(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, i);
        return cal.getTimeInMillis();
    }

    public static String getCalculatedStringDate(String duration) {
        String calDateString = "";
        int count = 0;
        //duration = "3 months";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat(StaticConstants.HEALTHBOOK_DATE_FORMAT);

        if (duration.contains("1 hr") || duration.contains("few hours")) {
            calDateString = dateFormat.format(addDay(calendar.getTime(), -1));
        } else if (duration.contains("day")) {
            count = Integer.parseInt("" + duration.charAt(0));
            calDateString = dateFormat.format(addDay(calendar.getTime(), -count));
        } else if (duration.contains("week")) {
            count = (Integer.parseInt("" + duration.charAt(0))) * 7;
            calDateString = dateFormat.format(addDay(calendar.getTime(), -count));
        } else if (duration.contains("month")) {
            count = (Integer.parseInt("" + duration.charAt(0)));
            calDateString = dateFormat.format(addMonth(calendar.getTime(), -count));
        } else if (duration.contains("yr")) {
            count = (Integer.parseInt("" + duration.charAt(0)));
            calDateString = dateFormat.format(addYear(calendar.getTime(), -count));
        } else if (duration.contains("more than 10 years")) {
            calDateString = "more than 10 years";
        }

        return calDateString;

    }


    public static String getStringDateToDuration(String strDate) {
        String duration = "";
//        strDate = "5 02 2016";
        long timestamp = 0l;
        Date result = null;
        DateFormat df = new SimpleDateFormat(StaticConstants.HEALTHBOOK_DATE_FORMAT);

        try {

            result = df.parse(strDate);
            timestamp = result.getTime();

        } catch (ParseException e) {

            // TODO: Handle Exception
        }

        if (strDate.contains("more than 10 years")) {
            duration = "more than 10 years";
        } else {
            long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;
            long dateDiff = System.currentTimeMillis() - timestamp;
            long longDay = dateDiff / MILLISECS_PER_DAY;
            int daysValue = (int) Math.abs(longDay);
            if (daysValue <= 6) {
                duration = daysValue <= 1 ? "1 day" : daysValue + " days";
            } else if (daysValue < 30) {
                int weekValue = (daysValue / 7);
                duration = weekValue == 1 ? weekValue + " week" : weekValue + " weeks";
            } else if (daysValue < 365) {
                int monthValue = (daysValue / 30);
                daysValue = daysValue - monthValue * 30;
                int weekValue = (daysValue / 7);
                if (weekValue > 0) {
                    duration = monthValue == 1 ? monthValue + " month" : monthValue + " months " + (weekValue == 1 ? weekValue + " week" : weekValue + " weeks");
                } else {
                    duration = monthValue == 1 ? monthValue + " month" : monthValue + " months";
                }
            } else if (daysValue >= 365 && daysValue <= 365 * 10) {
                int yearValue = (daysValue / 365);
                int monthValue = (daysValue - (yearValue * 365)) / 30;
                if (monthValue > 0) {
                    duration = yearValue == 1 ? yearValue + " yr" : yearValue + " yrs " + (monthValue == 1 ? monthValue + " month" : monthValue + " months ");
                } else {
                    duration = yearValue == 1 ? yearValue + " yr" : yearValue + " yrs";
                }
            } else if (daysValue > 365 * 10) {
                duration = "more than 10 years";
            }
        }

        return duration;
    }

    public static boolean getValidateDate(String firstDate, String secondDate) {
        boolean temp = false;
        long timestamp1 = 0l;
        long timestamp2 = 0l;
        Date result1 = null;
        Date result2 = null;
        DateFormat df = new SimpleDateFormat(StaticConstants.HEALTHBOOK_DATE_FORMAT);
        try {
            result1 = df.parse(firstDate);
            timestamp1 = result1.getTime();
            result2 = df.parse(secondDate);
            timestamp2 = result2.getTime();
        } catch (ParseException e) {
            // TODO: Handle Exception
        }
        if (timestamp1 > timestamp2) {
            temp = true;
        }
        return temp;
    }
	
	
	    /**
     * Function to convert date to timestamp
     * @param date
     * @return
     */
    public static long convertDateToLongForHealthBook(String date) {

        long timestamp = 0l;
        Date result;
        DateFormat df = new SimpleDateFormat(StaticConstants.SIMPLE_DATE_FORMAT);

        try {

            result =  df.parse(date);
            timestamp = result.getTime();

        } catch (ParseException e) {

            // TODO: Handle Exception
        }

        return timestamp;
    }

    /*
    * Function to convert String to UTC
    * @param timestamp
    * @return
            */
    public static String convertStringToUTC(String dateString) {
        String str_date = dateString+" 00:00:00";
        String returnDate = "";
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = df.parse(str_date);
            df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            returnDate = df.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  returnDate.replace(" ", "T");
    }

    /*
    * Function to convert String to UTC
    * @param timestamp
    * @return
            */
    public static String convertUTCToString(String dateString) {
        String str_date = dateString.replace("T", " ");
        String returnDate = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //df.setTimeZone(TimeZone.getTimeZone("UTC"));
        df.setTimeZone(TimeZone.getDefault());
        Date date;
        try {
            date = df.parse(str_date);
            df = new SimpleDateFormat("dd-MM-yyyy");
            returnDate = df.format(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  returnDate;
    }




















    /**
     * Function to calculate start date from duration
     */
    public static long calculateTimestampFromDuration(int duration) {

        long timestamp = getCurrentTimestamp();
        Calendar calendar = Calendar.getInstance();

        switch (duration) {

            case 0:
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                timestamp = calendar.getTime().getTime();
                break;

            case 1:
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR_OF_DAY, -4);
                timestamp = calendar.getTime().getTime();
                break;

            case 2:
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                timestamp = calendar.getTime().getTime();
                break;

            case 3:
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_WEEK, -2);
                timestamp = calendar.getTime().getTime();
                break;

            case 4:
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_WEEK, -3);
                timestamp = calendar.getTime().getTime();
                break;

            case 5:
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_WEEK, -4);
                timestamp = calendar.getTime().getTime();
                break;

            case 6:
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_WEEK, -5);
                timestamp = calendar.getTime().getTime();
                break;

            case 7:
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_WEEK, -6);
                timestamp = calendar.getTime().getTime();
                break;

            case 8:
                calendar.setTime(new Date());
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
                timestamp = calendar.getTime().getTime();
                break;

            case 9:
                calendar.setTime(new Date());
                calendar.add(Calendar.WEEK_OF_MONTH, -2);
                timestamp = calendar.getTime().getTime();
                break;

            case 10:
                calendar.setTime(new Date());
                calendar.add(Calendar.WEEK_OF_MONTH, -3);
                timestamp = calendar.getTime().getTime();
                break;

            case 11:
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, -1);
                timestamp = calendar.getTime().getTime();
                break;

            case 12:
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, -2);
                timestamp = calendar.getTime().getTime();
                break;

            case 13:
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, -3);
                timestamp = calendar.getTime().getTime();
                break;

            case 14:
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, -6);
                timestamp = calendar.getTime().getTime();
                break;

            case 15:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -1);
                timestamp = calendar.getTime().getTime();
                break;

            case 16:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -2);
                timestamp = calendar.getTime().getTime();
                break;

            case 17:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -3);
                timestamp = calendar.getTime().getTime();
                break;

            case 18:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -4);
                timestamp = calendar.getTime().getTime();
                break;

            case 19:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -5);
                timestamp = calendar.getTime().getTime();
                break;

            case 20:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -6);
                timestamp = calendar.getTime().getTime();
                break;

            case 21:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -7);
                timestamp = calendar.getTime().getTime();
                break;

            case 22:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -8);
                timestamp = calendar.getTime().getTime();
                break;

            case 23:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -9);
                timestamp = calendar.getTime().getTime();
                break;

            case 24:
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, -10);
                timestamp = calendar.getTime().getTime();
                break;

            default:
                break;
        }

        return timestamp;
    }


    /**
     * Function to calculate Duration From Start Date
     * @param startTimestamp
     * @return
     */
    public static String calculateDurationFromStartDate(long startTimestamp) {

        String duration = null;
        Calendar start = Calendar.getInstance();
        start.setTime(new Date(startTimestamp));
        Calendar today = Calendar.getInstance();

        /**
         * Calculate Difference in Year
         */
        int differenceInYears = today.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        if (today.get(Calendar.MONTH) < start.get(Calendar.MONTH)) {
            differenceInYears--;

        } else if (today.get(Calendar.MONTH) == start.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < start.get(Calendar.DAY_OF_MONTH)) {
            differenceInYears--;
        }

        /**
         * Calculate Duration
         */
        if(differenceInYears > 0) {

            if(differenceInYears <= 9 && differenceInYears != 1) {

                duration = differenceInYears + " Years";

            } else if (differenceInYears == 1) {

                duration = differenceInYears + " Year";

            } else {

                duration = "More than 10 Years";
            }
        } else {

            // In case Difference is not in Years
            int differenceInMonths = today.get(Calendar.MONTH) - start.get(Calendar.MONTH);
            if(today.get(Calendar.YEAR) > start.get(Calendar.YEAR)) {
                differenceInMonths = differenceInMonths + 12;
            }

            if(differenceInMonths > 0) {

                if(differenceInMonths > 3 && differenceInMonths < 9) {

                    duration = "6 Months";

                } else if (differenceInMonths == 1) {

                    duration = "1 Month";

                } else if (differenceInMonths > 9) {

                    duration = "1 Year";

                } else {

                    duration = differenceInMonths + " Months";
                }

            } else {

                // In case Difference is not in Months
                int differenceInWeeks = today.get(Calendar.WEEK_OF_MONTH) - start.get(Calendar.WEEK_OF_MONTH);
                if(today.get(Calendar.MONTH) > start.get(Calendar.MONTH)) {

                    differenceInWeeks = differenceInWeeks + 4;
                }
                if(today.get(Calendar.DAY_OF_WEEK) < start.get(Calendar.DAY_OF_WEEK)) {
                    differenceInWeeks--;
                }

                if(differenceInWeeks > 0) {

                    if(differenceInWeeks > 3) {

                        duration = "1 Month";

                    } else if (differenceInWeeks == 1) {

                        duration = "1 Week";

                    } else {

                        duration = differenceInWeeks + " Weeks";
                    }

                } else {

                    // In case Difference is not in Weeks
                    int differenceInDays = today.get(Calendar.DAY_OF_WEEK) - start.get(Calendar.DAY_OF_WEEK);
                    if(today.get(Calendar.WEEK_OF_MONTH) > start.get(Calendar.WEEK_OF_MONTH)) {
                        differenceInDays = differenceInDays + 7;
                    }

                    if(differenceInDays > 0) {

                        if(differenceInDays == 1) {

                            duration = "1 Day";

                        } else {

                            duration = differenceInDays + " Days";
                        }

                    } else {

                        // In case difference is not in days
                        int differenceInHours = today.get(Calendar.HOUR_OF_DAY) - start.get(Calendar.HOUR_OF_DAY);
                        if(today.get(Calendar.DAY_OF_WEEK) > start.get(Calendar.DAY_OF_WEEK)) {
                            differenceInHours = differenceInHours + 24;
                        }

                        if(differenceInHours == 1) {

                            duration = "1 Hour";

                        } else {

                            duration = "Few Hours";
                        }
                    }
                }
            }
        }

        return duration;
    }
}
