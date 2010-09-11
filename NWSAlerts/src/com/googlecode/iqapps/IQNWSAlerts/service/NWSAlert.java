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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.googlecode.iqapps.BoundingBox;
import com.googlecode.iqapps.CAPStructure;
import com.googlecode.iqapps.EventDispatcher;
import com.googlecode.iqapps.EventItem;
import com.googlecode.iqapps.GeometryChecks;
import com.googlecode.iqapps.Logger;
import com.googlecode.iqapps.Point2D;
import com.googlecode.iqapps.IQNWSAlerts.CtyCorrelationDB;
import com.googlecode.iqapps.IQNWSAlerts.EventContainer;
import com.googlecode.iqapps.IQNWSAlerts.PreferenceHelper;
import com.googlecode.iqapps.IQNWSAlerts.R;
import com.googlecode.iqapps.IQNWSAlerts.EventContainer.Events;
import com.googlecode.iqapps.IQNWSAlerts.UI.NWSAlertUI;

public class NWSAlert extends Service implements Runnable {
	private static Logger logger = Logger.getLogger("NWSAlert");
	private static NWSAlert me = null;
	private static int MAX_DISTANCE = 1000;
	private static NotificationManager mNotificationManager = null;
	private static LocationManager locationManager = null;
	private static boolean enabled = false;
	private static boolean running = false;
	private static long lastUpdate = 0;
	private int interval = 10;
	private Timer timer = new Timer();
	static HashMap<String, CAPStructure> pertinentAlerts;
	public static EventDispatcher[] events;

	static PreferenceHelper properties;
	static Context mCtx = null;
	CtyCorrelationDB countyDB = null;
	Point2D.Double loc = null;
	public Handler mHandler;

	public NWSAlert(Context ctx) {
		logger.trace("In NWSAlert constructor");
		if (mCtx == null)
			mCtx = ctx;
		if (me == null) {
			logger.debug("First instantiation. Setting me to this.");
			me = this;
		} else
			logger.debug("Subsequent instantiation.");

		setup();
	}

	static public NWSAlert nwsAlertFactory(Context ctx) {
		logger.trace("In nwsAlertFactory.");

		if (me == null) {
			logger.trace("Creating new alert.");
			me = new NWSAlert(ctx);
		}
		return me;
	}

	public void onCreate() {
		super.onCreate();
		logger.trace("In onCreate.");
		if (mCtx == null) {
			try {
				// Someone said using this was a bad idea...
				// mCtx = getApplicationContext();
				mCtx = this;
			} catch (NullPointerException e) {
			}
		}
		properties = new PreferenceHelper(mCtx);

		// if (locationManager == null) {
		// logger.trace("Going into getSystemService.");
		// locationManager = (LocationManager) mCtx
		// .getSystemService(Context.LOCATION_SERVICE);
		// logger.trace("Back from getSystemService.");
		// }

		setup();
		Toast.makeText(mCtx, "Service started...", Toast.LENGTH_LONG).show();
	}

	public double findMinRadius(Point2D.Double location) {
		logger.trace("In findMinRadius.");
		double radius = 5;
		boolean keepGoing = true;

		while (keepGoing) {
			BoundingBox bbox = new BoundingBox(location, radius);
			logger.trace("getFIPS.  radius: " + radius);
			Vector<String> fips = countyDB.getFIPS(bbox);
			if (fips != null && fips.size() > 0) {
				keepGoing = false;
			} else
				radius = radius + 5;
			if (radius >= MAX_DISTANCE)
				keepGoing = false;
		}

		logger.debug("radius = " + radius);
		return radius;
	}

	public Vector<String> getFIPSCoverage(Point2D.Double loc) {
		logger.trace("In getFIPSCoverage.");
		double radius = findMinRadius(loc);

		BoundingBox bbox = new BoundingBox();
		bbox.setBox(loc, radius);

		Vector<String> fips = countyDB.getFIPS(bbox);

		for (String string : fips) {
			logger.debug("FIPS: " + string);
		}
		return fips;
	}

