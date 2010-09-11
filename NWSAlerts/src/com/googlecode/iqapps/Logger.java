package com.googlecode.iqapps;

import android.util.Log;

public class Logger {
	String TAG = null;

	public Logger(String temp) {
		TAG = new String(temp);
	}

	public static Logger getLogger(String class1) {
		String temp = new String(class1);
		return new Logger(temp);
	}

	public void fatal(String message) {
		Log.e(TAG, message);
	}

	public void error(String message) {
		Log.e(TAG, message);
	}

	public void warn(String message) {
		Log.w(TAG, message);
	}

	public void info(String message) {
		Log.i(TAG, message);
	}

	public void debug(String message) {
		Log.d(TAG, message);
	}

	public void trace(String message) {
		Log.v(TAG, message);
	}
}
