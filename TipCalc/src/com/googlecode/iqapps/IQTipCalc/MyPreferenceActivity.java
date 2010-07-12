/*
 * Copyright 2010 TipCalc authors.
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
package com.googlecode.iqapps.IQTipCalc;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * @author kronenpj
 * Borrowed heavily from ConnectBot's SettingsActivity. 
 */
public class MyPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			addPreferencesFromResource(R.xml.preferences);
		} catch (Exception e) {
			// Something bad happened when reading the preferences. Try to
			// recover.
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);

			Editor prefEditor = prefs.edit();
			// Make sure we're starting from scratch
			prefEditor.clear();
			prefEditor.commit();

			// This apparently needs a new editor created...
			PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
			prefEditor = prefs.edit();
			prefEditor.commit();
			
			// Try loading the preferences again.
			addPreferencesFromResource(R.xml.preferences);
		}
	}
}