	public String getStateForFIPS(String fipsLoc) {
		logger.trace("In getStateForFIPS.");

		String state = countyDB.getState(fipsLoc);

		return state;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void startservice() {
		logger.trace("In startservice.");
		enabled = true;
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				logger.trace("In TimerTask run.");
				// if (NWSAlert.enabled) {
				// NWSAlert.running = true;
				// } else {
				// NWSAlert.running = false;
				// System.exit(0);
				// }
				//
				// logger.trace("Preparing looper.");
				// Looper.prepare();
				//
				// logger.trace("Registering handler.");
				// mHandler = new Handler() {
				// public void handleMessage(Message msg) {
				// logger.trace("Received: " + msg.toString());
				// }
				// };
				//
				// logger.trace("Running looper.loop");
				// Looper.loop();
				doCheck();
			}
		}, 0, interval * 60 * 1000);
	}

	@Override
	public void run() {
		logger.trace("In NWSAlert run.");

		logger.trace("Preparing looper.");
		Looper.prepare();

		logger.trace("Registering handler.");
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				logger.trace("Received: " + msg.toString());
			}
		};

		logger.trace("Running looper.loop");
		Looper.loop();
	}

	public void stopservice() {
		logger.trace("In stopservice.");
		running = false;
		enabled = false;
		if (timer != null) {
			timer.cancel();
		}
		stopSelf();
	}

	public static HashMap<String, CAPStructure> getPertinentAlerts() {
		return new HashMap<String, CAPStructure>(pertinentAlerts);
	}

	public static long getLastUpdate() {
		return lastUpdate;
	}

	static void start(Context ctx) {
		logger.trace("In start.");
		enabled = true;
		NWSAlert service = new NWSAlert(ctx);
		if (!NWSAlert.running)
			service.startservice();
		else
			logger.trace("Not starting service, already running.");
	}

	private void setup() {
		logger.trace("In setup.");
		UnpackDatabase.doUnpack(mCtx);
		countyDB = new CtyCorrelationDB(mCtx);

		logger.debug("Creating event dispatcher.");
		events = new EventDispatcher[EventContainer.Events.values().length];
		for (Events evt : Events.values()) {
			events[evt.ordinal()] = new EventDispatcher();
		}

		try {
			if (mCtx == null) {
				// Someone said using this was a bad idea...
				// mCtx = getApplicationContext();
				mCtx = this;
			}
		} catch (NullPointerException e) {
		}

		try {
			if (properties == null)
				properties = new PreferenceHelper(mCtx);
		} catch (NullPointerException e) {
		}

		// Check to see if the monitoring process should start on boot.
		if (properties.getStartAtBoot()) {
			BroadcastReceiver receiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if (!NWSAlert.running) {
						setup();
						start(context);
						startservice();
					}
				}
			};
			IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
			mCtx.registerReceiver(receiver, filter);
		}

		interval = properties.getUpdateInterval();

		// Get the notification manager service.
		logger.trace("Retrieving the system notification manager service.");
		mNotificationManager = (NotificationManager) mCtx
				.getSystemService(NOTIFICATION_SERVICE);

		if (locationManager == null) {
			logger.trace("Going into getSystemService.");
			locationManager = (LocationManager) mCtx
					.getSystemService(Context.LOCATION_SERVICE);
			logger.trace("Back from getSystemService.");
			logger.debug("Location providers: "
					+ locationManager.getAllProviders());

			Criteria criteria = new Criteria();
			String bestProvider = locationManager.getBestProvider(criteria,
					false);
			logger.debug("Best provider: " + bestProvider);

			// Define a listener that responds to location updates
			LocationListener locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					// Called when a new location is found by the network
					// location provider.
					loc = new Point2D.Double(location.getLatitude(), location
							.getLongitude());
					logger.debug("Updated Location: " + loc.x + "/" + loc.y);
					doCheck();
				}

				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}

				public void onProviderEnabled(String provider) {
				}

				public void onProviderDisabled(String provider) {
				}
			};

			// Register the listener with the Location Manager to receive
			// location updates
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, interval * 30 * 1000,
					1000, locationListener);
			logger.debug("Registered listener.");
		}

		if (pertinentAlerts == null) {
			pertinentAlerts = new HashMap<String, CAPStructure>();
		}
	}

	private void doCheck() {
		logger.trace("In doCheck.");

		List<String> providers = locationManager.getProviders(false);
		logger.trace("Providers: " + providers);
		for (String provider : providers) {
			Location location = locationManager.getLastKnownLocation(provider);
			try {
				loc = new Point2D.Double(location.getLatitude(), location
						.getLongitude());
				logger.trace("Back from getLat/Lon.");
			} catch (NullPointerException e) {
				// TODO: Do something more appropriate...
				logger.debug("getLastKnownLocation " + provider
						+ " threw NPE. Trying another.");
			}
		}
		if (loc == null)
			return;

		// TODO: Remove this...
		if (properties.getReadFromFile())
			loc = new Point2D.Double(28.603212, -81.322861);

		logger.debug("Location: " + loc.x + " / " + loc.y);
		Vector<String> fipsCodes = getFIPSCoverage(loc);
		logger.debug("FIPS: " + fipsCodes);

		String state = new String(countyDB.getState(fipsCodes.get(0)));
		logger.debug("Retrieving alerts for state: " + state);
		String alertIndex = RetrieveAlerts.GetAlertIndex(state);
		// String alertIndex = RetrieveAlerts.GetAlertIndex();

		ParseAlertIndex alertParser = new ParseAlertIndex();
		Vector<CAPStructure> allAlerts = alertParser.processIndex(alertIndex);

		if (allAlerts == null) {
			logger.error("Alert Document is null.");
			return;
		}

		for (CAPStructure alertEntry : allAlerts) {
			for (String fipsCode : fipsCodes) {
				if (alertEntry.getFips().contains(fipsCode)
						|| alertEntry.getFips().contains("0" + fipsCode)) {
					logger.trace("Executing fillCapDetail for: "
							+ alertEntry.getNWSID());
					alertParser.fillCapDetail(alertEntry);

					processAlert(alertEntry);
				}
			}
		}

		processExpired();
		lastUpdate = System.currentTimeMillis();
		logger.debug("Set lastUpdate to: " + lastUpdate);
		logger.debug(new Date(lastUpdate).toString());
		logger.debug("Firing UPDATE_AVAILABLE event.");
		events[Events.UPDATE_AVAILABLE.ordinal()]
				.fireEvent(new EventItem(this));
	}

	/**
	 * @param alertEntry
	 */
	private void processAlert(CAPStructure alertEntry) {
		Vector<String> polygons = alertEntry.getPolygon();
		if (GeometryChecks.checkPolygon(loc, polygons)) {
			if (!pertinentAlerts.containsKey(alertEntry.getNWSID())) {
				Calendar effective = alertEntry.getEffective();
				Calendar expires = alertEntry.getEffective();
				Calendar now = GregorianCalendar.getInstance();
				now.setTimeInMillis(System.currentTimeMillis());
				// If now is equal to or after effective...
				// logger.debug("Now      : " + );
				logger.debug("Effective: "
						+ new Date(effective.getTimeInMillis()).toString());
				logger.debug("Expires  : "
						+ new Date(expires.getTimeInMillis()).toString());
				logger.debug("Now/Eff  : " + now.compareTo(effective));
				logger.debug("Now/Exp  : " + now.compareTo(expires));
				if (now.compareTo(effective) >= 0
						&& now.compareTo(expires) >= 0) {
					pertinentAlerts.put(alertEntry.getNWSID(), alertEntry);
					logger.debug(alertEntry.toString());
					logger.debug("- - - -");
					showNotification(alertEntry);
					makeAlarm();
				}
			}
		}
	}

	/**
	 * @param alertEntry
	 */
	private void processExpired() {
		Calendar now = GregorianCalendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		for (String alert : pertinentAlerts.keySet()) {
			Calendar expiration = pertinentAlerts.get(alert).getExpires();
			// If now is equal to or after effective...
			if (now.compareTo(expiration) >= 0) {
				logger.debug("Removing " + alert + " from alerts.");
				pertinentAlerts.remove(alert);
			}
		}
	}

	private void makeAlarm() {
		logger.trace("In makeAlarm.");
		String tone = properties.getAudioAlert();
		if (tone != null && tone.length() > 0) {
			Uri ringUri = Uri.parse(tone);
			Ringtone ring = RingtoneManager.getRingtone(mCtx, ringUri);
			if (!ring.isPlaying())
				ring.play();
		} else
			logger.debug("Alert tone empty/null.");
	}

	private void showNotification(CAPStructure alert) {
		logger.trace("In showNotification.");
		// int APP_ID = 0;

		logger.trace("Creating new intent.");
		Intent intent = new Intent(mCtx, NWSAlertUI.class);

		logger.trace("Creating new notification.");
		Notification notification = new Notification(
				R.drawable.ic_notify_alert, "Weather Alert!", alert
						.getEffective().getTimeInMillis());

		logger.trace("Setting latest event information.");
		notification.setLatestEventInfo(mCtx, "NWSAlerts", alert.getTitle(),
				PendingIntent.getActivity(mCtx, alert.hashCode(), intent,
						PendingIntent.FLAG_CANCEL_CURRENT));

		logger.trace("Notifying the notification manager.");
		mNotificationManager.notify(alert.hashCode(), notification);
	}
}
