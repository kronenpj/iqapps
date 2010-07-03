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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activity to allow the user to select a task to revive after being "deleted."
 * Tasks are "never" removed from the database so that entries always reference
 * a valid task.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class ReviveTaskHandler extends ListActivity {
	private static final String TAG = "ReviveTaskHandler";
	private TimeSheetDbAdapter db;
	private ListView tasksList;

	private Cursor taskCursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "In onCreate.");
		setTitle("Select a task to reactivate");
		setContentView(R.layout.revivetask);

		try {
			tasksList = (ListView) findViewById(android.R.id.list);
			tasksList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		db = new TimeSheetDbAdapter(this);
		setupDB();

		try {
			fillData();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		try {
			// Register listeners for the list items.
			tasksList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					String taskName = (String) parent
							.getItemAtPosition(position);
					reactivateTask(taskName);
					setResult(RESULT_OK, (new Intent()));
					finish();
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.toString());
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
		}
	}

	/** Called when the activity destroyed. */
	@Override
	public void onDestroy() {
		try {
			taskCursor.close();
		} catch (Exception e) {
			Log.e(TAG, "onDestroy: " + e.toString());
		}

		db.close();
		super.onDestroy();
	}

	private void reloadTaskCursor() {
		taskCursor = db.fetchAllDisabledTasks();
	}

	private void reactivateTask(String taskName) {
		Log.d(TAG, "Reactivating task " + taskName);
		db.activateTask(taskName);
	}

	private void fillData() {
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
	}
}