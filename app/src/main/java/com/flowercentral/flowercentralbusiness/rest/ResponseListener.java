package com.flowercentral.flowercentralbusiness.rest;


import com.flowercentral.flowercentralbusiness.volley.ErrorData;

/**
 * Created by Ashish Upadhyay on 7/19/16.
 */
public interface ResponseListener<O> {

    public void onDataReceived(O data);

    public void onDataError(ErrorData errorData);

}
