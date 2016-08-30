package com.careons.app.Shared.Interfaces;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface APIInterface {

    void onSuccess(JSONObject result);
    void onError(VolleyError error);
}
