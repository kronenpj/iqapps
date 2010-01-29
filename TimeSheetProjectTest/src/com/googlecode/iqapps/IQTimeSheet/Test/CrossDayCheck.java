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
package com.googlecode.iqapps.IQTimeSheet.Test;

import static com.googlecode.autoandroid.positron.PositronAPI.Key.DOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.autoandroid.positron.PositronAPI;
import com.googlecode.autoandroid.positron.junit4.TestCase;
import com.googlecode.iqapps.TimeHelpers;

/**
 * @author kronenpj
 * 
 */
public class CrossDayCheck extends TestCase {
	private Log log = LogFactory.getLog(CrossDayCheck.class);
	private static final String TAG = "WeekReportTest";
	private static long now = 0;
	private static final String insertIntoTasks = "INSERT INTO tasks (task, active, usage) "
			+ "VALUES ('";
	private static final String insertIntoTimeSheet = "INSERT INTO timesheet (chargeno, timein, timeout) "
			+ "VALUES (";

	@Before
	public void setUp() {
		backup();
		prefBackup();
		sql(Helpers.DATABASE_NAME, "DELETE FROM tasks;");
		sql(Helpers.DATABASE_NAME, "DELETE FROM timesheet; ");
		sql(Helpers.DATABASE_NAME, insertIntoTasks + Helpers.text1
				+ "', 1, 40);");
		sql(Helpers.DATABASE_NAME, insertIntoTasks + Helpers.text2
				+ "', 0, 30);");
		sql(Helpers.DATABASE_NAME, insertIntoTasks + Helpers.text3
				+ "', 1, 50);");
		sql(Helpers.DATABASE_NAME, insertIntoTasks + Helpers.text4
				+ "', 1, 20);");
		now = TimeHelpers.millisNow();
		setupTimeSheetDB();
		startActivity("com.googlecode.iqapps.IQTimeSheet",
				"com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
		pause();
	}

	/**
	 * 
	 */
	private void setupTimeSheetDB() {
		long yesterday = TimeHelpers.millisToStartOfDay(now) - 8 * 3600000; //5pm

		sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "2, " + yesterday
				+ ", " + 0 + ");");
	}

	@After
	public void tearDown() {
		finishAll();
		prefRestore();
		restore();
	}

	// @Ignore
	@Test
	public void crossDayClockTest1() {
		press(PositronAPI.Key.LEFT, PositronAPI.Key.ENTER);
		press(PositronAPI.Key.MENU, DOWN, PositronAPI.Key.ENTER);
		assertTrue(waitFor("class.simpleName", "DayReport", 500));
		 try {
			existsAt("#reportlist.0.text");
			fail("There should be no entries in the list, but found one.");
		} catch (Exception e) {
		}
		press(DOWN, PositronAPI.Key.LEFT, PositronAPI.Key.ENTER);
		Helpers.sleep(50);
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("8.00 hours", stringAt("#reportlist.0.1.text"));
	}

	// @Ignore
	@Test
	public void crossDayClockTest2() {
		press(PositronAPI.Key.RIGHT, PositronAPI.Key.ENTER);
		press(PositronAPI.Key.MENU, DOWN, PositronAPI.Key.ENTER);
		assertTrue(waitFor("class.simpleName", "DayReport", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		long midnight = TimeHelpers.millisToStartOfDay(now);
		float hours = TimeHelpers.calculateDuration(midnight, now);
		String appHourString = stringAt("#reportlist.0.1.text");
		float appHours = Float.valueOf(appHourString.substring(0, appHourString
				.indexOf(' ')));
		float delta = hours - appHours;
		if (delta < 0)
			delta = -delta;
		assertTrue(
				"Difference between hours and appHours is > 0.02 hours.  Was "
						+ delta + " hours.", delta < 0.02);
		press(DOWN, DOWN, PositronAPI.Key.LEFT, PositronAPI.Key.ENTER);
		Helpers.sleep(50);
		assertEquals("8.00 hours", stringAt("#reportlist.0.1.text"));
	}
}
