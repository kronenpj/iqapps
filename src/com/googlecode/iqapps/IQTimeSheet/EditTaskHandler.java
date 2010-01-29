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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity to provide an interface to change a task name and, potentially,
 * other attributes of a task.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class EditTaskHandler extends Activity {
	private static final String TAG = "EditTaskHandler";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "In onCreate.");
		showTaskEdit();
	}

	private EditText textField;
	private Button child[];
	private String oldData;

	protected void showTaskEdit() {
		Log.d(TAG, "Changing to addtask layout.");
		setContentView(R.layout.addtask);

		textField = (EditText) findViewById(R.id.EditTask);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			oldData = extras.getString("taskName");
			textField.setText(oldData);
		}

		child = new Button[] { (Button) findViewById(R.id.ChangeTask),
				(Button) findViewById(R.id.CancelEdit) };

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
	 * This method is what is registered with the button to cause an action to
	 * occur when it is pressed.
	 */
	private OnClickListener mButtonListener = new OnClickListener() {
		public void onClick(View v) {
			// Perform action on selected list item.

			String item = ((Button) v).getText().toString();
			if (item.equalsIgnoreCase("cancel")) {
				setResult(RESULT_CANCELED, (new Intent()).setAction(item));
			} else {
				String result = textField.getText().toString();
				Intent intent = new Intent();
				intent.setAction(result);
				intent.putExtra("oldTaskName", oldData);
				setResult(RESULT_OK, intent);
			}
			finish();
		}
	};
}