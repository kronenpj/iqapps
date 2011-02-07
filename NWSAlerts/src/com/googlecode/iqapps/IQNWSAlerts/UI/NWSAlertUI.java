package com.googlecode.iqapps.IQNWSAlerts.UI;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.iqapps.CAPStructure;
import com.googlecode.iqapps.Logger;
import com.googlecode.iqapps.Point2D;
import com.googlecode.iqapps.IQNWSAlerts.ActivityCodes;
import com.googlecode.iqapps.IQNWSAlerts.GeneralDbAdapter;
import com.googlecode.iqapps.IQNWSAlerts.MenuItems;
import com.googlecode.iqapps.IQNWSAlerts.MyPreferenceActivity;
import com.googlecode.iqapps.IQNWSAlerts.PreferenceHelper;
import com.googlecode.iqapps.IQNWSAlerts.R;
import com.googlecode.iqapps.IQNWSAlerts.service.NWSAlert;

public class NWSAlertUI extends Activity {
	private static Logger logger = Logger.getLogger("NWSAlertUI");
	private TextView tv;
	static PreferenceHelper properties;
	private GeneralDbAdapter capDB;
	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;
	Point2D.Double location;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		logger.trace("In onCreate.");

		tv = (TextView) findViewById(R.id.infoTV);
		tv.setText("");

		properties = new PreferenceHelper(this);

