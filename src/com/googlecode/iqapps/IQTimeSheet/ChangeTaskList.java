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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Activity to provide an interface to choose a new task for an entry to be
 * changed to.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class ChangeTaskList extends ListActivity {
	private TimeSheetDbAdapter db;
	private ListView taskList;
	private Cursor taskCursor;
	private final static String TAG = "ChangeTaskList";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Choose a new task");
		setContentView(R.layout.revivetask);

		taskList = (ListView) findViewById(android.R.id.list);
		taskList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		db = new TimeSheetDbAdapter(this);
		try {
			setupDB();
		} catch (Exception e) {
			Log.d(TAG, "setupDB: " + e.toString());
		}

		try {
			fillData();
		} catch (Exception e) {
			Log.d(TAG, "fillData: " + e.toString());
		}

		try {
			// Register listeners for the list items.
			taskList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					Cursor listCursor = (Cursor) parent
							.getItemAtPosition(position);
					String taskName = listCursor.getString(listCursor
							.getColumnIndex(TimeSheetDbAdapter.KEY_TASK));
					long taskID = db.getTaskIDByName(taskName);
					setResult(RESULT_OK, (new Intent()).setAction(Long.valueOf(
							taskID).toString()));
					try {
						listCursor.close();
					} catch (Exception e) {
						Log.e(TAG, "onItemClick: " + e.toString());
					}
					finish();
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.toString());
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

	private void reloadTaskCursor() {
		taskCursor = db.fetchAllTaskEntries();
	}

	private void fillData() {
		// Get all of the entries from the database and create the list
		reloadTaskCursor();

		// String[] items = new String[taskCursor.getCount()];
		// taskCursor.moveToFirst();
		// int i = 0;
		// while (!taskCursor.isAfterLast()) {
		// items[i] = new String(taskCursor.getString(1));
		// taskCursor.moveToNext();
		// i++;
		// }

		// taskList.setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_single_choice, items));

		// Populate list
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, taskCursor,
				new String[] { TimeSheetDbAdapter.KEY_TASK },
				new int[] { android.R.id.text1 });

		taskList.setAdapter(adapter);
	}
}
