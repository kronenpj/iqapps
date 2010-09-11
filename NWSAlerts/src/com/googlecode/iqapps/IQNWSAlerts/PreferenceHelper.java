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

/**
 * @author      (classes and interfaces only, required)
 * @version     (classes and interfaces only, required. See footnote 1)
 * @param       (methods and constructors only)
 * @return      (methods only)
 * @exception   (@throws is a synonym added in Javadoc 1.2)
 * @see         
 * @deprecated  (see How and When To Deprecate APIs)
 */
package com.googlecode.iqapps.IQNWSAlerts;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Class to encapsulate preference handling for the application.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class PreferenceHelper {
	private static final String TAG = "PreferenceHelper";
	private SharedPreferences prefs;

	static final String KEY_DATABASE_NAME = "jdbc.db";
	static final String VALUE_DATABASE_NAME = "county_corr.db";
	static final String KEY_READ_FROM_FILE = "alerts.from.file";
	static final String KEY_UPDATE_INTERVAL = "update.interval";
	static final String KEY_START_AT_BOOT = "start.at.boot";
	static final String KEY_AUDIO_ALERT = "audio.tone";

	public PreferenceHelper(Context mCtx) {
		prefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
	}

	public boolean getReadFromFile() {
		boolean flag = Boolean.valueOf(prefs.getBoolean(KEY_READ_FROM_FILE,
				false));
		Log.d(TAG, "Preference " + KEY_READ_FROM_FILE + ": " + flag);
		return flag;
	}

	public boolean getStartAtBoot() {
		boolean flag = Boolean.valueOf(prefs.getBoolean(KEY_START_AT_BOOT,
				false));
		Log.d(TAG, "Preference " + KEY_START_AT_BOOT + ": " + flag);
		return flag;
	}

	public int getUpdateInterval() {
		int flag = Integer.valueOf(prefs.getString(KEY_UPDATE_INTERVAL, "10"));
		Log.d(TAG, "Preference " + KEY_UPDATE_INTERVAL + ": " + flag);
		return flag;
	}

	public String getDatabaseName() {
		String dbName = prefs.getString(KEY_DATABASE_NAME, VALUE_DATABASE_NAME);
		Log.d(TAG, "Preference " + KEY_DATABASE_NAME + ": " + dbName);
		return dbName;
	}

	public String getAudioAlert() {
		String string = prefs.getString(KEY_AUDIO_ALERT, "Silent");
		Log.d(TAG, "Preference " + KEY_AUDIO_ALERT + ": " + string);
		return string;
	}

	public String getProperty(String string) {
		if (string.equalsIgnoreCase(KEY_DATABASE_NAME))
			return getDatabaseName();
		return null;
	}

}
