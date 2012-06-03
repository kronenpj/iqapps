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
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Activity to provide an interface to add a task to the database.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class AddTaskHandler extends Activity {
	private static final String TAG = "AddTaskHandler";

	private EditText textField;
	private Button child[];
	private TextView parentLabel;
	private Spinner taskSpinner;
	private CheckBox splitTask;
	private EditText percentLabel;
	private TextView percentSymbol;
	private SeekBar percentSlider;
	private TimeSheetDbAdapter db;
	private Cursor parents;

	/** Called when the activity is first created. */
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// Log.d(TAG, "In onCreate.");
	// }

	/** Called when the activity is first created. */
	public void onResume() {
		super.onResume();
		Log.d(TAG, "In onResume.");

		showTaskAdd();

		db = new TimeSheetDbAdapter(this);
		try {
			db.open();
		} catch (SQLException e) {
			Log.i(TAG, "Database open failed.");
			finish();
		}

		parents = db.fetchParentTasks();
		startManagingCursor(parents);
		String[] items = new String[parents.getCount()];
		parents.moveToFirst();
		int i = 0;
		while (!parents.isAfterLast()) {
			items[i] = new String(parents.getString(1));
			parents.moveToNext();
			i++;
		}

		taskSpinner.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items));

		percentSlider.setMax(100);
		// TODO: Retrieve from database or default to 100 if none.
		percentSlider.setProgress(100);
		percentLabel.setText("100");
	}

	protected void showTaskAdd() {
		Log.d(TAG, "Changing to addtask layout.");
		setContentView(R.layout.addtask);

		textField = (EditText) findViewById(R.id.EditTask);
		child = new Button[] { (Button) findViewById(R.id.ChangeTask),
				(Button) findViewById(R.id.CancelEdit) };
		parentLabel = (TextView) findViewById(R.id.ParentLabel);
		taskSpinner = (Spinner) findViewById(R.id.TaskSpinner);
		splitTask = (CheckBox) findViewById(R.id.SplitTask);
		percentLabel = (EditText) findViewById(R.id.PercentLabel);
		percentSymbol = (TextView) findViewById(R.id.PercentSymbol);
		percentSlider = (SeekBar) findViewById(R.id.PercentSlider);

		splitTask.setOnClickListener(mCheckBoxListener);

		percentSlider.setOnSeekBarChangeListener(mSeekBarListener);
		percentLabel.setOnFocusChangeListener(mTextListener);
		percentLabel.setOnEditorActionListener(mEditorListener);

		for (int count = 0; count < child.length; count++) {
			try {
				final int index = count;
				child[index].setOnClickListener(mButtonListener);
			} catch (NullPointerException e) {
				Log.e(TAG, "NullPointerException adding listener to button.");
			}
		}
	}

	/**
	 * 
	 */
	private void closeCursorDB() {
		try {
			parents.close();
		} catch (SQLException e) {
			Log.i(TAG, "Cursor close: " + e.toString());
		}
		try {
			db.close();
		} catch (SQLException e) {
			Log.i(TAG, "Database close: " + e.toString());
		}
	}

	/**
	 * This method is what is registered with the button to cause an action to
	 * occur when it is pressed.
	 */
	private OnClickListener mButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Perform action chosen by the user.

			String item = ((Button) v).getText().toString();
			if (item.equalsIgnoreCase("cancel")) {
				setResult(RESULT_CANCELED, (new Intent()).setAction(item));
			} else {
				String result = textField.getText().toString();
				Intent mIntent = new Intent();
				mIntent.putExtra("split", splitTask.isChecked());
				if (splitTask.isChecked()) {
					mIntent.putExtra("parent",
							(String) taskSpinner.getSelectedItem());
					mIntent.putExtra("percentage", percentSlider.getProgress());
				}
				mIntent.setAction(result);
				setResult(RESULT_OK, mIntent);
			}
			closeCursorDB();
			finish();
		}
	};

	/**
	 * This method is what is registered with the checkbox to cause an action to
	 * occur when it is pressed.
	 */
	private OnClickListener mCheckBoxListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Perform action on selected list item.

			if (((CheckBox) v).isChecked()) {
				parentLabel.setVisibility(View.VISIBLE);
				taskSpinner.setVisibility(View.VISIBLE);
				percentLabel.setVisibility(View.VISIBLE);
				percentSymbol.setVisibility(View.VISIBLE);
				percentSlider.setVisibility(View.VISIBLE);
			} else {
				parentLabel.setVisibility(View.INVISIBLE);
				taskSpinner.setVisibility(View.INVISIBLE);
				percentLabel.setVisibility(View.INVISIBLE);
				percentSymbol.setVisibility(View.INVISIBLE);
				percentSlider.setVisibility(View.INVISIBLE);
			}
		}
	};

	/**
	 * This method is registered with the percent slider to cause an action to
	 * occur when it is changed.
	 */
	private OnSeekBarChangeListener mSeekBarListener = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// percentLabel.setText(String.valueOf(seekBar.getProgress()));
			percentLabel.setText(String.valueOf(progress));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			seekBar.requestFocus();
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	};

	/**
	 * This method is registered with the percent slider to cause an action to
	 * occur when it is changed.
	 */
	private OnFocusChangeListener mTextListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				// percentLabel.setSelected(true);
			} else {
				try {
					int temp = Integer.valueOf(((TextView) v).getText()
							.toString());
					if (temp > 100)
						temp = 100;
					if (temp < 0)
						temp = 0;
					percentSlider.setProgress(temp);
				} catch (NumberFormatException e) {
					percentLabel.setText(String.valueOf(percentSlider
							.getProgress()));
				}
			}
		}
	};

	/**
	 * This method is registered with the percent label to cause an action to
	 * occur when it is changed.
	 */
	private OnEditorActionListener mEditorListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			try {
				int temp = Integer.valueOf(v.getText().toString());
				if (temp > 100)
					temp = 100;
				if (temp < 0)
					temp = 0;
				percentSlider.setProgress(temp);
			} catch (NumberFormatException e) {
				percentLabel
						.setText(String.valueOf(percentSlider.getProgress()));
			}
			v.clearFocus();
			return true;
		}
	};
}