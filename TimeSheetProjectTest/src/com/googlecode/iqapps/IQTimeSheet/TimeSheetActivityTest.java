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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.View;
import android.widget.ListView;

import com.googlecode.iqapps.TimeHelpers;
import com.jayway.android.robotium.solo.Solo;

public class TimeSheetActivityTest extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	private Log log = LogFactory.getLog(TimeSheetActivityTest.class);
	private static final String TAG = "TimeSheetActivityTest";

	private ListView result;
	private TimeSheetActivity timeSheetActivity;
	private Solo solo;

	public TimeSheetActivityTest() {
		super("com.googlecode.iqapps.IQTimeSheet", TimeSheetActivity.class);
	}

	public void setUp() {
		// try {
		// super.setUp();
		// } catch (Exception e) {
		// }

		timeSheetActivity = getActivity();
		solo = new Solo(getInstrumentation(), timeSheetActivity);
		result = (ListView) timeSheetActivity.findViewById(R.layout.tasklist);
	}

	@Override
	public void tearDown() {
		// try {
		// super.tearDown();
		// } catch (Exception e) {
		// }
		// finishAll();
		// prefRestore();
		// restore();
		timeSheetActivity.db.close();
		solo.finishInactiveActivities();
		solo.finishOpenedActivities();
	}

	// public void shouldSelectFirstNoteWithArrowKeysEvenAfterFinishAll() {
	// press(DOWN);
	// finishAll();
	// startActivity("com.googlecode.iqapps.IQTimeSheet",
	// "com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
	// pause();
	// press(DOWN);
	// assertEquals(0, intAt("selectedItemPosition"));
	// }

	// Perform Initial Setup
	public void test000InitialSetup() {
		timeSheetActivity.deleteDatabase(TimeSheetDbAdapter.DATABASE_NAME);
	}

	// Perform Initial Setup
	public void test001InitialSetup() {
		timeSheetActivity.db.runSQL("DELETE FROM tasks;");
		timeSheetActivity.db.runSQL("INSERT INTO tasks (task, active, usage) "
				+ "VALUES ('" + Helpers.text1 + "', 1, 40);");
		timeSheetActivity.db.runSQL("INSERT INTO tasks (task, active, usage) "
				+ "VALUES ('" + Helpers.text5 + "', 0, 30);");
		timeSheetActivity.db.runSQL("INSERT INTO tasks (task, active, usage) "
				+ "VALUES ('" + Helpers.text3 + "', 1, 50);");
		timeSheetActivity.db.runSQL("INSERT INTO tasks (task, active, usage) "
				+ "VALUES ('" + Helpers.text4 + "', 1, 20);");
		timeSheetActivity.db.runSQL("DELETE FROM timesheet;");

		assertTrue(true);
	}

	@Smoke
	// Verify the database is empty.
	public void test010VerifyDBisSetup() {
		assertTrue(solo.searchText(Helpers.text1));
	}

	@Smoke
	public void test020SetupDatabase() {
		// solo.clickLongOnTextAndPress(Helpers.text4, 0);
		assertTrue(solo.searchText(Helpers.text4));
		solo.clickLongOnText(Helpers.text4);
		while (!solo.searchText(Helpers.RENAMETASK)) {
			solo.sleep(100);
		}
		sendKeys("DPAD_DOWN DPAD_CENTER");
		while (!solo.searchText(Helpers.EDITTASKNAME)) {
			solo.sleep(100);
		}
		solo.clearEditText(0);
		solo.enterText(0, Helpers.text2);
		sendKeys("DPAD_DOWN DPAD_CENTER");
		while (solo.searchText(Helpers.EDITTASKNAME)) {
			solo.sleep(100);
		}
		assertTrue(solo.searchText(Helpers.text2));
	}

	@Smoke
	public void test030VerifyEntries() {
		// sendKeys("DPAD_DOWN");
		assertTrue(solo.searchText(Helpers.text3));
		assertTrue(solo.searchText(Helpers.text1));
		assertTrue(solo.searchText(Helpers.text2));
	}

	// public void test040shouldChangeSelectionWithTheArrowKeys() {
	// sendKeys("DPAD_DOWN DPAD_DOWN DPAD_DOWN DPAD_UP");
	// View focused = ((ListView) timeSheetActivity
	// .findViewById(R.layout.tasklist)).getFocusedChild();
	// if (focused == null) {
	// assertFalse(true);
	// } else {
	// int itemid = focused.getId();
	// String focusedText = timeSheetActivity.getText(itemid).toString();
	// // int selectedItemPosition = intAt("selectedItemPosition");
	// assertEquals(Helpers.text1, focusedText);
	// }
	// }

	// public void test050shouldSelectByTouchingAnEntry() {
	// timeSheetActivity.db.runSQL("DELETE FROM timesheet;");
	// solo.clickOnText(Helpers.text3);
	// // press(DOWN, PositronAPI.Key.ENTER);
	// Helpers.sleep(50);
	// // Needed to make the test consistent.
	// // press(UP);
	// // touch("listView.1");
	// solo.clickOnText(Helpers.text3);
	// // press(PositronAPI.Key.ENTER);
	// long ago = TimeHelpers.millisNow() - (3600 * 1000);
	// // sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + ago +
	// // ";");
	// timeSheetActivity.db.runSQL("UPDATE timesheet SET timein=" + ago + ";");
	// log.info(TAG + ": Post-database update.");
	// // press(PositronAPI.Key.MENU);
	// // press(DOWN, DOWN);
	// sendKeys("DPAD_MENU");
	// sendKeys("DPAD_DOWN DPAD_DOWN DPAD_CENTER");
	// // click();
	// // assertTrue(waitFor("class.simpleName", "DayReport", 500));
	// while(!solo.searchText("Day Report")) {
	// log.info(TAG + ": Wait for DayReport.");
	// solo.sleep(100);
	// }
	// log.info(TAG + ": Post wait for DayReport.");
	// // assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
	// assertTrue(solo.searchText(Helpers.text1));
	// // assertEquals("1.00 hours", stringAt("#reportlist.0.1.text"));
	// assertTrue(solo.searchText("1.00 hours"));
	// // assertEquals("Hours worked this day: 1.00",
	// // stringAt("#reportlist.1.text"));
	// assertTrue(solo.searchText("Hours worked this day: 1.00"));
	// // press(PositronAPI.Key.BACK);
	// sendKeys("DPAD_BACK");
	// // press(PositronAPI.Key.MENU);
	// sendKeys("DPAD_MENU");
	// // press(DOWN, PositronAPI.Key.RIGHT);
	// sendKeys("DPAD_RIGHT DPAD_CENTER");
	// // click();
	// // assertTrue(waitFor("class.simpleName", "WeekReport", 500));
	// assertTrue(solo.searchText("Week Report"));
	// // assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
	// assertTrue(solo.searchText(Helpers.text1));
	// // assertEquals("1.00 hours", stringAt("#reportlist.0.1.text"));
	// assertTrue(solo.searchText("1.00 hours"));
	// // assertEquals("Hours worked this week: 1.00",
	// // stringAt("#reportlist.1.text"));
	// assertTrue(solo.searchText("Hours worked this week: 1.00"));
	// }

	// public void test060checkRenameTask() {
	// contextMenu(0);
	// click();
	// assertTrue(waitFor("class.simpleName", "EditTaskHandler", 500));
	// contextMenu(0);
	// click();
	// sendKeyDownUp(PositronAPI.Key.DEL);
	// sendString(Helpers.text5);
	// press(DOWN, PositronAPI.Key.LEFT);
	// click();
	// assertEquals(Helpers.text5, stringAt("listView.0.text"));
	// }

	// public void test070checkRevive() {
	// press(PositronAPI.Key.MENU);
	// press(DOWN, PositronAPI.Key.RIGHT, PositronAPI.Key.RIGHT);
	// click();
	// press(UP);
	// click();
	// assertTrue(waitFor("class.simpleName", "ReviveTaskHandler", 500));
	// click();
	// press(DOWN, DOWN);
	// int selectedItemPosition = intAt("selectedItemPosition");
	// assertEquals(Helpers.text2,
	// stringAt(format("listView.%d.text", selectedItemPosition)));
	// }

	// public void test080verifyAddTask() {
	// sendKey("MENU");
	// solo.clickOnMenuItem(timeSheetActivity
	// .getString(R.string.menu_new_task));
	// sendString(Helpers.text5);
	// press(DOWN, PositronAPI.Key.LEFT);
	// click();
	// assertEquals(Helpers.text5, stringAt("listView.3.text"));
	// }

	// public void test090verifyEditTask() {
	// solo.clickOnText(Helpers.text1);
	// sendKeys("DPAD_MENU");
	// solo.clickOnMenuItem(timeSheetActivity.getText(
	// R.string.menu_edit_day_entries).toString());
	// solo.sleep(50);
	// sendKeys("ENTER");
	// long ago = TimeHelpers.millisNow() - (3600 * 1000);
	// timeSheetActivity.db.runSQL("UPDATE timesheet SET timein=" + ago + ";");
	// // press(PositronAPI.Key.MENU);
	// // press(PositronAPI.Key.RIGHT);
	// sendKeys("DPAD_MENU DPAD_RIGHT");
	// // click();
	// sendKeys("DPAD_CENTER");
	// // assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler",500));
	// // assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
	// // press(PositronAPI.Key.ENTER);
	// // press(DOWN, DOWN, PositronAPI.Key.LEFT, DOWN, DOWN,
	// // PositronAPI.Key.ENTER);
	// // // Align.
	// // press(UP, PositronAPI.Key.ENTER);
	// // // Accept
	// // press(PositronAPI.Key.BACK);
	// // press(PositronAPI.Key.MENU);
	// // press(DOWN, DOWN);
	// // click();
	// // assertTrue(waitFor("class.simpleName", "DayReport", 500));
	// // log.info(TAG + ": Post wait for DayReport.");
	// // assertEquals(Helpers.text1,
	// // stringAt("#reportlist.0.0.text"));
	// // assertEquals("1.00 hours",
	// // stringAt("#reportlist.0.1.text"));
	// // assertEquals("Hours worked this day: 1.00",
	// // stringAt("#reportlist.1.text"));
	// sendKeys("BACK");
	// // press(PositronAPI.Key.BACK);
	// }
}