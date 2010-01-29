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

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Main activity for the TimeSheet project. Implements the top-level user
 * interface for the application.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TimeSheetActivity extends ListActivity {
	private TimeSheetDbAdapter db;
	private ListView tasksList;
	private TimeListWrapper timeWrapper;
	private TimeListAdapter timeAdapter;
	private Cursor taskCursor;
	private Cursor reportCursor;
	private final static String TAG = "TimeSheetActivity";
	private static final int TASKADD_CODE = 0x00;
	private static final int TASKEDIT_CODE = 0x01;
	private static final int TASKREVIVE_CODE = 0x02;
	private static final int EDIT_CODE = 0x03;
	private static final int REPORT_CODE = 0x04;
	private static final int EDIT_ID = 0x20;
	private static final int RETIRE_ID = 0x21;
	private static final int MENU_NEW_TASK = 0x30;
	private static final int MENU_REVIVE_TASK = 0x31;
	private static final int MENU_EDITDAYENTRIES = 0x32;
	private static final int MENU_DAYREPORT = 0x33;
	private static final int MENU_WEEKREPORT = 0x34;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme);
		setTitle(R.string.app_name);
		setContentView(R.layout.main);
		Log.d(TAG, "onCreate.");

		try {
			tasksList = (ListView) findViewById(android.R.id.list);
			tasksList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		} catch (Exception e) {
			Log.e(TAG, "tasksList: " + e.toString());
		}

		db = new TimeSheetDbAdapter(this);
		setupDB();

		try {
			fillData();
		} catch (Exception e) {
			Log.e(TAG, "fillData: " + e.toString());
		}

		try {
			setSelected();
			// TODO: Consider a message here to mention a cross-day clocking and
			// what should be done about it.
		} catch (Exception e) {
			Log.e(TAG, "setSelected: " + e.toString());
		}

		try {
			// Register listeners for the list items.
			tasksList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					String taskName = (String) parent
							.getItemAtPosition(position);
					long taskID = db.getTaskIDByName(taskName);
					processChange(taskID);
				}
			});
		} catch (Exception e) {
			Log.e(TAG, "register listeners: " + e.toString());
		}

		// Register the context menu below with the tasksList ListView.
		registerForContextMenu(tasksList);
	}

	/** Called when the activity destroyed. */
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");

		try {
			taskCursor.close();
		} catch (Exception e) {
			Log.e(TAG, "onDestroy: (taskCursor) " + e.toString());
		}
		try {
			reportCursor.close();
		} catch (Exception e) {
			Log.e(TAG, "onDestroy: (reportCursor) " + e.toString());
		}
		db.close();
		super.onDestroy();
	}

	/*
	 * @return: If the final state is checked.
	 */
	private void processChange(long taskID) {
		Log.d(TAG, "processChange for task ID: " + taskID);

		long lastRowID = db.lastClockEntry();
		long lastTaskID = db.taskIDForLastClockEntry();

		Log.d(TAG, "Last Task Entry Row: " + lastRowID);
		Cursor c = db.fetchEntry(lastRowID, TimeSheetDbAdapter.KEY_TIMEOUT);

		long timeOut = -1;
		if (c == null)
			Log.d(TAG, "Cursor is null...  :(");
		if (!c.moveToFirst())
			Log.d(TAG, "Moving cursor to first failed.");
		else {
			timeOut = c.getLong(0);
			Log.d(TAG, "Last clock out at: " + timeOut);
		}
		try {
			c.close();
		} catch (Exception e) {
			Log.e(TAG, "Closing fetch entry cursor c: " + e.toString());
		}

		// Determine if the task has already been chosen and is now being
		// closed.
		if (timeOut == 0 && lastTaskID == taskID) {
			db.closeEntry(taskID);
			tasksList.clearChoices();
			Log.d(TAG, "processChange for task ID: " + taskID);
		} else {
			if (lastTaskID > 0 && lastTaskID != taskID)
				db.closeEntry();
			db.createEntry(taskID);
			Log.d(TAG, "processChange ID from " + lastTaskID + " to " + taskID);
		}
	}

	/**
	 * Encapsulate what's needed to open the database and make sure something is
	 * in it.
	 */
	private void setupDB() {
		Log.d(TAG, "setupDB");

		try {
			db.open();
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			Toast
					.makeText(this, e.toString() + " - Exiting",
							Toast.LENGTH_LONG).show();
			finish();
		}

		// Put something into the database
		try {
			long last = db.lastTaskEntry();
			if (last < 1) {
				db.createTask("Example task entry");
			}
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
		}
	}

	private void reloadTaskCursor() {
		taskCursor = db.fetchAllTaskEntries();
	}

	private String getTaskFromLocation(long position) {
		Log.d(TAG, "getTaskFromLocation");

		taskCursor.moveToPosition((int) position);
		if (!taskCursor.isAfterLast()) {
			return taskCursor.getString(taskCursor
					.getColumnIndex(TimeSheetDbAdapter.KEY_TASK));
		}
		return null;
	}

	private void fillData() {
		Log.d(TAG, "fillData");
		// Get all of the entries from the database and create the list
		reloadTaskCursor();
		startManagingCursor(taskCursor);

		String[] items = new String[taskCursor.getCount()];
		taskCursor.moveToFirst();
		int i = 0;
		while (!taskCursor.isAfterLast()) {
			items[i] = new String(taskCursor.getString(1));
			taskCursor.moveToNext();
			i++;
		}

		tasksList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice, items));
		updateTitleBar();
	}

	private void updateTitleBar() {
		Log.d(TAG, "updateTitleBar");
		// Display the time accumulated for today with time remaining.
		reportCursor = db.daySummary();
		if (reportCursor == null) {
			setTitle("Time Sheet " + String.format("(%.2fh / %.2fh)", 0.0, 8.0));
			return;
		}
		reportCursor.moveToFirst();
		if (!reportCursor.isAfterLast()) {
			int column = reportCursor
					.getColumnIndex(TimeSheetDbAdapter.KEY_TOTAL);
			float accum = 0;
			while (!reportCursor.isAfterLast()) {
				accum = accum + reportCursor.getFloat(column);
				reportCursor.moveToNext();
			}
			setTitle("Time Sheet "
					+ String.format("(%.2fh / %.2fh)", accum, 8.0 - accum));
		}
	}

	private void setSelected() {
		long lastRowID = db.lastClockEntry();
		long lastTaskID = db.taskIDForLastClockEntry();

		Log.d(TAG, "Last Task Entry Row: " + lastRowID);
		Log.d(TAG, "Last Task ID: " + lastTaskID);
		Cursor tempClockCursor = db.fetchEntry(lastRowID,
				TimeSheetDbAdapter.KEY_TIMEOUT);

		long timeOut = -1;
		if (tempClockCursor == null)
			Log.d(TAG, "Cursor is null...  :(");
		if (!tempClockCursor.moveToFirst())
			Log.d(TAG, "Moving cursor to first failed.");
		else {
			timeOut = tempClockCursor.getLong(0);
			Log.d(TAG, "Last clock out at: " + timeOut);
		}
		tempClockCursor.close();

		if (timeOut != 0) {
			Log.d(TAG, "Returning.");
			return;
		}

		Log.d(TAG, "tasksList child count is: " + tasksList.getChildCount());

		// Iterate over the entire cursor to find the name of the
		// entry that is to be selected.
		taskCursor.moveToFirst();
		while (!taskCursor.isAfterLast()) {
			Log.d(TAG, "Checking item at " + taskCursor.getPosition());
			Log.d(TAG, " Item is " + taskCursor.getLong(0));
			if (taskCursor.getLong(0) == lastTaskID) {
				Log.d(TAG, "  Selecting item at " + taskCursor.getPosition());
				tasksList.setItemChecked(taskCursor.getPosition(), true);
				return;
			}
			taskCursor.moveToNext();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, 0, "Rename Task");
		menu.add(0, RETIRE_ID, 0, "Retire Task");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case EDIT_ID:
			Log.d(TAG, "Edit task: " + info.id);
			Intent intent = new Intent(TimeSheetActivity.this,
					EditTaskHandler.class);
			intent.putExtra("taskName", getTaskFromLocation(info.id));
			try {
				startActivityForResult(intent, TASKEDIT_CODE);
			} catch (RuntimeException e) {
				Toast.makeText(TimeSheetActivity.this, "RuntimeException",
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, e.getLocalizedMessage());
				Log.e(TAG, "RuntimeException caught.");
			}
			return true;
		case RETIRE_ID:
			db.deactivateTask(getTaskFromLocation(info.id));
			fillData();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	/*
	 * Creates the menu items (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MENU_NEW_TASK, 0, R.string.menu_new_task);
		item.setIcon(R.drawable.ic_task_add);
		item = menu.add(0, MENU_EDITDAYENTRIES, 0,
				R.string.menu_edit_day_entries);
		item.setIcon(R.drawable.ic_menu_edit);
		item = menu.add(0, MENU_REVIVE_TASK, 0, R.string.menu_revive_task);
		item.setIcon(R.drawable.ic_menu_refresh);

		item = menu.add(0, MENU_DAYREPORT, 0, R.string.menu_reports);
		item.setIcon(R.drawable.ic_menu_info_details);
		item = menu.add(0, MENU_WEEKREPORT, 0, R.string.menu_week_reports);
		item.setIcon(R.drawable.ic_menu_info_details);
		return true;
	}

	/*
	 * Handles item selections (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case MENU_NEW_TASK:
			intent = new Intent(TimeSheetActivity.this, AddTaskHandler.class);
			try {
				startActivityForResult(intent, TASKADD_CODE);
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in onOptionsItemSelected");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		case MENU_REVIVE_TASK:
			intent = new Intent(TimeSheetActivity.this, ReviveTaskHandler.class);
			try {
				startActivityForResult(intent, TASKREVIVE_CODE);
			} catch (RuntimeException e) {
				Log
						.e(TAG,
								"RuntimeException caught in onOptionsItemSelected for ReviveTaskHandler");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		case MENU_EDITDAYENTRIES:
			intent = new Intent(TimeSheetActivity.this,
					EditDayEntriesHandler.class);
			try {
				startActivityForResult(intent, EDIT_CODE);
			} catch (RuntimeException e) {
				Log
						.e(TAG,
								"RuntimeException caught in onOptionsItemSelected for EditDayEntriesHandler");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		case MENU_DAYREPORT:
			intent = new Intent(TimeSheetActivity.this, DayReport.class);
			try {
				startActivityForResult(intent, REPORT_CODE);
			} catch (RuntimeException e) {
				Log
						.e(TAG,
								"RuntimeException caught in onOptionsItemSelected for DayReportHandler");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		case MENU_WEEKREPORT:
			intent = new Intent(TimeSheetActivity.this, WeekReport.class);
			try {
				startActivityForResult(intent, REPORT_CODE);
			} catch (RuntimeException e) {
				Log
						.e(TAG,
								"RuntimeException caught in onOptionsItemSelected for DayReportHandler");
				Log.e(TAG, e.getLocalizedMessage());
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
		switch (requestCode) {
		case TASKADD_CODE:
			// This is a standard resultCode that is sent back if the
			// activity doesn't supply an explicit result. It will also
			// be returned if the activity failed to launch.
			if (resultCode == RESULT_OK) {
				// Our protocol with the sending activity is that it will send
				// text in 'data' as its result.
				if (data != null) {
					db.createTask(data.getAction());
				}
				fillData();
			}
			break;
		case TASKREVIVE_CODE:
			// This one is a special case, since it has its own database
			// adapter, we let it change the state itself rather than passing
			// the result back to us.
			if (resultCode == RESULT_OK) {
				fillData();
			}
			break;
		case TASKEDIT_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String result = data.getAction();
					String oldData = null;

					Bundle extras = data.getExtras();
					if (extras != null) {
						oldData = extras.getString("oldTaskName");
					}

					if (oldData != null && result != null) {
						db.renameTask(oldData, result);
					}
				}
				fillData();
			}
			break;
		}
	}

	/*
	 * For some bizarre reason, this stuff doesn't work... So, I went in another
	 * direction.
	 */
	private void brokenFillData() {
		// Get all of the entries from the database and create the list
		Cursor c = db.fetchAllTaskEntries();
		startManagingCursor(c);

		String[] from = new String[] { TimeSheetDbAdapter.KEY_ROWID,
				TimeSheetDbAdapter.KEY_TASK };
		int[] to = new int[] { R.id.tasklistrow };

		try {
			// Now create an array adapter and set it to display using our row
			timeAdapter = new TimeListAdapter(this,
					android.R.layout.simple_list_item_single_choice, c, from,
					to);
			timeWrapper = new TimeListWrapper(timeAdapter);

			tasksList.setAdapter(timeWrapper);
		} catch (InflateException e) {
			Log.e(TAG, e.toString());
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}
}
