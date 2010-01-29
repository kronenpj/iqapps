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

public class TimeAlignTests extends TestCase {
	private Log log = LogFactory.getLog(TimeAlignTests.class);
	private static final String TAG = "TimeSheetActivityTest";
	private int waitTime = 99; // Milliseconds

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

	// @Ignore
	@Test
	public void verifyTime2Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 0);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 59);
		setAlignTimePreferenceViaMenu(1); // 2-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:00 to 10:59", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime3Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 1);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 59);
		setAlignTimePreferenceViaMenu(2); // 3-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:01 to 10:59", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime4Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 1);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 58);
		setAlignTimePreferenceViaMenu(3); // 4-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:01 to 10:58", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime5Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 2);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 58);
		setAlignTimePreferenceViaMenu(4); // 5-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:02 to 10:58", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime6Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 2);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 58);
		setAlignTimePreferenceViaMenu(5); // 6-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:02 to 10:58", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime10Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 4);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 56);
		setAlignTimePreferenceViaMenu(6); // 10-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:04 to 10:56", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime12Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 5);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 55);
		setAlignTimePreferenceViaMenu(7); // 12-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:05 to 10:55", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime15Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 7);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 53);
		setAlignTimePreferenceViaMenu(8); // 15-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:07 to 10:53", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime20Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 9);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 51);
		setAlignTimePreferenceViaMenu(9); // 20-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:09 to 10:51", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime30Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 14);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 46);
		setAlignTimePreferenceViaMenu(10); // 30-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:14 to 10:46", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyTime60Align() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 29);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 31);
		setAlignTimePreferenceViaMenu(11); // 60-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:29 to 10:31", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:00 to 11:00");
		checkDayReport("3.00");
	}

	// @Ignore
	@Test
	public void verifyAlignEdge1() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 3);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 56);
		setAlignTimePreferenceViaMenu(5); // 6-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:03 to 10:56", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:06 to 10:54");
		checkDayReport("2.80");
	}

	// @Ignore
	@Test
	public void verifyAlignEdge2() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 8);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 51);
		setAlignTimePreferenceViaMenu(5); // 6-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:08 to 10:51", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:06 to 10:54");
		checkDayReport("2.80");
	}

	// @Ignore
	@Test
	public void verifyAlignEdge3() {
		long now = TimeHelpers.millisNow();
		long eightAM = TimeHelpers.millisSetTime(now, 8, 9);
		long elevenAM = TimeHelpers.millisSetTime(now, 10, 50);
		setAlignTimePreferenceViaMenu(5); // 6-minute
		pressTaskTwice(1);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + eightAM
				+ ", timeout=" + elevenAM + ";");
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:09 to 10:50", stringAt("#reportlist.0.1.text"));
		alignWithButton("08:12 to 10:48");
		checkDayReport("2.60");
	}

	// @Ignore
	@Test
	public void testDailyReport() {
		long now = TimeHelpers.millisNow();

		setAlignTimePreferenceViaMenu(5); // 6-minute

		pressTaskTwice(1);
		pressTaskTwice(0);
		pressTaskTwice(3);

		long startTime = TimeHelpers.millisSetTime(now, 8, 2);
		long stopTime = TimeHelpers.millisSetTime(now, 9, 57);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + startTime
				+ ", timeout=" + stopTime + " where chargeno=2;");

		startTime = TimeHelpers.millisSetTime(now, 9, 58);
		stopTime = TimeHelpers.millisSetTime(now, 10, 32);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + startTime
				+ ", timeout=" + stopTime + " where chargeno=4;");

		startTime = TimeHelpers.millisSetTime(now, 10, 32);
		stopTime = TimeHelpers.millisSetTime(now, 11, 58);
		sql(Helpers.DATABASE_NAME, "UPDATE timesheet SET timein=" + startTime
				+ ", timeout=" + stopTime + " where chargeno=5;");

		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU, PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "EditDayEntriesHandler", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("08:02 to 09:57", stringAt("#reportlist.0.1.text"));
		assertEquals(Helpers.text3, stringAt("#reportlist.1.0.text"));
		assertEquals("09:58 to 10:32", stringAt("#reportlist.1.1.text"));
		assertEquals(Helpers.text4, stringAt("#reportlist.2.0.text"));
		assertEquals("10:32 to 11:58", stringAt("#reportlist.2.1.text"));
		alignWithButton("08:00 to 10:00");

		press(PositronAPI.Key.MENU, PositronAPI.Key.RIGHT,
				PositronAPI.Key.ENTER);
		press(DOWN);
		press(PositronAPI.Key.ENTER);
		press(DOWN, DOWN, PositronAPI.Key.LEFT, DOWN, DOWN,
				PositronAPI.Key.ENTER); // Align.
		press(UP, PositronAPI.Key.ENTER); // Accept
		assertEquals("10:00 to 10:30", stringAt("#reportlist.1.1.text"));
		press(PositronAPI.Key.BACK);

		press(PositronAPI.Key.MENU, PositronAPI.Key.RIGHT,
				PositronAPI.Key.ENTER);
		press(DOWN, DOWN);
		press(PositronAPI.Key.ENTER);
		press(DOWN, DOWN, PositronAPI.Key.LEFT, DOWN, DOWN,
				PositronAPI.Key.ENTER); // Align.
		press(UP, PositronAPI.Key.ENTER); // Accept
		assertEquals("10:30 to 12:00", stringAt("#reportlist.2.1.text"));
		press(PositronAPI.Key.BACK);

		Helpers.sleep(waitTime);
		press(PositronAPI.Key.MENU);
		press(DOWN, DOWN);
		click();
		assertTrue(waitFor("class.simpleName", "DayReport", 500));
		// log.info(TAG + ": Post wait for DayReport.");
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("2.00 hours", stringAt("#reportlist.0.1.text"));
		assertEquals(Helpers.text4, stringAt("#reportlist.1.0.text"));
		assertEquals("1.50 hours", stringAt("#reportlist.1.1.text"));
		assertEquals(Helpers.text3, stringAt("#reportlist.2.0.text"));
		assertEquals("0.50 hours", stringAt("#reportlist.2.1.text"));
		assertEquals("Hours worked this day: 4.00",
				stringAt("#reportlist.3.text"));
		press(PositronAPI.Key.BACK);
	}

	private void pressTaskTwice(int taskNo) {
		Helpers.sleep(waitTime);
		press(UP, UP, UP);
		for (int i = 0; i < taskNo; i++)
			press(DOWN);
		press(PositronAPI.Key.ENTER);
		Helpers.sleep(waitTime*2); // Needed to make the test consistent.
		press(PositronAPI.Key.ENTER);
	}

	private void setAlignTimePreferenceViaMenu(int downCount) {
		press(PositronAPI.Key.MENU);
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.RIGHT, PositronAPI.Key.RIGHT);
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.ENTER);
		assertTrue(waitFor("class.simpleName", "MyPreferenceActivity", 500));
		press(PositronAPI.Key.ENTER);
		Helpers.sleep(waitTime);
		for (int i = 0; i < Helpers.alignments; i++)
			press(UP); // Up from any pre-set value.
		for (int i = 0; i < downCount; i++)
			press(DOWN);
		press(PositronAPI.Key.ENTER);
		Helpers.sleep(waitTime);
		for (int i = downCount; i < Helpers.alignments; i++)
			press(DOWN);
		press(DOWN, PositronAPI.Key.ENTER, PositronAPI.Key.BACK);
	}

	private void alignWithButton(String expected) {
		press(PositronAPI.Key.ENTER);
		press(DOWN, DOWN, PositronAPI.Key.LEFT, DOWN, DOWN,
				PositronAPI.Key.ENTER); // Align.
		press(UP, PositronAPI.Key.ENTER); // Accept
		assertEquals(expected, stringAt("#reportlist.0.1.text"));
		press(PositronAPI.Key.BACK);
	}

	private void checkDayReport(String expected) {
		press(PositronAPI.Key.MENU);
		press(DOWN, DOWN);
		click();
		assertTrue(waitFor("class.simpleName", "DayReport", 500));
		// log.info(TAG + ": Post wait for DayReport.");
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals(expected + " hours", stringAt("#reportlist.0.1.text"));
		assertEquals("Hours worked this day: " + expected,
				stringAt("#reportlist.1.text"));
		press(PositronAPI.Key.BACK);
	}
}