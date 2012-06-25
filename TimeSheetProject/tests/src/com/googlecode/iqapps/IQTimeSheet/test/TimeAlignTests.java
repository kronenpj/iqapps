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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either exmPositron.press or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.iqapps.IQTimeSheet.test;

import android.app.Instrumentation;
import android.content.Context;
import android.database.SQLException;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.ListView;

import com.googlecode.iqapps.Helpers;
import com.googlecode.iqapps.TimeHelpers;
import com.googlecode.iqapps.IQTimeSheet.MenuItems;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetDbAdapter;
import com.jayway.android.robotium.solo.Solo;

public class TimeAlignTests extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	// private Log log = LogFactory.getLog(TimeAlignTests.class);
	private static final String TAG = "TimeAlignTests";
	private static int SLEEPTIME = 99; // Milliseconds

	private Solo solo;

	private TimeSheetActivity mActivity;
	private ListView mView;
	private Context mCtx;
	private Instrumentation mInstr;
	// private Positron mPositron;
	private TimeSheetDbAdapter db;

	public TimeAlignTests() {
		super(TimeSheetActivity.class);
	}

	public void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		mInstr = getInstrumentation();
		mCtx = mInstr.getTargetContext();
		solo = new Solo(mInstr, getActivity());
		// mPositron = new Positron(mInstr);

		Helpers.backup(solo, mInstr, mActivity);
		// mPositron.backup();
		// mPositron.prefBackup();
		db = new TimeSheetDbAdapter(mActivity);
		try {
			db.open();
		} catch (SQLException e) {
			assertFalse(e.toString(), true);
		}
		db.runSQL("DELETE FROM tasks; "
				+ "INSERT INTO tasks (task, active, usage) " + "VALUES ('"
				+ Helpers.text1 + "', 1, 40); "
				+ "INSERT INTO tasks (task, active, usage) " + "VALUES ('"
				+ Helpers.text2 + "', 0, 30);"
				+ "INSERT INTO tasks (task, active, usage) " + "VALUES ('"
				+ Helpers.text3 + "', 1, 50); "
				+ "INSERT INTO tasks (task, active, usage) " + "VALUES ('"
				+ Helpers.text4 + "', 1, 20);");
		db.runSQL("DELETE FROM timesheet; ");
		// startActivity("com.googlecode.iqapps.IQTimeSheet",
		// "com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
		solo.sleep(SLEEPTIME);
	}

	public void tearDown() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		Helpers.restore(solo, mInstr, mActivity);

		solo.finishOpenedActivities();
		// mPositron.prefRestore();
		// mPositron.restore();
	}

	public void testverifyTime2Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 0);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 59);
		setAlignTimePreferenceViaMenu(1); // 2-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		solo.searchText(Helpers.text1);
		// assertEquals("08:00 to 10:59",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:00 to 10:59");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime3Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 1);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 59);
		setAlignTimePreferenceViaMenu(2); // 3-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:01 to 10:59",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:01 to 10:59");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime4Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 1);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 58);
		setAlignTimePreferenceViaMenu(3); // 4-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:01 to 10:58",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:01 to 10:58");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime5Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 2);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 58);
		setAlignTimePreferenceViaMenu(4); // 5-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:02 to 10:58",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:02 to 10:58");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime6Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 2);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 58);
		setAlignTimePreferenceViaMenu(5); // 6-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:02 to 10:58",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:02 to 10:58");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime10Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 4);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 56);
		setAlignTimePreferenceViaMenu(6); // 10-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:04 to 10:56",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:04 to 10:56");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime12Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 5);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 55);
		setAlignTimePreferenceViaMenu(7); // 12-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:05 to 10:55",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:05 to 10:55");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime15Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 7);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 53);
		setAlignTimePreferenceViaMenu(8); // 15-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:07 to 10:53",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:07 to 10:53");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime20Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 9);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 51);
		setAlignTimePreferenceViaMenu(9); // 20-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:09 to 10:51",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:09 to 10:51");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime30Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 14);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 46);
		setAlignTimePreferenceViaMenu(10); // 30-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:14 to 10:46",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:14 to 10:46");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyTime60Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 29);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 31);
		setAlignTimePreferenceViaMenu(11); // 60-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:29 to 10:31",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:29 to 10:31");
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	public void testverifyAlignEdge1() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 3);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 56);
		setAlignTimePreferenceViaMenu(5); // 6-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:03 to 10:56",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:03 to 10:56");
		alignWithButton("08:06 to 10:54");
		checkDayReport("2.80");
	}

	public void testverifyAlignEdge2() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 8);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 51);
		setAlignTimePreferenceViaMenu(5); // 6-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:08 to 10:51",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:08 to 10:51");
		alignWithButton("08:06 to 10:54");
		checkDayReport("2.80");
	}

	public void testverifyAlignEdge3() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 9);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 50);
		setAlignTimePreferenceViaMenu(5); // 6-minute
		pressTaskTwice(1);
		db.runSQL("UPDATE timesheet SET timein=" + eightAM + ", timeout="
				+ elevenAM + ";");
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:09 to 10:50",
		// mPositron.stringAt("#reportlist.0.1.text"));
		solo.searchText("08:09 to 10:50");
		alignWithButton("08:12 to 10:48");
		checkDayReport("2.60");
	}

	public void testDailyReport() {
		long now = TimeHelpers.millisNow();

		setAlignTimePreferenceViaMenu(5); // 6-minute

		pressTaskTwice(1);
		pressTaskTwice(0);
		pressTaskTwice(3);

		long startTime = TimeHelpers.millisSetTime(now, 8, 2);
		long stopTime = TimeHelpers.millisSetTime(now, 9, 57);
		db.runSQL("UPDATE timesheet SET timein=" + startTime + ", timeout="
				+ stopTime + " where chargeno=2;");

		startTime = TimeHelpers.millisSetTime(now, 9, 58);
		stopTime = TimeHelpers.millisSetTime(now, 10, 32);
		db.runSQL("UPDATE timesheet SET timein=" + startTime + ", timeout="
				+ stopTime + " where chargeno=4;");

		startTime = TimeHelpers.millisSetTime(now, 10, 32);
		stopTime = TimeHelpers.millisSetTime(now, 11, 58);
		db.runSQL("UPDATE timesheet SET timein=" + startTime + ", timeout="
				+ stopTime + " where chargeno=5;");

		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("08:02 to 09:57",
		// mPositron.stringAt("#reportlist.0.1.text"));
		assertTrue(solo.searchText("08:02 to 09:57"));
		// assertEquals(Helpers.text3,
		// mPositron.stringAt("#reportlist.1.0.text"));
		assertTrue(solo.searchText(Helpers.text3));
		// assertEquals("09:58 to 10:32",
		// mPositron.stringAt("#reportlist.1.1.text"));
		assertTrue(solo.searchText("09:58 to 10:32"));
		// assertEquals(Helpers.text4,
		// mPositron.stringAt("#reportlist.2.0.text"));
		assertTrue(solo.searchText(Helpers.text4));
		// assertEquals("10:32 to 11:58",
		// mPositron.stringAt("#reportlist.2.1.text"));
		assertTrue(solo.searchText("09:58 to 10:32"));
		alignWithButton("08:00 to 10:00");

		// mPositron.press(PositronAPI.Key.MENU, PositronAPI.Key.RIGHT,
		// PositronAPI.Key.ENTER);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		// mPositron.press(PositronAPI.Key.DOWN);
		// mPositron.press(PositronAPI.Key.ENTER);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);

		// mPositron.press(PositronAPI.Key.DOWN, PositronAPI.Key.DOWN,
		// PositronAPI.Key.LEFT, PositronAPI.Key.DOWN,
		// PositronAPI.Key.DOWN, PositronAPI.Key.ENTER); // Align.
		// solo.clickOnText(solo
		// .getString(com.googlecode.iqapps.IQTimeSheet.R.string.slign));
		solo.clickOnText("Align");
		// mPositron.press(PositronAPI.Key.UP, PositronAPI.Key.ENTER); // Accept
		solo.clickOnText(solo
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.accept));
		// assertEquals("10:00 to 10:30",
		// mPositron.stringAt("#reportlist.1.1.text"));
		assertTrue(solo.searchText("10:00 to 10:30"));
		solo.goBack();

		// mPositron.press(PositronAPI.Key.MENU, PositronAPI.Key.RIGHT,
		// PositronAPI.Key.ENTER);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		// mPositron.press(PositronAPI.Key.DOWN, PositronAPI.Key.DOWN);
		// mPositron.press(PositronAPI.Key.ENTER);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		// mPositron.press(PositronAPI.Key.DOWN, PositronAPI.Key.DOWN,
		// PositronAPI.Key.LEFT, PositronAPI.Key.DOWN,
		// PositronAPI.Key.DOWN, PositronAPI.Key.ENTER); // Align.
		// solo.clickOnText(solo
		// .getString(com.googlecode.iqapps.IQTimeSheet.R.string.slign));
		solo.clickOnText("Align");
		// mPositron.press(PositronAPI.Key.UP, PositronAPI.Key.ENTER); // Accept
		solo.clickOnText(solo
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.accept));
		// assertEquals("10:30 to 12:00",
		// mPositron.stringAt("#reportlist.2.1.text"));
		assertTrue(solo.searchText("10:30 to 12:00"));
		// mPositron.press(PositronAPI.Key.BACK);
		solo.goBack();

		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.DAY_REPORT.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("DayReport", 500));
		// log.info(TAG + ": Post wait for DayReport.");
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals("2.00 hours",
		// mPositron.stringAt("#reportlist.0.1.text"));
		assertTrue(solo.searchText("2.00 hours"));
		// assertEquals(Helpers.text4,
		// mPositron.stringAt("#reportlist.1.0.text"));
		assertTrue(solo.searchText(Helpers.text4));
		// assertEquals("1.50 hours",
		// mPositron.stringAt("#reportlist.1.1.text"));
		assertTrue(solo.searchText("1.50 hours"));
		// assertEquals(Helpers.text3,
		// mPositron.stringAt("#reportlist.2.0.text"));
		assertTrue(solo.searchText(Helpers.text3));
		// assertEquals("0.50 hours",
		// mPositron.stringAt("#reportlist.2.1.text"));
		assertTrue(solo.searchText("0.50 hours"));
		// assertEquals("Hours worked this day: 4.00",
		// mPositron.stringAt("#reportlist.3.text"));
		assertTrue(solo.searchText("Hours worked this day: 4.00"));
		// mPositron.press(PositronAPI.Key.BACK);
		solo.goBack();
	}

	private void pressTaskTwice(int taskNo) {
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
		for (int i = 0; i < taskNo; i++)
			mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME * 2); // Needed to make the test consistent.
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
	}

	private void setAlignTimePreferenceViaMenu(int downCount) {
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.SETTINGS.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("MyPreferenceActivity", 500));
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);
		while (solo.scrollUpList(0))
			;
		for (int i = 0; i < downCount; i++) {
			mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
			solo.sleep(SLEEPTIME);
		}
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);
		for (int i = downCount; i < Helpers.alignments; i++)
			mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.goBack();
	}

	private void alignWithButton(String expected) {
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		// mPositron.press(PositronAPI.Key.ENTER);
		// mPositron.press(PositronAPI.Key.DOWN, PositronAPI.Key.DOWN,
		// PositronAPI.Key.LEFT, PositronAPI.Key.DOWN,
		// PositronAPI.Key.DOWN, PositronAPI.Key.ENTER); // Align.
		// solo.clickOnText(solo
		// .getString(com.googlecode.iqapps.IQTimeSheet.R.string.align));
		solo.clickOnText("Align");
		// mPositron.press(PositronAPI.Key.UP, PositronAPI.Key.ENTER); // Accept
		solo.clickOnText(solo
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.accept));
		// assertEquals(expected, mPositron.stringAt("#reportlist.0.1.text"));
		assertTrue(solo.searchText(expected));
		// mPositron.press(PositronAPI.Key.BACK);
		solo.goBack();
	}

	private void checkDayReport(String expected) {
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.DAY_REPORT.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("DayReport", 500));
		// log.info(TAG + ": Post wait for DayReport.");
		// assertEquals(Helpers.text1,
		// mPositron.stringAt("#reportlist.0.0.text"));
		assertTrue(solo.searchText(Helpers.text1));
		// assertEquals(expected + " hours",
		// mPositron.stringAt("#reportlist.0.1.text"));
		assertTrue(solo.searchText(expected + " hours"));
		// assertEquals("Hours worked this day: " + expected,
		// mPositron.stringAt("#reportlist.1.text"));
		assertTrue(solo.searchText("Hours worked this day: " + expected));
		// mPositron.press(PositronAPI.Key.BACK);
		solo.goBack();
	}
}