		// Open the database
		capDB = new GeneralDbAdapter(this);
		capDB.open();
	}

	/**
	 * Called when the activity is started or resumed.
	 */
	@Override
	public void onStart() {
		super.onStart();
		logger.trace("In onStart.");

		// If the service isn't supposed to be started on boot, start it
		// here.
		if (!properties.getStartAtBoot()) {
			startNWSService();
		}

		// Obtain a binding to the service.
		doBindService();
	}

	/**
	 * Called when the activity is resumed.
	 */
	@Override
	public void onResume() {
		super.onResume();
		logger.trace("In onResume.");

		if (location == null) {
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			List<String> providers = locationManager.getProviders(true);
			logger.trace("Providers: " + providers);
			for (String provider : providers) {
				Location tmpLoc = locationManager
						.getLastKnownLocation(provider);
				try {
					location = new Point2D.Double(tmpLoc.getLatitude(),
							tmpLoc.getLongitude());
					logger.trace("Back from getLat/Lon.");
				} catch (NullPointerException e) {
					location = new Point2D.Double(40.0, -90.0);
				}
			}

		}
		updateScreen();
	}

	/**
	 * Called when the activity is destroyed.
	 */
	@Override
	public void onDestroy() {
		logger.trace("In onDestroy.");
		capDB.close();

		// If the service isn't supposed to be started on boot, stop it here.
		if (!properties.getStartAtBoot()) {
			stopNWSService();
		}

		logger.trace("Calling super.onDestroy.");
		super.onDestroy();
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
						+ "onOptionsItemSelected for AboutDialog");
				logger.error(e.getLocalizedMessage());
			}
			return true;
		}
		return false;
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

	// Update the screen as a result of some change.
	private void updateScreen() {
		logger.trace("In updateScreen.");

		if (capDB == null)
			capDB.open();
		String[] alertIDS = capDB.getNWSIDs();
		try {
			logger.debug("alertIDS size: " + alertIDS.length);
		} catch (Exception e) {
		}

		try {
			tv.setText("");
			tv.append("Location:" + location.x + "/" + location.y + "\n");
			tv.append("- = - = - = -\n");
			tv.append("Active Filters:\n");
			tv.append("Certainty: " + properties.getMinCertainty() + "\n");
			tv.append("Severity: " + properties.getMinSeverity() + "\n");
			tv.append("Urgency: " + properties.getMinUrgency() + "\n");
			tv.append("- = - = - = -\n");
			try {
				if (NWSAlert.getLastUpdate() < 1) {
					logger.debug("Setting: Waiting for first update.");
					tv.append("Waiting for first update.\n");
				} else if (alertIDS == null || alertIDS.length == 0) {
					logger.debug("Setting: No alerts for your location.");
					tv.append("No alerts for your location.");
				} else {
					logger.debug("Displaying alerts.");
					for (String alert : alertIDS) {
						logger.debug("AlertID: " + alert);
						// tv.append((CAPStructure.deserializeCAP(capDB
						// .getCAPSerialized(alert))).toString());
						byte[] serCAP = capDB.getCAPSerialized(alert);
						if (serCAP != null && serCAP.length > 1) {
							String title = CAPStructure.deserializeCAP(serCAP)
									.getTitle();
							tv.append(title);
						} else {
							logger.debug("Deleting alert " + alert);
							tv.append("Deleting alert " + alert);
							capDB.deleteCAP(alert);
						}

						tv.append("- = - = - = -\n");
					}
				}
			} catch (NullPointerException e) {
				logger.debug("Displaying alert: " + e.toString());
				logger.debug(Log.getStackTraceString(e));
			}
		} catch (RuntimeException e) {
			logger.error(e.toString());
		}
		// logger.debug("Adding textView to layout.");
		// setContentView(tv);
	}

	// Handle starting the service.
	private void startNWSService() {
		logger.debug("Starting service.");
		Intent serviceIntent = new Intent();
		serviceIntent.setClass(NWSAlertUI.this, NWSAlert.class);
		startService(serviceIntent);
	}

	// Handle stopping the service.
	private void stopNWSService() {
		logger.debug("Stopping service.");

		Message msg = Message.obtain(null, NWSAlert.MSG_STOP_SERVICE);
		msg.replyTo = mMessenger;
		try {
			mMessenger.send(msg);
		} catch (RemoteException e) {
			logger.debug("Caught RemoteException: " + e.toString());
		}
		doUnbindService();
	}

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NWSAlert.MSG_UPDATE_AVAILABLE:
				if (location == null)
					location = new Point2D.Double();
				location.x = msg.getData().getDouble("lat");
				location.y = msg.getData().getDouble("lon");
				updateScreen();
				tv.append("Received from service: " + location.x + " / "
						+ location.y);
				tv.append("\n");
				break;
			case NWSAlert.MSG_STOP_SERVICE:
				try {
					mService.send(Message.obtain(null,
							NWSAlert.MSG_STOP_SERVICE));
				} catch (RemoteException e) {
				}
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
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = new Messenger(service);
			tv.append(getText(R.string.remote_service_connected));
			tv.append("\n");

			// We want to monitor the service for as long as we are
			// connected to it.
			try {
				Message msg = Message
						.obtain(null, NWSAlert.MSG_REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				mService.send(msg);

			} catch (RemoteException e) {
				// In this case the service has crashed before we could even
				// do anything with it; we can count on soon being
				// disconnected (and then reconnected if it can be restarted)
				// so there is no need to do anything here.
			}

			// As part of the sample, tell the user what happened.
			Toast.makeText(NWSAlertUI.this, R.string.remote_service_connected,
					Toast.LENGTH_SHORT).show();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;
			tv.append(getText(R.string.remote_service_disconnected));
			tv.append("\n");

			// As part of the sample, tell the user what happened.
			Toast.makeText(NWSAlertUI.this,
					R.string.remote_service_disconnected, Toast.LENGTH_SHORT)
					.show();
		}
	};

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because there is no reason to be able to let other
		// applications replace our component.
		bindService(new Intent(NWSAlertUI.this, NWSAlert.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
		tv.append(getText(R.string.remote_service_binding));
		tv.append("\n");
	}

	void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with
			// it, then now is the time to unregister.
			if (mService != null) {
				try {
					Message msg = Message.obtain(null,
							NWSAlert.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mService.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
			}

			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
			tv.append(getText(R.string.remote_service_unbinding));
			tv.append("\n");
		}
	}
}
