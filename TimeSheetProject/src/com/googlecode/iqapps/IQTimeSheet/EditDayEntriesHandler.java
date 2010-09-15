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
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activity to provide an interface to edit entries for a selected day.
 *
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class EditDayEntriesHandler extends ListActivity {
	private static final String TAG = "EditDayEntriesHandler";
	private ListView reportList;
	private TimeSheetDbAdapter db;
	private Cursor timeEntryCursor;
	private long day = TimeHelpers.millisNow();
	private Button[] child;
	private final int ENTRY_CODE = 0x01;
	public final static String ENTRY_ID = "entryID";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "In onCreate.");
		showReport();
		setTitle("Entries for today");

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

		try {
			// Register listeners for the list items.
			reportList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					long itemID = parent.getItemIdAtPosition(position);
					Log.d(TAG, "itemID: " + itemID);
					processChange(itemID);
				}
			});
		} catch (Exception e) {
			Log.e(TAG, "setOnItemClickLister setup");
			Log.e(TAG, e.toString());
		}

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

	/*
	 * Populate the view elements.
	 */
	private void fillData() {
		Log.d(TAG, "In fillData.");

		// Cheat a little on the date. This was originally referencing timeIn
		// from the cursor below.
		String date = TimeHelpers.millisToDate(day);
		setTitle("Entries for " + date);

		try {
			timeEntryCursor.close();
		} catch (NullPointerException e) {
			// Do nothing, this is expected sometimes.
		} catch (Exception e) {
			Log.e(TAG, "timeEntryCursor.close: " + e.toString());
		}
		timeEntryCursor = db.dayEntryReport(day);

		try {
			timeEntryCursor.moveToFirst();
		} catch (NullPointerException e) {
			Log.d(TAG, "timeEntryCursor.moveToFirst: " + e.toString());
			return;
		}
		Log.d(TAG, "timeEntryCursor has " + timeEntryCursor.getCount()
				+ " entries.");

		reportList.setAdapter(new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, timeEntryCursor,
				new String[] { TimeSheetDbAdapter.KEY_TASK,
						TimeSheetDbAdapter.KEY_RANGE }, new int[] {
						android.R.id.text1, android.R.id.text2 }));
	}

	/*
	 * Change the view to the report.
	 */
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
				Toast.makeText(EditDayEntriesHandler.this,
						"NullPointerException", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/*
	 * Process the changes
	 *
	 * @param itemID The entry ID being changed.
	 */
	private void processChange(long itemID) {
		Log.d(TAG, "processChange: " + itemID);
		Intent intent = new Intent(EditDayEntriesHandler.this,
				ChangeEntryHandler.class);

		Cursor entryCursor = db.fetchEntry(itemID);
		entryCursor.moveToFirst();
		if (entryCursor.isAfterLast()) {
			Log.d(TAG, "processChange cursor had no entries for itemID "
					+ itemID);
			return;
		}

		try {
			intent.putExtra(ENTRY_ID, itemID);
			intent.putExtra(TimeSheetDbAdapter.KEY_CHARGENO, entryCursor
					.getString(entryCursor
							.getColumnIndex(TimeSheetDbAdapter.KEY_CHARGENO)));
			intent.putExtra(TimeSheetDbAdapter.KEY_TIMEIN, entryCursor
					.getLong(entryCursor
							.getColumnIndex(TimeSheetDbAdapter.KEY_TIMEIN)));
			intent.putExtra(TimeSheetDbAdapter.KEY_TIMEOUT, entryCursor
					.getLong(entryCursor
							.getColumnIndex(TimeSheetDbAdapter.KEY_TIMEOUT)));
		} catch (Exception e) {
			Log.d(TAG, e.toString() + " populating intent.");
		}

		try {
			startActivityForResult(intent, ENTRY_CODE);
		} catch (RuntimeException e) {
			Log.e(TAG, "RuntimeException caught in processChange");
			Log.e(TAG, e.toString());
		}
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
		switch (requestCode) {
		case ENTRY_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String result = data.getAction();
					Log.d(TAG, "Got result from activity: " + result);
					if (result.equalsIgnoreCase("accept")) {
						Bundle extras = data.getExtras();
						if (extras != null) {
							Log.d(TAG, "Processing returned data.");
							long entryID = extras.getLong(ENTRY_ID);
							String newTask = extras
									.getString(TimeSheetDbAdapter.KEY_TASK);
							long newTimeIn = extras
									.getLong(TimeSheetDbAdapter.KEY_TIMEIN);
							long newTimeOut = extras
									.getLong(TimeSheetDbAdapter.KEY_TIMEOUT);
							long chargeNo = db.getTaskIDByName(newTask);
							db.updateEntry(entryID, chargeNo, null, newTimeIn,
									newTimeOut);
						}
					} else if (result.equalsIgnoreCase("acceptadjacent")) {
						// Pass something back in the extra package to specify
						// adjust adjacent.
						Bundle extras = data.getExtras();
						if (extras != null) {
							Log.d(TAG, "Processing returned data.");
							long entryID = extras.getLong(ENTRY_ID);
							String newTask = extras
									.getString(TimeSheetDbAdapter.KEY_TASK);
							long newTimeIn = extras
									.getLong(TimeSheetDbAdapter.KEY_TIMEIN);
							long newTimeOut = extras
									.getLong(TimeSheetDbAdapter.KEY_TIMEOUT);
							long chargeNo = db.getTaskIDByName(newTask);
							try {
								long prev = db.getPreviousClocking(entryID);
								if (prev > 0)
									db.updateEntry(prev, -1, null, -1,
											newTimeIn);
							} catch (SQLException e) {
								// Don't do anything.
							}
							try {
								long next = db.getNextClocking(entryID);
								if (next > 0)
									db.updateEntry(next, -1, null, newTimeOut,
											-1);
							} catch (SQLException e) {
								// Don't do anything.
							}
							// Change this last because the getNext/Previous
							// depend on the DB data.
							db.updateEntry(entryID, chargeNo, null, newTimeIn,
									newTimeOut);
						}
					} else if (result.equalsIgnoreCase("delete")) {
						Bundle extras = data.getExtras();
						if (extras != null) {
							Log.d(TAG, "Processing returned data.");
							long entryID = extras.getLong(ENTRY_ID);
							db.deleteEntry(entryID);
						}
					}
				}
				fillData();
			}
			break;
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
