/*
 * Copyright 2010 NWSAlerts authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.iqapps.IQNWSAlerts.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.googlecode.iqapps.CAPStructure;
import com.googlecode.iqapps.Logger;
import com.googlecode.iqapps.Point2D;
import com.googlecode.iqapps.IQNWSAlerts.CtyCorrelationDB;
import com.googlecode.iqapps.IQNWSAlerts.GeneralDbAdapter;
import com.googlecode.iqapps.IQNWSAlerts.PreferenceHelper;
import com.googlecode.iqapps.IQNWSAlerts.R;
import com.googlecode.iqapps.IQNWSAlerts.UI.NWSAlertUI;

public class NWSAlert extends Service {
	private final static Logger logger = Logger.getLogger("NWSAlert");
	/** Keeps track of all current registered clients. */
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	/** Holds last value set by a client. */
	int mValue = 0;

	/**
	 * Command to the service to register a client, receiving callbacks from the
	 * service. The Message's replyTo field must be a Messenger of the client
	 * where callbacks should be sent.
	 */
	public static final int MSG_REGISTER_CLIENT = 1;

	/**
	 * Command to the service to unregister a client, ot stop receiving
	 * callbacks from the service. The Message's replyTo field must be a
	 * Messenger of the client as previously given with MSG_REGISTER_CLIENT.
	 */
	public static final int MSG_UNREGISTER_CLIENT = 2;

	/**
	 * Command to service to set a new value. This can be sent to the service to
	 * supply a new value, and will be sent by the service to any registered
	 * clients with the new value.
	 */
	// public static final int MSG_SET_VALUE = 3;

	/**
	 * Command to client to notify that an update is available. This will be
	 * sent by the service to any registered clients with the new value.
	 */
	public static final int MSG_UPDATE_AVAILABLE = 4;

	/**
	 * Command from client to notify service that is should stop.
	 */
	public static final int MSG_STOP_SERVICE = 5;

	private static boolean enabled = false;
	private static int running = 0;
	private static long lastUpdate = 0;
	private AlertRetriever retriever;
	private NotificationManager mNM;
	private int interval = 10;
	private Timer timer;
	private LocListener locationListener;
	private HashMap<String, Integer> certainty = new HashMap<String, Integer>();
	private HashMap<String, Integer> severity = new HashMap<String, Integer>();
	private HashMap<String, Integer> urgency = new HashMap<String, Integer>();
	// private final IBinder binder = new AlertBinder();
	// private EventDispatcher[] events = null;
	// private static LocationManager locationManager = null;

	static PreferenceHelper properties;
	Context mCtx;
	CtyCorrelationDB countyDB;
	GeneralDbAdapter capDB;
	Point2D.Double loc;

	@Override
	public void onCreate() {
		super.onCreate();

		logger.trace("In setup");
		mCtx = this;

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Display a notification about us starting.
		showNotification();

		// Set up variables
		timer = new Timer("NWSAlert");
		retriever = new AlertRetriever(mCtx);
		properties = new PreferenceHelper(mCtx);

		// Open databases
		countyDB = new CtyCorrelationDB(mCtx);
		capDB = new GeneralDbAdapter(mCtx);
		capDB.open();

		// Setup local hash maps with values from resource files.
		Resources res = mCtx.getResources();
		String[] array = res.getStringArray(R.array.certainty_levels);
		for (int i = 0; i < array.length; i++) {
			certainty.put(array[i], i);
		}
		array = res.getStringArray(R.array.severity_levels);
		for (int i = 0; i < array.length; i++) {
			severity.put(array[i], i);
		}
		array = res.getStringArray(R.array.urgency_levels);
		for (int i = 0; i < array.length; i++) {
			urgency.put(array[i], i);
		}

		// Check to see if the monitoring process should start on boot.
		if (properties.getStartAtBoot() && !enabled) {
			BroadcastReceiver receiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if (NWSAlert.running == 0) {
						Intent serviceIntent = new Intent();
						serviceIntent.setClass(getApplicationContext(),
								NWSAlert.class);
						startService(serviceIntent);
					}
				}
			};
			IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
			mCtx.registerReceiver(receiver, filter);
		}
	}

	// This is the old onStart method that will be called on the pre-2.0
	// platform.  On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId) {
	    handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    handleCommand(intent);
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	public void handleCommand(Intent intent) {
		logger.trace("In onStartCommand.");

		running++;
		if (running > 1)
			return;
		enabled = true;
		timer.cancel();
		try {
			timer = new Timer("NWSAlert");
			timer.scheduleAtFixedRate(runOnce, 0, interval * 60 * 1000);
		} catch (IllegalStateException e) {
		}

		// Register with the Location Manager to receive location updates
		locationListener = new LocListener();
	}

	public void onStop() {
		logger.trace("In onStop.");

		running = 0;
		timer.cancel();

		// Un-register the listener to stop receiving location updates
		locationListener.unregister();
		logger.debug("De-registered location listener.");

		stopService();
		Toast.makeText(mCtx, "Service stopped...", Toast.LENGTH_LONG).show();
	}

	public void onDestroy() {
		logger.trace("In onDestroy.");

		running = 0;
		enabled = false;
		if (timer != null)
			timer.cancel();

		countyDB.close();
		capDB.close();

		// Cancel the persistent notification.
		mNM.cancel(R.string.app_name);

		// Tell the user we stopped.
		Toast.makeText(this, "NWSAlert service stopped", Toast.LENGTH_SHORT)
				.show();

		logger.trace("Calling super.onDestroy.");
		super.onDestroy();
	}

	private TimerTask runOnce = new TimerTask() {
		public void run() {
			logger.trace("In TimerTask run.");
			doCheck();
		}
	};

	public void stopService() {
		logger.trace("In stopservice.");
		running--;
		if (running == 0) {
			enabled = false;
			if (timer != null) {
				timer.cancel();
			}
			stopSelf();
		}
	}

	public double getLat() {
		logger.trace("In getLat.");

		try {
			return loc.x;
		} catch (NullPointerException e) {
			return 0.0;
		}
	}

	public double getLon() {
		logger.trace("In getLon.");

		try {
			return loc.y;
		} catch (NullPointerException e) {
			return 0.0;
		}
	}

	public void locationChanged(Point2D.Double tmpLoc) {
		loc = new Point2D.Double(tmpLoc.x, tmpLoc.y);
		doCheck();
	}

	public static long getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Main processing piece. Need more documentation.
	 */
	private void doCheck() {
		logger.trace("In doCheck.");

		if (loc == null) {
			logger.debug("doCheck: loc is null, returning.");
			return;
		}

		logger.debug("Location: " + loc.x + " / " + loc.y);
		Vector<String> fipsCodes = null;
		try {
			fipsCodes = retriever.getFIPSCoverage(loc);
		} catch (SQLiteException e) {
			return;
		} catch (NullPointerException e) {
			return;
		}
		logger.debug("FIPS: " + fipsCodes);

		Vector<CAPStructure> allAlerts = null;
		ParseAlertIndex alertParser = null;
		try {
			String state = new String(countyDB.getState(fipsCodes.get(0)));
			logger.debug("Retrieving alerts for state: " + state);
			String alertIndex = RetrieveAlerts.GetAlertIndex(state);

			alertParser = new ParseAlertIndex();
			allAlerts = alertParser.processIndex(alertIndex);
		} catch (SQLiteException e) {
			return;
		} catch (NullPointerException e) {
			return;
		}

		if (allAlerts == null) {
			logger.error("Alert Document is null.");
			return;
		}

		// TODO: Consider whether and/or how to process message types
		int minCertainty = certainty.get(properties.getMinCertainty());
		int minSeverity = severity.get(properties.getMinSeverity());
		int minUrgency = urgency.get(properties.getMinUrgency());
		for (CAPStructure alertEntry : allAlerts) {

			// Skip further processing if the alert's characteristics are less
			// than thresholds.
			try {
				if (certainty.get(alertEntry.getCertainty()) < minCertainty) {
					logger.info("Alert certainty under minimum: "
							+ alertEntry.getCertainty());
					continue;
				}
			} catch (NullPointerException e) {
				logger.info("Skipping certainty check, NULL");
			}
			try {
				if (severity.get(alertEntry.getSeverity()) < minSeverity) {
					logger.info("Alert severity under minimum: "
							+ alertEntry.getSeverity());
					continue;
				}
			} catch (NullPointerException e) {
				logger.info("Skipping severity check, NULL");
			}
			try {
				if (urgency.get(alertEntry.getUrgency()) < minUrgency) {
					logger.info("Alert urgency under minimum: "
							+ alertEntry.getUrgency());
					continue;
				}
			} catch (NullPointerException e) {
				logger.info("Skipping urgency check, NULL");
			}

			for (String fipsCode : fipsCodes) {
				if (alertEntry.getFips().contains(fipsCode)
						|| alertEntry.getFips().contains("0" + fipsCode)) {
					logger.trace("Executing fillCapDetail for: "
							+ alertEntry.getNWSID());
					// Retrieve details if the event is null or the status is
					// Update.
					if (alertEntry.getEvent() == null
							|| alertEntry.getStatus().compareToIgnoreCase(
									"Update") == 0)
						alertParser.fillCapDetail(alertEntry);

					retriever.processAlert(alertEntry);
					// Don't re-process alert for another FIPS code.
					break;
				}
			}
		}

		retriever.processExpired();
		lastUpdate = System.currentTimeMillis();
		logger.debug("Set lastUpdate to: " + lastUpdate);
		logger.debug(new Date(lastUpdate).toString());
		logger.debug("Firing UPDATE_AVAILABLE event.");

		Message upd = Message.obtain(null, MSG_UPDATE_AVAILABLE);
		try {
			mMessenger.send(upd);
		} catch (RemoteException e) {
			// Probably because no clients are registered.
		}
	}

	private class LocListener implements LocationListener {
		private final Logger logger = Logger.getLogger("NWSAlert:LocListener");
		private LocationManager locationManager;
		private Point2D.Double loc;
		private int interval = 10;

		public LocListener() {
			logger.trace("In constructor");
			interval = properties.getUpdateInterval();

			if (locationManager == null) {
				logger.trace("Copying location manager.");
				locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

			Toast.makeText(mCtx, "Location listener registered...",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLocationChanged(Location location) {
			logger.trace("In onLocationChanged");
			// Called when a new location is found by the location provider.
			Point2D.Double tempLoc = new Point2D.Double(location.getLatitude(),
					location.getLongitude());
			if (loc != null && (tempLoc.x != loc.x || tempLoc.y != loc.y)) {
				loc.setLocation(tempLoc.x, tempLoc.y);
				logger.debug("Location update: " + loc.x + ", " + loc.y);
				locationChanged(loc);
			}
			if (loc == null) {
				loc = new Point2D.Double(tempLoc.x, tempLoc.y);
				logger.debug("Location update: " + loc.x + ", " + loc.y);
				locationChanged(loc);
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
			logger.trace("In unRegister");
			locationManager.removeUpdates(this);
			Toast.makeText(mCtx, "Stopped listener...", Toast.LENGTH_LONG)
					.show();
		}
	}

	/**
	 * Handler of incoming messages from clients.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				logger.debug("Handling Register client");
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				logger.debug("Handling Unregister client");
				mClients.remove(msg.replyTo);
				break;
			case MSG_UPDATE_AVAILABLE:
				logger.debug("Handling Update Available");
				for (int i = mClients.size() - 1; i >= 0; i--) {
					try {
						Message mesg = Message.obtain(null,
								MSG_UPDATE_AVAILABLE);
						Bundle data = new Bundle();
						data.putDouble("lat", loc.x);
						data.putDouble("lon", loc.y);
						mesg.setData(data);
						mClients.get(i).send(mesg);
					} catch (RemoteException e) {
						// The client is dead. Remove it from the list;
						// we are going through the list from back to front
						// so this is safe to do inside the loop.
						mClients.remove(i);
					}
				}
				break;
			case MSG_STOP_SERVICE:
				logger.debug("Handling Stop Service");
				stopService();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = getText(R.string.app_name);

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.icon_small,
				text, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, NWSAlertUI.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, getText(R.string.app_name), text,
				contentIntent);

		// Send the notification.
		// We use a string id because it is a unique number. We use it later to
		// cancel.
		mNM.notify(R.string.app_name, notification);
	}
}
