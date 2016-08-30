package com.careons.app.Patient.Activity.Main.HealthSearch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.R;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.careons.app.Shared.Utils.Validation;

public class WebViewActivity extends AppCompatActivity {

    private static Activity activity;
    private static WebView webView;
    private static LinearLayout loader;
    private static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Initialize activity
        activity = this;

        // Extract id from intent
        Bundle extras = getIntent().getExtras();
        String problem = extras.getString(StringConstants.KEY_HEALTH_SEARCH_PROBLEM_NAME);
        String hyperlink = extras.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK);

        // Toolbar setup
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(problem);
        setSupportActionBar(toolbar);


        // Set Loader
        loader = (LinearLayout) findViewById(R.id.loader);

        // Accelerate Web view loading
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        // Initialize web view
        webView = (WebView) findViewById(R.id.webview);

        /**
         * Enable Webview Settings
         */
        WebSettings webSettings = webView.getSettings();
        webView.setWebChromeClient(new WebChromeClient());
        //webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        //webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


        /**
         * Load Url in Webview
         */
        getUrl(getApplicationContext(), problem, hyperlink);

        // Override URL loading in web view
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                toolbar.setTitle(url);
                return false;
            }
        });


        // Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_clear_white_24dp));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // I do not want this...
                // Home as up button is to navigate to Home-Activity not previous activity
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        // Handle back button to go back
        if (webView.canGoBack()) {
            webView.goBack();
        } else {

            finish();
        }
    }


    /**
     * Function to get web url
     * @param context
     * @param problem
     * @param hyperlink
     */
    public static void getUrl(final Context context, final String problem, String hyperlink) {

        String url;
        if(hyperlink == null || hyperlink.equalsIgnoreCase("null") || hyperlink.isEmpty()) {

            url = ServiceUrls.KEY_HEALTH_SEARCH_FETCH_URL.concat(problem.replace(" ", "%20"));

        } else {

            url = hyperlink;
        }

        if (Validation.isConnected(context)) {

            webView.loadUrl(url);

        } else {

            // No internet connection
            ErrorHandlers.handleInternetConnectionFailure(activity);
            loader.setVisibility(View.GONE);
        }
    }
}
