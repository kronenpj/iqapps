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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.googlecode.iqapps.TimeHelpers;

/**
 * Activity to provide an interface to change an entry.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class ChangeEntryHandler extends Activity {
	private static final String TAG = "ChangeEntryHandler";
	private static final int TASKCHOOSE_CODE = 0x01;
	private static final int CHANGETIMEIN_CODE = 0x02;
	private static final int CHANGETIMEOUT_CODE = 0x03;
	private static final int CHANGEDATE_CODE = 0x04;
	private static final int CONFIRM_DIALOG = 0x10;
	private Cursor entryCursor;
	private Button child[];
	private TimeSheetDbAdapter db;
	private long entryID = -1;
	private String newTask = null;
	private long newTimeIn = -1;
	private long newTimeOut = -1;
	private long newDate = -1;
	private int alignMinutes = 0;
	private boolean alignMinutesAuto = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			showChangeLayout();
		} catch (RuntimeException e) {
			Log.e(TAG, e.toString() + " calling showChangeLayout");
		}

		alignMinutes = TimeSheetActivity.prefs.getAlignMinutes();
		alignMinutesAuto = TimeSheetActivity.prefs.getAlignMinutesAuto();

		Button changeButton = (Button) findViewById(R.id.changealign);
		changeButton.setText("Align (" + alignMinutes + " min)");
		if (alignMinutesAuto)
			changeButton.setClickable(false);

		db = new TimeSheetDbAdapter(this);
		setupDB();

		retrieveData();
		fillData();
	}

	/** Called when the activity destroyed. */
	@Override
	public void onDestroy() {
		entryCursor.close();
		db.close();
		super.onDestroy();
	}

	/** Called when the activity is first created to create a dialog. */
	@Override
	protected Dialog onCreateDialog(int dialogId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete this entry?")
				.setCancelable(true).setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent();
								intent.putExtra(EditDayEntriesHandler.ENTRY_ID,
										entryID);
								intent.setAction("delete");
								setResult(RESULT_OK, intent);
								ChangeEntryHandler.this.finish();
							}
						}).setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		return alert;
	}

	protected void showChangeLayout() {
		setContentView(R.layout.changeentry);

		child = new Button[] { (Button) findViewById(R.id.defaulttask),
				(Button) findViewById(R.id.date),
				(Button) findViewById(R.id.starttime),
				(Button) findViewById(R.id.endtime),
				(Button) findViewById(R.id.changeok),
				(Button) findViewById(R.id.changecancel),
				(Button) findViewById(R.id.changedelete),
				(Button) findViewById(R.id.changealign) };

		for (int count = 0; count < child.length; count++) {
			try {
				final int index = count;
				child[index].setOnClickListener(mButtonListener);
			} catch (NullPointerException e) {
				Toast.makeText(ChangeEntryHandler.this, "NullPointerException",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void retrieveData() {
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Log.d(TAG, "Extras bundle is empty.");
			return;
		}
		entryID = extras.getLong(EditDayEntriesHandler.ENTRY_ID);
		entryCursor = db.fetchEntry(entryID);

		long chargeNo = entryCursor.getLong(entryCursor
				.getColumnIndex(TimeSheetDbAdapter.KEY_CHARGENO));
		newTask = db.getTaskNameByID(chargeNo);

		newTimeIn = entryCursor.getLong(entryCursor
				.getColumnIndex(TimeSheetDbAdapter.KEY_TIMEIN));

		newTimeOut = entryCursor.getLong(entryCursor
				.getColumnIndex(TimeSheetDbAdapter.KEY_TIMEOUT));

		newDate = TimeHelpers.millisToStartOfDay(newTimeIn);
	}

	private void fillData() {
		int hour, minute;

		child[0].setText(newTask);
		child[1].setText(TimeHelpers.millisToDate(newDate));

		if (alignMinutesAuto) {
			newTimeIn = TimeHelpers.millisToAlignMinutes(newTimeIn,
					alignMinutes);
		}
		hour = TimeHelpers.millisToHour(newTimeIn);
		minute = TimeHelpers.millisToMinute(newTimeIn);
		child[2].setText(TimeHelpers.formatHours(hour) + ":"
				+ TimeHelpers.formatMinutes(minute));

		if (newTimeOut == 0) {
			child[3].setText("Now");
		} else {
			if (alignMinutesAuto) {
				newTimeOut = TimeHelpers.millisToAlignMinutes(newTimeOut,
						alignMinutes);
			}
			hour = TimeHelpers.millisToHour(newTimeOut);
			minute = TimeHelpers.millisToMinute(newTimeOut);
			child[3].setText(TimeHelpers.formatHours(hour) + ":"
					+ TimeHelpers.formatMinutes(minute));
		}
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
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			finish();
		}
	}

	/**
	 * This method is what is registered with the button to cause an action to
	 * occur when it is pressed.
	 */
	private OnClickListener mButtonListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent;
			// Perform action on selected list item.

			Log.d(TAG, "onClickListener view id: " + v.getId());
			Log.d(TAG, "onClickListener defaulttask id: " + R.id.defaulttask);

			switch (v.getId()) {
			case R.id.changecancel:
				setResult(RESULT_CANCELED, (new Intent()).setAction("cancel"));
				finish();
				break;
			case R.id.defaulttask:
				intent = new Intent(ChangeEntryHandler.this,
						ChangeTaskList.class);
				try {
					startActivityForResult(intent, TASKCHOOSE_CODE);
				} catch (RuntimeException e) {
					Log.e(TAG, "startActivity ChangeTaskList: " + e.toString());
				}
				break;
			case R.id.date:
				intent = new Intent(ChangeEntryHandler.this, ChangeDate.class);
				intent.putExtra("time", newDate);
				try {
					startActivityForResult(intent, CHANGEDATE_CODE);
				} catch (RuntimeException e) {
					Log.e(TAG, "startActivity ChangeDate: " + e.toString());
				}
				break;
			case R.id.starttime:
				intent = new Intent(ChangeEntryHandler.this, ChangeTime.class);
				intent.putExtra("time", newTimeIn);
				try {
					startActivityForResult(intent, CHANGETIMEIN_CODE);
				} catch (RuntimeException e) {
					Log.e(TAG, "startActivity ChangeTime: " + e.toString());
				}
				break;
			case R.id.endtime:
				intent = new Intent(ChangeEntryHandler.this, ChangeTime.class);
				intent.putExtra("time", newTimeOut);
				try {
					startActivityForResult(intent, CHANGETIMEOUT_CODE);
				} catch (RuntimeException e) {
					Log.e(TAG, "startActivity ChangeTime: " + e.toString());
				}
				break;
			case R.id.changealign:
				newTimeIn = TimeHelpers.millisToAlignMinutes(newTimeIn,
						alignMinutes);
				if (newTimeOut != 0) {
					newTimeOut = TimeHelpers.millisToAlignMinutes(newTimeOut,
							alignMinutes);
				}
				fillData();
				break;
			case R.id.changeok:
				intent = new Intent();
				intent.putExtra(EditDayEntriesHandler.ENTRY_ID, entryID);
				// Push task title into response.
				intent.putExtra(TimeSheetDbAdapter.KEY_TASK, newTask);
				// Push start and end time milliseconds into response
				// bundle.
				intent.putExtra(TimeSheetDbAdapter.KEY_TIMEIN, newTimeIn);
				intent.putExtra(TimeSheetDbAdapter.KEY_TIMEOUT, newTimeOut);
				// TODO: Push option to adjust previous/next task time along
				// with this one.
				intent.setAction("accept");
				setResult(RESULT_OK, intent);
				finish();
				break;
			case R.id.changedelete:
				showDialog(CONFIRM_DIALOG);
				break;
			}
		}
	};

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
		case TASKCHOOSE_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Log.d(TAG, "onActivityResult action: " + data.getAction());
					// db.updateEntry(entryID, Long.parseLong(data.getAction()),
					// null, -1, -1);
					newTask = db
							.getTaskNameByID(Long.valueOf(data.getAction()));
					fillData();
				}
			}
			break;
		case CHANGETIMEIN_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					newTimeIn = Long.valueOf(data.getAction());
					Log.d(TAG, "onActivityResult action: " + newTimeIn);
					fillData();
				}
			}
			break;
		case CHANGETIMEOUT_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					newTimeOut = Long.valueOf(data.getAction());
					Log.d(TAG, "onActivityResult action: " + newTimeOut);
					fillData();
				}
			}
			break;
		case CHANGEDATE_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Log.d(TAG, "onActivityResult action: " + data.getAction());
					newDate = Long.valueOf(data.getAction());
					newTimeIn = TimeHelpers.millisSetTime(newDate, TimeHelpers
							.millisToHour(newTimeIn), TimeHelpers
							.millisToMinute(newTimeIn));
					newTimeOut = TimeHelpers.millisSetTime(newDate, TimeHelpers
							.millisToHour(newTimeOut), TimeHelpers
							.millisToMinute(newTimeOut));
					fillData();
				}
			}
			break;
		}
	}
}