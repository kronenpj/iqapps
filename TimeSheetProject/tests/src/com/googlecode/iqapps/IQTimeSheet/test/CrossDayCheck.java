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

/**
 * @author      (classes and interfaces only, required)
 * @version     (classes and interfaces only, required. See footnote 1)
 * @param       (methods and constructors only)
 * @return      (methods only)
 * @exception   (@throws is a synonym added in Javadoc 1.2)
 * @see         
 */
package com.googlecode.iqapps.IQTimeSheet.test;

import java.util.ArrayList;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.iqapps.TimeHelpers;
import com.googlecode.iqapps.IQTimeSheet.MenuItems;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetDbAdapter;
import com.googlecode.iqapps.testtools.Helpers;
import com.googlecode.iqapps.testtools.Positron;
import com.jayway.android.robotium.solo.Solo;

/**
 * @author kronenpj
 * 
 */
@Suppress
public class CrossDayCheck extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	// private Log log = LogFactory.getLog(CrossDayCheck.class);
	private static final String TAG = "CrossDayCheck";
	private static final int SLEEPTIME = 50;
	private static long now = 0;
	private static final String insertIntoTasks = "INSERT INTO tasks (task, active, usage) "
			+ "VALUES ('";
	private static final String insertIntoTimeSheet = "INSERT INTO timesheet (chargeno, timein, timeout) "
			+ "VALUES (";
	private TimeSheetActivity mActivity;
	private Instrumentation mInstr;
	private Solo solo;
	private Positron mPositron;
	private TimeSheetDbAdapter db;

	public CrossDayCheck() {
		super(TimeSheetActivity.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		mInstr = getInstrumentation();
		solo = new Solo(mInstr, mActivity);
		mPositron = new Positron(mInstr);

		Helpers.backup(solo, mInstr, mActivity);
		// mPositron.backup();
		Helpers.prefBackup(mInstr);

		// Reset the database
		db = new TimeSheetDbAdapter(mActivity);
		try {
			db.open();
		} catch (SQLException e) {
			assertFalse(e.toString(), true);
		}

		db.runSQL("DELETE FROM tasks;");
		db.runSQL("DELETE FROM timesheet;");
		db.runSQL(insertIntoTasks + Helpers.text1 + "', 1, 40);");
		db.runSQL(insertIntoTasks + Helpers.text2 + "', 0, 30);");
		db.runSQL(insertIntoTasks + Helpers.text3 + "', 1, 50);");
		db.runSQL(insertIntoTasks + Helpers.text4 + "', 1, 20);");
		now = TimeHelpers.millisNow();
		setupTimeSheetDB();
		// startActivity("com.googlecode.iqapps.IQTimeSheet",
		// "com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
		Helpers.sleep();
	}

	/**
	 * 
	 */
	private void setupTimeSheetDB() {
		long yesterday = TimeHelpers.millisToStartOfDay(now) - 8 * 3600000; // 5pm

		db.runSQL(insertIntoTimeSheet + "2, " + yesterday + ", " + 0 + ");");
	}

	public void tearDown() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		Helpers.prefRestore(mInstr);
		Helpers.restore(solo, mInstr, mActivity);
		// mPositron.restore();

		solo.finishOpenedActivities();
	}

	public void test021crossDayClockTest1() {
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.DAY_REPORT.ordinal()).getItemId();
		Assert.assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("DayReport", 500));
		try {
			// mPositron.existsAt("#reportlist.0.text");
			assertFalse(
					"There should be no entries in the list, but found one.",
					solo.searchText(Helpers.text1));
			// fail("There should be no entries in the list, but found one.");
		} catch (Exception e) {
		}
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("8.00 hours",
		// mPositron.stringAt("#reportlist.0.1.text"));
		assertTrue(solo.searchText("8.00 hours"));
	}

	public void test022crossDayClockTest2() {
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.DAY_REPORT.ordinal()).getItemId();
		Assert.assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("DayReport", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		long midnight = TimeHelpers.millisToStartOfDay(now);
		float hours = TimeHelpers.calculateDuration(midnight, now);
		Toast.makeText(mActivity, "About to request android:list",
				Toast.LENGTH_LONG).show();
		// String appHourString = mPositron.stringAt("#android:list.0.1.text");
		ArrayList<ListView> listViews = solo.getCurrentListViews();
		String appHourString = null;
		try {
			appHourString = listViews.get(0).getChildAt(1).toString();
		} catch (CursorIndexOutOfBoundsException e) {
			Log.e(TAG, e.toString());
		}

		float appHours = Float.valueOf(appHourString.substring(0,
				appHourString.indexOf(' ')));
		assertTrue(solo.searchText(String.valueOf(hours)));
		float delta = hours - appHours;
		if (delta < 0)
			delta = -delta;
		assertTrue(
				"Difference between hours and appHours is > 0.02 hours.  Was "
						+ delta + " hours.", delta < 0.02);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);
		// assertEquals("8.00 hours",
		// mPositron.stringAt("#reportlist.0.1.text"));
		assertTrue(solo.searchText("8.00 hours"));
	}
}
