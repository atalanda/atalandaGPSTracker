package com.atalanda.gpstracker.asyncTasks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.atalanda.gpstracker.LocationCache;

import android.app.ApplicationErrorReport.BatteryInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;
import android.util.Pair;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

public class LocationUploadTask extends AsyncTask<LocationCache, Integer, Boolean> {

	@Override
	protected Boolean doInBackground(LocationCache... cacheEntries) {
		Log.d("Atalanda", "uploading "+cacheEntries.length+" locations");
		for (int i = 0; i < cacheEntries.length; i++) {
			LocationCache locationCache = cacheEntries[i];
			Location location = locationCache.getLocation();

			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			HttpClient httpclient = new DefaultHttpClient();

      SchemeRegistry registry = new SchemeRegistry();
      SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
      socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
      registry.register(new Scheme("https", socketFactory, 443));
      SingleClientConnManager mgr = new SingleClientConnManager(httpclient.getParams(), registry);
      DefaultHttpClient httpClient = new DefaultHttpClient(mgr, httpclient.getParams());

      // Set verifier
      HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

	    HttpPost httppost = new HttpPost(locationCache.getUrl());
			httppost.setHeader("Content-type", "application/json");

	    JSONObject body;
			JSONObject additionalHeader;
			try {
					body = new JSONObject(locationCache.getParameters());
			    body.put("verticalAccuracy", null);
			    body.put("horizontalAccuracy", location.getAccuracy());
			    body.put("heading", location.getBearing());
					body.put("latitude", location.getLatitude());
					body.put("longitude", location.getLongitude());
			    body.put("velocity", location.getSpeed());
			    body.put("batteryLevel", locationCache.getBatteryLevel());
			    body.put("timestamp", locationCache.getTimestamp());

					additionalHeader = new JSONObject(body.remove("additionalHeader").toString());

			    httppost.setEntity(new StringEntity(body.toString()));
					httppost.addHeader(additionalHeader.get("key").toString(), additionalHeader.get("value").toString());
			} catch (JSONException | UnsupportedEncodingException e) {
				Log.e("error encoding JSON", e.getMessage());
				continue;
			}

		    Log.d("uploading position", "to "+locationCache.getUrl());

		    try {
		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        Log.d("upload response code", response.getStatusLine().toString());
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    	Log.e("upload failed", e.getLocalizedMessage());
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    	Log.e("upload failed", e.getLocalizedMessage());
		    }

		}

		return true;
	}
}
