/*
 * Copyright 2010 TimeSheet authors.
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
package com.googlecode.iqapps.IQTimeSheet;

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
	private static final String TAG = "ChangeEntryHandler";
	private SharedPreferences prefs;

	public static final String KEY_ALIGN_MINUTES = "align.minutes";
	public static final String KEY_ALIGN_MINUTES_AUTO = "align.minutes.auto";

	public PreferenceHelper(Context mCtx) {
		prefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
	}

	public int getAlignMinutes() {
		// Be careful here - The list used by the preferences activity is based
		// on String, not any other primitive or class... This threw cast
		// exceptions early on in development.
		int alignMinutes = Integer.valueOf(prefs.getString(KEY_ALIGN_MINUTES,
				"1"));
		Log.d(TAG, "Preference " + KEY_ALIGN_MINUTES + ": " + alignMinutes);
		return alignMinutes;
	}

	public boolean getAlignMinutesAuto() {
		boolean alignMinutesAuto = prefs.getBoolean(KEY_ALIGN_MINUTES_AUTO,
				false);
		Log.d(TAG, "Preference " + KEY_ALIGN_MINUTES_AUTO + ": "
				+ alignMinutesAuto);
		return alignMinutesAuto;
	}
}
