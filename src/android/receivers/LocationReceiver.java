package com.atalanda.gpstracker.receivers;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.atalanda.gpstracker.LocationCache;
import com.atalanda.gpstracker.asyncTasks.LocationUploadTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.util.Log;
import android.util.Pair;

public class LocationReceiver extends BroadcastReceiver {

	protected static ArrayList<LocationCache> locations = new ArrayList<LocationCache>();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("Atalanda", "receive");

		String locationKey = LocationManager.KEY_LOCATION_CHANGED;

	    if (intent.hasExtra(locationKey)) {
			Location location = (Location)intent.getExtras().get(locationKey);
			String parameters = intent.getStringExtra("parameters");
			String url = intent.getStringExtra("url");
			Float batteryLevel = getBatteryLevel(context.getApplicationContext());
			locations.add(new LocationCache(location, parameters, url, batteryLevel));
			Log.d("Atalanda", "Got Location Lat: "+location.getLatitude()+" Long: "+location.getLongitude());

			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()) {
			    uploadData();
			}
	    }
	}

	private void uploadData() {
		LocationCache[] locationEntires = new LocationCache[locations.size()];
		LocationCache locationEntries[] = locations.toArray(locationEntires);
		new LocationUploadTask().execute(locationEntries);
		locations.clear();
	}

	private float getBatteryLevel(Context context) {
	    Intent batteryIntent =  context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

	    return ((float)level / (float)scale);
	}

}
