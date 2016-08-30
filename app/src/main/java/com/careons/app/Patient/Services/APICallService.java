package com.careons.app.Patient.Services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.careons.app.Patient.Commons.StringConstants;
import com.careons.app.Shared.Interfaces.APIInterface;
import com.careons.app.Shared.Utils.ErrorHandlers;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class APICallService {

    /**
     * Volley Service to GET with REST APIs
     * @param context
     * @param url
     * @param data
     * @param headers
     * @param callback
     */
    public static void GetAPICall(final Activity activity,
                                  final Context context,
                                  final String url,
                                  final HashMap<String, String> data,
                                  final HashMap<String, String> headers,
                                  final APIInterface callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof AuthFailureError) {

                            ErrorHandlers.handleAPIAuthFailure(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof ServerError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof NetworkError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof ParseError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onSuccess(null);

                        } else {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);
                        }
                    }
                })  {

            // set body
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = data;
                return params;
            }

            // set headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = headers;
                return params;
            }
        };

        // Push request in queue
        APIInstanceRequestService.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    /**
     * Volley Service to POST request with REST APIs
     * @param context
     * @param url
     * @param data
     * @param headers
     * @param callback
     */
    public static void PostAPICall(final Activity activity,
                                   final Context context,
                                   final String url,
                                   final HashMap<String, Object> data,
                                   final HashMap<String, String> headers,
                                   final APIInterface callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof AuthFailureError) {

                            ErrorHandlers.handleAPIAuthFailure(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof ServerError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof NetworkError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof ParseError) {

                            callback.onSuccess(null);

                        } else {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);
                        }
                    }
                })  {

            // set body
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                return params;
            }

            // set headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = headers;
                return params;
            }
        };

        // Push request in queue
        APIInstanceRequestService.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    /**
     * Volley Service to DELETE request with REST APIs
     * @param context
     * @param url
     * @param headers
     * @param callback
     */
    public static void PutAPICall(final Activity activity,
                                  final Context context,
                                  final String url,
                                  final HashMap<String, Object> data,
                                  final HashMap<String, String> headers,
                                  final APIInterface callback) {


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof AuthFailureError) {

                            ErrorHandlers.handleAPIAuthFailure(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof ServerError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof NetworkError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof ParseError) {

                            callback.onSuccess(null);

                        } else {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);
                        }
                    }
                })  {

            // set body
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            // set headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = headers;
                return params;
            }
        };

        // Push request in queue
        APIInstanceRequestService.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }



    /**
     * Volley Service to DELETE request with REST APIs
     * @param context
     * @param url
     * @param headers
     * @param callback
     */
    public static void DeleteAPICall(final Activity activity,
                                     final Context context,
                                     final String url,
                                     final HashMap<String, String> headers,
                                     final APIInterface callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof AuthFailureError) {

                            ErrorHandlers.handleAPIAuthFailure(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof ServerError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof NetworkError) {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);

                        } else if (error instanceof ParseError) {

                            callback.onSuccess(null);

                        } else {

                            ErrorHandlers.handleAPIError(activity);
                            Crashlytics.logException(error);
                            callback.onError(error);
                        }
                    }
                })  {

            // set body
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            // set headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = headers;
                return params;
            }
        };

        // Push request in queue
        APIInstanceRequestService.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }



    /**
     * Volley Service to Login request with REST APIs
     * @param context
     * @param url
     * @param data
     * @param headers
     * @param callback
     */
    public static void TokenAPICall(final Context context,
                                    final String url,
                                    final HashMap<String, Object> data,
                                    final HashMap<String, String> headers,
                                    final APIInterface callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                            callback.onError(error);

                        } else if (error instanceof AuthFailureError) {

                            callback.onError(error);

                        } else if (error instanceof ServerError) {

                            callback.onError(error);

                        } else if (error instanceof NetworkError) {

                            callback.onError(error);

                        } else if (error instanceof ParseError) {

                            callback.onSuccess(null);

                        } else {

                            callback.onError(error);
                        }
                    }
                })  {

            // set body


            @Override
            public byte[] getBody() {

                String httpPostBody="grant_type=password&username="
                        .concat(data.get(StringConstants.KEY_USERNAME).toString())
                        .concat("&password=")
                        .concat(data.get(StringConstants.KEY_PASSWORD).toString());

                // usually you'd have a field with some values you'd want to escape,
                // you need to do it yourself if overriding getBody. here's how you do it

                try {

                    httpPostBody=httpPostBody+"&randomFieldFilledWithAwkwardCharacters="+ URLEncoder.encode("{{%stuffToBe Escaped/","UTF-8");

                } catch (UnsupportedEncodingException exception) {

                    Log.e("ERROR", "exception", exception);
                    // return null and don't pass any POST string if you encounter encoding error
                    return null;
                }

                return httpPostBody.getBytes();
            }

            // set headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        // Push request in queue
        APIInstanceRequestService.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
