package com.googlecode.iqapps.testtools;

import static android.os.SystemClock.uptimeMillis;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static com.googlecode.iqapps.testtools.ReflectionUtils.staticInt;
import static com.googlecode.iqapps.testtools.ReflectionUtils.staticIntFields;
import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * The positron instrumentation.
 * 
 * This class implements the PositronAPI and exposes it over http for use as a
 * simple service.
 * 
 * @author philhsmith
 */
public class Positron extends Instrumentation {
	public final static String TAG = "Positron";

	private boolean quit = false;
	private Instrumentation mInstr;
	private LinkedList<Activity> activities;
	private Databases databaseFixture;
	private Preferences preferencesFixture;
	private PauseButton pauseButton;
	private ViewShorthand viewShorthand;

	public Positron() {
		mInstr = this;
	}

	public Positron(Instrumentation instr) {
		this.mInstr = instr;
	}

	@Override
	public void onStart() {
		super.onStart();
		// try {
		setKeyGuardEnabled(false);
		backup();
		// } catch (IOException e) {
		// Log.e(TAG, e.getMessage());
		// }
	}

	private void setKeyGuardEnabled(boolean enable) {
		Object keyguardService = getContext().getSystemService(
				Context.KEYGUARD_SERVICE);
		KeyguardLock keyguardLock = ((KeyguardManager) keyguardService)
				.newKeyguardLock(TAG);

		if (enable) {
			keyguardLock.reenableKeyguard();
		} else {
			keyguardLock.disableKeyguard();
		}
	}

	private int decodeFlag(String value) {
		try {
			return ReflectionUtils.staticInt(Intent.class, value);
		} catch (IllegalArgumentException e) {
			return parseInt(value);
		}
	}

	@Override
	public void onCreate(Bundle arguments) {
		super.onCreate(arguments);
		activities = new LinkedList<Activity>();
		databaseFixture = new Databases(mInstr);
		preferencesFixture = new Preferences(mInstr);
		pauseButton = new PauseButton(mInstr);
		viewShorthand = new ViewShorthand(mInstr);
		start();
	}

	@Override
	public void callActivityOnResume(Activity activity) {
		super.callActivityOnResume(activity);
		Log.v(TAG, "Noticed " + activity.getClass().getName() + " resumed.");
		synchronized (activities) {
			if (activities.contains(activity))
				activities.remove(activity);
			activities.addFirst(activity);
		}
	}

	/** Get a snapshot of the known activity stack. */
	protected List<Activity> activities() {
		synchronized (activities) {
			for (Iterator<Activity> i = activities.iterator(); i.hasNext();) {
				Activity finished = i.next();
				if (finished.isFinishing()) {
					Log.v(TAG, "Noticed " + finished.getClass().getName()
							+ " finished.");
					i.remove();
				}
			}

			return new ArrayList<Activity>(activities);
		}
	}

	protected Activity activity() {
		synchronized (activities) {
			return activities.get(0);
		}
	}

