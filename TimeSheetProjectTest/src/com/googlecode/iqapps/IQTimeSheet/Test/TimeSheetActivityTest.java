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
package com.googlecode.iqapps.IQTimeSheet.Test;

import static com.googlecode.autoandroid.positron.PositronAPI.Key.DOWN;
import static com.googlecode.autoandroid.positron.PositronAPI.Key.UP;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.autoandroid.positron.PositronAPI;
import com.googlecode.autoandroid.positron.junit4.TestCase;
import com.googlecode.iqapps.TimeHelpers;

public class TimeSheetActivityTest extends TestCase {
	private Log log = LogFactory.getLog(TimeSheetActivityTest.class);
	private static final String TAG = "TimeSheetActivityTest";

	@Before
	public void setUp() {
		backup();
		prefBackup();
		sql(Helpers.DATABASE_NAME, "DELETE FROM tasks; "
				+ "INSERT INTO tasks (task, active, usage) " + "VALUES ('"
				+ Helpers.text1 + "', 1, 40); "
				+ "INSERT INTO tasks (task, active, usage) " + "VALUES ('"
				+ Helpers.text2 + "', 0, 30);"
				+ "INSERT INTO tasks (task, active, usage) " + "VALUES ('"
				+ Helpers.text3 + "', 1, 50); "
				+ "INSERT INTO tasks (task, active, usage) " + "VALUES ('"
				+ Helpers.text4 + "', 1, 20);");
		sql(Helpers.DATABASE_NAME, "DELETE FROM timesheet; ");
		startActivity("com.googlecode.iqapps.IQTimeSheet",
				"com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
		pause();
	}

	@After
	public void tearDown() {
		finishAll();
		prefRestore();
		restore();
	}

	// // http://code.google.com/p/autoandroid/issues/detail?id=7
	// // @Ignore
	// // Only so the test can be committed while working on something else
	// @Test
	// public void shouldSelectFirstNoteWithArrowKeysEvenAfterFinishAll() {
	// press(DOWN);
	// finishAll();
	// startActivity("com.googlecode.iqapps.IQTimeSheet",
	// "com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
	// pause();
	// press(DOWN);
	// assertEquals(0, intAt("selectedItemPosition"));
	// }

	// @Ignore
	@Test
	public void verifyOrderOfEntries() {
		press(DOWN);
		assertEquals(Helpers.text3, stringAt("listView.0.text"));
		assertEquals(Helpers.text1, stringAt("listView.1.text"));
		assertEquals(Helpers.text4, stringAt("listView.2.text"));
	}

	// @Ignore
	@Test
	public void shouldChangeSelectionWithTheArrowKeys() {
		press(DOWN, DOWN, DOWN, UP);
		int selectedItemPosition = intAt("selectedItemPosition");
		assertEquals(Helpers.text1, stringAt(format("listView.%d.text",
				selectedItemPosition)));
	}

	// @Ignore
	@Test
	public void shouldSelectByTouchingAnEntry() {
		// touch("listView.1");
		press(DOWN, PositronAPI.Key.ENTER);
		Helpers.sleep(50); // Needed to make the test consistent.
		// press(UP);
		// touch("listView.1");
		press(PositronAPI.Key.ENTER);
		long ago = TimeHelpers.millisNow() - (3600 * 1000);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + ago + ";");
		log.info(TAG + ": Post-database update.");
		press(PositronAPI.Key.MENU);
		press(DOWN, DOWN);
		click();
		assertTrue(waitFor("class.simpleName", "DayReport", 500));
		log.info(TAG + ": Post wait for DayReport.");
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("1.00 hours", stringAt("#reportlist.0.1.text"));
		assertEquals("Hours worked this day: 1.00",
				stringAt("#reportlist.1.text"));
		press(PositronAPI.Key.BACK);
		press(PositronAPI.Key.MENU);
		press(DOWN, PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "WeekReport", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("1.00 hours", stringAt("#reportlist.0.1.text"));
		assertEquals("Hours worked this week: 1.00",
				stringAt("#reportlist.1.text"));
	}

	// @Ignore
	@Test
	public void checkRenameTask() {
		contextMenu(0);
		click();
		assertTrue(waitFor("class.simpleName", "EditTaskHandler", 500));
		contextMenu(0);
		click();
		sendKeyDownUp(PositronAPI.Key.DEL);
		sendString(Helpers.text5);
		press(DOWN, PositronAPI.Key.LEFT);
		click();
		assertEquals(Helpers.text5, stringAt("listView.0.text"));
	}

	// @Ignore
	@Test
	public void checkRevive() {
		press(PositronAPI.Key.MENU);
		press(DOWN, PositronAPI.Key.RIGHT, PositronAPI.Key.RIGHT);
		click();
		press(UP);
		click();
		assertTrue(waitFor("class.simpleName", "ReviveTaskHandler", 500));
		click();
		press(DOWN, DOWN);
		int selectedItemPosition = intAt("selectedItemPosition");
		assertEquals(Helpers.text2, stringAt(format("listView.%d.text",
				selectedItemPosition)));
	}

	// @Ignore
	@Test
	public void verifyAddTask() {
		press(PositronAPI.Key.MENU);
		click();
		sendString(Helpers.text5);
		press(DOWN, PositronAPI.Key.LEFT);
		click();
		assertEquals(Helpers.text5, stringAt("listView.3.text"));
	}

	// @Ignore
	@Test
	public void verifyEditTask() {
		// touch("listView.1");
		press(DOWN, PositronAPI.Key.ENTER);
		// touch("listView.1");
		Helpers.sleep(50);
		press(PositronAPI.Key.ENTER);
		long ago = TimeHelpers.millisNow() - (3600 * 1000);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + ago + ";");
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		press(PositronAPI.Key.ENTER);
		press(DOWN, DOWN, PositronAPI.Key.LEFT, DOWN, DOWN,
				PositronAPI.Key.ENTER); // Align.
		press(UP, PositronAPI.Key.ENTER); // Accept
		press(PositronAPI.Key.BACK);
		press(PositronAPI.Key.MENU);
		press(DOWN, DOWN);
		click();
		assertTrue(waitFor("class.simpleName", "DayReport", 500));
		log.info(TAG + ": Post wait for DayReport.");
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("1.00 hours", stringAt("#reportlist.0.1.text"));
		assertEquals("Hours worked this day: 1.00",
				stringAt("#reportlist.1.text"));
		press(PositronAPI.Key.BACK);
	}
}