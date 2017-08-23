package com.flowercentral.flowercentralbusiness.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CustomJsonObjectRequest extends JsonObjectRequest {

    private HttpResponseListener responseListener;
    private NetworkResponse networkResponse;
    private Map<String, String> headerValues;

    @SuppressWarnings("unchecked")
    public CustomJsonObjectRequest(int method, String Url, JSONObject jsonObjectData, Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, Url, jsonObjectData, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headerValues == null) {
            return super.getHeaders();
        } else {
            return headerValues;
        }
    }

    public void appendHeaderValues(Map<String, String> headers) {
        if (headers != null) {
            if (headerValues == null) {
                headerValues = new HashMap<>();

            }
            headerValues.putAll(headers);
        }
    }

    public void setCustomResponseListener(HttpResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        networkResponse = response;
        return super.parseNetworkResponse(response);

    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void deliverResponse(JSONObject response) {
        super.deliverResponse(response);
        if (responseListener != null && networkResponse != null) {
            responseListener.onSuccess(networkResponse.statusCode, networkResponse.headers, response);
        }

    }
}
