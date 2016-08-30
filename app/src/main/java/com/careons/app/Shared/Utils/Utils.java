package com.careons.app.Shared.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.design.widget.FloatingActionButton;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.LinearLayout;

import com.careons.app.Patient.Commons.StaticConstants;

import java.io.File;

public class Utils {

    /**
     * Function to extract IMEI number of mobile phone
     * @param context
     * @return
     */
    public static String getIMEINumber(Context context) {

        TelephonyManager telephonyManager =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }


    /**
     * Function to get normal Typeface
     * @param context
     * @return
     */
    public static Typeface getTypeFaceNormal(Context context){
        return Typeface.createFromAsset(context.getAssets(), StaticConstants.TYPEFACE_FONT_ROBOTO_LIGHT);
    }


    /**
     * Function to get Monospace Typeface
     * @param context
     * @return
     */
    public static Typeface getTypeFaceMonospace(Context context) {
        return Typeface.createFromAsset(context.getAssets(), StaticConstants.TYPEFACE_FONT_ROBOTO_BOLD);
    }



    /**
     * Function to convert Drawable to Bitmap
     * @param context
     * @return
     */
    public static Bitmap convertDrawableToBitmap(Context context, int drawable) {

        return BitmapFactory.decodeResource(context.getResources(), drawable);
    }


    /**
     * Function to Check External Memory Available
     *
     * @return
     */
    public static boolean externalMemoryAvailable() {

        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read
     * @return
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Function to get Internal Memory Available
     *
     * @return
     */
    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize, false);
    }

    /**
     * Function to get Total Internal Memory Available
     *
     * @return
     */

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize, false);
    }

    /**
     * Function to get External Memory Available
     *
     * @return
     */

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize, false);
        } else {
            return "error";
        }
    }

    /**
     * Function to get Total External Memory Available
     *
     * @return
     */

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize, false);
        } else {
            return "error";
        }
    }

    /**
     * Function to get Format size
     *
     * @param size
     * @return
     */

    public static String formatSize(long size, boolean isSuffix) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            } else {
                size = 1;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        if (isSuffix) {
            int commaOffset = resultBuffer.length() - 3;
            while (commaOffset > 0) {
                resultBuffer.insert(commaOffset, ',');
                commaOffset -= 3;
            }
            if (suffix != null) resultBuffer.append(suffix);
        }
        return resultBuffer.toString();
    }


    /**
     * Function to make activity full screen
     */
    public static void setFullScreen(Activity activity) {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {

            // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);

        } else if(Build.VERSION.SDK_INT >= 19) {

            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    /**
     * Function to toggle CTA in onboarding with keyboard
     */
    public static void toggleCTAKeyboard(View root, final LinearLayout cta_layout) {

        Rect r = new Rect();
        root.getWindowVisibleDisplayFrame(r);
        int screenHeight = root.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        int keypadHeight = screenHeight - r.bottom;

        if (keypadHeight < screenHeight * 0.15) {

            // Splash activity handler
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    cta_layout.setVisibility(View.VISIBLE);
                }
            }, StaticConstants.APP_CTA_TIMEOUT);

        } else {

            cta_layout.setVisibility(View.GONE);
        }
    }


    /**
     * Function to toggle FAB in onboarding with keyboard
     */
    public static void toggleFABKeyboard(View root, final FloatingActionButton fab) {

        Rect r = new Rect();
        root.getWindowVisibleDisplayFrame(r);
        int screenHeight = root.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        int keypadHeight = screenHeight - r.bottom;

        if (keypadHeight < screenHeight * 0.15) {

            // Splash activity handler
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    fab.setVisibility(View.VISIBLE);
                }
            }, StaticConstants.APP_CTA_TIMEOUT);

        } else {

            fab.setVisibility(View.GONE);
        }
    }


    /**
     * Function to toggle FAB in onboarding with keyboard
     */
    public static void toggleFABKeyboard(View root, final FloatingActionButton fab, final int size) {

        Rect r = new Rect();
        root.getWindowVisibleDisplayFrame(r);
        int screenHeight = root.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        int keypadHeight = screenHeight - r.bottom;

        if (keypadHeight < screenHeight * 0.15) {

            // Splash activity handler
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(size > 0) {
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }, StaticConstants.APP_CTA_TIMEOUT);

        } else {

            fab.setVisibility(View.GONE);
        }
    }
}
