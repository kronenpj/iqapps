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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.database.SQLException;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.view.KeyEvent;

import com.googlecode.iqapps.TimeHelpers;
import com.googlecode.iqapps.IQTimeSheet.MenuItems;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetDbAdapter;
import com.googlecode.iqapps.testtools.Helpers;
import com.googlecode.iqapps.testtools.Positron;
import com.googlecode.iqapps.testtools.ViewShorthand;
import com.jayway.android.robotium.solo.Solo;

/**
 * @author kronenpj
 * 
 */
//@Suppress //#$##
public class WeekReportTest extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	// private Log log = LogFactory.getLog(WeekReportTest.class);
	private static final String TAG = "WeekReportTest";
	private static int test = 0;
	private static final String insertIntoTasks = "INSERT INTO tasks (task, active, usage) "
			+ "VALUES ('";
	private static final String insertIntoTimeSheet = "INSERT INTO timesheet (chargeno, timein, timeout) "
			+ "VALUES (";
	private static final int SLEEPTIME = 50;

	private TimeSheetActivity mActivity;
	private Solo solo;
	private Context mCtx;
	private Instrumentation mInstr;
	private Positron mPositron;
	private TimeSheetDbAdapter db;
	private LinkedList<Activity> activities;
	private ViewShorthand viewShorthand;

	public WeekReportTest() {
		super(TimeSheetActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();

		mActivity = getActivity();
		mInstr = getInstrumentation();
		mCtx = mInstr.getTargetContext();
		solo = new Solo(mInstr, mActivity);
		mPositron = new Positron(mInstr);
		activities = new LinkedList<Activity>();
		viewShorthand = new ViewShorthand(mPositron);

		// Open the database
		db = new TimeSheetDbAdapter(mActivity);
		try {
			db.open();
		} catch (SQLException e) {
			assertFalse(e.toString(), true);
		}

		test++;
		// log.info("value of test: " + test);
		Helpers.backup(solo, mInstr, mActivity);
		// prefBackup();
		db.runSQL("DELETE FROM tasks;");
		db.runSQL("DELETE FROM timesheet; ");
		db.runSQL(insertIntoTasks + Helpers.text1 + "', 1, 40);");
		db.runSQL(insertIntoTasks + Helpers.text2 + "', 0, 30);");
		db.runSQL(insertIntoTasks + Helpers.text3 + "', 1, 50);");
		db.runSQL(insertIntoTasks + Helpers.text4 + "', 1, 20);");
		setupTimeSheetDB();
		// startActivity("com.googlecode.iqapps.IQTimeSheet",
		// "com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
		solo.sleep(SLEEPTIME);
	}

	/**
	 * 
	 */
	private void setupTimeSheetDB() {
		long now = TimeHelpers.millisNow();
		long monday = TimeHelpers.millisToStartOfWeek(now) + 8 * 3600000; // 8am.
		long tuesday = monday + 24 * 3600000;
		long wednesday = tuesday + 24 * 3600000;
		long thursday = wednesday + 24 * 3600000;
		long friday = thursday + 24 * 3600000;

		switch (test) {
		case 1:
			db.runSQL(insertIntoTimeSheet + "2, " + monday + ", "
					+ (monday + 8 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "4, " + tuesday + ", "
					+ (tuesday + 8 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "5, " + wednesday + ", "
					+ (wednesday + 8 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "4, " + thursday + ", "
					+ (thursday + 8 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "2, " + friday + ", "
					+ (friday + 8 * 3600000) + ");");
			break;
		case 2:
			db.runSQL(insertIntoTimeSheet + "2, " + monday + ", "
					+ (monday + 3 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "4, " + (monday + 4 * 3600000)
					+ ", " + (monday + 9 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "4, " + tuesday + ", "
					+ (tuesday + 5 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "2, " + (tuesday + 6 * 3600000)
					+ ", " + (tuesday + 9 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "5, " + wednesday + ", "
					+ (wednesday + 8 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "4, " + thursday + ", "
					+ (thursday + 8 * 3600000) + ");");
			db.runSQL(insertIntoTimeSheet + "2, " + friday + ", "
					+ (friday + 8 * 3600000) + ");");
			break;
		}
	}

	public void tearDown() {
		solo.finishOpenedActivities();
		// prefRestore();
	}

	/**
	 * Restore the database after we're done messing with it.
	 */
	public void test000RestoreDB() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		Helpers.restore(solo, mInstr, mActivity);
	}

	/**
	 * Take a backup of the database so that we can restore the current state
	 * later..
	 */
	@Suppress
	public void test001BackupDB() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		Helpers.backup(solo, mInstr, mActivity);
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void test002EraseDB() {
		eraseDatabase();
	}

	/**
	 * Physically remove the database file
	 */
	private void eraseDatabase() {
		// Delete the databases associated with the project.
		String[] databases = mCtx.databaseList();
		for (int db = 0; db < databases.length; db++) {
			// assertTrue("dbList: " + databases[db], false);
			mCtx.deleteDatabase(databases[db]);
		}
	}

	/**
	 * Restore the database after we're done messing with it.
	 */
	@Suppress
	public void testzzzRestoreDB() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		Helpers.restore(solo, mInstr, mActivity);
	}

	public void testweeklyReportTest1() {
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.WEEK_REPORT.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		solo.sleep(SLEEPTIME);
		assertTrue(solo.waitForActivity("WeekReport", 500));
		// assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("16.00 hours", stringAt("#reportlist.0.1.text"));
		assertTrue(solo.searchText("16.00 hours"));
		// assertEquals(Helpers.text3, stringAt("#reportlist.1.0.text"));
		assertTrue(solo.searchText(Helpers.text3));
		// assertEquals("16.00 hours", stringAt("#reportlist.1.1.text"));
		assertTrue(solo.searchText("16.00 hours"));
		// assertEquals(Helpers.text4, stringAt("#reportlist.2.0.text"));
		assertTrue(solo.searchText(Helpers.text4));
		// assertEquals("8.00 hours", stringAt("#reportlist.2.1.text"));
		assertTrue(solo.searchText("8.00 hours"));
		// assertEquals("Hours worked this week: 40.00",
		// stringAt("#reportlist.3.text"));
		assertTrue(solo.searchText("Hours worked this week: 40.00"));
		solo.goBack();
	}

	public void testweeklyReportTest2() {
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.WEEK_REPORT.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		// solo.sleep(SLEEPTIME);
		assertTrue(solo.waitForActivity("WeekReport", 500));
		// assertEquals(Helpers.text3, stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text3));
		// assertEquals("18.00 hours", stringAt("#reportlist.0.1.text"));
		assertTrue(solo.searchText("18.00 hours"));
		// assertEquals(Helpers.text1, stringAt("#reportlist.1.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("14.00 hours", stringAt("#reportlist.1.1.text"));
		assertTrue(solo.searchText("14.00 hours"));
		// assertEquals(Helpers.text4, stringAt("#reportlist.2.0.text"));
		assertTrue(solo.searchText(Helpers.text4));
		// assertEquals("8.00 hours", stringAt("#reportlist.2.1.text"));
		assertTrue(solo.searchText("8.00 hours"));
		// assertEquals("Hours worked this week: 40.00",
		// stringAt("#reportlist.3.text"));
		assertTrue(solo.searchText("Hours worked this week: 40.00"));
		solo.goBack();
	}

	/**
	 * Evaluate the ViewShorthand path starting from the current activity.
	 * 
	 * @return The result as a String. If the result would be a CharSequence,
	 *         call toString on it.
	 */
	private String stringAt(String path) {
		return stringAt(0, path);
	}

	private String stringAt(int depth, String path) {
		return at(Object.class, activities().get(depth), path).toString();
	}

	/** Get a snapshot of the known activity stack. */
	protected List<Activity> activities() {
		synchronized (activities) {
			for (Iterator<Activity> i = activities.iterator(); i.hasNext();) {
				Activity finished = i.next();
				if (finished.isFinishing()) {
					Log.v(TAG, "Noticed " + finished.getClass().getName()
							+ " finished.");
					i.remove();
				}
			}

			return new ArrayList<Activity>(activities);
		}
	}

	/**
	 * Evaluate the ViewShorthand path starting from the given point.
	 * 
	 * @return The result as the passed type.
	 */
	protected <T> T at(Class<T> asA, Object from, String path) {
		return viewShorthand.evaluate(asA, from, path);
	}

}
