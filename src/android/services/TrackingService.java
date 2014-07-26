package com.atalanda.gpstracker.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import com.atalanda.gpstracker.receivers.LocationReceiver;

public class TrackingService extends Service {

	protected LocationManager locationManager;
	protected PendingIntent locationReceiverPendingIntent;
	protected Intent locationIntent;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("Atalanda", "tracking service start");

		if(intent == null) {
			Log.w("Atalanda", "Started service without intent");
			return;
		}

		locationIntent = new Intent(this, LocationReceiver.class);
		locationIntent.putExtra("parameters", intent.getStringExtra("parameters"));
		locationIntent.putExtra("url", intent.getStringExtra("url"));
		locationReceiverPendingIntent = PendingIntent.getBroadcast(this, 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_FINE); // FINE tries to use GPS

	    long minimumWaitBetweenLocationUpdatesInMilliSeconds = 1000;
	    float minimumLoctaionChangeInMeters = 50;
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(minimumWaitBetweenLocationUpdatesInMilliSeconds, minimumLoctaionChangeInMeters, criteria, locationReceiverPendingIntent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("Atalanda", "stopping tracking service");
		if(locationManager == null) {
			Log.w("Atalanda", "locationManager was null");
			return;
		}
		locationManager.removeUpdates(locationReceiverPendingIntent);
	}
}
