/*
 * Copyright 2010 NWSAlert authors.
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
import java.util.Vector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.googlecode.iqapps.BoundingBox;
import com.googlecode.iqapps.CAPStructure;
import com.googlecode.iqapps.GeometryChecks;
import com.googlecode.iqapps.Logger;
import com.googlecode.iqapps.Point2D;
import com.googlecode.iqapps.IQNWSAlerts.CtyCorrelationDB;
import com.googlecode.iqapps.IQNWSAlerts.GeneralDbAdapter;
import com.googlecode.iqapps.IQNWSAlerts.PreferenceHelper;
import com.googlecode.iqapps.IQNWSAlerts.R;
import com.googlecode.iqapps.IQNWSAlerts.UI.NWSAlertUI;

public class AlertRetriever {
	private final static Logger logger = Logger.getLogger("AlertRetriever");
	private NotificationManager mNotificationManager = null;
	private HashMap<String, Integer> certainty = new HashMap<String, Integer>();
	private HashMap<String, Integer> severity = new HashMap<String, Integer>();
	private HashMap<String, Integer> urgency = new HashMap<String, Integer>();
	private HashMap<String, CAPStructure> pertinentAlerts;
	private final int MAX_DISTANCE = 1000;

	PreferenceHelper properties;
	static Context mCtx;
	CtyCorrelationDB countyDB;
	GeneralDbAdapter capDB;
	Point2D.Double loc;

	public AlertRetriever(Context ctx) {
		mCtx = ctx;

		try {
			if (properties == null)
				properties = new PreferenceHelper(mCtx);
		} catch (NullPointerException e) {
		}

		// Open databases
		countyDB = new CtyCorrelationDB(mCtx);
		capDB = new GeneralDbAdapter(mCtx);
		capDB.open();

		mNotificationManager = (NotificationManager) mCtx
				.getSystemService(Context.NOTIFICATION_SERVICE);

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

		pertinentAlerts = new HashMap<String, CAPStructure>();
	}

	/**
	 * 
	 */
	public double findMinRadius(Point2D.Double location) {
		logger.trace("In findMinRadius.");
		double radius = 5;
		boolean keepGoing = true;

		while (keepGoing) {
			BoundingBox bbox = new BoundingBox(location, radius);
			// logger.trace("getFIPS.  radius: " + radius);
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

	/**
	 * 
	 * @param loc
	 * @return
	 */
	public Vector<String> getFIPSCoverage(Point2D.Double loc) {
		logger.trace("In getFIPSCoverage.");
		if (countyDB == null)
			return null;

		double radius = findMinRadius(loc);

		BoundingBox bbox = new BoundingBox();
		bbox.setBox(loc, radius);

		Vector<String> fips = countyDB.getFIPS(bbox);

		try {
			for (String string : fips) {
				logger.debug("FIPS: " + string);
			}
		} catch (NullPointerException e) {
			return null;
		}
		return fips;
	}

	/**
	 * Process the alert to see if it is still effective, place it into the
	 * database, displays a notification and sounds an alert..
	 * 
	 * @param alertEntry
	 */
	public void processAlert(CAPStructure alertEntry) {
		Vector<String> polygons = alertEntry.getPolygon();
		boolean isUpdate = false;
		if (GeometryChecks.checkPolygon(loc, polygons)) {
			try {
				String status = alertEntry.getStatus();
				if (status.compareToIgnoreCase("Cancel") == 0) {
					removeAlert(alertEntry);
					return;
				}
				if (status.compareToIgnoreCase("Update") == 0)
					isUpdate = true;
			} catch (NullPointerException e) {
				logger.debug("processAlert: " + e.toString());
			}
			if (!pertinentAlerts.containsKey(alertEntry.getNWSID()) || isUpdate) {
				Calendar effective = alertEntry.getEffective();
				Calendar expires = alertEntry.getExpires();
				Calendar now = GregorianCalendar.getInstance();
				now.setTimeInMillis(System.currentTimeMillis());
				// logger.debug("Now      : " + new
				// Date(now.getTimeInMillis()).toString()));
				logger.debug("Effective: "
						+ new Date(effective.getTimeInMillis()).toString());
				logger.debug("Expires  : "
						+ new Date(expires.getTimeInMillis()).toString());
				logger.debug("Now/Eff  : " + now.compareTo(effective));
				logger.debug("Now/Exp  : " + now.compareTo(expires));
				// If now is equal to or after effective & before expires...
				if (now.compareTo(effective) >= 0
						&& now.compareTo(expires) <= 0) {
					pertinentAlerts.put(alertEntry.getNWSID(), alertEntry);
					logger.debug("Alert is effective, adding to database");

					makeAlarm();
					showNotification(alertEntry);
					storeAlert(alertEntry);
				}
			}
		}
	}

	/**
	 * 
	 * @param fipsLoc
	 * @return
	 */
	public String getStateForFIPS(String fipsLoc) {
		logger.trace("In getStateForFIPS.");

		String state = countyDB.getState(fipsLoc);

		return state;
	}

	/**
	 * Process expired CAP entries in the database.
	 * 
	 * @param alertEntry
	 */
	public void processExpired() {
		Calendar now = GregorianCalendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		if (capDB == null)
			capDB.open();
		String[] nwsids = capDB.getNWSIDs();
		try {
			for (int idx = 0; idx < nwsids.length; idx++) {
				byte[] bSer = capDB.getCAPSerialized(capDB
						.getCAPIndexForID(nwsids[idx]));
				CAPStructure temp = CAPStructure.deserializeCAP(bSer);
				Calendar expiration = temp.getExpires();
				// If now is equal to or after effective...
				if (now.compareTo(expiration) >= 0) {
					logger.debug("Removing " + nwsids[idx] + " from alerts.");
					capDB.deleteCAP(nwsids[idx]);
				}
				capDB.vacuum();
			}
		} catch (NullPointerException e) {
		}
	}

	/**
	 * Store a CAP alert into the database.
	 * 
	 * @param alertEntry
	 */
	private void storeAlert(CAPStructure alertEntry) {
		byte[] bOut = CAPStructure.serializeCAP(alertEntry);
		if (capDB == null)
			capDB.open();
		// FIXME: See if there's one there already first...
		if (capDB.getCAPIndexForID(alertEntry.getNWSID()) > 0) {
			logger.debug("Object is already in database.");
		} else {
			capDB.putCAPSerialized(bOut, alertEntry.getNWSID());
			logger.debug("Wrote object to database.");
		}
	}

	/**
	 * Delete a CAP alert from the database.
	 * 
	 * @param alertEntry
	 */
	private void removeAlert(CAPStructure alertEntry) {
		if (capDB == null)
			capDB.open();
		capDB.deleteCAP(alertEntry.getNWSID());
		logger.debug("Deleted object in database.");
	}

	/**
	 * Plays the alert tone.
	 */
	private void makeAlarm() {
		logger.trace("In makeAlarm.");
		String tone = properties.getAudioAlert();
		logger.debug("Tone: " + tone);
		if (tone != null && !tone.equalsIgnoreCase("silent")
				&& tone.length() > 0) {
			Uri ringUri = Uri.parse(tone);
			Ringtone ring = RingtoneManager.getRingtone(mCtx, ringUri);
			logger.debug("About to play tone.");
			if (!ring.isPlaying())
				ring.play();
		} else
			logger.debug("Alert tone empty/null.");
		logger.debug("Leaving makeAlarm");
	}

	/**
	 * Display a notification.
	 * 
	 * @param alert
	 */
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
