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

import com.googlecode.iqapps.TimeHelpers;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity to display a summary report of tasks and their cumulative durations
 * for a selected day.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class DayReport extends ListActivity {
	private static final String TAG = "DayReport";
	private final int FOOTER_ID = 0xDEAD;
	private ListView reportList;
	private TextView footerView;
	private TimeSheetDbAdapter db;
	private Cursor timeEntryCursor;
	private long day = TimeHelpers.millisNow();
	private Button[] child;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "In onCreate.");

		try {
			showReport();
		} catch (RuntimeException e) {
			Log.e(TAG, e.toString() + " calling showReport");
		}
		setTitle("Day Report");

		db = new TimeSheetDbAdapter(this);
		try {
			setupDB();
		} catch (Exception e) {
			Log.e(TAG, "setupDB: " + e.toString());
			finish();
		}

		try {
			fillData();
		} catch (Exception e) {
			Log.e(TAG, "fillData: " + e.toString());
			finish();
		}
		Log.d(TAG, "Back from fillData.");
	}

	/** Called when the activity destroyed. */
	@Override
	public void onDestroy() {
		try {
			timeEntryCursor.close();
		} catch (Exception e) {
			Log.e(TAG, "onDestroy: " + e.toString());
		}
		db.close();
		super.onDestroy();
	}

	/**
	 * Encapsulate what's needed to open the database and make sure something is
	 * in it.
	 */
	private void setupDB() {
		try {
			db.open();
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			finish();
		}
	}

	private void fillData() {
		Log.d(TAG, "In fillData.");

		// Cheat a little on the date. This was originally referencing timeIn
		// from the cursor below.
		String date = TimeHelpers.millisToDate(day);
		setTitle("Day Report - " + date);

		if (reportList.getFooterViewsCount() == 0) {
			footerView = new TextView(this);
			footerView.setId(FOOTER_ID);
			reportList.addFooterView(footerView);
		}

		footerView.setText("Hours worked this day: 0");

		try {
			timeEntryCursor.close();
		} catch (NullPointerException e) {
			// Do nothing, this is expected sometimes.
		} catch (Exception e) {
			Log.e(TAG, "fillData: " + e.toString());
			return;
		}

		timeEntryCursor = db.daySummary(day);
		// startManagingCursor(timeEntryCursor);

		try {
			timeEntryCursor.moveToFirst();
		} catch (NullPointerException e) {
			return;
		} catch (Exception e) {
			Log.e(TAG, "timeEntryCursor.moveToFirst: " + e.toString());
			return;
		}

		float accum = 0;
		while (!timeEntryCursor.isAfterLast()) {
			accum = accum
					+ timeEntryCursor.getFloat(timeEntryCursor
							.getColumnIndex(TimeSheetDbAdapter.KEY_TOTAL));
			timeEntryCursor.moveToNext();
		}

		footerView.setText("Hours worked this day: "
				+ String.format("%.2f", accum));

		try {
			reportList.setAdapter(new ReportCursorAdapter(this,
					android.R.layout.simple_list_item_2, timeEntryCursor,
					new String[] { TimeSheetDbAdapter.KEY_TASK,
							TimeSheetDbAdapter.KEY_TOTAL }, new int[] {
							android.R.id.text1, android.R.id.text2 }));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			finish();
		}
	}

	protected void showReport() {
		Log.d(TAG, "Changing to report layout.");

		try {
			setContentView(R.layout.report);
		} catch (Exception e) {
			Log.e(TAG, "Caught " + e.toString()
					+ " while calling setContentView(R.layout.report)");
		}

		reportList = (ListView) findViewById(R.id.reportlist);
		child = new Button[] { (Button) findViewById(R.id.previous),
				(Button) findViewById(R.id.today),
				(Button) findViewById(R.id.next) };

		for (int count = 0; count < child.length; count++) {
			try {
				final int index = count;
				child[index].setOnClickListener(mButtonListener);
			} catch (NullPointerException e) {
				Log.e(TAG, "setOnClickListener: " + e.toString());
			}
		}
	}

	/**
	 * This method is what is registered with the button to cause an action to
	 * occur when it is pressed.
	 */
	private OnClickListener mButtonListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "onClickListener view id: " + v.getId());

			switch (v.getId()) {
			case R.id.previous:
				day = TimeHelpers.millisToStartOfDay(day) - 1000;
				break;
			case R.id.today:
				day = TimeHelpers.millisNow();
				break;
			case R.id.next:
				day = TimeHelpers.millisToEndOfDay(day) + 1000;
				break;
			}
			fillData();
		}
	};
}