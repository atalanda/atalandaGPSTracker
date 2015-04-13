package com.atalanda.gpstracker;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.atalanda.gpstracker.services.TrackingService;

public class atalandaGPSTracker extends CordovaPlugin {
    private Intent trackingServiceIntent;
    private String url = "";
    private String additionalHeaderKey;
    private String additionalHeaderValue;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        trackingServiceIntent = new Intent(this.cordova.getActivity(), TrackingService.class);
    }

    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
      Activity activity = this.cordova.getActivity();
      Boolean result = false;

      if(action.equals("startTracking")) {
      if(url.equals("")) {
      callbackContext.error("Tracking URL endpoint not configured. Please call configure first");
      return false;
      }
      try {
      JSONObject parameters = data.getJSONObject(0);
      trackingServiceIntent.putExtra("parameters", parameters.toString());
      trackingServiceIntent.putExtra("url", url);
      trackingServiceIntent.putExtra("additionalHeaderKey", additionalHeaderKey);
      trackingServiceIntent.putExtra("additionalHeaderValue", additionalHeaderValue);
      activity.startService(trackingServiceIntent);
          callbackContext.success();
          result = true;
    } catch (JSONException e) {
      result = false;
      callbackContext.error("Error: " + e.getMessage());
    }
      }

      if(action.equals("stopTracking")) {
        activity.stopService(trackingServiceIntent);
        callbackContext.success();
        result = true;
      }

      if(action.equals("configure")) {
        try {
          url = data.getJSONObject(0).getString("url");
          callbackContext.success();
          result = true;
        } catch (JSONException e) {
          Log.e("error configuring plugin", e.getMessage());
          callbackContext.error("Error: " + e.getMessage());
          result = false;
        }
      }

      if(action.equals("setAdditionalHeader")) {
        try {
          additionalHeaderKey = data.getJSONObject(0).getString("key");
          additionalHeaderValue = data.getJSONObject(0).getString("value");
          callbackContext.success();
          result = true;
        } catch (JSONException e) {
          Log.e("error setAdditionalHeader plugin", e.getMessage());
          callbackContext.error("Error: " + e.getMessage());
          result = false;
        }
      }

      return result;
    }
}