	private void finish(final Activity activity) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		runOnMainSync(new Runnable() {
			public void run() {
				if (!activity.isFinishing())
					activity.finish();
			}
		});
		if (wasPaused)
			pause();
	}

	public void finish() {
		finish(activity());
	}

	public void finishAll() {
		resume();
		for (Activity activity : activities())
			finish(activity);
		try {
			for (int tries = 0; tries < 20 && !activities().isEmpty(); tries++)
				sleep(250);
		} catch (InterruptedException e) {
			Log.e(TAG,
					"Interrupted while waiting for all activities to finish.",
					e);
		}
		if (!activities().isEmpty())
			Log.e(TAG, "Timed out waiting for all activities to finish.");
	}

	/**
	 * Pause execution of the target package.
	 * 
	 * This is useful for inspecting the view state of the target package, since
	 * doing so while it is running can create race conditions. Resume execution
	 * with resume().
	 * 
	 * Pausing an already-paused application is a safe no-op.
	 * 
	 * Paused applications can't respond to events, nor execute synchronous
	 * commands such as sendStringSync (or any *Sync.) Keep this in mind when
	 * writing tests.
	 * 
	 * Pausing is done by asynchronously calling Instrumentation.runOnMainSync
	 * on a blocking operation.
	 */
	public void pause() {
		waitForIdleSync();
		pauseButton.pause();
	}

	/**
	 * Resume executing the target package if it has been paused.
	 * 
	 * Resuming an unpaused package is a safe noop.
	 */
	public void resume() {
		pauseButton.resume();
	}

	/** Is the target package paused? */
	public boolean paused() {
		return pauseButton.paused();
	}

	/** Backup all databases in the tested application. */
	public void backup() {
		databaseFixture.backup();
	}

	/** Backup the given database. */
	public void backup(String database) {
		databaseFixture.backup(database);
	}

	/** Execute the given Sql scripts (as raw resources in the target context.) */
	protected void sql(String database, int scriptsAsRawResources) {
		databaseFixture.sql(database, scriptsAsRawResources);
	}

	/** Execute the given Sql script. */
	public void sql(String database, String script) {
		databaseFixture.sql(database, script);
	}

	/** Restore all databases in the tested application that have backups. */
	public void restore() {
		databaseFixture.restore();
	}

	/** Stop the positron instrumentation and cleanup. */
	public void quit() {
		finishAll();
		pauseButton.quit();

		restore();
		setKeyGuardEnabled(true);
		waitForIdleSync();

		quit = true;
	}

	/**
	 * Restore the given database from a previous backup call. This deletes the
	 * backup as well.
	 */
	public void restore(String database) {
		databaseFixture.restore(database);
	}

	/** invokeMenuActionSync, resuming momentarily if necessary. */
	protected boolean invokeMenuAction(Activity targetActivity, int id, int flag) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		boolean result = invokeMenuActionSync(targetActivity, id, flag);
		if (wasPaused)
			pause();
		return result;
	}

	/** invokeContextMenuAction, resuming momentarily if necessary. */
	public boolean invokeContextMenuAction(Activity targetActivity, int id,
			int flag) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		boolean result = super
				.invokeContextMenuAction(targetActivity, id, flag);
		if (wasPaused)
			pause();
		return result;
	}

	/** sendCharacterSync, resuming momentarily if necessary. */
	public void sendCharacter(int keyCode) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		sendCharacterSync(keyCode);
		if (wasPaused)
			pause();
	}

	/** sendKeyDownUpSync, resuming momentarily if necessary. */
	public void sendKeyDownUp(int key) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		sendKeyDownUpSync(key);
		if (wasPaused)
			pause();
	}

	/** sendKeyDownUp(KEYCODE_DPAD_CENTER) */
	public void click() {
		sendKeyDownUp(KEYCODE_DPAD_CENTER);
	}

	/**
	 * sendKeyDownUpSync on all keys in order, resuming momentarily if
	 * necessary.
	 * 
	 * @param keys
	 *            A mixture of ints and Strings. ints are sent with
	 *            sendKeyDownUpSync, Strings are send with sendStringSync.
	 * @throws IllegalArgumentException
	 *             if something other than an int or String was passed.
	 */
	public void press(Object... keys) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		for (Object key : keys) {
			if (key instanceof String
					&& staticIntFields(PositronAPI.Key.class).contains(key)) {
				key = staticInt(PositronAPI.Key.class, (String) key);
			}

			if (key instanceof Integer)
				sendKeyDownUpSync((Integer) key);
			else if (key instanceof String)
				sendStringSync((String) key);
			else
				throw new IllegalArgumentException();
		}
		if (wasPaused)
			pause();
	}

	/**
	 * Evaluate the ViewShorthand path starting from the given point.
	 * 
	 * @return The result as the passed type.
	 */
	protected <T> T at(Class<T> asA, Object from, String path) {
		return viewShorthand.evaluate(asA, from, path);
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return The result as the passed type.
	 */
	protected <T> T at(Class<T> asA, String path) {
		return at(asA, activity(), path);
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return The result as a String. If the result would be a CharSequence,
	 *         call toString on it.
	 */
	public String stringAt(String path) {
		return stringAt(0, path);
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return The result as a float.
	 */
	public float floatAt(int depth, String path) {
		return at(Float.class, activities().get(depth), path);
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return The result as a float.
	 */
	public float floatAt(String path) {
		return floatAt(0, path);
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return The result as an int.
	 */
	public int intAt(String path) {
		return intAt(0, path);
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return The result as a boolean.
	 */
	public boolean booleanAt(String path) {
		return booleanAt(0, path);
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return The result as a double.
	 */
	public double doubleAt(String path) {
		return doubleAt(0, path);
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return True if the object is not null. False otherwise.
	 */
	public boolean existsAt(String path) {
		return existsAt(0, path);
	}

	/** sendKeySync, resuming momentarily if necessary. */
	protected void sendKey(KeyEvent event) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		sendKeySync(event);
		if (wasPaused)
			pause();
	}

	/** sendKeySync, resuming momentarily if necessary. */
	public void sendKey(int action, int code) {
		sendKey(new KeyEvent(action, code));
	}

	/** sendKeySync, resuming momentarily if necessary. */
	public void sendKey(long eventTimeAfterDown, int action, int code,
			int repeat) {
		long downTime = uptimeMillis();
		sendKey(new KeyEvent(downTime, downTime + eventTimeAfterDown, action,
				code, repeat));
	}

	/** sendKeySync, resuming momentarily if necessary. */
	public void sendKey(long eventTimeAfterDown, int action, int code,
			int repeat, int metaState) {
		long downTime = uptimeMillis();
		sendKey(new KeyEvent(downTime, downTime + eventTimeAfterDown, action,
				code, repeat, metaState));
	}

	/** sendKeySync, resuming momentarily if necessary. */
	public void sendKey(long eventTimeAfterDown, int action, int code,
			int repeat, int metaState, int device, int scancode) {
		long downTime = uptimeMillis();
		sendKey(new KeyEvent(downTime, downTime + eventTimeAfterDown, action,
				code, repeat, metaState, device, scancode));
	}

	/** sendKeySync, resuming momentarily if necessary. */
	public void sendKey(long eventTimeAfterDown, int action, int code,
			int repeat, int metaState, int device, int scancode, int flags) {
		long downTime = uptimeMillis();
		sendKey(new KeyEvent(downTime, downTime + eventTimeAfterDown, action,
				code, repeat, metaState, device, scancode, flags));
	}

	/** sendStringSync, resuming momentarily if necessary. */
	public void sendString(String text) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		sendStringSync(text);
		if (wasPaused)
			pause();
	}

	/** startActivitySync, resuming momentarily if necessary. */
	protected void startActivity(Intent intent) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		startActivitySync(intent);
		if (wasPaused)
			pause();
	}

	@Override
	public boolean invokeMenuActionSync(Activity targetActivity, int id,
			int flag) {
		if (paused()) {
			String message = "The tested application is paused! Call invokeMenuAction instead.";
			Log.e(TAG, message);
			throw new IllegalStateException(message);
		}

		try {
			return super.invokeMenuActionSync(targetActivity, id, flag);
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void sendCharacterSync(int keyCode) {
		if (paused()) {
			String message = "The tested application is paused! Call sendCharacter instead.";
			Log.e(TAG, message);
			throw new IllegalStateException(message);
		}

		try {
			super.sendCharacterSync(keyCode);
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void sendKeyDownUpSync(int key) {
		if (paused()) {
			String message = "The tested application is paused! Call sendKeyDownUp instead.";
			Log.e(TAG, message);
			throw new IllegalStateException(message);
		}

		try {
			super.sendKeyDownUpSync(key);
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void sendKeySync(KeyEvent event) {
		if (paused()) {
			String message = "The tested application is paused! Call sendKey instead.";
			Log.e(TAG, message);
			throw new IllegalStateException(message);
		}

		try {
			super.sendKeySync(event);
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void sendStringSync(String text) {
		if (paused()) {
			String message = "The tested application is paused! Call sendString instead.";
			Log.e(TAG, message);
			throw new IllegalStateException(message);
		}

		try {
			super.sendStringSync(text);
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public Activity startActivitySync(Intent intent) {
		if (paused()) {
			String message = "The tested application is paused! Call startActivity instead.";
			Log.e(TAG, message);
			throw new IllegalStateException(message);
		}

		try {
			return super.startActivitySync(intent);
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void waitForIdleSync() {
		if (paused()) {
			String message = "The tested application is paused! resume() it first.";
			Log.e(TAG, message);
			throw new IllegalStateException(message);
		}

		try {
			super.waitForIdleSync();
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	public void finish(int depth) {
		finish(activities().get(depth));
	}

	public boolean menu(int id) {
		return menu(id, 0);
	}

	public boolean menu(int id, int flag) {
		return menu(0, id, flag);
	}

	public boolean menu(int depth, int id, int flag) {
		return invokeMenuAction(activities().get(depth), id, flag);
	}

	public boolean contextMenu(int id) {
		return contextMenu(id, 0);
	}

	public boolean contextMenu(int id, int flag) {
		return contextMenu(0, id, flag);
	}

	public boolean contextMenu(int depth, int id, int flag) {
		return invokeContextMenuAction(activities().get(depth), id, flag);
	}

	public void startActivity(String action) {
		startActivity(action, null, null, null, null);
	}

	public void startActivity(String packageName, String className) {
		Intent intent = new Intent();
		intent.setClassName(packageName, className);
		startActivity(intent);
	}

	public void startActivity(String action, String data, String type) {
		startActivity(action, data, type, null, null);
	}

	public void startActivity(String action, String[] categories) {
		startActivity(action, null, null, categories, null);
	}

	public void startActivity(String action, String[] categories, int[] flags) {
		startActivity(action, null, null, categories, flags);
	}

	public void startActivity(String action, String data, String type,
			String[] categories, int[] flags) {
		Intent intent = new Intent();

		if (action != null)
			intent.setAction(action);

		if (data != null && type != null) {
			intent.setDataAndType(Uri.parse(data), type);
		} else if (data != null) {
			intent.setData(Uri.parse(data));
		} else if (type != null) {
			intent.setType(type);
		}

		if (categories != null) {
			for (String category : categories)
				intent.addCategory(category);
		}

		if (flags != null) {
			for (int flag : flags)
				intent.addFlags(flag);
		}

		startActivity(intent);
	}

	public boolean booleanAt(int depth, String path) {
		return at(Boolean.class, activities().get(depth), path);
	}

	public double doubleAt(int depth, String path) {
		return at(Double.class, activities().get(depth), path);
	}

	public int intAt(int depth, String path) {
		return at(Integer.class, activities().get(depth), path);
	}

	public String stringAt(int depth, String path) {
		return at(Object.class, activities().get(depth), path).toString();
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return True if the object is not null. False otherwise.
	 */
	public boolean existsAt(int depth, String path) {
		try {
			if (viewShorthand.evaluate(depth, path) != null) {
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException e) {
		}
		return false;
	}

	public void touch(float x, float y) {
		long downTime = uptimeMillis();
		sendPointer(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
		sendPointer(downTime, downTime + 250, MotionEvent.ACTION_UP, x, y, 0);
	}

	public void touch(String path) {
		float[] center = centerOf(path);
		touch(center[0], center[1]);
	}

	private float[] centerOf(String path) {
		int[] location = new int[2];
		View view = at(View.class, path);
		view.getLocationOnScreen(location);
		float[] center = new float[] { location[0] + view.getWidth() / 2,
				location[1] + view.getHeight() / 2 };
		Log.i(TAG, "Calculated center of " + path + " as " + center[0] + ", "
				+ center[1]);
		return center;
	}

	public void drag(float startX, float startY, float endX, float endY) {
		long downTime = uptimeMillis();
		sendPointer(downTime, downTime, MotionEvent.ACTION_DOWN, startX,
				startY, 0);
		sendPointer(downTime, downTime + 50, MotionEvent.ACTION_MOVE, endX,
				endY, 0);
		sendPointer(downTime, downTime + 75, MotionEvent.ACTION_UP, 0, 0, 0);
	}

	public void drag(String start, String end) {
		float[] centerOfStart = centerOf(start);
		float[] centerOfEnd = centerOf(end);
		drag(centerOfStart[0], centerOfStart[1], centerOfEnd[0], centerOfEnd[1]);
	}

	public void sendPointer(int action, float x, float y) {
		sendPointer(action, x, y, 0);
	}

	public void sendPointer(int action, float x, float y, int metaState) {
		sendPointer(0, action, x, y, metaState);
	}

	public void sendPointer(long eventTimeAfterDown, int action, float x,
			float y, int metaState) {
		long downTime = uptimeMillis();
		long eventTime = downTime + eventTimeAfterDown;
		sendPointer(downTime, eventTime, action, x, y, metaState);
	}

	private void sendPointer(long downTime, long eventTime, int action,
			float x, float y, int metaState) {
		MotionEvent event = null;
		try {
			event = MotionEvent.obtain(downTime, eventTime, action, x, y,
					metaState);
			sendPointer(event);
		} finally {
			if (event != null)
				event.recycle();
		}
	}

	public void sendPointer(long eventTimeAfterDown, int action, float x,
			float y, float pressure, float size, int metaState,
			float xPrecision, float yPrecision, int deviceId, int edgeFlags) {
		long downTime = uptimeMillis();
		long eventTime = downTime + eventTimeAfterDown;
		MotionEvent event = null;
		try {
			event = MotionEvent.obtain(downTime, eventTime, action, x, y,
					pressure, size, metaState, xPrecision, yPrecision,
					deviceId, edgeFlags);
			sendPointer(event);
		} finally {
			if (event != null)
				event.recycle();
		}
	}

	protected void sendPointer(MotionEvent event) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		sendPointerSync(event);
		if (wasPaused)
			pause();
	}

	@Override
	public void sendPointerSync(MotionEvent event) {
		if (paused()) {
			String message = "The tested application is paused! Call sendPointer instead.";
			Log.e(TAG, message);
			throw new IllegalStateException(message);
		}

		try {
			super.sendPointerSync(event);
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	public void flick(float x, float y) {
		sendTrackball(MotionEvent.ACTION_MOVE, x, y);
	}

	public void sendTrackball(int action, float x, float y) {
		sendTrackball(action, x, y, 0);
	}

	public void sendTrackball(int action, float x, float y, int metaState) {
		sendTrackball(0, action, x, y, metaState);
	}

	public void sendTrackball(long eventTimeAfterDown, int action, float x,
			float y, int metaState) {
		long downTime = uptimeMillis();
		long eventTime = downTime + eventTimeAfterDown;
		sendTrackball(downTime, eventTime, action, x, y, metaState);
	}

	private void sendTrackball(long downTime, long eventTime, int action,
			float x, float y, int metaState) {
		MotionEvent event = null;
		try {
			event = MotionEvent.obtain(downTime, eventTime, action, x, y,
					metaState);
			sendTrackball(event);
		} finally {
			if (event != null)
				event.recycle();
		}
	}

	public void sendTrackball(long eventTimeAfterDown, int action, float x,
			float y, float pressure, float size, int metaState,
			float xPrecision, float yPrecision, int deviceId, int edgeFlags) {
		long downTime = uptimeMillis();
		long eventTime = downTime + eventTimeAfterDown;
		MotionEvent event = null;
		try {
			event = MotionEvent.obtain(downTime, eventTime, action, x, y,
					pressure, size, metaState, xPrecision, yPrecision,
					deviceId, edgeFlags);
			sendTrackball(event);
		} finally {
			if (event != null)
				event.recycle();
		}
	}

	protected void sendTrackball(MotionEvent event) {
		boolean wasPaused = paused();
		if (wasPaused)
			resume();
		sendTrackballEventSync(event);
		if (wasPaused)
			pause();
	}

	@Override
	public void sendTrackballEventSync(MotionEvent event) {
		if (paused()) {
			String message = "The tested application is paused! Call sendTrackball instead.";
			Log.e(TAG, message);
			throw new IllegalStateException(message);
		}

		try {
			super.sendTrackballEventSync(event);
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
			throw e;
		}
	}

	public boolean waitFor(String path, String value, long timeout) {
		return waitFor(0, path, value, timeout);
	}

	public boolean waitFor(int depth, String path, String value, long timeout) {
		timeout += currentTimeMillis();
		boolean conditionMet = false;

		while (currentTimeMillis() < timeout) {
			resume();
			try {
				sleep(50);
			} catch (InterruptedException e) {
				Log.e(TAG, "Interrupted while polling in waitFor()", e);
			}
			pause();

			if (conditionMet = value.equals(stringAt(depth, path)))
				break;
		}

		return conditionMet;
	}

	/** Backup all databases in the tested application. */
	public void prefBackup() {
		Log.i(TAG, "Backing up preferences...");
		preferencesFixture.backup();
	}

	/** Backup the given database. */
	public void prefBackup(String preferences) {
		Log.i(TAG, "Backing up preference: " + preferences);
		preferencesFixture.backup(preferences);
	}

	/** Restore all databases in the tested application that have backups. */
	public void prefRestore() {
		preferencesFixture.restore();
	}

	/**
	 * Restore the given database from a previous backup call. This deletes the
	 * backup as well.
	 */
	public void prefRestore(String preferences) {
		preferencesFixture.restore(preferences);
	}

}
