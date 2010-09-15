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

package com.googlecode.iqapps.IQTimeSheet;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * An extension of the SimpleCursorAdapter to allow a list item to properly
 * display the desired data on the second line. In this case it's the number of
 * hours an entry represents.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class ReportCursorAdapter extends SimpleCursorAdapter {
	private static final String TAG = "ReportCursorAdapter";

	/**
	 * @param context
	 * @param layout
	 * @param c
	 * @param from
	 * @param to
	 */
	public ReportCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		// TODO: Remove the extraneous tempString and temp declarations and
		// assignments.
		TextView t = (TextView) view.findViewById(android.R.id.text1);
		String tempString = cursor.getString(cursor
				.getColumnIndex(TimeSheetDbAdapter.KEY_TASK));
		t.setText(tempString);

		t = (TextView) view.findViewById(android.R.id.text2);
		Float temp = Float.valueOf(cursor.getFloat(cursor
				.getColumnIndex(TimeSheetDbAdapter.KEY_TOTAL)));
		Log.d(TAG, "bindView: task: " + tempString + ", total: "
				+ String.format("%1.2f hours", temp));
		t.setText(String.format("%1.2f hours", temp));
	}
}
