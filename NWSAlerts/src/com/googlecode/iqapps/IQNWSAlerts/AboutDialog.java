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
package com.googlecode.iqapps.IQNWSAlerts;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * @author kronenpj
 * 
 */
public class AboutDialog extends Activity {
	private static final String TAG = "AboutDialog";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "In onCreate.");
		
		setContentView(R.layout.about);
		TextView version = (TextView) findViewById(R.id.version);
		TextView aboutText = (TextView) findViewById(R.id.abouttext);
		
		version.setText(R.string.msg_version);
		aboutText.setText(R.string.about_summary);
		Log.d(TAG, "Falling out of onCreate.");
	}
}
