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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.iqapps.TimeHelpers;

/**
 * Main activity for the TimeSheet project. Implements the top-level user
 * interface for the application.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TimeSheetActivity extends ListActivity {
	static PreferenceHelper prefs;

	TimeSheetDbAdapter db;
	Menu optionsMenu;
	private ListView tasksList;
	// private TimeListWrapper timeWrapper;
	// private TimeListAdapter timeAdapter;
	private Cursor taskCursor;
	private Cursor reportCursor;
	private String applicationName;
	private String myPackage;
	private static final String TAG = "TimeSheetActivity";
	private static final int CROSS_DIALOG = 0x40;
	private static final int CONFIRM_RESTORE_DIALOG = 0x41;

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

		prefs = new PreferenceHelper(this);
		myPackage = new String(this.getPackageName());

		try {
			tasksList = (ListView) findViewById(android.R.id.list);
			tasksList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		} catch (Exception e) {
			Log.e(TAG, "tasksList: " + e.toString());
		}

		Resources res = getResources();
		applicationName = res.getString(R.string.app_name);

		try {
			// Register listeners for the list items.
			tasksList.setOnItemClickListener(new OnItemClickListener() {
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

	/** Called when the activity resumed. */
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();

		db = new TimeSheetDbAdapter(this);
		setupDB();

		try {
			fillData();
		} catch (Exception e) {
			Log.e(TAG, "onResume: (fillData) " + e.toString());
		}

		try {
			checkCrossDayClock();
		} catch (Exception e) {
			Log.d(TAG, "onResume: (checkCrossDayClock) " + e.toString());
		}

		try {
			setSelected();
		} catch (Exception e) {
			Log.e(TAG, "onResume: (setSelected) " + e.toString());
		}
		try {
			updateTitleBar();
		} catch (Exception e) {
			Log.e(TAG, "onResume: (updateTitlebar) " + e.toString());
		}
	}

	/** Called when the activity destroyed. */
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");

		try {
			taskCursor.close();
		} catch (Exception e) {
			Log.i(TAG, "onDestroy: (taskCursor) " + e.toString());
		}
		try {
			reportCursor.close();
		} catch (Exception e) {
			Log.i(TAG, "onDestroy: (reportCursor) " + e.toString());
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
			if (timeOut == 0 && lastTaskID != taskID)
				db.closeEntry();
			db.createEntry(taskID);
			fillData();
			setSelected();
			Log.d(TAG, "processChange ID from " + lastTaskID + " to " + taskID);
		}
	}

	/**
	 * Encapsulate what's needed to open the database and make sure something is
	 * in it.
	 */
	void setupDB() {
		Log.d(TAG, "setupDB");

		try {
			db.open();
		} catch (SQLException e) {
			Log.e(TAG, e.toString());
			Toast.makeText(this, e.toString() + " - Exiting", Toast.LENGTH_LONG)
					.show();
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
		} catch (NullPointerException e) {
			Log.e(TAG, e.toString());
		}
	}

	void reloadTaskCursor() {
		taskCursor = db.fetchAllTaskEntries();
	}

	String getTaskFromLocation(long position) {
		Log.d(TAG, "getTaskFromLocation");

		taskCursor.moveToPosition((int) position);
		if (!taskCursor.isAfterLast()) {
			return taskCursor.getString(taskCursor
					.getColumnIndex(TimeSheetDbAdapter.KEY_TASK));
		}
		return null;
	}

	void fillData() {
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

	void updateTitleBar() {
		Log.d(TAG, "updateTitleBar");
		float hoursPerDay = prefs.getHoursPerDay();
		// Display the time accumulated for today with time remaining.
		reportCursor = db.daySummary(false);
		if (reportCursor == null) {
			setTitle(applicationName + " "
					+ String.format("(%.2fh / %.2fh)", 0.0, hoursPerDay));
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
			setTitle(applicationName
					+ " "
					+ String.format("(%.2fh / %.2fh)", accum, hoursPerDay
							- accum));
		}
	}

	void checkCrossDayClock() {
		long lastRowID = db.lastClockEntry();
		long lastTaskID = db.taskIDForLastClockEntry();
		Cursor tempClockCursor = db.fetchEntry(lastRowID);

		long timeOut = -1;
		timeOut = tempClockCursor.getLong(tempClockCursor
				.getColumnIndex(TimeSheetDbAdapter.KEY_TIMEOUT));

		if (timeOut != 0) {
			tempClockCursor.close();
			return;
		}

		// Handle cross-day clockings.
		// tempClockCursor.moveToFirst();
		long now = TimeHelpers.millisNow();
		long lastClockIn = tempClockCursor.getLong(tempClockCursor
				.getColumnIndex(TimeSheetDbAdapter.KEY_TIMEIN));
		// TODO: The following doesn't behave correctly for a cross-year
		// clocking.
		int delta = (TimeHelpers.millisToDayOfYear(now) - TimeHelpers
				.millisToDayOfYear(lastClockIn));
		// If the difference in days is 1, ask. If it's greater than 1, just
		// close it.
		Log.d(TAG,
				"checkCrossDayClock: now=" + now + " / "
						+ TimeHelpers.millisToTimeDate(now));
		Log.d(TAG, "checkCrossDayClock: lastClockIn=" + lastClockIn + " / "
				+ TimeHelpers.millisToTimeDate(lastClockIn));
		Log.d(TAG, "checkCrossDayClock: delta=" + delta);
		// TODO: This should be handled better.
		// Less than one day
		if (delta < 1) {
			Log.d(TAG, "Ignoring.  delta = " + delta);
		} else if (delta <= 2) { // Between one and two days.
			Log.d(TAG, "Opening dialog.  delta = " + delta);
			showDialog(CROSS_DIALOG);
		} else if (delta > 2) { // More than two days.
			Log.d(TAG, "Closing entry.  delta = " + delta + " days.");
			long lastClockDay = TimeHelpers.millisToEndOfDay(lastClockIn);
			Log.d(TAG, "With timeOut = " + lastClockDay);
			db.closeEntry(lastTaskID, lastClockDay);
			taskCursor.requery();
		}

		tempClockCursor.close();
	}

	void setSelected() {
		long lastRowID = db.lastClockEntry();
		long lastTaskID = db.taskIDForLastClockEntry();

		Log.d(TAG, "Last Task Entry Row: " + lastRowID);
		Log.d(TAG, "Last Task ID: " + lastTaskID);
		Cursor tempClockCursor = db.fetchEntry(lastRowID);

		long timeOut = -1;
		if (tempClockCursor == null)
			Log.d(TAG, "Cursor is null...  :(");
		if (!tempClockCursor.moveToFirst())
			Log.d(TAG, "Moving cursor to first failed.");
		else {
			timeOut = tempClockCursor.getLong(tempClockCursor
					.getColumnIndex(TimeSheetDbAdapter.KEY_TIMEOUT));
			Log.d(TAG, "Last clock out at: " + timeOut);
		}
		tempClockCursor.close();

		if (timeOut != 0) {
			Log.d(TAG, "Returning.");
			return;
		}

		Log.d(TAG, "tasksList child count is: " + tasksList.getChildCount());

		tasksList.clearChoices();
		// Iterate over the entire cursor to find the name of the
		// entry that is to be selected.
		taskCursor.moveToFirst();
		while (!taskCursor.isAfterLast()) {
			Log.d(TAG, "Checking item at " + taskCursor.getPosition());
			Log.d(TAG, " Item is " + taskCursor.getLong(0));
			if (taskCursor.getLong(0) == lastTaskID) {
				Log.d(TAG, "  Selecting item at " + taskCursor.getPosition());
				tasksList.setItemChecked(taskCursor.getPosition(), true);
				tasksList.setSelection(taskCursor.getPosition());
				return;
			}
			taskCursor.moveToNext();
		}
	}

	/** Called when the activity is first created to create a dialog. */
	@Override
	protected Dialog onCreateDialog(int dialogId) {
		Dialog dialog = null;
		AlertDialog.Builder builder = null;
		switch (dialogId) {
		case CROSS_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"The last entry is still open from yesterday."
							+ "  What should I do?")
					.setCancelable(false)
					.setPositiveButton("Close",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									long taskID = db.taskIDForLastClockEntry();
									long now = TimeHelpers.millisNow();
									long today = TimeHelpers
											.millisToStartOfDay(now) - 1000;
									db.closeEntry(taskID, today);
									// TODO: The item selected remains so, even
									// though that task has been closed.
									tasksList.clearChoices();
									setSelected();
								}
							})
					.setNegativeButton("Close & Re-open",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									long taskID = db.taskIDForLastClockEntry();
									long now = TimeHelpers.millisNow();
									long today = TimeHelpers
											.millisToStartOfDay(now) - 1000;
									db.closeEntry(taskID, today);
									db.createEntry(taskID, today);
									setSelected();
								}
							});
			dialog = builder.create();
			break;
		case CONFIRM_RESTORE_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"This will overwrite the database." + "  Proceed?")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									db.close();
									if (!SDBackup.doSDRestore(
											TimeSheetDbAdapter.DATABASE_NAME,
											myPackage)) {
										Log.w(TAG, "doSDRestore failed.");
										Toast.makeText(TimeSheetActivity.this,
												"Database restore failed.",
												Toast.LENGTH_LONG).show();
									} else {
										Log.i(TAG, "doSDRestore succeeded.");
										Toast.makeText(TimeSheetActivity.this,
												"Database restore succeeded.",
												Toast.LENGTH_SHORT).show();
									}
									setupDB();
									fillData();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			dialog = builder.create();
			break;
		}

		return dialog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, ActivityCodes.EDIT_ID.ordinal(), 0, R.string.taskedit);
		menu.add(0, ActivityCodes.RETIRE_ID.ordinal(), 0, R.string.taskretire);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == ActivityCodes.EDIT_ID.ordinal()) {
			Log.d(TAG, "Edit task: " + info.id);
			Intent intent = new Intent(TimeSheetActivity.this,
					EditTaskHandler.class);
			intent.putExtra("taskName", getTaskFromLocation(info.id));
			try {
				startActivityForResult(intent, ActivityCodes.TASKEDIT.ordinal());
			} catch (RuntimeException e) {
				Toast.makeText(TimeSheetActivity.this, "RuntimeException",
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, e.getLocalizedMessage());
				Log.e(TAG, "RuntimeException caught.");
			}
			return true;
		}
		if (item.getItemId() == ActivityCodes.RETIRE_ID.ordinal()) {
			db.deactivateTask(getTaskFromLocation(info.id));
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/*
	 * Creates the menu items (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, MenuItems.NEW_TASK.ordinal(), 1,
				R.string.menu_new_task);
		item.setIcon(R.drawable.ic_task_add);
		item = menu.add(0, MenuItems.EDITDAY_ENTRIES.ordinal(), 2,
				R.string.menu_edit_day_entries);
		item.setIcon(R.drawable.ic_menu_edit);
		item = menu
				.add(0, MenuItems.SETTINGS.ordinal(), 3, R.string.menu_prefs);
		item.setIcon(R.drawable.ic_menu_preferences);
		item = menu.add(0, MenuItems.DAY_REPORT.ordinal(), 4,
				R.string.menu_reports);
		item.setIcon(R.drawable.ic_menu_info_details);
		item = menu.add(0, MenuItems.WEEK_REPORT.ordinal(), 5,
				R.string.menu_week_reports);
		item.setIcon(R.drawable.ic_menu_info_details);
		item = menu.add(0, MenuItems.REVIVE_TASK.ordinal(), 6,
				R.string.menu_revive_task);
		item.setIcon(R.drawable.ic_menu_refresh);
		if (prefs.getSDCardBackup()) {
			item = menu.add(0, MenuItems.BACKUP.ordinal(), 7,
					R.string.menu_backup);
			item.setIcon(R.drawable.ic_menu_info_details);
			item = menu.add(0, MenuItems.RESTORE.ordinal(), 8,
					R.string.menu_restore);
			item.setIcon(R.drawable.ic_menu_info_details);
		}
		item = menu.add(0, MenuItems.ABOUT.ordinal(), 9, R.string.menu_about);
		// Hanging on to this so it can be used for testing.
		optionsMenu = menu;
		item.setIcon(R.drawable.ic_menu_info_details);
		return true;
	}

	/*
	 * Handles item selections (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		if (item.getItemId() == MenuItems.NEW_TASK.ordinal()) {
			intent = new Intent(TimeSheetActivity.this, AddTaskHandler.class);
			try {
				startActivityForResult(intent, ActivityCodes.TASKADD.ordinal());
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in onOptionsItemSelected");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		}
		if (item.getItemId() == MenuItems.REVIVE_TASK.ordinal()) {
			intent = new Intent(TimeSheetActivity.this, ReviveTaskHandler.class);
			try {
				startActivityForResult(intent,
						ActivityCodes.TASKREVIVE.ordinal());
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in "
						+ "onOptionsItemSelected for ReviveTaskHandler");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		}
		if (item.getItemId() == MenuItems.EDITDAY_ENTRIES.ordinal()) {
			intent = new Intent(TimeSheetActivity.this,
					EditDayEntriesHandler.class);
			try {
				startActivityForResult(intent, ActivityCodes.EDIT.ordinal());
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in "
						+ "onOptionsItemSelected for EditDayEntriesHandler");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		}
		if (item.getItemId() == MenuItems.DAY_REPORT.ordinal()) {
			intent = new Intent(TimeSheetActivity.this, DayReport.class);
			try {
				startActivityForResult(intent, ActivityCodes.REPORT.ordinal());
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in "
						+ "onOptionsItemSelected for DayReportHandler");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		}
		if (item.getItemId() == MenuItems.WEEK_REPORT.ordinal()) {
			intent = new Intent(TimeSheetActivity.this, WeekReport.class);
			try {
				startActivityForResult(intent, ActivityCodes.REPORT.ordinal());
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in "
						+ "onOptionsItemSelected for WeekReportHandler");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		}
		if (item.getItemId() == MenuItems.SETTINGS.ordinal()) {
			intent = new Intent(TimeSheetActivity.this,
					MyPreferenceActivity.class);
			try {
				startActivityForResult(intent, ActivityCodes.PREFS.ordinal());
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in "
						+ "onOptionsItemSelected for MyPreferenceActivity");
				Log.e(TAG, e.getLocalizedMessage());
			}
			return true;
		}
		if (item.getItemId() == MenuItems.BACKUP.ordinal()) {
			if (prefs.getSDCardBackup()) {
				db.close();
				if (!SDBackup.doSDBackup(TimeSheetDbAdapter.DATABASE_NAME,
						myPackage)) {
					Log.w(TAG, "doSDBackup failed.");
					Toast.makeText(TimeSheetActivity.this,
							"Database backup failed.", Toast.LENGTH_LONG)
							.show();
				} else {
					Log.i(TAG, "doSDBackup succeeded.");
					Toast.makeText(TimeSheetActivity.this,
							"Database backup succeeded.", Toast.LENGTH_SHORT)
							.show();
				}
				setupDB();
			}
			return true;
		}
		if (item.getItemId() == MenuItems.RESTORE.ordinal()) {
			if (prefs.getSDCardBackup()) {
				// TODO: Need a confirmation dialog here.
				showDialog(CONFIRM_RESTORE_DIALOG);
			}
			return true;
		}
		if (item.getItemId() == MenuItems.ABOUT.ordinal()) {
			intent = new Intent(TimeSheetActivity.this, AboutDialog.class);
			try {
				startActivity(intent);
			} catch (RuntimeException e) {
				Log.e(TAG, "RuntimeException caught in "
						+ "onOptionsItemSelected for DayReportHandler");
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
		if (requestCode == ActivityCodes.TASKADD.ordinal()) {
			// This is a standard resultCode that is sent back if the
			// activity doesn't supply an explicit result. It will also
			// be returned if the activity failed to launch.
			if (resultCode == RESULT_OK) {
				// Our protocol with the sending activity is that it will send
				// text in 'data' as its result.
				if (data != null) {
					if (!data.hasExtra("parent"))
						db.createTask(data.getAction());
					else {
						db.createTask(data.getAction(),
								data.getStringExtra("parent"),
								data.getIntExtra("percentage", 100));
					}
				}
				fillData();
			}
		} else if (requestCode == ActivityCodes.TASKREVIVE.ordinal()) {
			// This one is a special case, since it has its own database
			// adapter, we let it change the state itself rather than passing
			// the result back to us.
			if (resultCode == RESULT_OK) {
				fillData();
			}
		} else if (requestCode == ActivityCodes.TASKEDIT.ordinal()) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					String result = data.getAction();
					String oldData = null;

					Bundle extras = data.getExtras();
					if (extras != null) {
						oldData = extras.getString("oldTaskName");
					}

					// TODO: Determine what needs to be done to change these
					// database fields.
					if (data.hasExtra("parent")) {
						long taskID = db.getTaskIDByName(oldData);
						// int oldSplit = db.getSplitTaskFlag(oldData);
						long parentID = db.getTaskIDByName(data
								.getStringExtra("parent"));
						db.alterSplitTask(taskID, parentID,
								data.getIntExtra("percentage", 100),
								data.getIntExtra("split", 0));
					}

					if (oldData != null && result != null) {
						db.renameTask(oldData, result);
					}

				}
				fillData();
			}
		}
	}

	/**
	 * Return the menu object for testing.
	 * 
	 * @return optionMenu
	 */
	public Menu getOptionsMenu() {
		return optionsMenu;
	}
}
