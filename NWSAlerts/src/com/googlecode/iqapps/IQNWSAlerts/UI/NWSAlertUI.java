package com.googlecode.iqapps.IQNWSAlerts.UI;

import java.util.EventObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.googlecode.iqapps.CAPStructure;
import com.googlecode.iqapps.EventInterface;
import com.googlecode.iqapps.Logger;
import com.googlecode.iqapps.IQNWSAlerts.AboutDialog;
import com.googlecode.iqapps.IQNWSAlerts.ActivityCodes;
import com.googlecode.iqapps.IQNWSAlerts.GeneralDbAdapter;
import com.googlecode.iqapps.IQNWSAlerts.MenuItems;
import com.googlecode.iqapps.IQNWSAlerts.MyPreferenceActivity;
import com.googlecode.iqapps.IQNWSAlerts.PreferenceHelper;
import com.googlecode.iqapps.IQNWSAlerts.R;
import com.googlecode.iqapps.IQNWSAlerts.EventContainer.Events;
import com.googlecode.iqapps.IQNWSAlerts.service.NWSAlert;

public class NWSAlertUI extends Activity {
	private static Logger logger = Logger.getLogger("NWSAlertUI");
	private GeneralDbAdapter capDB;
	static PreferenceHelper properties;
	static NWSAlert myAlert = null;

	Context mCtx;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Someone said using this was a bad idea...
		// mCtx = getApplicationContext();
		mCtx = this;
		properties = new PreferenceHelper(mCtx);
		main();
	}

	/** Called when the activity is first created. */
	/*
	 * @Override public void onResume() { super.onResume(); }
	 */

	/** Called when the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!properties.getStartAtBoot()) {
			logger.debug("Stopping service.");
			try {
				myAlert.stopservice();
			} catch (NullPointerException e) {
			}
		}
	}

	/**
	 * 
	 */
	public void main() {
		// Start the service. It'll take care of all the background stuff.
		if (mCtx == null)
			// Someone said using this was a bad idea...
			// mCtx = getApplicationContext();
			mCtx = this;

		myAlert = new NWSAlert(mCtx);
		logger.debug("Starting service.");
		// Intent serviceIntent = new Intent();
		// serviceIntent.setAction("com.googlecode.iqapps.IQNWSAlerts.NWSAlert");
		// startService(serviceIntent);
		myAlert.startservice();

		NWSAlert.events[Events.UPDATE_AVAILABLE.ordinal()]
				.addEventListener(new EventInterface() {
					@Override
					public void eventOccurred(EventObject evt) {
						logger.debug("Received UPDATE Available");
						updateScreen();
					}
				});

		capDB = new GeneralDbAdapter(mCtx);
		capDB.open();
	}

	void updateScreen() {
		logger.trace("In updateScreen.");
		TextView tv = (TextView) findViewById(R.id.infoTV);

		if (capDB == null)
			capDB.open();
		String[] alertIDS = capDB.getNWSIDs();
		try {
			logger.debug("alertIDS size: " + alertIDS.length);
		} catch (Exception e) {
		}
		try {
			if (NWSAlert.getLastUpdate() < 1) {
				logger.debug("Setting: Waiting for first update.");
				tv.setText("Waiting for first update.");
			} else if (alertIDS == null || alertIDS.length == 0) {
				logger.debug("Setting: No alerts for your area.");
				tv.setText("No alerts for your area.");
			} else {
				logger.debug("Displaying alert.");
				tv.setText("");
				for (String alert : alertIDS) {
					tv.append((CAPStructure.deserializeCAP(capDB
							.getCAPSerialized(alert))).toString());
					tv.append("- = - = - = -\n");
				}
			}
		} catch (NullPointerException e) {
			logger.debug("Displaying alert: " + e.toString());
			e.printStackTrace();
		}
		// logger.debug("Adding textView to layout.");
		// setContentView(tv);
	}

	/**
	 * This method is called when the sending activity has finished, with the
	 * result it supplied.
	 * 
	 * @param requestCode
	 *            The original request code as given to startActivity().
	 * @param resultCode
	 *            From sending activity as per setResult().
	 * @param data
	 *            From sending activity as per setResult().
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check to see that what we received is what we wanted to see.
		if (requestCode == ActivityCodes.PREFS.ordinal()) {
			// This is a standard resultCode that is sent back if the
			// activity doesn't supply an explicit result. It will also
			// be returned if the activity failed to launch.
			if (resultCode == RESULT_OK) {
				// Do something here?
			}
		}
	}

	/*
	 * Creates the menu items (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MenuItems.SETTINGS.ordinal(), 0,
				R.string.menu_prefs);
		item.setIcon(R.drawable.ic_menu_preferences);
		item = menu.add(0, MenuItems.ABOUT.ordinal(), 1, R.string.menu_about);
		item.setIcon(R.drawable.ic_menu_info_details);
		return true;
	}

	/*
	 * Handles item selections (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		if (item.getItemId() == MenuItems.SETTINGS.ordinal()) {
			intent = new Intent(NWSAlertUI.this, MyPreferenceActivity.class);
			try {
				startActivityForResult(intent, ActivityCodes.PREFS.ordinal());
			} catch (RuntimeException e) {
				logger.error("RuntimeException caught in "
						+ "onOptionsItemSelected for MyPreferenceActivity");
				logger.error(e.getLocalizedMessage());
			}
			return true;
		}
		if (item.getItemId() == MenuItems.ABOUT.ordinal()) {
			intent = new Intent(NWSAlertUI.this, AboutDialog.class);
			try {
				startActivity(intent);
			} catch (RuntimeException e) {
				logger.error("RuntimeException caught in "
						+ "onOptionsItemSelected for DayReportHandler");
				logger.error(e.getLocalizedMessage());
			}
			return true;
		}
		return false;
	}
}
