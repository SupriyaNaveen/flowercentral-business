package com.flowercentral.flowercentralbusiness.volley;

import java.util.Map;

public interface HttpResponseListener<T> {

    void onSuccess(int statusCode, Map<String, String> headers, T response);

    void onError(ErrorData error);
}
