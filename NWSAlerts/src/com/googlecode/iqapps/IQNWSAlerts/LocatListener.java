package com.googlecode.iqapps.IQNWSAlerts;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.googlecode.iqapps.Logger;
import com.googlecode.iqapps.Point2D;
import com.googlecode.iqapps.IQNWSAlerts.service.NWSAlert;

public class LocatListener implements LocationListener {
	private final static Logger logger = Logger.getLogger("LocatListener");
	private static boolean eventsRegistered = false;
	private static LocationManager locationManager = null;
	private Point2D.Double loc;
	private NWSAlert serviceBinder;
	private int interval = 10;
	static PreferenceHelper properties;
	private Context mCtx = null;

	public LocatListener(Context myCtx) {
		// if (eventsRegistered)
		// return;
		mCtx = myCtx;

		properties = new PreferenceHelper(mCtx);
		interval = properties.getUpdateInterval();

		if (locationManager == null) {
			logger.trace("Copying location manager.");
			locationManager = (LocationManager) mCtx
					.getSystemService(Context.LOCATION_SERVICE);

			logger.debug("Location providers: "
					+ locationManager.getAllProviders());

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setSpeedRequired(false);
			criteria.setCostAllowed(true);
			String bestProvider = locationManager.getBestProvider(criteria,
					true);
			logger.debug("Best provider: " + bestProvider);
			locationManager.requestLocationUpdates(bestProvider,
					interval * 60 * 1000, 1000, this);
			logger.debug("Registered locationListener.");
		}

		Toast.makeText(mCtx, "Service started...", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Called when a new location is found by the
		// location provider.
		Point2D.Double tempLoc = new Point2D.Double(location.getLatitude(),
				location.getLongitude());
		if (loc != null && (tempLoc.x != loc.x || tempLoc.y != loc.y)) {
			loc.setLocation(tempLoc.x, tempLoc.y);
			logger.debug("Location update: " + loc.x + ", " + loc.y);
			serviceBinder.locationChanged(loc);
		}
		if (loc == null) {
			loc = new Point2D.Double(tempLoc.x, tempLoc.y);
			logger.debug("Location update: " + loc.x + ", " + loc.y);
			serviceBinder.locationChanged(loc);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	public void unregister() {
		locationManager.removeUpdates(this);
		Toast.makeText(mCtx, "Stopped listener...", Toast.LENGTH_LONG).show();
	}
}
