package com.careons.app.Patient.Activity.Main.HealthSearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.careons.app.Patient.Adapters.Adapter;
import com.careons.app.Patient.Commons.BuildProperties;
import com.careons.app.Patient.Commons.ServiceUrls;
import com.careons.app.Patient.Commons.StaticConstants;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.Patient.Models.HealthSearch.HealthEducation;
import com.careons.app.R;
import com.careons.app.Patient.Services.APICallService;
import com.careons.app.Shared.Utils.Validation;
import com.careons.app.databinding.ListItemSearchOptionBinding;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class SearchActivity extends AppCompatActivity {

    private static Activity activity;
    private static RecyclerView searchOptionsRecyclerView;
    private static LinearLayout loader;
    private static TextView emptySearchLayout, errorSearchLayout, internetFailureSearchLayout;
    private static EditText searchBarEditText;

    private static Adapter<HealthEducation, ListItemSearchOptionBinding> listItemSearchOptionBindingAdapter;
    private static ArrayList<HealthEducation> searchOptions = new ArrayList<>();

    private String previousSearchString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize activity
        this.activity = this;

        // Initiate Fabric Crashlytics
        Fabric.with(this, new Crashlytics());

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize loader
        loader = (LinearLayout) findViewById(R.id.loader);
        loader.setVisibility(View.GONE);

        // Initialize empty Search list
        emptySearchLayout = (TextView) findViewById(R.id.empty_search);

        // Initialize error Search list
        errorSearchLayout = (TextView) findViewById(R.id.error_search);

        // Initialize internet failure Search list
        internetFailureSearchLayout = (TextView) findViewById(R.id.internet_failure_search);

        // Recycler view - Search options list
        searchOptionsRecyclerView = (RecyclerView) findViewById(R.id.hs_recycler_view);


        // Bind listener to search field
        searchBarEditText = (EditText) findViewById(R.id.search_bar_field);
        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!(previousSearchString.equalsIgnoreCase(s.toString()))) {
                    previousSearchString = s.toString();
                    if(count > 2) {

                        // Fetch search results via API
                        fetchSearchList(getApplicationContext(), s);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Set on enter key pressed listener on search box
        searchBarEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if(searchBarEditText.getText().toString().length() > 2) {
                        goToProblemDetail(getApplicationContext(), searchBarEditText.getText().toString(), null);
                        return true;
                    }
                }
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                    finish();
            }

        return true;
    }



    /**
     * Function to fetch search list options via API
     * @param context
     * @param string
     */
    public static void fetchSearchList(final Context context, CharSequence string) {

        // Set Initial State
        loader.setVisibility(View.VISIBLE);
        emptySearchLayout.setVisibility(View.GONE);
        errorSearchLayout.setVisibility(View.GONE);
        internetFailureSearchLayout.setVisibility(View.GONE);
        searchOptionsRecyclerView.setVisibility(View.GONE);

        // Set URL
        final String url = String.format(ServiceUrls.KEY_HEALTH_SEARCH, string.toString().replace(" ", "%20"));

        // Set Headers
        final HashMap<String, String> headers = new HashMap<>();
        headers.put(StaticConstants.KEY_CONTENT_TYPE, StaticConstants.KEY_URLENCODED);
        headers.put(StaticConstants.KEY_HOST, StaticConstants.KEY_HOSTNAME);
        headers.put(StaticConstants.KEY_API_KEY, BuildProperties.azureSearchKey);


        if (Validation.isConnected(context)) {

            // API Call
            APICallService.GetAPICall(activity, context, url, null, headers, new APIInterface() {
                @Override
                public void onSuccess(JSONObject response) {

                    try {

                        if (response.has(StringConstants.KEY_VALUES)
                                && response.getJSONArray(StringConstants.KEY_VALUES).length() > 0) {

                            JSONArray healthSearchOptionArray = response.getJSONArray(StringConstants.KEY_VALUES);

                            // Clear search options
                            searchOptions.clear();

                            for (int i = 0; i < healthSearchOptionArray.length(); i++) {

                                JSONObject healthSearchObject = healthSearchOptionArray.getJSONObject(i);
                                String name = healthSearchObject.getString(StringConstants.KEY_SEARCH_LIST_NAME);

                                // Trim Name
                                if (name.length() > 30) {
                                    name = name.substring(0, 27).concat(context.getString(R.string.ellipsize));
                                }

                                HealthEducation healthEducation = new HealthEducation(
                                        healthSearchObject.getString(StringConstants.KEY_SEARCH_LIST_ID),
                                        name,
                                        healthSearchObject.getString(StringConstants.KEY_SEARCH_LIST_CATEGORY),
                                        healthSearchObject.getString(StringConstants.KEY_SEARCH_LIST_HYPERLINK)
                                );
                                searchOptions.add(healthEducation);
                            }

                            // Set adapter
                            listItemSearchOptionBindingAdapter =
                                    new Adapter<>(searchOptions, R.layout.list_item_search_option,
                                            StaticConstants.LIST_SEARCH_ADAPTER);

                            searchOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            searchOptionsRecyclerView.setAdapter(listItemSearchOptionBindingAdapter);

                            // Show Content
                            loader.setVisibility(View.GONE);
                            searchOptionsRecyclerView.setVisibility(View.VISIBLE);

                        } else {

                            // No result found
                            loader.setVisibility(View.GONE);
                            emptySearchLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        errorSearchLayout.setVisibility(View.VISIBLE);

                        // Log error
                        Crashlytics.logException(e);

                    } catch (Exception e) {

                        // Show Content
                        loader.setVisibility(View.GONE);
                        errorSearchLayout.setVisibility(View.VISIBLE);

                        // Log error
                        Crashlytics.logException(e);
                    }
                }

                @Override
                public void onError(VolleyError error) {

                    // Show Content
                    loader.setVisibility(View.GONE);
                    errorSearchLayout.setVisibility(View.VISIBLE);
                }
            });

        } else {

            // Show Content
            loader.setVisibility(View.GONE);
            internetFailureSearchLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Function to navigate to Webview
     * @param context
     * @param problem
     */
    public static void goToProblemDetail(Context context, String problem, String hyperlink) {

        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(StringConstants.KEY_HEALTH_SEARCH_PROBLEM_NAME, problem);
        intent.putExtra(StringConstants.KEY_SEARCH_LIST_HYPERLINK, hyperlink);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
